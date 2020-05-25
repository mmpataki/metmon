import metmon.model.metric.Metric;
import metmon.model.metric.MetricRecord;
import metmon.model.metric.MetricSerializer;
import metmon.model.metric.ProcIdentifier;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestMetricSerializer {

    MetricSerializer S;
    MetricRecord mr;

    @Before
    public void setup() {
        S = new MetricSerializer();
        mr = new MetricRecord();
        mr.setTs(0xcafebabe);
        mr.setId(new ProcIdentifier("grp", "proc"));
        for (int i = 0; i < 5; i++) {
            mr.addRecord(new Metric((short)i, i + 0.56));
        }
    }

    @Test
    public void T1_testBinarySerializer() throws IOException {
        /* serialize */
        byte[] o = S.fromMetricRecord(mr);

        /* de-serialize */
        MetricRecord om = S.toMetricRecord(o);

        /* compare */
        assert mr.equals(om);
    }

    @Test
    public void T2_benchMarkSerialization() throws IOException {
        for (int i = 0; i < 1024 * 1024; i++) {
            S.fromMetricRecord(mr);
        }
    }

    @Test
    public void T3_benchMarkDeSerialization() throws IOException {
        byte[] buf = S.fromMetricRecord(mr);
        for (int i = 0; i < 1024 * 1024; i++) {
            S.toMetricRecord(buf);
        }
    }

    @Test
    public void T4_printMessageSize() throws IOException {
        System.out.println("input=" + mr.toString());
        System.out.println("outputLen = " + S.fromMetricRecord(mr).length);
    }

}
