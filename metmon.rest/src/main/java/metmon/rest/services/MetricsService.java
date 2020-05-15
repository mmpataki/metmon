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
    MetricMetaService MS;

    Stores<Short, Double, MetricRecord, Metric> stores;

    List<MetricsFilter<String, Double>> filters;

    @PostConstruct
    public void init() throws Exception {
        stores = new Stores<>("metricstore", conf, new SerDes.ShortSerde(),
                new SerDes.DoubleSerde());
        setupFilters(conf);
    }

    public void consume(MetricRecord rec) throws Exception {
        Store<Short, Double, MetricRecord, Metric> store = stores.open(rec.getId(), true);

        for (MetricsFilter<String, Double> mf : filters) {
            rec = mf.doFilter(rec);
        }

        final MetricRecord u = rec;
        store.put(u, r -> r, c -> new StoreCell<Short, Double>(c.getKey(), c.getValue()));
    }

    public List<MetricRecord> fetch(MetricRequest<Short> req) throws Exception {
        Store<Short, Double, MetricRecord, Metric> store = stores.open(req.getId(), false);
        FromStoreRecord<Short, Double, MetricRecord, Metric> f = (r) -> new MetricRecord(r, req.getId());
        return store.get(req, f, Metric::new);
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
