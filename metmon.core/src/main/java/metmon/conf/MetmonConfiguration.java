package metmon.conf;

import java.util.Map;
import org.apache.hadoop.conf.Configuration;

public class MetmonConfiguration extends Configuration {

	/* storage */
	public static final String STORAGE_BACKEND_CLASS = "storage.backend.class";
	public static final String STORAGE_BACKEND_CLASS_DEFAULT = "metmon.inmemstore.InMemStore";

	/* store decorators */
	public static final String STORE_DECOR_PROFILE = "store.%s.decorators";
	public static final String STORE_DECOR_DEF_PROFILE = "";

	/* metric filters */
	public static final String FILTER_CLASS_LIST = "metrics.filter.classes";
	public static final String FILTER_CLASS_LIST_DEFAULT = "metmon.rest.services.metricfilters.HadoopMetricFilter";

	public MetmonConfiguration() {
		super(false);
		addResource(getClass().getClassLoader().getResource("metmon-site.xml"));
	}

	public static MetmonConfiguration create() {
		return new MetmonConfiguration();
	}

	public Map<?, ?> getAsMap() {
		return getProps();
	}

	public String getFormatted(String key, String val, String def) {
		return get(String.format(key, val), def);
	}

	public static void main(String[] args) {
		new MetmonConfiguration();
	}
}
