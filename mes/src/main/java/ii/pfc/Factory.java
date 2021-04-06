package ii.pfc;

import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.manager.CommsManager;
import ii.pfc.manager.ICommsManager;
import java.net.InetSocketAddress;

public class Factory {

    private final ICommsManager commsManager;

    public Factory() {
        this.commsManager = new CommsManager(new InetSocketAddress("127.0.0.1", 4840));
    }

    /*

     */

    public void start() {

    }
    /* LINEAR*/
    Conveyor LIN1 = new Conveyor(1, EnumConveyorType.LINEAR);
    Conveyor LIN2 = new Conveyor(2, EnumConveyorType.LINEAR);
    Conveyor LIN3 = new Conveyor(3, EnumConveyorType.LINEAR);
    Conveyor LIN4 = new Conveyor(4, EnumConveyorType.LINEAR);
    Conveyor LIN5 = new Conveyor(5, EnumConveyorType.LINEAR);
    Conveyor LIN6 = new Conveyor(6, EnumConveyorType.LINEAR);
    Conveyor LIN7 = new Conveyor(7, EnumConveyorType.LINEAR);
    Conveyor LIN8 = new Conveyor(8, EnumConveyorType.LINEAR);
    Conveyor LIN9 = new Conveyor(9, EnumConveyorType.LINEAR);
    Conveyor LIN10 = new Conveyor(10, EnumConveyorType.LINEAR);
    Conveyor LIN11 = new Conveyor(11, EnumConveyorType.LINEAR);
    Conveyor LIN12 = new Conveyor(12, EnumConveyorType.LINEAR);
    Conveyor LIN13 = new Conveyor(13, EnumConveyorType.LINEAR);
    Conveyor LIN14 = new Conveyor(14, EnumConveyorType.LINEAR);
    Conveyor LIN15 = new Conveyor(15, EnumConveyorType.LINEAR);
    Conveyor LIN16 = new Conveyor(16, EnumConveyorType.LINEAR);
    Conveyor LIN17 = new Conveyor(17, EnumConveyorType.LINEAR);
    Conveyor LIN18 = new Conveyor(18, EnumConveyorType.LINEAR);
    Conveyor LIN19 = new Conveyor(19, EnumConveyorType.LINEAR);
    Conveyor LIN20 = new Conveyor(20, EnumConveyorType.LINEAR);
    Conveyor LIN21 = new Conveyor(21, EnumConveyorType.LINEAR);

    /*ROTATIVE*/
    Conveyor ROT31 = new Conveyor(31, EnumConveyorType.ROTATIVE);
    Conveyor ROT32 = new Conveyor(32, EnumConveyorType.ROTATIVE);
    Conveyor ROT33 = new Conveyor(33, EnumConveyorType.ROTATIVE);
    Conveyor ROT34 = new Conveyor(34, EnumConveyorType.ROTATIVE);
    Conveyor ROT35 = new Conveyor(35, EnumConveyorType.ROTATIVE);
    Conveyor ROT36 = new Conveyor(36, EnumConveyorType.ROTATIVE);
    Conveyor ROT37 = new Conveyor(37, EnumConveyorType.ROTATIVE);
    Conveyor ROT38 = new Conveyor(38, EnumConveyorType.ROTATIVE);

    /*PUSHER*/
    Conveyor PSH51 = new Conveyor(51, EnumConveyorType.PUSHER);
    Conveyor PSH52 = new Conveyor(52, EnumConveyorType.PUSHER);
    Conveyor PSH53 = new Conveyor(53, EnumConveyorType.PUSHER);

    /*SLIDER*/
    Conveyor SLD61 = new Conveyor(61, EnumConveyorType.SLIDER);
    Conveyor SLD62 = new Conveyor(62, EnumConveyorType.SLIDER);
    Conveyor SLD63 = new Conveyor(63, EnumConveyorType.SLIDER);

    /*ASSEMBLEY*/
    Conveyor ASM101 = new Conveyor(101, EnumConveyorType.ASSEMBLY);
    Conveyor ASM102 = new Conveyor(102, EnumConveyorType.ASSEMBLY);
    Conveyor ASM103 = new Conveyor(103, EnumConveyorType.ASSEMBLY);
    Conveyor ASM104 = new Conveyor(104, EnumConveyorType.ASSEMBLY);
    Conveyor ASM105 = new Conveyor(105, EnumConveyorType.ASSEMBLY);
    Conveyor ASM106 = new Conveyor(106, EnumConveyorType.ASSEMBLY);
    Conveyor ASM107 = new Conveyor(107, EnumConveyorType.ASSEMBLY);
    Conveyor ASM108 = new Conveyor(108, EnumConveyorType.ASSEMBLY);

}
