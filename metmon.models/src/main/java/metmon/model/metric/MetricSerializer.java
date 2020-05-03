package metmon.model.metric;

import java.io.*;

/** TODO: protobuf has no such capabilities, write a new project or a PR. */
public class MetricSerializer {

    public byte[] fromMetricRecord(MetricRecord rec) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeLong(rec.getTs());
        dos.writeUTF(rec.getId().getProcessGrp());
        dos.writeUTF(rec.getId().getProcess());
        dos.writeShort(rec.getRecords().size());
        for (Metric m : rec.getRecords()) {
            dos.writeShort(m.getKey());
            dos.writeDouble(m.getValue());
        }
        return bos.toByteArray();
    }

    public MetricRecord toMetricRecord(byte[] buf) throws IOException {
        MetricRecord mr = new MetricRecord();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buf));
        mr.setTs(dis.readLong());
        ProcIdentifier pId = new ProcIdentifier();
        pId.setProcessGrp(dis.readUTF());
        pId.setProcess(dis.readUTF());
        mr.setId(pId);
        short len = dis.readShort();
        for (int i = 0; i < len; i++) {
            Metric m = new Metric();
            m.setKey(dis.readShort());
            m.setValue(dis.readDouble());
            mr.addRecord(m);
        }
        return mr;
    }
}
