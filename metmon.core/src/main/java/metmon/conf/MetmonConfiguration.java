package metmon.conf;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MetmonConfiguration {

	Map<String, String> map = new HashMap<>();


	/* storage */
	public static final String STORAGE_BACKEND_CLASS = "storage.backend.class";
	public static final String STORAGE_BACKEND_CLASS_DEFAULT = "metmon.inmemstore.InMemStore";

	/* store decorators */
	public static final String STORE_DECOR_PROFILE = "store.%s.decorators";
	public static final String STORE_DECOR_DEF_PROFILE = "";

	/* metric filters */
	public static final String FILTER_CLASS_LIST = "metrics.filter.classes";
	public static final String FILTER_CLASS_LIST_DEFAULT = "metmon.rest.services.metricfilters.HadoopMetricFilter";

	public MetmonConfiguration() throws IOException {
		addResource(getClass().getClassLoader().getResource("metmon.properties"));
	}

	private void addResource(URL resource) throws IOException {
		new Properties().load(resource.openStream());
	}

	public static MetmonConfiguration create() throws IOException {
		return new MetmonConfiguration();
	}

	public Map<String, String> getAsMap() {
		return map;
	}

	public String getFormatted(String key, String val, String def) {
		return get(String.format(key, val), def);
	}

	public String get(String key, String defValue) {
		if(map.containsKey(key))
			return map.get(key);
		return defValue;
	}

	public static void main(String[] args) throws IOException {
		new MetmonConfiguration();
	}
}
