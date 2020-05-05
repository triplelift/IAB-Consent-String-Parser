package com.iab.gdpr;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;

import java.util.List;

/**
 * This class implements a parser for the IAB consent string as specified in
 * https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md#vendor-consent-string-format-
 */

public class ConsentStringParser implements ConsentInfo {

	private static final int VENDOR_ENCODING_RANGE = 1;

	private static final int VERSION_BIT_OFFSET = 0;
	private static final int VERSION_BIT_SIZE = 6;
	private static final int CREATED_BIT_OFFSET = 6;
	private static final int CREATED_BIT_SIZE = 36;
	private static final int UPDATED_BIT_OFFSET = 42;
	private static final int UPDATED_BIT_SIZE = 36;
	private static final int CMP_ID_OFFSET = 78;
	private static final int CMP_ID_SIZE = 12;
	private static final int CMP_VERSION_OFFSET = 90;
	private static final int CMP_VERSION_SIZE = 12;
	private static final int CONSENT_SCREEN_SIZE_OFFSET = 102;
	private static final int CONSENT_SCREEN_SIZE = 6;
	private static final int CONSENT_LANGUAGE_OFFSET = 108;
	private static final int CONSENT_LANGUAGE_SIZE = 12;
	private static final int VENDOR_LIST_VERSION_OFFSET = 120;
	private static final int VENDOR_LIST_VERSION_SIZE = 12;
	private static final int PURPOSES_OFFSET = 132;
	private static final int PURPOSES_SIZE = 24;
	private static final int MAX_VENDOR_ID_OFFSET = 156;
	private static final int MAX_VENDOR_ID_SIZE = 16;
	private static final int ENCODING_TYPE_OFFSET = 172;
	private static final int ENCODING_TYPE_SIZE = 1;
	private static final int VENDOR_BITFIELD_OFFSET = 173;
	private static final int DEFAULT_CONSENT_OFFSET = 173;
	private static final int NUM_ENTRIES_OFFSET = 174;
	private static final int NUM_ENTRIES_SIZE = 12;
	private static final int RANGE_ENTRY_OFFSET = 186;
	private static final int VENDOR_ID_SIZE = 16;

	private String consentString;
	private final Bits bits;
	// fields contained in the consent string
	private final int version;
	private final Instant consentRecordCreated;
	private final Instant consentRecordLastUpdated;
	private final int cmpID;
	private final int cmpVersion;
	private final int consentScreenID;
	private final String consentLanguage;
	private final int vendorListVersion;
	private final int maxVendorSize;
	private final int vendorEncodingType;
	private final List<Boolean> purposeConsents = new ArrayList<Boolean>();
	// only used when range entry is enabled
	private List<RangeEntry> rangeEntries;
	private boolean defaultConsent;

	private final List<Purpose.PurposeV1> consentedPurposes;

	private static Decoder decoder = Base64.getUrlDecoder();

	/**
	 * Constructor.
	 *
	 * @param consentString
	 *            (required). The binary user consent data encoded as url and filename safe base64 string
	 *
	 * @throws ParseException
	 *             if the consent string cannot be parsed
	 */
	public ConsentStringParser(String consentString) throws ParseException {
		this(decoder.decode(consentString));
		this.consentString = consentString;
	}

