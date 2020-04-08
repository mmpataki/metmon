package metmon.model.meta;

import java.beans.Transient;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import metmon.model.metric.ProcIdentifier;
import metmon.store.StoreCell;

public class View extends StoreCell<String, String> {

	public static class ViewData {
		Map<String, String> extra;
		Set<String> keys;

		public Set<String> getKeys() {
			return keys;
		}

		public void setKeys(Set<String> keys) {
			this.keys = keys;
		}
		
		public Map<String, String> getExtra() {
			return extra;
		}

		public void setExtra(Map<String, String> extra) {
			this.extra = extra;
		}

		public ViewData(Set<String> keys) {
			super();
			this.keys = keys;
		}

		public ViewData() {
		}

		@Override
		public String toString() {
			return new Gson().toJson(this);
		}
		@Override
		public boolean equals(Object obj) {
			ViewData v = (ViewData)obj;
			if(v==this)
				return true;
			if(!v.getKeys().equals(getKeys()))
				return false;
			return true;
		}
	}

	String name;
	
	ProcIdentifier pId;
	
	ViewData vData;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProcIdentifier getpId() {
		return pId;
	}

	public void setpId(ProcIdentifier pId) {
		this.pId = pId;
	}

	@Override
	@Transient
	public String getKey() {
		return getpId() + "/" + getName();
	}
	
	@Override
	@Transient
	public String getValue() {
		return new Gson().toJson(vData);
	}

	public ViewData getvData() {
		return vData;
	}

	public void setvData(ViewData vData) {
		this.vData = vData;
	}

	public View(ProcIdentifier pId, String name, ViewData vData) {
		this.pId = pId;
		this.name = name;
		this.vData = vData;
	}

	public View(String name, String vDataJson) {
		String chunks[] = name.split("/");
		pId = new ProcIdentifier(chunks[0], chunks[1]);
		this.name = chunks[2];
		vData = new Gson().fromJson(vDataJson, ViewData.class);
	}
	
	public View() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean equals(Object obj) {
		View v = (View)obj;
		if(!v.getName().equals(getName()))
			return false;
		if(!v.getpId().equals(getpId()))
			return false;
		if(!v.getvData().equals(getvData()))
			return false;
		return true;
	}
}
