package com.iab.gdpr;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * This class implements a parser for the IAB consent string as specified in
 * https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#creating-a-tc-string
 */

public class ConsentStringParserV2 implements ConsentInfo {
	private static final int SEGMENT_TYPE_OFFSET = 0;
	private static final int SEGMENT_TYPE_SIZE = 3;
	private static final int VENDOR_ID_SIZE = 16;
	private static final int VERSION_OFFSET = 0;
	private static final int VERSION_SIZE = 6;
	private static final int CREATED_OFFSET = 6;
	private static final int CREATED_SIZE = 36;
	private static final int LAST_UPDATED_OFFSET = 42;
	private static final int LAST_UPDATED_SIZE = 36;
	private static final int CMP_ID_OFFSET = 78;
	private static final int CMP_ID_SIZE = 12;
	private static final int CMP_VERSION_OFFSET = 90;
	private static final int CMP_VERSION_SIZE = 12;
	private static final int CONSENT_SCREEN_OFFSET = 102;
	private static final int CONSENT_SCREEN_SIZE = 6;
	private static final int CONSENT_LANGUAGE_OFFSET = 108;
	private static final int CONSENT_LANGUAGE_SIZE = 12;
	private static final int VENDOR_LIST_VERSION_OFFSET = 120;
	private static final int VENDOR_LIST_VERSION_SIZE = 12;
	private static final int TCF_POLICY_VERSION_OFFSET = 132;
	private static final int TCF_POLICY_VERSION_SIZE = 6;
	private static final int IS_SERVICE_SPECIFIC_OFFSET = 138;
	private static final int USE_NON_STANDARD_STACKS_OFFSET = 139;
	private static final int SPECIAL_FEATURE_OPT_INS_OFFSET = 140;
	private static final int SPECIAL_FEATURE_OPT_INS_SIZE = 12;
	private static final int PURPOSES_CONSENT_OFFSET = 152;
	private static final int PURPOSES_CONSENT_SIZE = 24;
	private static final int PURPOSES_LI_TRANSPARENCY_OFFSET = 176;
	private static final int PURPOSES_LI_TRANSPARENCY_SIZE = 24;
	private static final int PURPOSE_ONE_TREATMENT_OFFSET = 200;
	private static final int PUBLISHER_CC_OFFSET = 201;
	private static final int PUBLISHER_CC_SIZE = 12;
	private static final int NUM_ENTRIES_SIZE = 12;
	private static final int NUM_PUB_RESTRICTIONS_SIZE = 12;
	private static final int PURPOSE_ID_SIZE = 6;
	private static final int RESTRICTION_TYPE_SIZE = 2;
	private static final int PUB_PURPOSES_CONTENT_SIZE = 24;
	private static final int PUB_PURPOSES_LI_TRANSPARENCY_SIZE = 24;
	private static final int NUM_CUSTOM_PURPOSES_SIZE = 6;

	private static Base64.Decoder decoder = Base64.getUrlDecoder();

	private String consentString;
	private int version;
	private Instant consentRecordCreated;
	private Instant consentRecordLastUpdated;
	private int cmpId;
	private int cmpVersion;
	private int consentScreen;
	private String consentLanguage;
	private int vendorListVersion;
	private int tcfPolicyVersion;
	private boolean serviceSpecific;
	private boolean nonStandardStacks;
	private List<Boolean> featureOptins;
	private List<Boolean> purposeConsents;
	private List<Integer> consentedPurposes;
	private List<Boolean> purposeLegitInterests;
	private boolean purposeOneDisclosed;
	private String publisherCc;
	private List<Boolean> vendorConsentsBitField;
	private List<RangeEntry> vendorConsentsRanges;
	private List<Boolean> vendorLegitInterestsBitField;
	private List<RangeEntry> vendorLegitInterestRanges;
	private List<PubRestrictionEntry> publisherRestrictions;
	private List<Boolean> vendorDisclosureBitField;
	private List<RangeEntry> vendorDisclosureRanges;
	private List<Boolean> vendorAllowancesBitField;
	private List<RangeEntry> vendorAllowancesRanges;
	private List<Boolean> pubPurposeConsents;
	private List<Boolean> pubPurposeLegitInterests;
	private List<Boolean> customPurposeConsents;
	private List<Boolean> customPurposeLegitInterests;

