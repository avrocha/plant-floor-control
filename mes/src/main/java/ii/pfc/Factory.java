package ii.pfc;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.manager.CommsManager;
import ii.pfc.manager.ICommsManager;
import ii.pfc.manager.IRoutingManager;
import ii.pfc.manager.RoutingManager;
import ii.pfc.route.RouteData;
import java.net.InetSocketAddress;
import java.util.function.Function;

public class Factory {

    private final ICommsManager commsManager;
    
    private final IRoutingManager routingManager;

    public Factory() {
        this.commsManager = new CommsManager(new InetSocketAddress("127.0.0.1", 4840));
        this.routingManager = RoutingManager.builder()
            .bidirectional(LIN1, LIN2, DEFAULT_WEIGHT)
            .bidirectional(LIN2, LIN3, DEFAULT_WEIGHT)
            .build();
    }

    /*

     */

    public void start() {

    }

    /*

     */
    
    private Function<RouteData, Double> DEFAULT_WEIGHT = (routeData) -> 1.0;

    /* LINEAR*/
    private Conveyor LIN1 = new Conveyor(1, EnumConveyorType.LINEAR);
    private Conveyor LIN2 = new Conveyor(2, EnumConveyorType.LINEAR);
    private Conveyor LIN3 = new Conveyor(3, EnumConveyorType.LINEAR);
    private Conveyor LIN4 = new Conveyor(4, EnumConveyorType.LINEAR);
    private Conveyor LIN5 = new Conveyor(5, EnumConveyorType.LINEAR);
    private Conveyor LIN6 = new Conveyor(6, EnumConveyorType.LINEAR);
    private Conveyor LIN7 = new Conveyor(7, EnumConveyorType.LINEAR);
    private Conveyor LIN8 = new Conveyor(8, EnumConveyorType.LINEAR);
    private Conveyor LIN9 = new Conveyor(9, EnumConveyorType.LINEAR);
    private Conveyor LIN10 = new Conveyor(10, EnumConveyorType.LINEAR);
    private Conveyor LIN11 = new Conveyor(11, EnumConveyorType.LINEAR);
    private Conveyor LIN12 = new Conveyor(12, EnumConveyorType.LINEAR);
    private Conveyor LIN13 = new Conveyor(13, EnumConveyorType.LINEAR);
    private Conveyor LIN14 = new Conveyor(14, EnumConveyorType.LINEAR);
    private Conveyor LIN15 = new Conveyor(15, EnumConveyorType.LINEAR);
    private Conveyor LIN16 = new Conveyor(16, EnumConveyorType.LINEAR);
    private Conveyor LIN17 = new Conveyor(17, EnumConveyorType.LINEAR);
    private Conveyor LIN18 = new Conveyor(18, EnumConveyorType.LINEAR);
    private Conveyor LIN19 = new Conveyor(19, EnumConveyorType.LINEAR);
    private Conveyor LIN20 = new Conveyor(20, EnumConveyorType.LINEAR);
    private Conveyor LIN21 = new Conveyor(21, EnumConveyorType.LINEAR);

    /*ROTATIVE*/
    private Conveyor ROT31 = new Conveyor(31, EnumConveyorType.ROTATIVE);
    private Conveyor ROT32 = new Conveyor(32, EnumConveyorType.ROTATIVE);
    private Conveyor ROT33 = new Conveyor(33, EnumConveyorType.ROTATIVE);
    private Conveyor ROT34 = new Conveyor(34, EnumConveyorType.ROTATIVE);
    private Conveyor ROT35 = new Conveyor(35, EnumConveyorType.ROTATIVE);
    private Conveyor ROT36 = new Conveyor(36, EnumConveyorType.ROTATIVE);
    private Conveyor ROT37 = new Conveyor(37, EnumConveyorType.ROTATIVE);
    private Conveyor ROT38 = new Conveyor(38, EnumConveyorType.ROTATIVE);

    /*PUSHER*/
    private Conveyor PSH51 = new Conveyor(51, EnumConveyorType.PUSHER);
    private Conveyor PSH52 = new Conveyor(52, EnumConveyorType.PUSHER);
    private Conveyor PSH53 = new Conveyor(53, EnumConveyorType.PUSHER);

    /*SLIDER*/
    private Conveyor SLD61 = new Conveyor(61, EnumConveyorType.SLIDER);
    private Conveyor SLD62 = new Conveyor(62, EnumConveyorType.SLIDER);
    private Conveyor SLD63 = new Conveyor(63, EnumConveyorType.SLIDER);

    /*ASSEMBLEY*/
    private Conveyor ASM101 = new Conveyor(101, EnumConveyorType.ASSEMBLY);
    private Conveyor ASM102 = new Conveyor(102, EnumConveyorType.ASSEMBLY);
    private Conveyor ASM103 = new Conveyor(103, EnumConveyorType.ASSEMBLY);
    private Conveyor ASM104 = new Conveyor(104, EnumConveyorType.ASSEMBLY);
    private Conveyor ASM105 = new Conveyor(105, EnumConveyorType.ASSEMBLY);
    private Conveyor ASM106 = new Conveyor(106, EnumConveyorType.ASSEMBLY);
    private Conveyor ASM107 = new Conveyor(107, EnumConveyorType.ASSEMBLY);
    private Conveyor ASM108 = new Conveyor(108, EnumConveyorType.ASSEMBLY);

}
