package metmon.model.meta;

import java.util.Collections;
import java.util.Set;

import metmon.store.StoreRequest;

public class MetaRecordRequest implements StoreRequest<String> {

	long from, to;
	
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
		return Collections.emptySet();
	}

	public MetaRecordRequest(long from, long to) {
		super();
		this.from = from;
		this.to = to;
	}

}
