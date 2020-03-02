package com.iab.gdpr;

import java.time.Instant;
import java.util.List;

public interface ConsentInfo {
	/**
	 * @return the source consent string
	 */
	String getConsentString();

	/**
	 * @return the version of the consent record format
	 */
	int getVersion();

	/**
	 * @return the {@link Instant} at which the consent record was created
	 */
	Instant getConsentRecordCreated();

	/**
	 * @return the {@link Instant} at whjch the consent record was last updated
	 */
	Instant getConsentRecordLastUpdated();

	/**
	 * @return the id of the consent management partner that created this record
	 */
	int getCmpId();

	/**
	 * @return the version of the consent management partner that created this record
	 */
	int getCmpVersion();

	/**
	 * @return the id of the string through which the user gave consent in the CMP UI
	 */
	int getConsentScreen();

	/**
	 * @return the two letter ISO639-1 language code in which the CMP asked for consent
	 */
	String getConsentLanguage();

	/**
	 * @return the vendor list version which was used in creating this consent string
	 */
	int getVendorListVersion();

	/**
	 * @param purposeId
	 *      Id of the purpose to check consent info for
	 * @return
	 *      boolean for whether or not the purpose was consented to
	 */
	boolean isPurposeConsented(int purposeId);

	/**
	 * @return list of Ids for all purposes that were consented to
	 */
	List<Integer> getConsentedPurposes();

	/**
	 * @param vendorId
	 *      Id of the vendor to check consent info for
	 * @return
	 *      boolean for whether or not the vendor was consented to
	 */
	boolean isVendorConsented(int vendorId);

	// V2 ADDITIONS

	/**
	 * @return the version of the policy used within the global vendor list
	 */
	default int getTcfPolicyVersion() {
		return 0;
	}

	/**
	 * @return whether the signals encoded were from service-specific versus global storage
	 */
	default boolean isServiceSpecific() {
		return false;
	}

	/**
	 * @return whether the publisher is using customized stack descriptions
	 */
	default boolean useNonStandardStacks() {
		return false;
	}

	/**
	 * @param featureId
	 *      Id of the feature to check option status for
	 * @return
	 *      boolean for whether or not the feature was optioned by the user
	 */
	default boolean isFeatureOptioned(int featureId) {
		return false;
	}

	/**
	 * @param purposeId
	 *      Id of the purpose to check legitimate interest establishment for
	 * @return
	 *      boolean for whether the purpose has established legitimate interest
	 */
	default boolean isPurposeLegitInterestEstablished(int purposeId) {
		return false;
	}

	/**
	 * @return whether Purpose 1 was disclosed to the user
	 */
	default boolean isPurposeOneDisclosed() {
		return false;
	}

	/**
	 * @return ISO 3166-1 alpha-2 country code for the country in which the publisher's business identity is established
	 */
	default String getPublisherCc() {
		return null;
	}

	/**
	 * @param vendorId
	 *      Id of the vendor to check legitimate interest establishment for
	 * @return
	 *      boolean for whether the vendor has established legitimate interest
	 */
	default boolean isVendorLegitInterestEstablished(int vendorId) {
		return false;
	}

	/**
	 * @param vendorId
	 *      Id of the vendor to check disclosure status for
	 * @return
	 *      boolean for whether the vendor has been disclosed to the user
	 */
	default boolean isVendorDisclosed(int vendorId) {
		return false;
	}

	/**
	 * @param vendorId
	 *      Id of the vendor to check Pub's OOB legal base permission for
	 * @return
	 *      boolean for whether the vendor is allow to use OOB legal bases under this publisher
	 */
	default boolean isVendorAllowed(int vendorId) {
		return false;
	}

	/**
	 * @param purposeId
	 *      Id of the publisher purpose to check consent status for
	 * @return
	 *      boolean for whether the publisher purpose is consented to
	 */
	default boolean isPubPurposesConsented(int purposeId) {
		return false;
	}

	/**
	 * @param purposeId
	 *      Id of the publisher purpose to check legitimate interest establishment for
	 * @return
	 *      boolean for whether the publisher purpose established legitimate interest
	 */
	default boolean isPubPurposeLegitInterestEstablished(int purposeId) {
		return false;
	}

	/**
	 * @param purposeId
	 *      Id of the custom purpose to check consent status for
	 * @return
	 *      boolean for whether the custom purpose is consented to
	 */
	default boolean isCustomPurposeConsented(int purposeId) {
		return false;
	}

	/**
	 * @param purposeId
	 *      Id of the custom purpose to check legitimate interest establishment for
	 * @return
	 *      boolean for whether the custom purpose established legitimate interest
	 */
	default boolean isCustomPurposeLegitInterestEstablished(int purposeId) {
		return false;
	}


}
