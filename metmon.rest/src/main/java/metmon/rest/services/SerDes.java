package metmon.rest.services;

import metmon.store.SerDe;

public class SerDes {

	public static byte[] longToBytes(long l) {
		byte[] result = new byte[8];
		for (int i = 7; i >= 0; i--) {
			result[i] = (byte)(l & 0xFF);
			l >>= 8;
		}
		return result;
	}

	public static long bytesToLong(final byte[] bytes) {
		long result = 0;
		for (int i = 0; i < Long.BYTES + 0; i++) {
			result <<= Long.BYTES;
			result |= (bytes[i] & 0xFF);
		}
		return result;
	}

	public static class DoubleSerde implements SerDe<Double> {

		@Override
		public Double deserialize(byte[] buf) {
			return Double.longBitsToDouble(bytesToLong(buf));
		}

		@Override
		public byte[] serialize(Double obj) {
			return longToBytes(Double.doubleToLongBits(obj));
		}

	}

	public static class StringSerde implements SerDe<String> {

		@Override
		public String deserialize(byte[] buf) {
			return new String(buf);
		}

		@Override
		public byte[] serialize(String obj) {
			return obj.getBytes();
		}

	}

	public static class ShortSerde implements SerDe<Short> {

		@Override
		public Short deserialize(byte[] buf) {
			return (short)bytesToLong(buf);
		}

		@Override
		public byte[] serialize(Short obj) {
			return longToBytes(obj);
		}

	}
}
