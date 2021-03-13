package ii.pfc.command.impl;

import ii.pfc.command.CommandResponse;
import ii.pfc.order.TransformationOrder;
import ii.pfc.part.PartType;
import java.util.ArrayList;
import java.util.List;

public class CommandResponseOrderList implements CommandResponse {

    private final List<TransformationOrder> responseList;

    public CommandResponseOrderList() {
        this.responseList = new ArrayList<>();
    }

}
