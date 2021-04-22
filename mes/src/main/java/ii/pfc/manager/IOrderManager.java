package ii.pfc.manager;

import ii.pfc.order.LoadOrder;
import ii.pfc.order.TransformationOrder;
import ii.pfc.order.UnloadOrder;

import java.util.Collection;

public interface IOrderManager {

    Collection<LoadOrder> pollLoadOrders();

    Collection<UnloadOrder> pollUnloadOrders();

    Collection<TransformationOrder> pollTransformOrders();

    /*

     */

    void enqueueLoadOrder(LoadOrder loadOrder);

    void enqueueUnloadOrder(UnloadOrder unloadOrder);

    void enqueueTransformationOrder(TransformationOrder transformationOrder);

}
