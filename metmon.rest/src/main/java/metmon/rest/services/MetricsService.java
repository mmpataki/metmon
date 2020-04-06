package metmon.rest.services;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import metmon.conf.MetmonConfiguration;
import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.MetricRequest;
import metmon.store.FromStoreRecord;
import metmon.store.Store;
import metmon.store.StoreCell;

@Service
public class MetricsService {

	@Autowired
	MetmonConfiguration conf;

	@Autowired
	MetaService MS;

	Stores<String, Double> stores;

	List<MetricsFilter<String, Double>> filters;

	@PostConstruct
	public void init() throws Exception {
		stores = new Stores<String, Double>(conf, new SerDes.StringSerde(), new SerDes.DoubleSerde());
		setupFilters(conf);
	}

	public void consume(MetricRecord<String, Double> rec) throws Exception {

		MS.sink(rec);

		Store<String, Double> store = stores.open(rec.getId(), true);

		for (MetricsFilter<String, Double> mf : filters) {
			rec = mf.doFilter(rec);
		}

		final MetricRecord<String, Double> u = rec;
		store.put(u, r -> r, c -> new StoreCell<String, Double>(u.getCtxt() + '\0' + c.getKey(), c.getValue()));
	}

	public List<MetricRecord<String, Double>> fetch(MetricRequest<String> req) throws Exception {
		req.setKeys(req.getKeys().stream().map(s -> s.replace(':', '\0')).collect(Collectors.toSet()));
		Store<String, Double> store = stores.open(req.getId(), false);

		FromStoreRecord<String, Double, MetricRecord<String, Double>, Metric<String, Double>> f = (r) -> new MetricRecord<String, Double>(r, "don't use", req.getId());
		return store.get(req, f, (k, v) -> new Metric<>(k.replace('\0', ':'), v));
	}

	@SuppressWarnings("unchecked")
	private void setupFilters(MetmonConfiguration conf) throws Exception {
		filters = new LinkedList<>();
		String filterClasses[] = conf
				.get(MetmonConfiguration.FILTER_CLASS_LIST, MetmonConfiguration.FILTER_CLASS_LIST_DEFAULT).split(",");
		if (filterClasses[0].isEmpty())
			return;
		for (String clazz : filterClasses) {
			filters.add((MetricsFilter<String, Double>) Class.forName(clazz).getConstructor(MetmonConfiguration.class)
					.newInstance(conf));
		}
	}
}