package metmon.hadoop.sink;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import metmon.model.meta.KeyRegisterRequest;
import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsSink;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.log4j.Logger;

import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.ProcIdentifier;


public class MetmonSink implements MetricsSink {

    Logger LOG = Logger.getLogger(MetmonSink.class);
    RestClient C;
    String procGrp;
    String proc;
    ProcIdentifier mrid;

    /* buffering data-structures */
    int maxPublishBuffered;
    int publisCalls = 0;
    ConcurrentLinkedQueue<AbstractMetric> buffer;

    /**
     * optimization for reducing nw i/o. for the first time, register the keys
     * using the keys API and some short numbers for them, use them to report the
     * metrics.
     */
    Map<String, Short> keyMap = new HashMap<>();

    String resolve(String val) {
        if (val.startsWith("-D"))
            return System.getProperty(val.substring(2));
        else
            return val;
    }

    /* for hadoop */
    public MetmonSink() {
    }

    /* for other users */
    public MetmonSink(ProcIdentifier pId, String url) {
        mrid = pId;
        C = new RestClient(url);
    }

    @Override
    public void init(SubsetConfiguration conf) {
        System.out.println(conf);
        C = new RestClient(resolve(conf.getString("url")));
        mrid = new ProcIdentifier(resolve(conf.getString("procGrp")), resolve(conf.getString("procName")));
        maxPublishBuffered = conf.getInt("bufferedPublishes", 5);
    }

    @Override
    public void putMetrics(org.apache.hadoop.metrics2.MetricsRecord r) {
        LOG.debug("posting metrics + " + r);
        MetricRecord mr = new MetricRecord(r.timestamp(), mrid);

        List<String> missingKeys = null;
        List<AbstractMetric> unregisteredMetrics = null;

        for (AbstractMetric m : r.metrics()) {
            Short key = keyMap.get(r.name() + ":" + m.name());
            if (key == null) {
                if (missingKeys == null) {
                    missingKeys = new LinkedList<>();
                    unregisteredMetrics = new LinkedList<>();
                }
                missingKeys.add(r.name() + ":" + m.name());
                unregisteredMetrics.add(m);
            } else {
                mr.addRecord(new Metric(key, m.value().doubleValue()));
            }
        }

        /* if some keys are missing, get them from the server (null check is not heavy) */
        if (missingKeys != null) {
            try {
                keyMap.putAll(C.getKeys(new KeyRegisterRequest(mrid, missingKeys)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < unregisteredMetrics.size(); i++) {
                mr.addRecord(new Metric(keyMap.get(missingKeys.get(i)), unregisteredMetrics.get(i).value().doubleValue()));
            }
        }

        try {
            LOG.warn("before post MDEBUG");
            C.postMetric(mr);
            LOG.warn("after post MDEBUG");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {

    }

}
