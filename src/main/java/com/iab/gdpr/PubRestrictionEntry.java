package com.iab.gdpr;

import java.util.ArrayList;
import java.util.List;

public class PubRestrictionEntry {

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

	public enum RestrictionType {
		NOT_ALLOWED(0), REQUIRE_CONSENT(1), REQUIRE_LEGIT_INTEREST(2), UNDEFINED(3), UNKNOWN(-1);
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
				return UNKNOWN;
			}
		}

		public int getValue() {
			return value;
		}
	}
}
