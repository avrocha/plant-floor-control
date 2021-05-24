package ii.pfc.gui;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ii.pfc.conveyor.Conveyor;
import ii.pfc.conveyor.EnumConveyorType;
import ii.pfc.manager.IDatabaseManager;
import ii.pfc.manager.IRoutingManager;
import ii.pfc.part.Part;
import ii.pfc.part.PartType;
import org.apache.commons.lang3.compare.ObjectToStringComparator;

import java.util.ArrayList;
import java.util.UUID;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;


public class GUI extends JFrame {

    private final IDatabaseManager databaseManager;

    private final IRoutingManager routingManager;

    /*

     */
    private Object[][] dataAssemblers;

    private Object[][] dataParts;

    private Object[][] dataUnloadedParts;

    private String[] unloadInfo;

    private JTable assemblersTable;

    public GUI(IDatabaseManager databaseManager, IRoutingManager routingManager) {
        super("Interface");

        this.databaseManager = databaseManager;
        this.routingManager = routingManager;
    }

    public void updateAssemblesTable() {
        int assemblerConveyorIndex = 0;

        Collection<PartType> partTypes = PartType.getTypes();
        Collection<Conveyor> assemblerConveyors = routingManager.getConveyors(EnumConveyorType.ASSEMBLY);

        for(Conveyor assemblerConveyor : assemblerConveyors) {
            dataAssemblers[assemblerConveyorIndex][0] = String.format("Assembler %d", assemblerConveyorIndex + 1);

            Map<PartType, Duration> processDurations = databaseManager.fetchProcessDurations(assemblerConveyor.getId());

            Duration totalValue = Duration.ZERO;
            for (PartType type : partTypes) {
                Duration value = processDurations.getOrDefault(type, Duration.ZERO);
                totalValue = totalValue.plus(value);

            }

            dataAssemblers[assemblerConveyorIndex][1] = totalValue.toString();

            int parts = databaseManager.countProcessedParts(assemblerConveyor.getId());

            dataAssemblers[assemblerConveyorIndex][2] = parts;

            assemblerConveyorIndex++;

        }
    }

    public void updateUnloadedPartsTable () {
        Collection<PartType> partTypes = PartType.getTypes();
        Collection<Conveyor> sliderConveyors = routingManager.getConveyors(EnumConveyorType.SLIDER);

        unloadInfo = new String[1 + sliderConveyors.size() + 1];

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
    }

    public void updateInventoryTable() {
        Collection<PartType> partTypes = PartType.getTypes();
        Part.PartState[] partStates = Part.PartState.values();

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
    }