	/**
	 * Constructor
	 *
	 * @param bytes:
	 *            the byte string encoding the user consent data
	 * @throws ParseException
	 *             when the consent string cannot be parsed
	 */
	public ConsentStringParser(byte[] bytes) throws ParseException {
		this.bits = new Bits(bytes);
		// begin parsing

		this.version = bits.getInt(VERSION_BIT_OFFSET, VERSION_BIT_SIZE);
		this.consentRecordCreated = bits.getInstantFromEpochDemiseconds(CREATED_BIT_OFFSET, CREATED_BIT_SIZE);
		this.consentRecordLastUpdated = bits.getInstantFromEpochDemiseconds(UPDATED_BIT_OFFSET, UPDATED_BIT_SIZE);
		this.cmpID = bits.getInt(CMP_ID_OFFSET, CMP_ID_SIZE);
		this.cmpVersion = bits.getInt(CMP_VERSION_OFFSET, CMP_VERSION_SIZE);
		this.consentScreenID = bits.getInt(CONSENT_SCREEN_SIZE_OFFSET, CONSENT_SCREEN_SIZE);
		this.consentLanguage = bits.getSixBitString(CONSENT_LANGUAGE_OFFSET, CONSENT_LANGUAGE_SIZE);
		this.vendorListVersion = bits.getInt(VENDOR_LIST_VERSION_OFFSET, VENDOR_LIST_VERSION_SIZE);
		this.maxVendorSize = bits.getInt(MAX_VENDOR_ID_OFFSET, MAX_VENDOR_ID_SIZE);
		this.vendorEncodingType = bits.getInt(ENCODING_TYPE_OFFSET, ENCODING_TYPE_SIZE);
		for (int i = PURPOSES_OFFSET, ii = PURPOSES_OFFSET + PURPOSES_SIZE; i < ii; i++) {
			purposeConsents.add(bits.getBit(i));
		}
		List<Purpose.PurposeV1> purposes = new ArrayList<Purpose.PurposeV1>();
		for (int i = 1, ii = purposeConsents.size(); i <= ii; i++) {
			if (isPurposeConsented(i)) {
				purposes.add(Purpose.PurposeV1.valueOf(i));
			}
		}
		this.consentedPurposes = purposes;
		if (vendorEncodingType == VENDOR_ENCODING_RANGE) {
			this.rangeEntries = new ArrayList<RangeEntry>();
			this.defaultConsent = bits.getBit(DEFAULT_CONSENT_OFFSET);
			int numEntries = bits.getInt(NUM_ENTRIES_OFFSET, NUM_ENTRIES_SIZE);
			int currentOffset = RANGE_ENTRY_OFFSET;
			for (int i = 0; i < numEntries; i++) {
				boolean range = bits.getBit(currentOffset);
				currentOffset++;
				if (range) {
					int startVendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
					currentOffset += VENDOR_ID_SIZE;
					int endVendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
					currentOffset += VENDOR_ID_SIZE;
					rangeEntries.add(new RangeEntry(startVendorId, endVendorId));
				} else {
					int vendorId = bits.getInt(currentOffset, VENDOR_ID_SIZE);
					currentOffset += VENDOR_ID_SIZE;
					rangeEntries.add(new RangeEntry(vendorId));
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConsentString() {
		return consentString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instant getConsentRecordCreated() {
		return consentRecordCreated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instant getConsentRecordLastUpdated() {
		return consentRecordLastUpdated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getVersion() {
		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCmpId() {
		return cmpID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCmpVersion() {
		return cmpVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getConsentScreen() {
		return consentScreenID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConsentLanguage() {
		return consentLanguage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Purpose> getConsentedPurposes() {
		return new ArrayList<Purpose>(consentedPurposes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getVendorListVersion() {
		return vendorListVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPurposeConsented(int purposeId) {
		if (purposeId < 1 || purposeId > purposeConsents.size()) {
			return false;
		}
		return purposeConsents.get(purposeId - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPurposeConsented(Purpose purpose) {
		if (purpose.getVersion() != this.version) {
			return false;
		}
		return isPurposeConsented(purpose.getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVendorConsented(int vendorId) {
		if (vendorEncodingType == VENDOR_ENCODING_RANGE) {
			boolean present = RangeEntry.isVendorIdInRange(vendorId, rangeEntries);
			return present != defaultConsent;
		} else {
			boolean allowed;
			try {
				allowed = bits.getBit(VENDOR_BITFIELD_OFFSET + vendorId - 1);
			} catch (ParseException e) {
				// index of out bounds
				allowed = false;
			}
			return allowed;
		}

	}
}
