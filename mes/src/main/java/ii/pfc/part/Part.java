package ii.pfc.part;

import java.awt.Color;
import java.util.UUID;

public class Part {

    private final UUID id;

    private PartType type;

    private Color color;

    public Part(UUID id, PartType type) {
        this(id, type, type.getDefaultColor());
    }

    public Part(UUID id, PartType type, Color color) {
        this.id = id;
        this.type = type;
        this.color = color;
    }

    /*

     */

    public UUID getId() {
        return id;
    }

    public PartType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    /*

     */

    @Override
    public String toString() {
        return "Part{" +
                "id=" + id +
                ", type=" + type +
                ", color=" + color +
                '}';
    }
}
