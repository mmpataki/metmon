package metmon.rest.services;

import org.apache.hadoop.hbase.util.Bytes;

import metmon.store.SerDe;

public class SerDes {
	public static class DoubleSerde implements SerDe<Double> {

		@Override
		public Double deserialize(byte[] buf) {
			return Bytes.toDouble(buf);
		}

		@Override
		public byte[] serialize(Double obj) {
			return Bytes.toBytes(obj);
		}

	}

	public static class StringSerde implements SerDe<String> {

		@Override
		public String deserialize(byte[] buf) {
			return Bytes.toString(buf);
		}

		@Override
		public byte[] serialize(String obj) {
			return Bytes.toBytes(obj);
		}

	}
}
