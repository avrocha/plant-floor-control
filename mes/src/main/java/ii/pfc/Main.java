package ii.pfc;

import javax.swing.*;

public class Main {

    /*

     */

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        Factory factory = new Factory();
        factory.start();
    }

}
