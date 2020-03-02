package com.iab.gdpr;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ConsentInfoDecoder {
	private static final int VERSION_OFFSET = 0;
	private static final int VERSION_SIZE = 6;

	/**
	 *
	 * @param consentString
	 *      Consent String passed in from the publisher
	 * @return the version appropriate parser/consentInfo
	 */
	public static ConsentInfo decode(String consentString) {
		try {
			switch (getVersion(consentString)) {
			case 1:
				return new ConsentStringParser(consentString);
			case 2:
				return new ConsentStringParserV2(consentString);
			default:
				return new ConsentInfoStub();
			}
		} catch (Exception e) {
			return new ConsentInfoStub();
		}
	}

	/**
	 *
	 * @param consentString
	 *      Consent String passed in from the publisher
	 * @return the perceived version number of the Consent String
	 */
	public static int getVersion(String consentString) {
		// for v2+ we need to consider segmented consentStrings
		try {
			List<String> segments = Arrays.asList(consentString.split("\\."));
			String coreString = segments.get(0);
			Bits bits = new Bits(Base64.getDecoder().decode(coreString));
			return bits.getInt(VERSION_OFFSET, VERSION_SIZE);
		} catch (Exception e) {
			return 0;
		}
	}
}
