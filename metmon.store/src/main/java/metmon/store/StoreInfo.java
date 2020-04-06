package metmon.store;

import metmon.conf.MetmonConfiguration;

/**
 * Store metadata.
 *
 * @param <K> : key type
 * @param <V> : value type
 */
public class StoreInfo<K, V> {

	String procGroup;

	String proc;
	
	MetmonConfiguration conf;

	SerDe<K> kSerde;
	SerDe<V> vSerde;
	
	
	public SerDe<K> getkSerde() {
		return kSerde;
	}

	public void setkSerde(SerDe<K> kSerde) {
		this.kSerde = kSerde;
	}

	public SerDe<V> getvSerde() {
		return vSerde;
	}

	public void setvSerde(SerDe<V> vSerde) {
		this.vSerde = vSerde;
	}

	public String getProcGroup() {
		return procGroup;
	}

	public void setProcGroup(String procGroup) {
		this.procGroup = procGroup;
	}

	public String getProc() {
		return proc;
	}

	public void setProc(String proc) {
		this.proc = proc;
	}

	public MetmonConfiguration getConf() {
		return conf;
	}

	public void setConf(MetmonConfiguration conf) {
		this.conf = conf;
	}

	public StoreInfo(String procGroup, String proc, MetmonConfiguration conf, SerDe<K> kSerde, SerDe<V> vSerde) {
		super();
		this.procGroup = procGroup;
		this.proc = proc;
		this.conf = conf;
		this.kSerde = kSerde;
		this.vSerde = vSerde;
	}

}
