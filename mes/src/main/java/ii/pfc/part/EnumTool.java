package ii.pfc.part;

import java.util.HashMap;
import java.util.Map;

public enum EnumTool {

    T1(1),
    T2(2),
    T3(3);

    private final short id;

    EnumTool(int id) {
        this.id = (short) id;
    }

    /*

     */

    public short getId() {
        return id;
    }

    private static final Map<Short, EnumTool> registry = new HashMap<>();
    static {
        for(EnumTool tool : values() ){
            registry.put(tool.id, tool);
        }
    }

    public static EnumTool getTool(short id) {
        return registry.get(id);
    }
}
