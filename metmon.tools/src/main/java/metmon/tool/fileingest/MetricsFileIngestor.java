package metmon.tool.fileingest;

import metmon.hadoop.sink.MetmonSink;
import metmon.model.metric.ProcIdentifier;
import metmon.tool.Tool;
import metmon.tool.ToolRunner;
import org.apache.hadoop.metrics2.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MetricsFileIngestor implements Tool {

    String procGrp;

    public static void main(String[] args) throws Exception {
        if (args.length == 0 && !Boolean.getBoolean("DEBUG")) {
            args = new String[]{"http://localhost:8080", "fileingest", "/home/mmp/mmp/hadoop/hbase/1.3.1/hbase-rel-1.3.1/test.txt"};
        }
        ToolRunner.runTool(new MetricsFileIngestor(args[1]), args);
    }

    MetricsFileIngestor(String procGrp) {
        this.procGrp = procGrp;
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println(help());
            return;
        }
        for (int i = 2; i < args.length; i++) {
            new Thread(new Publisher(procGrp, args[i], args[0])).start();
        }
    }

    static class MyTag extends MetricsTag {
        public MyTag(MetricsInfo info, String value) {
            super(info, value);
        }
    }

    static class MyMetricsInfo implements MetricsInfo {

        String name;
        String desc;

        public MyMetricsInfo(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String description() {
            return desc;
        }
    }

    static class MyMetricsRecord implements MetricsRecord {

        long ts;
        String ctxt, name, desc;
        List<MetricsTag> tags;
        List<AbstractMetric> metrics;

        public MyMetricsRecord(long ts, String ctxt, String name, String desc) {
            this.ts = ts;
            this.ctxt = ctxt;
            this.name = name;
            this.desc = desc;
            tags = new LinkedList<>();
            metrics = new LinkedList<>();
        }

        @Override
        public long timestamp() {
            return ts;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String description() {
            return desc;
        }

        @Override
        public String context() {
            return ctxt;
        }

        @Override
        public Collection<MetricsTag> tags() {
            return tags;
        }

        @Override
        public Iterable<AbstractMetric> metrics() {
            return metrics;
        }

        public void addMetric(MyMetric mm) {
            metrics.add(mm);
        }

        public void addTag(MyTag mt) {
            tags.add(mt);
        }
    }

    static class MyMetric extends AbstractMetric {

        double val;


        protected MyMetric(MyMetricsInfo mi, double val) {
            super(mi);
            this.val = val;
        }

        @Override
        public Number value() {
            return val;
        }

        @Override
        public MetricType type() {
            return MetricType.COUNTER;
        }

        @Override
        public void visit(MetricsVisitor visitor) {
        }
    }

    static class Publisher implements Runnable {

        ProcIdentifier pid;
        String file;
        MetricsSink S;

        Logger LOG = LoggerFactory.getLogger(Publisher.class);

        public Publisher(String procGrp, String file, String url) {
            this.pid = new ProcIdentifier(procGrp, new File(file).getName().replace(".", "_").replace(":", "_"));
            this.file = file;
            this.S = new MetmonSink(pid, url);
        }

        public void publish(String line) throws Exception {
            try {
                String[] chunks = line.split(", ");
                if (chunks.length < 2) {
                    return;
                }
                String[] metaChunks = chunks[0].split(" ");
                long ts = 0xffff;
                ts = Long.parseLong(metaChunks[0]);
                String ctxt = metaChunks[1].split("\\.")[0];
                String name = metaChunks[1].split("\\.")[1].replace(":", "");

                MyMetricsRecord mr = new MyMetricsRecord(ts, ctxt, name, "");

                for (int i = 1; i < chunks.length; i++) {
                    String[] kvp = chunks[i].split("=");
                    if (kvp.length < 2) {
                        kvp = new String[]{kvp[0], ""};
                        LOG.warn("A keyvalue pair [{}] has no value, assuming empty string.", kvp[0]);
                    }
                    MyMetricsInfo mi = new MyMetricsInfo(kvp[0], "");
                    try {
                        double v = Double.parseDouble(kvp[1]);
                        mr.addMetric(new MyMetric(mi, v));
                    } catch (NumberFormatException e) {
                        mr.addTag(new MyTag(mi, kvp[1]));
                    }
                }
                S.putMetrics(mr);
            }catch (Exception e) {
                System.out.println("publish failed for line: \n" + line);
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            LOG.info("Processing {}", file);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = br.readLine()) != null) {
                    try {
                        publish(line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOG.info("Processing {} is done", file);
        }
    }

    @Override
    public String help() {
        return String.format(
                "Usage: %s <metmon-url> <proc-group-name> <hadoop-metric-files>*\n" +
                        "\t metmon-url : http url of the metmon\n" +
                        "\t proc-group-name : name for the group of processes\n" +
                        "\t hadoop-metric-file : file generated by the hadoop FileSink\n",
                getClass().getCanonicalName()
        );
    }
}
