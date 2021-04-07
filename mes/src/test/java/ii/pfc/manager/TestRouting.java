package ii.pfc.manager;

import ii.pfc.Factory;
import ii.pfc.route.Route;
import junit.framework.TestCase;
import org.junit.Test;

public class TestRouting extends TestCase {

    private Factory factory;

    @Override
    protected void setUp() throws Exception {
        this.factory = new Factory();
    }

    @Test
    public void testRoutes() {
        Route route = this.factory.routingManager.traceRoute(null, this.factory.LIN13, this.factory.LIN10);
        System.out.println(route.toString());

        //Route[] routes = this.factory.routingManager.traceRoutes(this.factory.LIN13, this.factory.ASM);
        //System.out.println(Lists.newArrayList(routes));
    }
}
