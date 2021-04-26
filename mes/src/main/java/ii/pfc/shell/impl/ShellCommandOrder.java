package ii.pfc.shell.impl;

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Ints;
import ii.pfc.command.impl.CommandRequestUnload;
import ii.pfc.manager.ICommandManager;
import ii.pfc.part.PartType;
import ii.pfc.shell.ShellCommand;
import org.apache.commons.lang3.RandomUtils;

import java.net.InetSocketAddress;

public class ShellCommandOrder extends ShellCommand {

    private final ICommandManager commandManager;

    public ShellCommandOrder(ICommandManager commandManager) {
        super("order", "Dispatch orders manually");

        this.commandManager = commandManager;

        registerSubcommand(new ShellCommand("unload", "Dispatch an unload order") {
            @Override
            public boolean onCommand(String[] args) {

                if (args.length < 2) {
                    logger.error("Usage: {} <type> <conveyorId> [quantity]", this.getLabel());
                    return true;
                }

                PartType type = PartType.getType(args[0]);
                if (type == null) {
                    logger.error("Invalid part type: {}",  args[0]);
                    return true;
                }

                Integer conveyorId = Ints.tryParse(args[1]);
                if (conveyorId == null) {
                    logger.error("Invalid conveyorId: {}", args[1]);
                    return true;
                }

                Integer quantity = 1;
                if (args.length > 2) {
                    quantity = Ints.tryParse(args[2]);

                    if (quantity == null) {
                        logger.error("Invalid quantity: {}", args[2]);
                        return true;
                    }
                }

                Stopwatch stopwatch = Stopwatch.createStarted();
                CommandRequestUnload commandRequestUnload = new CommandRequestUnload(
                        RandomUtils.nextInt(),
                        type,
                        conveyorId,
                        quantity
                        );

                commandManager.enqueueRequest(commandRequestUnload, new InetSocketAddress(25));

                logger.info("Completed in {}", stopwatch.stop().toString());

                return true;
            }
        });
    }
}
