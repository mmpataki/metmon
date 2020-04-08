package metmon.store;

import java.util.List;

public class DeleteRequest<K> {
	
	long ts;
	
	List<K> keys;

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public List<K> getKeys() {
		return keys;
	}

	public void setKeys(List<K> keys) {
		this.keys = keys;
	}

	public DeleteRequest(long ts, List<K> keys) {
		super();
		this.ts = ts;
		this.keys = keys;
	}
	
}
