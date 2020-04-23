package metmon.rest;

import java.util.Random;

import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.ProcIdentifier;

public class MetricProducer {

	long ts = 4;
	int nnCnt;
	ProcIdentifier mrid;
	String keys[];
	String ctxt;
	Random R = new Random();

	/**
	 * Initialize the keys of the metrics produced.
	 * 
	 * @param numMetrics  : number of numeric metric keys
	 * @param metricKeys  : keys (meta keys followed by numeric keys)
	 */
	public MetricProducer(ProcIdentifier mrid, String ctxt, int numMetrics, String... metricKeys) {
		if (numMetrics > metricKeys.length)
			throw new IllegalArgumentException("metaMetrics + numericMetrics < keys.length");
		nnCnt = numMetrics;
		keys = metricKeys;
		this.mrid = mrid;
		this.ctxt = ctxt;
	}

	public MetricRecord pop() {
		MetricRecord mr = new MetricRecord(ts++, mrid);
		for (int i = 0; i < nnCnt; i++) {
			mr.addRecord(new Metric((short)i, (double) R.nextInt(1)));
		}
		return mr;
	}

}
