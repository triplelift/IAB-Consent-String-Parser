package com.iab.gdpr;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;

/**
 * This class implements a parser for the IAB consent string as specified in
 * https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/TCFv2/IAB%20Tech%20Lab%20-%20Consent%20string%20and%20vendor%20list%20formats%20v2.md#creating-a-tc-string
 */

public class ConsentStringParserV2 {
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
	private static final int PURPOSES_LI_TRANSPARENT_SIZE = 24;
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
	private List<Boolean> optedFeatures;
	private List<Boolean> consentedPurposes;
	private List<Boolean> establishedLegitInterests;
	private boolean purposeOneUndisclosed;
	private String publisherCc;
	private List<Boolean> consentedVendors;
	private List<RangeEntry> consentedVendorRanges;
	private List<Boolean> vendorLegitInterests;
	private List<RangeEntry> vendorLegitInterestRanges;
	private List<PubRestrictionEntry> publisherRestrictions;
	private List<Boolean> disclosedVendors;
	private List<RangeEntry> disclosedVendorRanges;
	private List<Boolean> allowedVendors;
	private List<RangeEntry> allowedVendorRanges;
	private List<Boolean> consentedPubPurposes;
	private List<Boolean> pubPurposeLegitInterests;
	private List<Boolean> consentedCustomPurposes;
	private List<Boolean> customPurposeLegitInterests;

	public ConsentStringParserV2(String consentString) throws ParseException {
		this.consentString = consentString;
		List<String> segments = Arrays.asList(consentString.split("."));
		// the core segment is required and should always be available
		String coreSegment = segments.remove(0);
		parseCore(new Bits(decoder.decode(coreSegment)));
		for (String segment : segments) {
			parseSegment(decoder.decode(segment));
		}
	}