	public ConsentStringParserV2(String consentString) throws ParseException {
		this.consentString = consentString;
		List<String> segments = Arrays.asList(consentString.split("\\."));
		for (int i = 0; i < segments.size(); i++) {
			if (i == 0) {
				// the core segment is required and should always be in the first slot
				parseCore(new Bits(decoder.decode(segments.get(i))));
			} else {
				parseSegment(decoder.decode(segments.get(i)));
			}
		}
	}

	private void parseSegment(byte[] bytes) throws ParseException {
		Bits bits = new Bits(bytes);
		switch (SegmentType.valueOf(bits.getInt(SEGMENT_TYPE_OFFSET, SEGMENT_TYPE_SIZE))) {
		case DISCLOSED_VENDORS:
			parseDisclosedVendors(bits);
			return;
		case ALLOWED_VENDORS:
			parseAllowedVendors(bits);
			return;
		case PUBLISHER_TC:
			parsePublisherTc(bits);
			return;
		default:
		}
	}

	private void parseCore(Bits bits) throws ParseException {
		this.version = bits.getInt(VERSION_OFFSET, VERSION_SIZE);
		this.consentRecordCreated = bits.getInstantFromEpochDemiseconds(CREATED_OFFSET, CREATED_SIZE);
		this.consentRecordLastUpdated = bits.getInstantFromEpochDemiseconds(LAST_UPDATED_OFFSET, LAST_UPDATED_SIZE);
		this.cmpId = bits.getInt(CMP_ID_OFFSET, CMP_ID_SIZE);
		this.cmpVersion = bits.getInt(CMP_VERSION_OFFSET, CMP_VERSION_SIZE);
		this.consentScreen = bits.getInt(CONSENT_SCREEN_OFFSET, CONSENT_SCREEN_SIZE);
		this.consentLanguage = bits.getSixBitString(CONSENT_LANGUAGE_OFFSET, CONSENT_LANGUAGE_SIZE);
		this.vendorListVersion = bits.getInt(VENDOR_LIST_VERSION_OFFSET, VENDOR_LIST_VERSION_SIZE);
		this.tcfPolicyVersion = bits.getInt(TCF_POLICY_VERSION_OFFSET, TCF_POLICY_VERSION_SIZE);
		this.serviceSpecific = bits.getBit(IS_SERVICE_SPECIFIC_OFFSET);
		this.nonStandardStacks = bits.getBit(USE_NON_STANDARD_STACKS_OFFSET);
		this.featureOptins = bits.getBitList(SPECIAL_FEATURE_OPT_INS_OFFSET, SPECIAL_FEATURE_OPT_INS_SIZE);
		this.purposeConsents = bits.getBitList(PURPOSES_CONSENT_OFFSET, PURPOSES_CONSENT_SIZE);
		this.consentedPurposes = new ArrayList<Integer>();
		for (int i = 1; i <= this.purposeConsents.size(); i++) {
			if (isPurposeConsented(i)) {
				this.consentedPurposes.add(i);
			}
		}
		this.purposeLegitInterests = bits.getBitList(PURPOSES_LI_TRANSPARENCY_OFFSET, PURPOSES_LI_TRANSPARENCY_SIZE);
		this.purposeOneDisclosed = !bits.getBit(PURPOSE_ONE_TREATMENT_OFFSET);
		this.publisherCc = bits.getSixBitString(PUBLISHER_CC_OFFSET, PUBLISHER_CC_SIZE);

		// parse Consented Vendor Range section or BitField section
		int variableOffset = PUBLISHER_CC_OFFSET + PUBLISHER_CC_SIZE;
		RangeOrBitFieldParser rangeOrBitFieldParser = new RangeOrBitFieldParser(bits, variableOffset);
		this.vendorConsentsRanges = rangeOrBitFieldParser.getRangeEntries();
		this.vendorConsentsBitField = rangeOrBitFieldParser.getBitField();
		variableOffset = rangeOrBitFieldParser.getOffset();

		// parse Vendor Legitimate Interest Range section or BitField section
		rangeOrBitFieldParser = new RangeOrBitFieldParser(bits, variableOffset);
		this.vendorLegitInterestRanges = rangeOrBitFieldParser.getRangeEntries();
		this.vendorLegitInterestsBitField = rangeOrBitFieldParser.getBitField();
		variableOffset = rangeOrBitFieldParser.getOffset();

		// parse Publisher Restrictions
		int numPubRestrictions = bits.getInt(variableOffset, NUM_PUB_RESTRICTIONS_SIZE);
		variableOffset += NUM_PUB_RESTRICTIONS_SIZE;
		this.publisherRestrictions = new ArrayList<PubRestrictionEntry>();
		for (int i = 0; i < numPubRestrictions; i++) {
			int purposeId = bits.getInt(variableOffset, PURPOSE_ID_SIZE);
			variableOffset += PURPOSE_ID_SIZE;
			PubRestrictionEntry.RestrictionType restrictionType = PubRestrictionEntry.RestrictionType
					.valueOf(bits.getInt(variableOffset, RESTRICTION_TYPE_SIZE));
			variableOffset += RESTRICTION_TYPE_SIZE;
			RangeSectionParser rangeSectionParser = new RangeSectionParser(bits, variableOffset);
			variableOffset = rangeSectionParser.getOffset();
			this.publisherRestrictions
					.add(new PubRestrictionEntry(purposeId, restrictionType, rangeSectionParser.getEntries()));
		}
	}

