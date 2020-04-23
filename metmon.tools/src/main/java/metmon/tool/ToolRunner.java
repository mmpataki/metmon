package metmon.tool;

public class ToolRunner {

    public static void runTool(Tool tool, String[] args) throws Exception {
        if(args.length > 0 && args[0].equals("-help")) {
            System.out.println(tool.help());
            return;
        }
        tool.run(args);
    }

}
