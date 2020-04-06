package metmon.model.metric;

import java.util.Set;

import metmon.store.StoreRequest;

public class MetricRequest<K> implements StoreRequest<K> {

	long from, to;

	ProcIdentifier id;

	Set<K> keys;

	public MetricRequest() {
	}

	public MetricRequest(long from, long to, ProcIdentifier id, Set<K> keys) {
		super();
		this.from = from;
		this.to = to;
		this.id = id;
		this.keys = keys;
	}

	public ProcIdentifier getId() {
		return id;
	}

	public void setId(ProcIdentifier id) {
		this.id = id;
	}

	public void setKeys(Set<K> keys) {
		this.keys = keys;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public void setTo(long to) {
		this.to = to;
	}

	@Override
	public long getFrom() {
		return from;
	}

	@Override
	public long getTo() {
		return to;
	}

	@Override
	public Set<K> getKeys() {
		return keys;
	}

}
