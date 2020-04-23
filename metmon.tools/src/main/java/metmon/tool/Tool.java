package metmon.tool;

public interface Tool {

    public void run(String args[]) throws Exception;

    public String help();

}
