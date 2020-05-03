package metmon.model.meta;

import java.beans.Transient;
import java.util.List;

import metmon.model.metric.ProcIdentifier;
import metmon.store.StoreRecord;

public class Views extends StoreRecord<String, String, View> {

	ProcIdentifier pId;

	public Views() {
		super(-1);
	}
	
	public Views(ProcIdentifier pId) {
		this();
		this.pId = pId;
	}

	public ProcIdentifier getpId() {
		return pId;
	}

	public void setpId(ProcIdentifier pId) {
		this.pId = pId;
	}
	
	@Override
	public void addRecord(View c) {
		pId = c.getpId();
		super.addRecord(c);
	}
	
	@Override
	@Transient
	public long getTs() {
		return super.getTs();
	}
	
	@Override
	public List<View> getRecords() {
		return super.getRecords();
	}

	public List<View> getViews() {
		return super.getRecords();
	}
	
	public void setViews(List<View> records) {
		super.setRecords(records);
	}
}
