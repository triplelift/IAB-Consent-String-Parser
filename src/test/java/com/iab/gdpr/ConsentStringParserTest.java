package com.iab.gdpr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.text.ParseException;
import java.time.Instant;

import org.junit.Test;

public class ConsentStringParserTest {

	@Test
	public void testBitField() throws ParseException {
		String consentString = "BN5lERiOMYEdiAOAWeFRAAYAAaAAptQ";

		ConsentStringParser consent = new ConsentStringParser(consentString);
		assertEquals(14, consent.getCmpId());
		assertEquals(22, consent.getCmpVersion());
		assertEquals("FR", consent.getConsentLanguage());
		assertEquals(Instant.ofEpochMilli(14924661858L * 100), consent.getConsentRecordCreated());
		assertEquals(Instant.ofEpochMilli(15240021858L * 100), consent.getConsentRecordLastUpdated());
		assertEquals(5, consent.getConsentedPurposes().size());
		assertTrue(consent.isPurposeConsented(2));
		assertFalse(consent.isPurposeConsented(1));
		assertTrue(consent.isPurposeConsented(21));
		assertTrue(consent.isVendorConsented(1));
		assertTrue(consent.isVendorConsented(5));
		assertTrue(consent.isVendorConsented(7));
		assertTrue(consent.isVendorConsented(9));
		assertFalse(consent.isVendorConsented(0));
		assertFalse(consent.isVendorConsented(10));
		assertEquals(consentString, consent.getConsentString());
	}

	@Test
	public void testRangeEntryNoConsent() throws ParseException {
		String consentString = "BN5lERiOMYEdiAKAWXEND1HoSBE6CAFAApAMgBkIDIgM0AgOJxAnQA==";

		ConsentStringParser consent = new ConsentStringParser(consentString);
		assertEquals(10, consent.getCmpId());
		assertEquals(22, consent.getCmpVersion());
		assertEquals("EN", consent.getConsentLanguage());
		assertEquals(Instant.ofEpochMilli(14924661858L * 100), consent.getConsentRecordCreated());
		assertEquals(Instant.ofEpochMilli(15240021858L * 100), consent.getConsentRecordLastUpdated());
		assertEquals(8, consent.getConsentedPurposes().size());
		assertTrue(consent.isPurposeConsented(4));
		assertFalse(consent.isPurposeConsented(1));
		assertTrue(consent.isPurposeConsented(24));
		assertFalse(consent.isPurposeConsented(25));
		assertFalse(consent.isPurposeConsented(0));
		assertFalse(consent.isVendorConsented(1));
		assertFalse(consent.isVendorConsented(3));
		assertTrue(consent.isVendorConsented(225));
		assertTrue(consent.isVendorConsented(5000));
		assertTrue(consent.isVendorConsented(515));
		assertFalse(consent.isVendorConsented(0));
		assertFalse(consent.isVendorConsented(3244));
		assertEquals(consentString, consent.getConsentString());

	}

	@Test
	public void testRangeEntryConsent() throws ParseException {
		String consentString = "BONZt-1ONZt-1AHABBENAO-AAAAHCAEAASABmADYAOAAeA";
		ConsentStringParser consent = new ConsentStringParser(consentString);

		assertTrue(consent.isPurposeConsented(1));
		assertTrue(consent.isPurposeConsented(3));
		assertTrue(consent.isVendorConsented(28));
		assertFalse(consent.isVendorConsented(1));
		assertFalse(consent.isVendorConsented(3));
		assertTrue(consent.isVendorConsented(27));
	}

}
