package ii.pfc.part;

import java.time.Duration;

public class Process {

    private final PartType source;

    private final PartType result;

    private final EnumTool tool;

    private final Duration duration;

    public Process(PartType source, PartType result, EnumTool tool, Duration duration) {
        this.source = source;
        this.result = result;
        this.tool = tool;
        this.duration = duration;
    }

    /*

     */

    public PartType getSource() {
        return source;
    }

    public PartType getResult() {
        return result;
    }

    public EnumTool getTool() {
        return tool;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Process{" +
                "source=" + source +
                ", result=" + result +
                ", tool=" + tool +
                ", duration=" + duration +
                '}';
    }
}
