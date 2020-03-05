package com.iab.gdpr;

public interface Purpose {
	int getValue();

	int getVersion();

	enum PurposeV1 implements Purpose {
		/**
		 * The storage of information, or access to information that is already stored,
		 * on user device such as accessing advertising identifiers
		 * and/or other device identifiers, and/or using cookies or similar technologies.
		 */
		STORAGE_AND_ACCESS(1),

		/**
		 * The collection and processing of information about user of a site to subsequently personalize advertising
		 * for them in other contexts, i.e. on other sites or apps, over time.
		 * Typically, the content of the site or app is used to make inferences about user interests,
		 * which inform future selections.
		 */
		PERSONALIZATION(2),

		/**
		 * The collection of information and combination with previously collected information, to select and
		 * deliver advertisements and to measure the delivery and effectiveness of such advertisements.
		 * This includes using previously collected information about user interests to select ads,
		 * processing data about what advertisements were shown, how often they were shown,
		 * when and where they were shown, and whether they took any action related to the advertisement,
		 * including for example clicking an ad or making a purchase.
		 */
		AD_SELECTION(3),

		/**
		 * The collection of information, and combination with previously collected information, to select and
		 * deliver content and to measure the delivery and effectiveness of such content.
		 * This includes using previously collected information about user interests to select content,
		 * processing data about what content was shown, how often or how long it was shown,
		 * when and where it was shown, and whether they took any action related to the content,
		 * including for example clicking on content.
		 */
		CONTENT_DELIVERY(4),

		/**
		 * The collection of information about user use of content,
		 * and combination with previously collected information, used to measure, understand,
		 * and report on user usage of content.
		 */
		MEASUREMENT(5),

		/**
		 * Purpose ID that is currently not defined
		 */
		UNDEFINED(-1),
		;

		private final int value;

		PurposeV1(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public int getVersion() {
			return 1;
		}

		/**
		 * Map numeric purpose ID to Enum
		 *
		 * @param value purpose ID
		 * @return Enum value of purpose
		 */
		public static PurposeV1 valueOf(int value) {
			switch (value) {
			case 1:
				return STORAGE_AND_ACCESS;
			case 2:
				return PERSONALIZATION;
			case 3:
				return AD_SELECTION;
			case 4:
				return CONTENT_DELIVERY;
			case 5:
				return MEASUREMENT;
			default:
				return UNDEFINED;
			}
		}
	}

	enum PurposeV2 implements Purpose {
		/**
		 * Vendors can store and access information on the device such as cookies and device identifiers for the
		 *      purposes presented to a user.
		 */
		STORAGE_AND_ACCESS(1),

		/**
		 * Vendors can use real-time information about the context in which the ad will be shown, to show the ad,
		 *      including information about the content and the device, such as: device type and capabilities,
		 *      user agent, URL, IP address.
		 * Vendors can use a user’s non-precise geolocation data. Vendors can control the frequency of ads shown to
		 *      a user.
		 * Vendors can sequence the order in which ads are shown to a user.
		 * Vendors can prevent an ad from serving in an unsuitable editorial (brand-unsafe) context
		 *
		 * Vendors cannot create a personalised ads profile using this information for the selection of future ads
		 *      without a separate legal basis to create a personalised ads profile.
		 *      N.B. Non-precise means only an approximate location involving at least a radius of 500 meters is permitted.
		 */
		BASIC_ADS(2),

		/**
		 * Vendors can collect information about a user, including a user's activity, interests, visits to sites or
		 *      apps, demographic information, or location, to create or edit a user profile for use
		 *      in personalised advertising.
		 * Vendors can combine this information with other information previously collected, including from across
		 *      websites and apps, to create or edit a user profile for use in personalised advertising.
		 */
		PERSONALISED_AD_PROFILE(3),

		/**
		 * Vendors can select personalised ads based on a user profile or other historical user data,
		 * including a user’s prior activity, interests, visits to sites or apps, location, or demographic information.
		 */
		PERSONALISED_ADS(4),

