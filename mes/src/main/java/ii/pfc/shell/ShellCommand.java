package ii.pfc.shell;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class ShellCommand {

    protected final Logger logger;

    private ShellCommand parent;

    /*

     */

    private final String name;

    private final String description;

    private final Map<String, ShellCommand> subCommands = new HashMap<>();

    /*

     */

    public ShellCommand(String name, String description) {
        this.name = name;
        this.description = description;

        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    /*

     */

    public String getName() {
        return name;
    }

    public String getLabel() {
        LinkedList<String> label = new LinkedList<>();
        label.addFirst(this.name);

        ShellCommand _parent = parent;
        while (_parent != null) {
            label.addFirst(_parent.name);
            _parent = _parent.parent;
        }

        return Joiner.on(' ').join(label);
    }

    public String getDescription() {
        return description;
    }

    /*

     */

    protected void registerSubcommand(ShellCommand command) {
        command.parent = this;

        subCommands.put(command.name.toLowerCase(), command);
    }

    /*

     */

    public void dispatchCommand(String[] args) {
        if (args.length > 0) {
            String cmd = args[0];
            ShellCommand subCommand = subCommands.get(cmd.toLowerCase());

            if (subCommand != null) {
                String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

                subCommand.dispatchCommand(newArgs);
                return;
            }
        }

        boolean status = this.onCommand(args);
        if (!status) {

            logger.info("{} | {}", getLabel(), this.getDescription());
            logger.info("Subcommands: [{}]", Joiner.on(',').join(this.subCommands.keySet()));
        }
    }

    /*
        Return false if it should show the usage of the command
     */

    public boolean onCommand(String[] args) {
        return false;
    }
}
