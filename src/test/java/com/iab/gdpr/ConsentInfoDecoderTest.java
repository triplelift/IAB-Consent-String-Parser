package com.iab.gdpr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConsentInfoDecoderTest {

	@Test
	public void testDecodeConsentInfoV1() {
		String consentString = "BN5lERiOMYEdiAOAWeFRAAYAAaAAptQ";
		assertEquals(1, ConsentInfoDecoder.getVersion(consentString));

		ConsentInfo consentInfo = ConsentInfoDecoder.decode(consentString);
		assertTrue(consentInfo instanceof ConsentStringParser);
	}

	@Test
	public void testDecoreConsentInfoV2() {
		String consentString = "COvf4CzOvf4CzEqAiYENAPC4AAgAABIAAIAAASgAAQAAAFkQAQFkAAA";
		assertEquals(2, ConsentInfoDecoder.getVersion(consentString));

		ConsentInfo consentInfo = ConsentInfoDecoder.decode(consentString);
		assertTrue(consentInfo instanceof ConsentStringParserV2);
	}

	@Test
	public void testDecodeConsentInfoStub() {
		String consentString = "AOvf4CzOvf4CzEqAiYENAPC4AAgAABIAAIAAASgAAQAAAFkQAQFkAAA=";
		assertEquals(0, ConsentInfoDecoder.getVersion(consentString));

		ConsentInfo consentInfo = ConsentInfoDecoder.decode(consentString);
		assertTrue(consentInfo instanceof ConsentInfoStub);
	}

	@Test
	public void testDecodeConsentInfoStub_fromException() {
		String consentString = "adasdassadvf4CzEqAiYENAPC4AAgAABIAAIAAASgAAQAAAFkQAQFkAAA=";
		assertEquals(0, ConsentInfoDecoder.getVersion(consentString));

		ConsentInfo consentInfo = ConsentInfoDecoder.decode(consentString);
		assertTrue(consentInfo instanceof ConsentInfoStub);
	}
}
