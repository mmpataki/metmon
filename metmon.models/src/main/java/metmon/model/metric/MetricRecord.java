package metmon.model.metric;

import metmon.store.StoreRecord;

public class MetricRecord extends StoreRecord<Short, Double, Metric> {

	ProcIdentifier id;

	public MetricRecord() {
		this(-1);
	}

	public MetricRecord(long ts) {
		this(ts, null);
	}

	public MetricRecord(long ts, ProcIdentifier id) {
		super(ts);
		this.id = id;
	}

	public ProcIdentifier getId() {
		return id;
	}

	public void setId(ProcIdentifier id) {
		this.id = id;
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
		return String.format("{ts=%d, id=%s, records=%s}", getTs(), getId(), getRecords());
	}

}
