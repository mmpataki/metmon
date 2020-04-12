package metmon.hadoop.sink;

import java.util.Collection;

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
	
	String resolve(String val) {
		if(val.startsWith("-D"))
			return System.getProperty(val.substring(2));
		else
			return val;
	}
	
	@Override
	public void init(SubsetConfiguration conf) {
		System.out.println(conf);
		C = new RestClient(resolve(conf.getString("url")));
		procGrp = resolve(conf.getString("procGrp"));
		proc = resolve(conf.getString("procName"));
	}

	@Override
	public void putMetrics(org.apache.hadoop.metrics2.MetricsRecord r) {
		LOG.warn("posting metrics + " + r);
		ProcIdentifier mrid = new ProcIdentifier(procGrp, find(r.tags(), "Hostname") + "-" + proc);
		MetricRecord mr = new MetricRecord(r.timestamp(), r.name(), mrid);
		for (AbstractMetric	m : r.metrics()) {
			mr.addRecord(new Metric(m.name(), m.value().doubleValue()));
		}
		try {
			LOG.warn("before post MDEBUG");
			C.postMetric(mr);
			LOG.warn("after post MDEBUG");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private String find(Collection<MetricsTag> tags, String name) {
		for (MetricsTag m : tags) {
			if(m.name().equals(name))
				return m.value();
		}
		return "<NULL>";
	}

	@Override
	public void flush() {
		
	}

}
