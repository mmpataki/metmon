import metmon.hadoop.sink.MetmonSink;
import org.junit.Test;

public class TestNameResolution {

    MetmonSink ms = new MetmonSink();

    @Test
    public void testWithSingleString() {
        assert ms.resolve("prop1").equals("prop1");
    }

    @Test
    public void testWithSingleSubString() {
        System.setProperty("prop1", "abcd");
        assert ms.resolve("-Dprop1(1,2)").equals("b");
    }

    @Test
    public void testWithMultiString() {
        System.setProperty("prop1", "abcd");
        System.setProperty("prop2", "efgh");
        assert ms.resolve("-Dprop1;-Dprop2").equals("abcd_efgh");
    }

    @Test
    public void testWithMultiSubString() {
        System.setProperty("prop1", "abcd");
        System.setProperty("prop2", "efgh");
        assert ms.resolve("-Dprop1(1,2);-Dprop2(1,2)").equals("b_f");
    }

    @Test
    public void testWithMultiHeteroString() {
        System.setProperty("prop1", "abcd");
        System.setProperty("prop2", "efgh");
        assert ms.resolve("-Dprop1(1,2);-Dprop2;ijkl").equals("b_efgh_ijkl");
    }
}
