package ii.pfc.command.impl;

import ii.pfc.command.CommandResponse;
import ii.pfc.part.PartType;
import java.util.ArrayList;
import java.util.List;

public class CommandResponsePartList implements CommandResponse {

    private final List<ResponsePartData> responseList;

    public CommandResponsePartList() {
        this.responseList = new ArrayList<>();
    }

    /*

     */

    private class ResponsePartData {

        private final PartType type;

        private final int quantity;

        public ResponsePartData(PartType type, int quantity) {
            this.type = type;
            this.quantity = quantity;
        }
    }

}
