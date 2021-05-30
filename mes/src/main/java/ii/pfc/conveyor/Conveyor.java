package ii.pfc.conveyor;

public class Conveyor {

    private final short id;

    private final EnumConveyorType type;

    public Conveyor(int id, EnumConveyorType type) {
        this.id = (short) id;
        this.type = type;
    }

    /*

     */

    public short getId() {
        return id;
    }

    public EnumConveyorType getType() {
        return type;
    }

    /*

     */

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
