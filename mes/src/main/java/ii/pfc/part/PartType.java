package ii.pfc.part;

import java.awt.Color;

public class PartType {

    private final String name;

    private final Color defaultColor;

    public PartType(String name, Color defaultColor) {
        this.name = name;
        this.defaultColor = defaultColor;
    }

    /*

     */

    public String getName() {
        return name;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }
}
