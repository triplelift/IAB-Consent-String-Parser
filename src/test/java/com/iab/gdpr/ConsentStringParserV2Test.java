package com.iab.gdpr;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

public class ConsentStringParserV2Test {

	@Test
	public void testParseCoreString() throws ParseException {
		String consentString = "COvf4CzOvf4CzEqAiYENAPC4AAgAABIAAIAAASgAAQAAAFkQAQFkAAA";
		ConsentInfo consent = new ConsentStringParserV2(consentString);
		assertEquals(298, consent.getCmpId());
		assertEquals(34, consent.getCmpVersion());
		assertEquals(24, consent.getConsentScreen());
		assertEquals(15, consent.getVendorListVersion());
		assertEquals("EN", consent.getConsentLanguage());
		assertEquals("AA", consent.getPublisherCc());
		assertTrue(consent.isVendorConsented(18));
		assertFalse(consent.isVendorConsented(12));
		assertTrue(consent.isPurposeConsented(5));
		assertTrue(consent.isPurposeConsented(Purpose.PurposeV2.PERSONALISED_CONTENT_PROFILE));
		assertFalse(consent.isPurposeConsented(Purpose.PurposeV1.CONTENT_DELIVERY));
		assertFalse(consent.isPurposeConsented(4));
		assertTrue(consent.isVendorLegitInterestEstablished(712));
		assertFalse(consent.isVendorLegitInterestEstablished(714));
		assertTrue(consent.isPurposeLegitInterestEstablished(4));
		assertTrue(consent.isPurposeLegitInterestEstablished(7));
		assertTrue(consent.isPurposeLegitInterestEstablished(Purpose.PurposeV2.MEASURE_ADS));
		assertFalse(consent.isPurposeLegitInterestEstablished(6));
		assertTrue(consent.isServiceSpecific());
		assertFalse(consent.isPurposeOneDisclosed());
		assertTrue(consent.useNonStandardStacks());
		assertTrue(consent.isFeatureOptioned(1));
		assertTrue(consent.isFeatureOptioned(SpecialFeature.SpecialFeatureV2.GEOLOCATION));
		assertFalse(consent.isFeatureOptioned(2));
	}

	@Test
	public void testParseAllowedVendors() throws ParseException {
		String consentString = "COvf4CzOvf4CzEqAiYENAPCYAAgAABIAAIAAASgAAQAAAFkQAQFkAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw.QFmQBAFiQLHAsgBZQCzA";
		ConsentInfo consent = new ConsentStringParserV2(consentString);
		assertTrue(consent.isVendorAllowed(708));
		assertTrue(consent.isVendorAllowed(711));
		assertTrue(consent.isVendorAllowed(712));
		assertTrue(consent.isVendorAllowed(714));
		assertTrue(consent.isVendorAllowed(716));
		assertFalse(consent.isVendorAllowed(719));
	}

	@Test
	public void testParseDisclosedVendors() throws ParseException {
		String consentString = "COvouH3OvouH3IyAAAENAPCAAAAAAAAAAAAAAAAAAAAA.IFoEUQQgAIQwgIwQABAEAAAAOIAACAIAAAAQAIAgEAACEAAAAAgAQBAAAAAAAGBAAgAAAAAAAFAAECAAAgAAQARAEQAAAAAJAAIAAgAAAYQEAAAQmAgBC3ZAYzUw";
		ConsentInfo consent = new ConsentStringParserV2(consentString);
		assertTrue(consent.isVendorDisclosed(2));
		assertTrue(consent.isVendorDisclosed(6));
		assertTrue(consent.isVendorDisclosed(12));
		assertTrue(consent.isVendorDisclosed(23));
		assertTrue(consent.isVendorDisclosed(42));
		assertFalse(consent.isVendorDisclosed(43));
	}
}
