package ii.pfc.part;

import java.util.UUID;

public class Part {

    private final UUID id;

    private final int orderId;

    private PartType type;

    public Part(UUID id, int orderId, PartType type) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
    }
    /*

     */

    public UUID getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public PartType getType() {
        return type;
    }

    /*

     */

    @Override
    public String toString() {
        return "Part{" +
            "id=" + id +
            ", orderId=" + orderId +
            ", type=" + type +
            '}';
    }
}
