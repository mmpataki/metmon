package metmon.model.kv;

import java.util.Set;

import metmon.store.StoreRequest;

public class KeyValueRequest implements StoreRequest<String> {

	long from, to;
	Set<String> keys;
	
	@Override
	public long getFrom() {
		return from;
	}

	@Override
	public long getTo() {
		return to;
	}

	@Override
	public Set<String> getKeys() {
		return keys;
	}

	public KeyValueRequest(long from, long to, Set<String> keys) {
		super();
		this.from = from;
		this.to = to;
		this.keys = keys;
	}

}
