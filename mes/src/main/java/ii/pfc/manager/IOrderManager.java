package ii.pfc.manager;

import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;

public interface IOrderManager {

    void pollUnloadOrders();

    void pollTransformOrders();

    /*

     */

    void enqueueUnloadOrder(UnloadOrder unloadOrder);

    void enqueueTransformationOrder(TransformationOrder transformationOrder);

}
