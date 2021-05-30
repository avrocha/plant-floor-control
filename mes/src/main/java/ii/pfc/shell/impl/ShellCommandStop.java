package ii.pfc.shell.impl;

import ii.pfc.Factory;
import ii.pfc.shell.ShellCommand;

public class ShellCommandStop extends ShellCommand {

    private final Factory factory;

    public ShellCommandStop(Factory factory) {
        super("stop", "Stop MES execution");

        this.factory = factory;
    }

    @Override
    public boolean onCommand(String[] args) {
        this.factory.stop();
        return true;
    }
}
