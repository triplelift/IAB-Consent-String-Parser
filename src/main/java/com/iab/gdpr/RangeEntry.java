package com.iab.gdpr;

import java.util.ArrayList;
import java.util.List;

public class RangeEntry {
	/**
	 * This class corresponds to the RangeEntry field given in the consent string specification.
	 */
	private final List<Integer> vendorIds = new ArrayList<Integer>();
	private final int maxVendorId;
	private final int minVendorId;

	public RangeEntry(int vendorId) {
		vendorIds.add(vendorId);
		this.maxVendorId = this.minVendorId = vendorId;
	}

	public RangeEntry(int startId, int endId) {
		this.maxVendorId = endId;
		this.minVendorId = startId;
		for (; startId <= endId; startId++) {
			vendorIds.add(startId);
		}
	}

	public boolean containsVendorId(int vendorId) {
		return vendorIds.indexOf(vendorId) >= 0;
	}

	public boolean idIsGreaterThanMax(int vendorId) {
		return vendorId > maxVendorId;
	}

	public boolean isIsLessThanMin(int vendorId) {
		return vendorId < minVendorId;
	}

	public int getMaxVendorId() {
		return maxVendorId;
	}

	public int getMinVendorId() {
		return minVendorId;
	}
}
