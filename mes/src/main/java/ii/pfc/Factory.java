package ii.pfc;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.manager.CommsManager;
import ii.pfc.manager.ICommsManager;
import ii.pfc.manager.IRoutingManager;
import ii.pfc.manager.RoutingManager;
import java.net.InetSocketAddress;
import java.util.function.Function;

public class Factory {

    public final ICommsManager commsManager;
    
    public final IRoutingManager routingManager;

    public Factory() {
        this.commsManager = new CommsManager(new InetSocketAddress("127.0.0.1", 4840));
        this.routingManager = RoutingManager.builder()

             /*unidirectional right side edges*/
            .unidirectional(LIN1, ROT31, DEFAULT_WEIGHT)
            .unidirectional(ROT31, LIN12, DEFAULT_WEIGHT)
            .unidirectional(LIN12, PSH51, DEFAULT_WEIGHT)
            .unidirectional(PSH51, SLD63, DEFAULT_WEIGHT)
            .unidirectional(PSH51, PSH52, DEFAULT_WEIGHT)
            .unidirectional(PSH52, SLD62, DEFAULT_WEIGHT)
            .unidirectional(PSH52, PSH53, DEFAULT_WEIGHT)
            .unidirectional(PSH53, SLD61, DEFAULT_WEIGHT)
            .unidirectional(PSH53, LIN11, DEFAULT_WEIGHT)
            .unidirectional(LIN8, ROT33, DEFAULT_WEIGHT)
            .unidirectional(LIN11, ROT33, DEFAULT_WEIGHT)
            .unidirectional(ROT33, LIN9, DEFAULT_WEIGHT)
            .unidirectional(LIN9, ROT34, DEFAULT_WEIGHT)
            .unidirectional(ROT34, LIN10, DEFAULT_WEIGHT)
            .unidirectional(LIN13, LIN14, DEFAULT_WEIGHT)
            .unidirectional(LIN14, LIN3, DEFAULT_WEIGHT)
            .unidirectional(LIN3, ROT32, DEFAULT_WEIGHT)
            .unidirectional(ROT32, LIN4, DEFAULT_WEIGHT)
            .unidirectional(LIN4, LIN5, DEFAULT_WEIGHT)
            .unidirectional(LIN5, LIN6, DEFAULT_WEIGHT)
            .unidirectional(LIN6, LIN7, DEFAULT_WEIGHT)
            .unidirectional(LIN7, ROT34, DEFAULT_WEIGHT)


            /*unidirectional left side edges*/
            .unidirectional(LIN15, LIN16, DEFAULT_WEIGHT)
            .unidirectional(LIN16, ROT36, DEFAULT_WEIGHT)
            .unidirectional(ROT36, ROT37, DEFAULT_WEIGHT)
            .unidirectional(ROT37, LIN17, DEFAULT_WEIGHT)
            .unidirectional(LIN17, LIN18, DEFAULT_WEIGHT)
            .unidirectional(LIN18, LIN19, DEFAULT_WEIGHT)
            .unidirectional(LIN19, LIN20, DEFAULT_WEIGHT)
            .unidirectional(LIN20, ROT35, DEFAULT_WEIGHT)
            .unidirectional(ROT35, LIN21, DEFAULT_WEIGHT)

            /*bidirectional right side edges*/
            .bidirectional(LIN7, ASM104, DEFAULT_WEIGHT)
            .bidirectional(LIN6, ASM103, DEFAULT_WEIGHT)
            .bidirectional(LIN5, ASM102, DEFAULT_WEIGHT)
            .bidirectional(LIN4, ASM101, DEFAULT_WEIGHT)
            .bidirectional(ROT32, LIN2, DEFAULT_WEIGHT)
            .bidirectional(ROT31, LIN2, DEFAULT_WEIGHT)

            /*bidirectional left side edges*/
            .bidirectional(LIN17, ASM105, DEFAULT_WEIGHT)
            .bidirectional(LIN18, ASM106, DEFAULT_WEIGHT)
            .bidirectional(LIN19, ASM107, DEFAULT_WEIGHT)
            .bidirectional(LIN20, ASM108, DEFAULT_WEIGHT)
            .build();
    }

    /*

     */

    public void start() {

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
    public Conveyor LIN7 = new Conveyor(7, EnumConveyorType.LINEAR);
    public Conveyor LIN8 = new Conveyor(8, EnumConveyorType.LINEAR);
    public Conveyor LIN9 = new Conveyor(9, EnumConveyorType.LINEAR);
    public Conveyor LIN10 = new Conveyor(10, EnumConveyorType.LINEAR);
    public Conveyor LIN11 = new Conveyor(11, EnumConveyorType.LINEAR);
    public Conveyor LIN12 = new Conveyor(12, EnumConveyorType.LINEAR);
    public Conveyor LIN13 = new Conveyor(13, EnumConveyorType.LINEAR);
    public Conveyor LIN14 = new Conveyor(14, EnumConveyorType.LINEAR);
    public Conveyor LIN15 = new Conveyor(15, EnumConveyorType.LINEAR);
    public Conveyor LIN16 = new Conveyor(16, EnumConveyorType.LINEAR);
    public Conveyor LIN17 = new Conveyor(17, EnumConveyorType.LINEAR);
    public Conveyor LIN18 = new Conveyor(18, EnumConveyorType.LINEAR);
    public Conveyor LIN19 = new Conveyor(19, EnumConveyorType.LINEAR);
    public Conveyor LIN20 = new Conveyor(20, EnumConveyorType.LINEAR);
    public Conveyor LIN21 = new Conveyor(21, EnumConveyorType.LINEAR);

    /*ROTATIVE*/
    public Conveyor ROT31 = new Conveyor(31, EnumConveyorType.ROTATIVE);
    public Conveyor ROT32 = new Conveyor(32, EnumConveyorType.ROTATIVE);
    public Conveyor ROT33 = new Conveyor(33, EnumConveyorType.ROTATIVE);
    public Conveyor ROT34 = new Conveyor(34, EnumConveyorType.ROTATIVE);
    public Conveyor ROT35 = new Conveyor(35, EnumConveyorType.ROTATIVE);
    public Conveyor ROT36 = new Conveyor(36, EnumConveyorType.ROTATIVE);
    public Conveyor ROT37 = new Conveyor(37, EnumConveyorType.ROTATIVE);

    /*PUSHER*/
    public Conveyor PSH51 = new Conveyor(51, EnumConveyorType.PUSHER);
    public Conveyor PSH52 = new Conveyor(52, EnumConveyorType.PUSHER);
    public Conveyor PSH53 = new Conveyor(53, EnumConveyorType.PUSHER);

    /*SLIDER*/
    public Conveyor SLD61 = new Conveyor(61, EnumConveyorType.SLIDER);
    public Conveyor SLD62 = new Conveyor(62, EnumConveyorType.SLIDER);
    public Conveyor SLD63 = new Conveyor(63, EnumConveyorType.SLIDER);

    /*ASSEMBLEY*/
    public Conveyor ASM101 = new Conveyor(101, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM102 = new Conveyor(102, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM103 = new Conveyor(103, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM104 = new Conveyor(104, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM105 = new Conveyor(105, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM106 = new Conveyor(106, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM107 = new Conveyor(107, EnumConveyorType.ASSEMBLY);
    public Conveyor ASM108 = new Conveyor(108, EnumConveyorType.ASSEMBLY);

}
