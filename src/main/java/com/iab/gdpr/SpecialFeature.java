package com.iab.gdpr;

public interface SpecialFeature {
	int getValue();

	int getVersion();

	enum SpecialFeatureV2 implements SpecialFeature {
		/**
		 * Vendors can collect and process precise geolocation data in support of one or more purposes.
		 */
		GEOLOCATION(1),

		/**
		 * Vendors can create an identifier using data collected via actively scanning a device for specific
		 *      characteristics, e.g. installed fonts or screen resolution.
		 * Vendors can use such an identifier to re-identify a device.
		 */
		SCAN_DEVICE(2),

		/**
		 * Special Feature ID that is currently not defined
		 */
		UNDEFINED(-1);

		private final int value;

		SpecialFeatureV2(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public int getVersion() {
			return 2;
		}

		/**
		 * Map numeric purpose ID to Enum
		 *
		 * @param value purpose ID
		 * @return Enum value of purpose
		 */
		public static SpecialFeatureV2 valueOf(int value) {
			switch (value) {
			case 1:
				return GEOLOCATION;
			case 2:
				return SCAN_DEVICE;
			default:
				return UNDEFINED;
			}
		}
	}
}