	private void parseDisclosedVendors(Bits bits) throws ParseException {
		RangeOrBitFieldParser parser = new RangeOrBitFieldParser(bits, SEGMENT_TYPE_SIZE);
		this.vendorDisclosureRanges = parser.getRangeEntries();
		this.vendorDisclosureBitField = parser.getBitField();
	}

	private void parseAllowedVendors(Bits bits) throws ParseException {
		RangeOrBitFieldParser parser = new RangeOrBitFieldParser(bits, SEGMENT_TYPE_SIZE);
		this.vendorAllowancesRanges = parser.getRangeEntries();
		this.vendorAllowancesBitField = parser.getBitField();
	}

	private void parsePublisherTc(Bits bits) throws ParseException {
		int offset = SEGMENT_TYPE_SIZE;
		this.pubPurposeConsents = bits.getBitList(offset, PUB_PURPOSES_CONTENT_SIZE);
		offset += PUB_PURPOSES_CONTENT_SIZE;
		this.pubPurposeLegitInterests = bits.getBitList(offset, PUB_PURPOSES_LI_TRANSPARENCY_SIZE);
		offset += PUB_PURPOSES_CONTENT_SIZE;
		int numCustomPurposes = bits.getInt(offset, NUM_CUSTOM_PURPOSES_SIZE);
		offset += NUM_CUSTOM_PURPOSES_SIZE;
		this.customPurposeConsents = bits.getBitList(offset, numCustomPurposes);
		offset += numCustomPurposes;
		this.customPurposeLegitInterests = bits.getBitList(offset, numCustomPurposes);
	}

	private boolean findVendorIdInRange(int vendorId, List<RangeEntry> rangeEntries) {
		int limit = rangeEntries.size();
		if (limit == 0) {
			return false;
		}
		int index = limit / 2;
		while (index >= 0 && index < limit) {
			RangeEntry entry = rangeEntries.get(index);
			if (entry.containsVendorId(vendorId)) {
				return true;
			}
			if (index == 0 || index == limit - 1) {
				return false;
			}
			if (entry.idIsGreaterThanMax(vendorId)) {
				index = (index + ((limit - index) / 2));
			} else {
				index = index / 2;
			}
		}
		return false;
	}

