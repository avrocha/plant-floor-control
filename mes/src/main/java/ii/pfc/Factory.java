package ii.pfc;

import com.google.common.base.Stopwatch;
import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.manager.CommandManager;
import ii.pfc.manager.CommsManager;
import ii.pfc.manager.DatabaseManager;
import ii.pfc.manager.ICommandManager;
import ii.pfc.manager.ICommsManager;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IOrderManager;
import ii.pfc.manager.IRoutingManager;
import ii.pfc.manager.OrderManager;
import ii.pfc.manager.RoutingManager;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import ii.pfc.route.Route;
import ii.pfc.shell.ShellCommand;
import ii.pfc.shell.impl.ShellCommandInventory;
import ii.pfc.shell.impl.ShellCommandStop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Factory {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    //

    public final ICommsManager commsManager;

    private final IDatabaseManager databaseManager;
    
    public final IRoutingManager routingManager;

    private final IOrderManager orderManager;

    private final ICommandManager commandManager;

    //

    private boolean running = false;

    //

    public Factory() {
        this.commsManager = new CommsManager(54321, new InetSocketAddress("127.0.0.1", 4840));
        this.databaseManager = new DatabaseManager();

        this.routingManager = RoutingManager.builder()

             /*unidirectional right side edges*/
            .unidirectional(LIN1, ROT31, DEFAULT_WEIGHT)
            .unidirectional(ROT31, LIN3, DEFAULT_WEIGHT)
            .unidirectional(LIN3, PSH51, DEFAULT_WEIGHT)
            .unidirectional(PSH51, SLD63, DEFAULT_WEIGHT)
            .unidirectional(PSH51, PSH52, DEFAULT_WEIGHT)
            .unidirectional(PSH52, SLD62, DEFAULT_WEIGHT)
            .unidirectional(PSH52, PSH53, DEFAULT_WEIGHT)
            .unidirectional(PSH53, SLD61, DEFAULT_WEIGHT)
            .unidirectional(PSH53, LIN4, DEFAULT_WEIGHT)
            .unidirectional(LIN4, ROT38, DEFAULT_WEIGHT)
            .unidirectional(LIN5, ROT38, DEFAULT_WEIGHT)
            .unidirectional(ROT38, LIN6, DEFAULT_WEIGHT)
            .unidirectional(LIN6, ROT39, DEFAULT_WEIGHT)
            .unidirectional(ROT39, LIN7, DEFAULT_WEIGHT)
            .unidirectional(LIN8, LIN9, DEFAULT_WEIGHT)
            .unidirectional(LIN9, ROT33, DEFAULT_WEIGHT)
            .unidirectional(ROT33, ROT32, DEFAULT_WEIGHT)
            .unidirectional(ROT32, ROT34, DEFAULT_WEIGHT)
            .unidirectional(ROT34, ROT35, DEFAULT_WEIGHT)
            .unidirectional(ROT35, ROT36, DEFAULT_WEIGHT)
            .unidirectional(ROT36, ROT37, DEFAULT_WEIGHT)
            .unidirectional(ROT37, ROT39, DEFAULT_WEIGHT)


            /*unidirectional left side edges*/
            .unidirectional(LIN10, LIN11, DEFAULT_WEIGHT)
            .unidirectional(LIN11, ROT46, DEFAULT_WEIGHT)
            .unidirectional(ROT46, ROT45, DEFAULT_WEIGHT)
            .unidirectional(ROT45, ROT44, DEFAULT_WEIGHT)
            .unidirectional(ROT44, ROT43, DEFAULT_WEIGHT)
            .unidirectional(ROT43, ROT42, DEFAULT_WEIGHT)
            .unidirectional(ROT42, ROT41, DEFAULT_WEIGHT)
            .unidirectional(ROT41, ROT40, DEFAULT_WEIGHT)
            .unidirectional(ROT40, LIN12, DEFAULT_WEIGHT)

            /*bidirectional right side edges*/
            .bidirectional(ROT37, ASM24, DEFAULT_WEIGHT)
            .bidirectional(ROT36, ASM23, DEFAULT_WEIGHT)
            .bidirectional(ROT35, ASM22, DEFAULT_WEIGHT)
            .bidirectional(ROT34, ASM21, DEFAULT_WEIGHT)
            .bidirectional(ROT32, LIN2, DEFAULT_WEIGHT)
            .bidirectional(ROT31, LIN2, DEFAULT_WEIGHT)

            /*bidirectional left side edges*/
            .bidirectional(ROT44, ASM25, DEFAULT_WEIGHT)
            .bidirectional(ROT43, ASM26, DEFAULT_WEIGHT)
            .bidirectional(ROT42, ASM27, DEFAULT_WEIGHT)
            .bidirectional(ROT41, ASM28, DEFAULT_WEIGHT)
            .build();

        this.orderManager = new OrderManager(commsManager, this.databaseManager, this.routingManager);
        this.commandManager = new CommandManager(commsManager, this.orderManager, this.databaseManager);

        this.registerShellCommand(new ShellCommandStop(this));
        this.registerShellCommand(new ShellCommandInventory(this.databaseManager));
    }

    /*

     */

    private static short LOAD_ORDER_ID = 1;

    private void mainTask() {
        this.databaseManager.openConnection();

        this.running = true;

        Stopwatch opcPollTimer = Stopwatch.createStarted();
        Stopwatch dbPollTimer = Stopwatch.createStarted();

        while(running) {
            commandManager.pollRequests();

            if (opcPollTimer.elapsed(TimeUnit.MILLISECONDS) > 100) {
                opcPollTimer.reset();

                short[] loadConveyors = { 1, 5 };
                for(short conveyorId : loadConveyors) {
                    boolean hasPart = commsManager.getLoadConveyorStatus(conveyorId);

                    if (hasPart) {
                        PartType type;

                        switch(conveyorId) {
                            // TODO
                            case 1: {
                                type = PartType.PART_2;
                                break;
                            }

                            default: {
                                type = PartType.PART_1;
                                break;
                            }
                        }

                        Part part = new Part(UUID.randomUUID(), 0, type);
                        Conveyor source = routingManager.getConveyor(conveyorId);

                        for(Conveyor target : routingManager.getConveyors(EnumConveyorType.WAREHOUSE_IN)) {
                            Route route = routingManager.traceRoute(part, source, target);

                            if (route == null) {
                                continue;
                            }

                            commsManager.sendPlcRoute(route);

                            if (!databaseManager.insertPart(part)) {
                                System.out.println("Could not insert part in the database!");
                            }

                            break;
                        }
                    }
                }
            }

            if (dbPollTimer.elapsed(TimeUnit.MILLISECONDS) > 1000) {
                dbPollTimer.reset();

                orderManager.pollUnloadOrders();
                orderManager.pollTransformOrders();
            }
        }
    }

    private void shellTask() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            do {
                String cmd = reader.readLine();

                String[] split = cmd.split(" ");

                ShellCommand command = commands.get(split[0].toLowerCase());
                if (command == null) {
                    System.out.println("Invalid command.");
                    continue;
                }

                command.dispatchCommand(Arrays.copyOfRange(split, 1, split.length));
            } while(this.running);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*

     */

    public void start() {
        this.executor.submit(this.commsManager::startUdpServer);
        this.executor.submit(this::mainTask);

        this.shellTask();
    }

    public void stop() {
        this.running = false;

        this.commsManager.stopUdpServer();
        this.databaseManager.closeConnection();

        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }
    }

    /*

     */

    private final Map<String, ShellCommand> commands = new HashMap<>();

    private void registerShellCommand(ShellCommand command) {
        commands.put(command.getName().toLowerCase(),command);
    }

    /*

     */

    public Function<RoutingManager.RouteData, Double> DEFAULT_WEIGHT = (routeData) -> 1.0;

    /* LINEAR*/
    public Conveyor LIN1 = new Conveyor(1, EnumConveyorType.LINEAR);
    public Conveyor LIN2 = new Conveyor(2, EnumConveyorType.LINEAR);
    public Conveyor LIN3 = new Conveyor(3, EnumConveyorType.LINEAR);
    public Conveyor LIN4 = new Conveyor(4, EnumConveyorType.LINEAR);
    public Conveyor LIN5 = new Conveyor(5, EnumConveyorType.LINEAR);
    public Conveyor LIN6 = new Conveyor(6, EnumConveyorType.LINEAR);
    public Conveyor LIN7 = new Conveyor(7, EnumConveyorType.WAREHOUSE_IN);
    public Conveyor LIN8 = new Conveyor(8, EnumConveyorType.WAREHOUSE_OUT);
    public Conveyor LIN9 = new Conveyor(9, EnumConveyorType.LINEAR);
    public Conveyor LIN10 = new Conveyor(10, EnumConveyorType.WAREHOUSE_OUT);
    public Conveyor LIN11 = new Conveyor(11, EnumConveyorType.LINEAR);
    public Conveyor LIN12 = new Conveyor(12, EnumConveyorType.WAREHOUSE_IN);

    /*ROTATIVE*/
    public Conveyor ROT31 = new Conveyor(31, EnumConveyorType.ROTATIVE);
    public Conveyor ROT32 = new Conveyor(32, EnumConveyorType.ROTATIVE);
    public Conveyor ROT33 = new Conveyor(33, EnumConveyorType.ROTATIVE);
    public Conveyor ROT34 = new Conveyor(34, EnumConveyorType.ROTATIVE);
    public Conveyor ROT35 = new Conveyor(35, EnumConveyorType.ROTATIVE);
    public Conveyor ROT36 = new Conveyor(36, EnumConveyorType.ROTATIVE);
    public Conveyor ROT37 = new Conveyor(37, EnumConveyorType.ROTATIVE);
    public Conveyor ROT38 = new Conveyor(38, EnumConveyorType.ROTATIVE);
    public Conveyor ROT39 = new Conveyor(39, EnumConveyorType.ROTATIVE);
    public Conveyor ROT40 = new Conveyor(40, EnumConveyorType.ROTATIVE);
    public Conveyor ROT41 = new Conveyor(41, EnumConveyorType.ROTATIVE);
    public Conveyor ROT42 = new Conveyor(42, EnumConveyorType.ROTATIVE);
    public Conveyor ROT43 = new Conveyor(43, EnumConveyorType.ROTATIVE);
    public Conveyor ROT44 = new Conveyor(44, EnumConveyorType.ROTATIVE);
    public Conveyor ROT45 = new Conveyor(45, EnumConveyorType.ROTATIVE);
    public Conveyor ROT46 = new Conveyor(46, EnumConveyorType.ROTATIVE);

    /*PUSHER*/
    public Conveyor PSH51 = new Conveyor(51, EnumConveyorType.PUSHER);
    public Conveyor PSH52 = new Conveyor(52, EnumConveyorType.PUSHER);
    public Conveyor PSH53 = new Conveyor(53, EnumConveyorType.PUSHER);

    /*SLIDER*/
    public Conveyor SLD61 = new Conveyor(61, EnumConveyorType.SLIDER);
    public Conveyor SLD62 = new Conveyor(62, EnumConveyorType.SLIDER);
    public Conveyor SLD63 = new Conveyor(63, EnumConveyorType.SLIDER);

    /*ASSEMBLEY*/
    public Conveyor ASM21 = new Conveyor(21, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM22 = new Conveyor(22, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM23 = new Conveyor(23, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM24 = new Conveyor(24, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM25 = new Conveyor(25, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM26 = new Conveyor(26, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM27 = new Conveyor(27, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM28 = new Conveyor(28, EnumConveyorType.ASSEMBLY);

}
