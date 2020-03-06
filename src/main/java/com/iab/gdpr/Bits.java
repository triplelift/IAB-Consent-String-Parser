package com.iab.gdpr;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// since java.util.BitSet is inappropiate to use here--as it reversed the bit order of the consent string--we
// implement our own bitwise operations here.
public class Bits {
	// big endian
	private static final byte[] bytePows = { -128, 64, 32, 16, 8, 4, 2, 1 };
	private final byte[] bytes;

	public Bits(byte[] b) {
		this.bytes = b;
	}

	/**
	 *
	 * @param index:
	 *            the nth number bit to get from the bit string
	 * @return boolean bit, true if the bit is switched to 1, false otherwise
	 * @throws ParseException
	 */
	public boolean getBit(int index) throws ParseException {
		int byteIndex = index / 8;
		int bitExact = index % 8;
		if (byteIndex >= bytes.length) {
			throw new ParseException("requesting bit beyond bit string length", index);
		}
		byte b = bytes[byteIndex];
		return (b & bytePows[bitExact]) != 0;
	}

	/**
	 *
	 * @param startInclusive: the nth to begin interpreting from
	 * @param size: the number of bits to interpret
	 * @return list of boolean bits
	 * @throws ParseException when requesting bit beyond bit string length
	 */
	public List<Boolean> getBitList(int startInclusive, int size) throws ParseException {
		List<Boolean> bitList = new ArrayList<Boolean>();
		for (int i = startInclusive, ii = startInclusive + size; i < ii; i++) {
			bitList.add(getBit(i));
		}
		return bitList;
	}

	/**
	 * interprets n number of bits as a big endiant int
	 *
	 * @param startInclusive:
	 *            the nth to begin interpreting from
	 * @param size:
	 *            the number of bits to interpret
	 * @return
	 * @throws ParseException
	 *             when the bits cannot fit in an int sized field
	 */
	public int getInt(int startInclusive, int size) throws ParseException {
		if (size > Integer.SIZE) {
			throw new ParseException("can't fit bit range in int.", startInclusive);
		}
		int val = 0;
		int sigMask = 1;
		int sigIndex = size - 1;

		for (int i = 0; i < size; i++) {
			if (getBit(startInclusive + i)) {
				val += (sigMask << sigIndex);
			}
			sigIndex--;
		}
		return val;
	}

	/**
	 * interprets n bits as a big endian long
	 *
	 * @param startInclusive:
	 *            the nth to begin interpreting from
	 * @param size:the
	 *            number of bits to interpret
	 * @return
	 * @throws ParseException
	 *             when the bits cannot fit in an int sized field
	 */
	public long getLong(int startInclusive, int size) throws ParseException {
		if (size > Long.SIZE) {
			throw new ParseException("can't fit bit range in long.", startInclusive);
		}
		long val = 0;
		long sigMask = 1;
		int sigIndex = size - 1;

		for (int i = 0; i < size; i++) {
			if (getBit(startInclusive + i)) {
				val += (sigMask << sigIndex);
			}
			sigIndex--;
		}
		return val;
	}

	/**
	 * returns an {@link Instant} derived from interpreting the given interval on the bit string as long
	 * representing the number of demiseconds from the unix epoch
	 *
	 * @param startInclusive:
	 *            the bit from which to begin interpreting
	 * @param size:
	 *            the number of bits to interpret
	 * @return
	 * @throws ParseException
	 *             when the number of bits requested cannot fit in a long
	 */
	public Instant getInstantFromEpochDemiseconds(int startInclusive, int size) throws ParseException {
		long epochDemi = getLong(startInclusive, size);
		return Instant.ofEpochMilli(epochDemi * 100);
	}

	/**
	 * @return the number of bits in the bit string
	 *
	 */
	public int length() {
		return bytes.length * 8;
	}

	/**
	 * This method interprets the given interval in the bit string as a series of six bit characters, where 0=A and
	 * 26=Z
	 *
	 * @param startInclusive:
	 *            the nth bit in the bitstring from which to start the interpretation
	 * @param size:
	 *            the number of bits to include in the string
	 * @return the string given by the above interpretation
	 * @throws ParseException
	 *             when the requested interval is not a multiple of six
	 */
	public String getSixBitString(int startInclusive, int size) throws ParseException {
		if (size % 6 != 0) {
			throw new ParseException("string bit length must be multiple of six", startInclusive);
		}
		int charNum = size / 6;
		StringBuilder val = new StringBuilder();
		for (int i = 0; i < charNum; i++) {
			int charCode = getInt(startInclusive + (i * 6), 6) + 65;
			val.append((char) charCode);
		}
		return val.toString().toUpperCase();

	}

	/**
	 *
	 * @return a string representation of the byte array passed in the constructor. for example, a byte array of [4]
	 *         yeilds a String of "0100"
	 */
	public String getBinaryString() throws ParseException {
		StringBuilder s = new StringBuilder();
		int i = 0;
		int ii = length();
		for (; i < ii; i++) {
			if (getBit(i)) {
				s.append("1");
			} else {
				s.append("0");
			}
		}
		return s.toString();
	}
}