	private boolean findIdInBitField(int index, List<Boolean> bitField) {
		if (index < 1 || index > bitField.size()) {
			return false;
		}
		return bitField.get(index - 1);
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
	public int getVersion() {
		return version;
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
	public int getCmpId() {
		return cmpId;
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
		return consentScreen;
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
	public int getVendorListVersion() {
		return vendorListVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPurposeConsented(int purposeId) {
		return findIdInBitField(purposeId, purposeConsents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> getConsentedPurposes() {
		return new ArrayList<Integer>(consentedPurposes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVendorConsented(int vendorId) {
		if (vendorConsentsBitField != null) {
			return findIdInBitField(vendorId, vendorConsentsBitField);
		} else {
			return findVendorIdInRange(vendorId, vendorConsentsRanges);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getTcfPolicyVersion() {
		return tcfPolicyVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isServiceSpecific() {
		return serviceSpecific;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean useNonStandardStacks() {
		return nonStandardStacks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFeatureOptioned(int featureId) {
		return findIdInBitField(featureId, featureOptins);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPurposeLegitInterestEstablished(int purposeId) {
		return findIdInBitField(purposeId, purposeLegitInterests);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPurposeOneDisclosed() {
		return purposeOneDisclosed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPublisherCc() {
		return publisherCc;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVendorLegitInterestEstablished(int vendorId) {
		if (vendorLegitInterestsBitField != null) {
			return findIdInBitField(vendorId, vendorLegitInterestsBitField);
		} else {
			return findVendorIdInRange(vendorId, vendorLegitInterestRanges);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVendorDisclosed(int vendorId) {
		if (vendorDisclosureBitField != null) {
			return findIdInBitField(vendorId, vendorDisclosureBitField);
		} else {
			return findVendorIdInRange(vendorId, vendorDisclosureRanges);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVendorAllowed(int vendorId) {
		if (vendorAllowancesBitField != null) {
			return findIdInBitField(vendorId, vendorAllowancesBitField);
		} else {
			return findVendorIdInRange(vendorId, vendorAllowancesRanges);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPubPurposesConsented(int purposeId) {
		return findIdInBitField(purposeId, pubPurposeConsents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPubPurposeLegitInterestEstablished(int purposeId) {
		return findIdInBitField(purposeId, pubPurposeLegitInterests);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCustomPurposeConsented(int purposeId) {
		return findIdInBitField(purposeId, customPurposeConsents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCustomPurposeLegitInterestEstablished(int purposeId) {
		return findIdInBitField(purposeId, customPurposeLegitInterests);
	}

	private static class RangeOrBitFieldParser {
		private int offset;
		private boolean rangeEncoding;
		private List<RangeEntry> rangeEntries;
		private List<Boolean> bitField;

		public RangeOrBitFieldParser(Bits bits, int offset) throws ParseException {
			this.offset = offset;
			int maxVendorId = bits.getInt(this.offset, VENDOR_ID_SIZE);
			this.offset += VENDOR_ID_SIZE;
			this.rangeEncoding = bits.getBit(this.offset);
			this.offset++;
			if (this.rangeEncoding) {
				RangeSectionParser rangeSectionParser = new RangeSectionParser(bits, this.offset);
				this.rangeEntries = rangeSectionParser.getEntries();
				this.offset = rangeSectionParser.getOffset();
			} else {
				this.bitField = bits.getBitList(this.offset, maxVendorId);
				this.offset += maxVendorId;
			}
		}

		public int getOffset() {
			return offset;
		}

		public boolean isRangeEncoding() {
			return rangeEncoding;
		}

		public List<RangeEntry> getRangeEntries() {
			return rangeEntries;
		}

		public List<Boolean> getBitField() {
			return bitField;
		}
	}

	private static class RangeSectionParser {
		private int offset;
		private List<RangeEntry> entries;

		public RangeSectionParser(Bits bits, int offset) throws ParseException {
			this.offset = offset;
			this.entries = new ArrayList<RangeEntry>();
			int numEntries = bits.getInt(this.offset, NUM_ENTRIES_SIZE);
			this.offset += NUM_ENTRIES_SIZE;

			for (int i = 0; i < numEntries; i++) {
				boolean range = bits.getBit(this.offset);
				this.offset++;
				if (range) {
					int startVendorId = bits.getInt(this.offset, VENDOR_ID_SIZE);
					this.offset += VENDOR_ID_SIZE;
					int endVendorId = bits.getInt(this.offset, VENDOR_ID_SIZE);
					this.offset += VENDOR_ID_SIZE;
					this.entries.add(new RangeEntry(startVendorId, endVendorId));
				} else {
					int vendorId = bits.getInt(this.offset, VENDOR_ID_SIZE);
					this.offset += VENDOR_ID_SIZE;
					this.entries.add(new RangeEntry(vendorId));
				}
			}
		}

		public int getOffset() {
			return offset;
		}

		public List<RangeEntry> getEntries() {
			return entries;
		}
	}

	private enum SegmentType {
		CORE(0), DISCLOSED_VENDORS(1), ALLOWED_VENDORS(2), PUBLISHER_TC(3), UNKNOWN(-1);
		private final int value;

		SegmentType(int value) {
			this.value = value;
		}

		public static SegmentType valueOf(int value) {
			switch (value) {
			case 0:
				return CORE;
			case 1:
				return DISCLOSED_VENDORS;
			case 2:
				return ALLOWED_VENDORS;
			case 3:
				return PUBLISHER_TC;
			default:
				return UNKNOWN;
			}
		}

		public int getValue() {
			return value;
		}
	}
}
