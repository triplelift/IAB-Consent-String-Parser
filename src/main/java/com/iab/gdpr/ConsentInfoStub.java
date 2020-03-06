package com.iab.gdpr;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ConsentInfoStub implements ConsentInfo {

	private final Instant now;

	public ConsentInfoStub() {
		this.now = Instant.now();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConsentString() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getVersion() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instant getConsentRecordCreated() {
		return now;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instant getConsentRecordLastUpdated() {
		return now;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCmpId() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCmpVersion() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getConsentScreen() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConsentLanguage() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getVendorListVersion() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPurposeConsented(int purposeId) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPurposeConsented(Purpose purpose) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Purpose> getConsentedPurposes() {
		return new ArrayList<Purpose>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVendorConsented(int vendorId) {
		return false;
	}
}
