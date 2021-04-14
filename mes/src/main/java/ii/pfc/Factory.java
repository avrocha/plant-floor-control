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
