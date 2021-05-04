package ii.pfc.gui;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IRoutingManager;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;


public class GUI extends JFrame {

    private final IDatabaseManager databaseManager;

    private final IRoutingManager routingManager;

    public GUI(IDatabaseManager databaseManager, IRoutingManager routingManager) {
        super("Interface");

        this.databaseManager = databaseManager;
        this.routingManager = routingManager;
    }

    public void init() {
        int width = 600;
        int height = 600;

        JLabel tab1Title = new JLabel("Assemblers Stats");
        JLabel tab2Title = new JLabel("Unloaded Parts");
        JLabel tab3Title = new JLabel("Inventory");

        JPanel p1=new JPanel();
        JPanel p2=new JPanel();
        JPanel p3=new JPanel();

        p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
        p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));
        p3.setLayout(new BoxLayout(p3, BoxLayout.PAGE_AXIS));

        Part.PartState[] partStates = Part.PartState.values();
        Collection<PartType> partTypes = PartType.getTypes();

        /*
            Info table about Assemblers
         */

        String assemblers[] = {"Assembler", "Operation time", "Number of operated parts"};

        Object[][] dataAssemblers = {
                {"Assembler 1", 0, 0},
                {"Assembler 2", 0, 0},
                {"Assembler 3", 0, 0},
                {"Assembler 4", 0, 0},
                {"Assembler 5", 0, 0},
                {"Assembler 6", 0, 0},
                {"Assembler 7", 0, 0},
                {"Assembler 8", 0, 0}};

        JTable assemblersTable= new JTable(dataAssemblers, assemblers){
            @Override
            public boolean editCellAt(int row, int column, EventObject event) {
                return false;
            }
        };
        assemblersTable.setRowSelectionAllowed(false);

        /*
           Info table about Unloading Parts
         */


        // Type | Slider 1 | Slider 2 | ... | Total
        // P1   |        0 |        1 | ... | 1

        Collection<Conveyor> sliderConveyors = routingManager.getConveyors(EnumConveyorType.SLIDER);
        String[] unloadInfo = new String[1 + sliderConveyors.size() + 1];
        unloadInfo[0] = "Type";

        for(int i = 0; i < sliderConveyors.size(); i++) {
            unloadInfo[1 + i] = String.format("Slider %d", i + 1);
        }

        unloadInfo[unloadInfo.length - 1] = "Total";

        Object[][] dataUnloadedParts = new Object[partTypes.size()][1 + sliderConveyors.size() + 1];

        int typeIndex = 0;
        for(PartType type : partTypes) {
            dataUnloadedParts[typeIndex][0] = type.getName();
            dataUnloadedParts[typeIndex][unloadInfo.length - 1] = 0;

            typeIndex++;
        }

        int sliderConveyorIndex = 0;
        for(Conveyor sliderConveyor : sliderConveyors) {
            Map<PartType, Integer> unloadedParts = databaseManager.countUnloadedParts(sliderConveyor.getId());

            typeIndex = 0;
            for (PartType type : partTypes) {
                int amount = unloadedParts.getOrDefault(type, 0);
                dataUnloadedParts[typeIndex][1 + sliderConveyorIndex] = amount;
                dataUnloadedParts[typeIndex][unloadInfo.length - 1] = ((int) dataUnloadedParts[typeIndex][unloadInfo.length - 1]) + amount;
                typeIndex++;
            }

            sliderConveyorIndex++;
        }

        JTable unloadedPartsTable = new JTable(dataUnloadedParts, unloadInfo){
            @Override
            public boolean editCellAt(int row, int column, EventObject event) {
                return false;
            }
        };
        unloadedPartsTable.setRowSelectionAllowed(false);

        /*
            Info table about Inventory
         */

        String[] partHeaders = new String[1 + partStates.length];
        partHeaders[0] = "Type";

        for(int i = 0; i < partStates.length; i++) {
            partHeaders[i + 1] = partStates[i].getDisplayName();
        }

        Collection<Part> parts = databaseManager.fetchParts();
        Map<Part.PartState, Multimap<PartType, Part>> stateMap = new HashMap<>();

        for(Part part : parts) {
            Part.PartState state = part.getState();
            Multimap<PartType, Part> typeMap = stateMap.get(state);

            if (typeMap == null) {
                typeMap = HashMultimap.create();
                stateMap.put(state, typeMap);
            }

            typeMap.put(part.getType(), part);
        }

        Object[][] dataParts = new Object[partTypes.size()][1 + partStates.length];

        int i = 0;
        for(PartType type : partTypes) {
            dataParts[i][0] = type.getName();

            for(int j = 0; j < partStates.length; j++) {
                if (stateMap.containsKey(partStates[j])) {
                    dataParts[i][j + 1] = stateMap.get(partStates[j]).get(type).size();
                } else {
                    dataParts[i][j + 1] = 0;
                }
            }

            i++;
        }

        JTable inventoryTable= new JTable(dataParts, partHeaders){
            @Override
            public boolean editCellAt(int row, int column, EventObject event) {
                return false;
            }
        };
        inventoryTable.setRowSelectionAllowed(false);

        JTabbedPane tp=new JTabbedPane();

        tp.add("Assemblers",p1);
        tp.add("Unloaded parts",p2);
        tp.add("Inventory", p3);

        p1.add(tab1Title);
        p1.add(Box.createRigidArea(new Dimension(2,10)));
        p1.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        p1.add(assemblersTable.getTableHeader());
        p1.add(assemblersTable);

        p2.add(tab2Title);
        p2.add(Box.createRigidArea(new Dimension(2,10)));
        p2.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        p2.add(unloadedPartsTable.getTableHeader());
        p2.add(unloadedPartsTable);

        p3.add(tab3Title);
        p3.add(Box.createRigidArea(new Dimension(2,10)));
        p3.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        p3.add(inventoryTable.getTableHeader());
        p3.add(inventoryTable);

        this.add(tp);
        this.setSize(width,height);
    }

}
