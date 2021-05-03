package ii.pfc.manager;

import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;

public interface IOrderManager {

    /*

     */

    void checkWarehouseEntries();

    void pollLoadOrders();

    void pollUnloadOrders();

    void pollTransformOrders();

    /*

     */

    void enqueueUnloadOrder(UnloadOrder unloadOrder);

    void enqueueTransformationOrder(TransformationOrder transformationOrder);

}
