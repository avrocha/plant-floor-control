package ii.pfc.part;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PartType {

    public static final PartType PART_1 = new PartType((short) 1, "P1", new Color(139, 69, 19));
    public static final PartType PART_2 = new PartType((short) 2, "P2", Color.red);
    public static final PartType PART_3 = new PartType((short) 3, "P3", Color.orange);
    public static final PartType PART_4 = new PartType((short) 4, "P4", Color.yellow);
    public static final PartType PART_5 = new PartType((short) 5, "P5", Color.green);
    public static final PartType PART_6 = new PartType((short) 6, "P6", Color.blue);
    public static final PartType PART_7 = new PartType((short) 7, "P7", Color.magenta);
    public static final PartType PART_8 = new PartType((short) 8, "P8", Color.gray);
    public static final PartType PART_9 = new PartType((short) 9, "P9", Color.white);

    public static final PartType UNKNOWN = new PartType((short) 0, "UNKNOWN", Color.black);

    /*

     */

    private static Map<String, PartType> registry;

    //

    private final short internalId;

    private final String name;

    private final Color defaultColor;

    private PartType(short internalId, String name, Color defaultColor) {
        this.internalId = internalId;
        this.name = name;
        this.defaultColor = defaultColor;

        if (registry == null) {
            registry = new HashMap<>();
        }

        registry.put(this.name, this);
    }

    /*

     */

    public short getInternalId() {
        return internalId;
    }

    public String getName() {
        return name;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public boolean isUnknown() {
        return this.equals(UNKNOWN);
    }

    @Override
    public String toString() {
        return this.name;
    }

    /*

     */

    public static final PartType getType(String name) {
        return registry.getOrDefault(name, UNKNOWN);
    }

    public static final Collection<PartType> getTypes() {
        return registry.values();
    }
}
