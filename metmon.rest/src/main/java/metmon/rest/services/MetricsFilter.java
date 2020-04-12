package metmon.rest.services;

import metmon.model.metric.MetricRecord;

/**
 * Filters for the metrics
 * Responsibilities 
 * 	- remove redundant information 
 *  - separate numeric metric and metadata
 *  - marshall/unmarshall the data if needed
 */
public interface MetricsFilter<K, V> {
	
	/**
	 * Filter the metric update and return a pair of metadata and numeric metrics
	 * @param key
	 * @param value
	 * @return
	 */
	MetricRecord doFilter(MetricRecord mr);
	
}
