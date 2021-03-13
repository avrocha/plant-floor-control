package ii.pfc.part;

public enum EnumTool {

    TOOL_1(1),
    TOOL_2(2),
    TOOL_3(3);

    private final int id;

    EnumTool(int id) {
        this.id = id;
    }

    /*

     */

    public int getId() {
        return id;
    }
}
