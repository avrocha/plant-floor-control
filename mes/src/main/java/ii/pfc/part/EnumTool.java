package ii.pfc.part;

public enum EnumTool {

    T1(1),
    T2(2),
    T3(3);

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
