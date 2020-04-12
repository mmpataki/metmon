package metmon.model.metric;

import metmon.store.StoreRecord;

public class MetricRecord extends StoreRecord<String, Double, Metric> {

	String ctxt;
	ProcIdentifier id;

	public MetricRecord() {
		this(-1);
	}

	public MetricRecord(long ts) {
		this(ts, "", null);
	}

	public MetricRecord(long ts, String ctxt, ProcIdentifier id) {
		super(ts);
		this.id = id;
		this.ctxt = ctxt;
	}

	public ProcIdentifier getId() {
		return id;
	}

	public void setId(ProcIdentifier id) {
		this.id = id;
	}

	public String getCtxt() {
		return ctxt;
	}

	public void setCtxt(String ctxt) {
		this.ctxt = ctxt;
	}

	@Override
	public boolean equals(Object obj) {
		MetricRecord o = (MetricRecord) obj;
		if (this == o)
			return true;
		return true;
	}

	@Override
	public String toString() {
		return String.format("{ts=%d, id=%s, ctxt=%s, records=%s}", getTs(), getId(), getCtxt(), getRecords());
	}

}