    public void init() {
        try {
            int width = 1000;
            int height = 1000;

            JTabbedPane tp=new JTabbedPane();

            JLabel tab1Title = new JLabel("Assemblers Stats");
            JLabel tab2Title = new JLabel("Unloaded Parts");
            JLabel tab3Title = new JLabel("Inventory");
            JLabel tab4Title = new JLabel("Admin");

            JPanel p1=new JPanel();
            JPanel p2=new JPanel();
            JPanel p3=new JPanel();
            JPanel p4=new JPanel();

            Part.PartState[] partStates = Part.PartState.values();
            Collection<PartType> partTypes = PartType.getTypes();


        /*
            Info table about Assemblers
         */

            // Assembler   | Operation time | Number of operated parts
            // Assembler 1 |        0       |        0
            // ...

            Collection<Conveyor> assemblerConveyors = routingManager.getConveyors(EnumConveyorType.ASSEMBLY);

            String assemblers[] = {"Assembler", "Operation time", "Number of operated parts"};

            dataAssemblers = new Object[assemblerConveyors.size()][assemblers.length];

            assemblersTable= new JTable(dataAssemblers, assemblers){
                @Override
                public boolean editCellAt(int row, int column, EventObject event) {
                    return false;
                }
            };
            assemblersTable.setRowSelectionAllowed(false);
            updateAssemblesTable();


        /*
           Info table about Unloading Parts
         */

            // Type | Slider 1 | Slider 2 | ... | Total
            // P1   |        0 |        1 | ... | 1
            // ...

            Collection<Conveyor> sliderConveyors = routingManager.getConveyors(EnumConveyorType.SLIDER);

            unloadInfo = new String[1 + sliderConveyors.size() + 1];

            unloadInfo[0] = "Type";

            for(int i = 0; i < sliderConveyors.size(); i++) {
                unloadInfo[1 + i] = String.format("Slider %d", i + 1);
            }

            unloadInfo[unloadInfo.length - 1] = "Total";

            dataUnloadedParts = new Object[partTypes.size()][1 + sliderConveyors.size() + 1];

            JTable unloadedPartsTable = new JTable(dataUnloadedParts, unloadInfo){
                @Override
                public boolean editCellAt(int row, int column, EventObject event) {
                    return false;
                }
            };
            unloadedPartsTable.setRowSelectionAllowed(false);
            updateUnloadedPartsTable();

        /*
            Info table about Inventory
         */

            // Type | Stored | Processing | ... | Completed
            // P1   |      0 |        1   | ... | 0
            //  ...

            String[] partHeaders = new String[1 + partStates.length];
            partHeaders[0] = "Type";

            for(int i = 0; i < partStates.length; i++) {
                partHeaders[i + 1] = partStates[i].getDisplayName();
            }

            Collection<Part> parts = databaseManager.fetchParts();
            Map<Part.PartState, Multimap<PartType, Part>> stateMap = new HashMap<>();


            JTable inventoryTable= new JTable(dataParts, partHeaders){
                @Override
                public boolean editCellAt(int row, int column, EventObject event) {
                    return false;
                }
            };
            inventoryTable.setRowSelectionAllowed(false);
            //updateInventoryTable();


        /*
            BUTTONS AND COMMANDS
         */
            JButton inventoryClean=new JButton("Clean Inventory");
            inventoryClean.setBounds(300,300,300, 90);
            inventoryClean.addActionListener(e -> {
                databaseManager.clearAllParts();
            });

            JButton inventoryPopulate=new JButton("Populate Inventory");
            inventoryPopulate.setBounds(300,300,300, 90);
            inventoryPopulate.addActionListener(e -> {
                Map<PartType, Integer> populateMap = new HashMap<>();
                populateMap.put(PartType.PART_1, 400);
                populateMap.put(PartType.PART_2, 40);
                populateMap.put(PartType.PART_3, 20);
                populateMap.put(PartType.PART_4, 20);
                populateMap.put(PartType.PART_5, 20);
                populateMap.put(PartType.PART_6, 20);

                Collection<Part> partsBatch = new ArrayList<>();
                for (Map.Entry<PartType, Integer> entry : populateMap.entrySet()) {
                    for (int j = 0; j < entry.getValue(); j++) {
                        Part part = new Part(
                                UUID.randomUUID(),
                                0,
                                entry.getKey(),
                                Part.PartState.STORED);

                        partsBatch.add(part);
                    }
                }

                databaseManager.insertParts(partsBatch);
            });

            JButton transformOrdersClean=new JButton("Clean Transform Orders");
            transformOrdersClean.setBounds(300,300,300, 90);
            transformOrdersClean.addActionListener(e -> {
                databaseManager.clearAllTransformOrders();
            });

            JButton unloadingOrdersClean=new JButton("Clean Unloading Orders");
            unloadingOrdersClean.setBounds(300,300,300, 90);
            unloadingOrdersClean.addActionListener(e -> {
                databaseManager.clearAllUnloadOrders();
            });


        /*
            REFRESH BUTTONS
         */
            JButton refreshButtonP1 = new JButton(("Refresh"));
            refreshButtonP1.setBounds(600,600,400,400);
            refreshButtonP1.addActionListener(e -> {
                updateAssemblesTable();
            });

            JButton refreshButtonP2= new JButton(("Refresh"));
            refreshButtonP2.setBounds(300,300,400,400);
            refreshButtonP2.addActionListener(e -> {
                updateUnloadedPartsTable();
            });
            JButton refreshButtonP3 = new JButton(("Refresh"));
            refreshButtonP3.setBounds(300,300,400,400);
            refreshButtonP3.addActionListener(e -> {
                updateInventoryTable();
            });


        /*
            SETTING PANELS LAYOUTS
         */
            p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
            p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));
            p3.setLayout(new BoxLayout(p3, BoxLayout.PAGE_AXIS));
            p4.setLayout(new BoxLayout(p4, BoxLayout.PAGE_AXIS));


        /*
            ADDING TO PANELS TABLES AND BUTTONS
         */
            p1.add(tab1Title);
            p1.add(Box.createRigidArea(new Dimension(2,10)));
            p1.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
            p1.add(assemblersTable.getTableHeader());
            p1.add(assemblersTable);
            p1.add(refreshButtonP1);

            p2.add(tab2Title);
            p2.add(Box.createRigidArea(new Dimension(2,10)));
            p2.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
            p2.add(unloadedPartsTable.getTableHeader());
            p2.add(unloadedPartsTable);
            p2.add(refreshButtonP2);

            /*p3.add(tab3Title);
            p3.add(Box.createRigidArea(new Dimension(2,10)));
            p3.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
            p3.add(inventoryTable.getTableHeader());
            p3.add(inventoryTable);
            p3.add(refreshButtonP3);*/

            p4.add(tab4Title);
            p4.add(Box.createRigidArea(new Dimension(2,10)));
            p4.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
            p4.add(inventoryClean);
            p4.add(inventoryPopulate);
            p4.add(transformOrdersClean);
            p4.add(unloadingOrdersClean);


        /*
            ADDING TO PANE THE TABS
         */
            tp.add("Assemblers", p1);
            tp.add("Unloaded parts",p2);
            tp.add("Inventory", p3);
            tp.add("Admin", p4);

            this.add(tp);
            this.setSize(width,height);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

}
