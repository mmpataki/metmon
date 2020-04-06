package metmon.conf;

import java.util.Map;
import org.apache.hadoop.conf.Configuration;

public class MetmonConfiguration extends Configuration {

	/* storage */
	public static final String STORAGE_BACKEND_CLASS = "storage.backend.class";
	public static final String STORAGE_BACKEND_CLASS_DEFAULT = "metmon.store.hbase.HBaseStore";
	
	/* metric filters */
	public static final String FILTER_CLASS_LIST = "metrics.filter.classes";
	public static final String FILTER_CLASS_LIST_DEFAULT = "metmon.rest.services.metricfilters.HadoopMetricFilter";
	
	
	public MetmonConfiguration() {
		super(false);
	}
	
	public static MetmonConfiguration create() {
		return new MetmonConfiguration();
	}

	public Map<?, ?> getAsMap() {
		return getProps();
	}
}
