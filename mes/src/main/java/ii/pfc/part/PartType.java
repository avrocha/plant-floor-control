package ii.pfc.part;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class PartType {

    public static final PartType PART_1 = new PartType("P1", new Color(139, 69, 19));
    public static final PartType PART_2 = new PartType("P2", Color.red);
    public static final PartType PART_3 = new PartType("P3", Color.orange);
    public static final PartType PART_4 = new PartType("P4", Color.yellow);
    public static final PartType PART_5 = new PartType("P5", Color.green);
    public static final PartType PART_6 = new PartType("P6", Color.blue);
    public static final PartType PART_7 = new PartType("P7", Color.magenta);
    public static final PartType PART_8 = new PartType("P8", Color.gray);
    public static final PartType PART_9 = new PartType("P9", Color.white);

    public static final PartType UNKNOWN = new PartType("UNKNOWN", Color.black);

    /*

     */

    private static Map<String, PartType> registry;

    //

    private final String name;

    private final Color defaultColor;

    private PartType(String name, Color defaultColor) {
        this.name = name;
        this.defaultColor = defaultColor;

        if (registry == null) {
            registry = new HashMap<>();
        }

        registry.put(this.name, this);
    }

    /*

     */

    public String getName() {
        return name;
    }

    public Color getDefaultColor() {
        return defaultColor;
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
}