	private void parseSegment(byte[] bytes) throws ParseException {
		Bits bits = new Bits(bytes);
		switch (Objects.requireNonNull(SegmentType.valueOf(bits.getInt(SEGMENT_TYPE_OFFSET, SEGMENT_TYPE_SIZE)))) {
		case DISCLOSED_VENDORS:
			parseDisclosedVendors(bits);
			return;
		case ALLOWED_VENDORS:
			parseAllowedVendors(bits);
			return;
		case PUBLISHER_TC:
			parsePublisherTc(bits);
			return;
		case CORE:
			parseCore(bits);
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
		this.optedFeatures = bits.getBitList(SPECIAL_FEATURE_OPT_INS_OFFSET, SPECIAL_FEATURE_OPT_INS_SIZE);
		this.consentedPurposes = bits.getBitList(PURPOSES_CONSENT_OFFSET, PURPOSES_CONSENT_SIZE);
		this.establishedLegitInterests = bits.getBitList(PURPOSES_LI_TRANSPARENCY_OFFSET, PURPOSES_LI_TRANSPARENT_SIZE);
		this.purposeOneUndisclosed = bits.getBit(PURPOSE_ONE_TREATMENT_OFFSET);
		this.publisherCc = bits.getSixBitString(PUBLISHER_CC_OFFSET, PUBLISHER_CC_SIZE);

		// parse Consented Vendor Range section or BitField section
		int variableOffset = PUBLISHER_CC_OFFSET + PUBLISHER_CC_SIZE;
		int maxVendorId = bits.getInt(variableOffset, VENDOR_ID_SIZE);
		variableOffset += VENDOR_ID_SIZE;
		boolean isRangeEncoding = bits.getBit(variableOffset);
		variableOffset++;
		if (isRangeEncoding) {
			RangeSectionParser rangeSectionParser = new RangeSectionParser(bits, variableOffset);
			this.consentedVendorRanges = rangeSectionParser.getEntries();
			variableOffset = rangeSectionParser.getOffset();
		} else {
			this.consentedVendors = bits.getBitList(variableOffset, maxVendorId);
			variableOffset += maxVendorId;
		}

		// parse Vendor Legitimate Interest Range section or BitField section
		maxVendorId = bits.getInt(variableOffset, VENDOR_ID_SIZE);
		variableOffset += VENDOR_ID_SIZE;
		isRangeEncoding = bits.getBit(variableOffset);
		variableOffset++;
		if (isRangeEncoding) {
			RangeSectionParser rangeSectionParser = new RangeSectionParser(bits, variableOffset);
			this.vendorLegitInterestRanges = rangeSectionParser.getEntries();
			variableOffset = rangeSectionParser.getOffset();
		} else {
			this.vendorLegitInterests = bits.getBitList(variableOffset, maxVendorId);
			variableOffset += maxVendorId;
		}

		// parse Publisher Restrictions
		int numPubRestrictions = bits.getInt(variableOffset, NUM_PUB_RESTRICTIONS_SIZE);
		variableOffset += NUM_PUB_RESTRICTIONS_SIZE;
		this.publisherRestrictions = new ArrayList<PubRestrictionEntry>();
		for (int i = 0; i < numPubRestrictions; i++) {
			int purposeId = bits.getInt(variableOffset, PURPOSE_ID_SIZE);
			variableOffset += PURPOSE_ID_SIZE;
			RestrictionType restrictionType = RestrictionType
					.valueOf(bits.getInt(variableOffset, RESTRICTION_TYPE_SIZE));
			variableOffset += RESTRICTION_TYPE_SIZE;
			RangeSectionParser rangeSectionParser = new RangeSectionParser(bits, variableOffset);
			variableOffset = rangeSectionParser.getOffset();
			this.publisherRestrictions
					.add(new PubRestrictionEntry(purposeId, restrictionType, rangeSectionParser.getEntries()));
		}
	}

	private void parseDisclosedVendors(Bits bits) throws ParseException {
		int offset = SEGMENT_TYPE_SIZE;
		int maxVendorId = bits.getInt(offset, VENDOR_ID_SIZE);
		offset += VENDOR_ID_SIZE;
		boolean isRangeEncoding = bits.getBit(offset);
		offset++;
		if (isRangeEncoding) {
			RangeSectionParser rangeSectionParser = new RangeSectionParser(bits, offset);
			this.disclosedVendorRanges = rangeSectionParser.getEntries();
		} else {
			this.disclosedVendors = bits.getBitList(offset, maxVendorId);
		}
	}

	private void parseAllowedVendors(Bits bits) throws ParseException {
		int offset = SEGMENT_TYPE_SIZE;
		int maxVendorId = bits.getInt(offset, VENDOR_ID_SIZE);
		offset += VENDOR_ID_SIZE;
		boolean isRangeEncoding = bits.getBit(offset);
		offset++;
		if (isRangeEncoding) {
			RangeSectionParser rangeSectionParser = new RangeSectionParser(bits, offset);
			this.allowedVendorRanges = rangeSectionParser.getEntries();
		} else {
			this.allowedVendors = bits.getBitList(offset, maxVendorId);
		}
	}

	private void parsePublisherTc(Bits bits) throws ParseException {
		int offset = SEGMENT_TYPE_SIZE;
		this.consentedPubPurposes = bits.getBitList(offset, PUB_PURPOSES_CONTENT_SIZE);
		offset += PUB_PURPOSES_CONTENT_SIZE;
		this.pubPurposeLegitInterests = bits.getBitList(offset, PUB_PURPOSES_LI_TRANSPARENCY_SIZE);
		offset += PUB_PURPOSES_CONTENT_SIZE;
		int numCustomPurposes = bits.getInt(offset, NUM_CUSTOM_PURPOSES_SIZE);
		offset += NUM_CUSTOM_PURPOSES_SIZE;
		this.consentedCustomPurposes = bits.getBitList(offset, numCustomPurposes);
		offset += numCustomPurposes;
		this.customPurposeLegitInterests = bits.getBitList(offset, numCustomPurposes);
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

	private static class PubRestrictionEntry {

		private int purposeId;
		private RestrictionType type;
		private List<RangeEntry> entries;

		public PubRestrictionEntry(int purposeId, RestrictionType type) {
			this.purposeId = purposeId;
			this.type = type;
			this.entries = new ArrayList<RangeEntry>();

		}

		public PubRestrictionEntry(int purposeId, RestrictionType type, List<RangeEntry> entries) {
			this.purposeId = purposeId;
			this.type = type;
			this.entries = entries;
		}
	}

	private enum SegmentType {
		CORE(0), DISCLOSED_VENDORS(1), ALLOWED_VENDORS(2), PUBLISHER_TC(3);
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
				return null;
			}
		}

		public int getValue() {
			return value;
		}
	}

	private enum RestrictionType {
		NOT_ALLOWED(0), REQUIRE_CONSENT(1), REQUIRE_LEGIT_INTEREST(2), UNDEFINED(3);
		private final int value;

		RestrictionType(int value) {
			this.value = value;
		}

		public static RestrictionType valueOf(int value) {
			switch (value) {
			case 0:
				return NOT_ALLOWED;
			case 1:
				return REQUIRE_CONSENT;
			case 2:
				return REQUIRE_LEGIT_INTEREST;
			case 3:
				return UNDEFINED;
			default:
				return null;
			}
		}

		public int getValue() {
			return value;
		}
	}
}
