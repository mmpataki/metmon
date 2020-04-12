package metmon.rest.services.metricfilters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import metmon.conf.MetmonConfiguration;
import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.rest.services.MetricsFilter;

public class HadoopMetricFilter implements MetricsFilter<String, Double> {

	MetmonConfiguration conf;
	static Set<String> keysToIgnore = new HashSet<>();

	/**
	 * Responsibilities:
	 * 	1. Removes the static metadata (refer the above list)
	 *  2. Separates the metadata and metrics
	 *  3. Replaces keys with (beanName || keys)
	 */
	@Override
	public MetricRecord doFilter(MetricRecord mr) {
		Iterator<Metric> mit = mr.getRecords().iterator();
		while(mit.hasNext()) {
			Metric m = mit.next();
			if(keysToIgnore.contains(m.getKey()))
				mit.remove();
		}
		return mr;
	}
	
	public HadoopMetricFilter(MetmonConfiguration conf) {
		this.conf = conf;
	}
}
