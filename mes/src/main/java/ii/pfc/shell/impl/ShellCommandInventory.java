package ii.pfc.shell.impl;

import com.google.common.base.Stopwatch;
import com.google.common.primitives.Ints;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.shell.ShellCommand;

import java.util.UUID;

public class ShellCommandInventory extends ShellCommand {

    private final IDatabaseManager databaseManager;

    public ShellCommandInventory(IDatabaseManager databaseManager) {
        super("inventory", "Manages the MES' inventory");

        this.databaseManager = databaseManager;

        registerSubcommand(new ShellCommand("clear", "Clears all the inventory") {
            @Override
            public boolean onCommand(String[] args) {

                Stopwatch stopwatch = Stopwatch.createStarted();
                logger.info("Clearing database inventory parts...");

                if (!databaseManager.clearAllParts()) {
                    logger.error("Something went wrong.");
                    return true;
                }

                logger.info("Completed in {}", stopwatch.stop().toString());

                return true;
            }
        });

        registerSubcommand(new ShellCommand("add", "Adds parts to the inventory") {
            @Override
            public boolean onCommand(String[] args) {

                if (args.length == 0) {
                    logger.error("Usage: {} <type> [quantity]", this.getLabel());
                    return true;
                }

                PartType type = PartType.getType(args[0]);
                if (type == null) {
                    logger.error("Invalid part type: {}", args[0]);
                    return true;
                }

                Integer quantity = 1;
                if (args.length > 1) {
                    quantity = Ints.tryParse(args[1]);

                    if (quantity == null) {
                        logger.error("Invalid quantity: {}", args[1]);
                        return true;
                    }
                }

                Stopwatch stopwatch = Stopwatch.createStarted();
                logger.info("Inserting {} {} part(s) in the inventory...", quantity, type.getName());

                for (int i = 0; i < quantity; i++) {
                    Part part = new Part(
                            UUID.randomUUID(),
                            0,
                            type,
                            Part.PartState.STORED);

                    if (!databaseManager.insertPart(part)) {
                        logger.error("Something went wrong.");
                        return true;
                    }
                }

                logger.info("Completed in {}", stopwatch.stop().toString());

                return true;
            }
        });
    }
}