		/**
		 * Vendors can collect information about a user, including a user's activity, interests, visits to sites or
		 *      apps, demographic information, or location, to create or edit a user profile for personalising content.
		 * Vendors can combine this information with other information previously collected, including from across
		 *      websites and apps, to create or edit a user profile for use in personalising content.
		 */
		PERSONALISED_CONTENT_PROFILE(5),

		/**
		 * Vendors can select personalised content based on a user profile or other historical user data,
		 * including a user’s prior activity, interests, visits to sites or apps, location, or demographic information.
		 */
		PERSONALISED_CONTENT(6),

		/**
		 * Vendors can measure whether and how ads were delivered to and interacted with by a user.
		 * Vendors can provide reporting about ads including their effectiveness and performance.
		 * Vendors can provide reporting about users who interacted with ads using data observed during the course of
		 *      the user's interaction with that ad.
		 * Vendors can provide reporting to publishers about the ads displayed on their property.
		 * Vendors can measure whether an ad is serving in a suitable editorial environment (brand-safe) context.
		 * Vendors can determine the percentage of the ad that had the opportunity to be seen and the duration of
		 *      that opportunity.
		 * Vendors can combine this information with other information previously collected, including from across
		 *      websites and apps.
		 *
		 * Vendors cannot apply panel- or similarly-derived audience insights data to ad measurement data without a
		 *      separate legal basis to apply market research to generate audience insights.
		 */
		MEASURE_ADS(7),

		/**
		 * Vendors can measure and report on how content was delivered to and interacted with by users.
		 * Vendors can provide reporting, using directly measurable or known information,about users who interacted
		 *      with the content.
		 * Vendors can combine this information with other information previously collected, including from across
		 *      websites and apps.
		 *
		 * Vendors cannot measure whether and how ads (including native ads) were delivered to and interacted with by a
		 *      user without a separate legal basis.
		 * Vendors cannot apply panel- or similarly derived audience insights data to ad measurement data without a
		 *      separate legal bases to apply market research to generate audience insights.
		 */
		MEASURE_CONTENT(8),

		/**
		 * Vendors can provide aggregate reporting to advertisers or their representatives about the audiences reached
		 *      by their ads, through panel-based and similarly derived insights.
		 * Vendors can Provide aggregate reporting to publishers about the audiences that were served or interacted
		 *      with content and/or ads on their property by applying panel-based and similarly derived insights.
		 * Vendors can Associate offline data with an online user for the purposes of market research to generate
		 *      audience insights if vendors have declared to match and combine offline data sources.
		 * Vendors can Combine this information with other information previously collected, including from across
		 *      websites and apps.
		 *
		 * Vendors cannot measure the performance and effectiveness of ads that a specific user was served or
		 *      interacted with, without a separate legal basis to measure ad performance.
		 * Vendors cannot measure which content a specific user was served and how they interacted with it,
		 *      without a separate legal basis to measure content performance.
		 */
		APPLY_MARKET_RESEARCH(9),

		/**
		 * Vendors can use information to improve their existing products with new features and to develop new products.
		 * Vendors can Create new models and algorithms through machine learning.
		 *
		 * Vendors cannot conduct any other data processing operation allowed under a different purpose under
		 *      this purpose.
		 */
		DEVELOP_AND_IMPROVE(10),

		/**
		 * Purpose ID that is currently not defined
		 */
		UNDEFINED(-1);

		private final int value;

		PurposeV2(int value) {
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
		public static PurposeV2 valueOf(int value) {
			switch (value) {
			case 1:
				return STORAGE_AND_ACCESS;
			case 2:
				return BASIC_ADS;
			case 3:
				return PERSONALISED_AD_PROFILE;
			case 4:
				return PERSONALISED_ADS;
			case 5:
				return PERSONALISED_CONTENT_PROFILE;
			case 6:
				return PERSONALISED_CONTENT;
			case 7:
				return MEASURE_ADS;
			case 8:
				return MEASURE_CONTENT;
			case 9:
				return APPLY_MARKET_RESEARCH;
			case 10:
				return DEVELOP_AND_IMPROVE;
			default:
				return UNDEFINED;
			}
		}
	}
}
