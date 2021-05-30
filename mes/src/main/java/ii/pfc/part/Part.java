package ii.pfc.part;

import java.util.UUID;

public class Part {

    private final UUID id;

    private final int orderId;

    private final PartType type;

    private final PartState state;

    public Part(UUID id, int orderId, PartType type, PartState state) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
        this.state = state;
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

    public PartState getState() {
        return state;
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

    /*

     */

    public static enum PartState {

        STORED("Stored"),
        PROCESSING("Processing"),
        UNLOADING("Unloading"),
        COMPLETED("Completed");

        private final String displayName;

        PartState(String displayName) {
            this.displayName = displayName;
        }

        /*

         */

        public String getDisplayName() {
            return displayName;
        }
    }
}
