package ii.pfc.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import ii.pfc.Factory;
import ii.pfc.route.Route;
import ii.pfc.route.RouteData;
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
        Route route = this.factory.routingManager.traceRoute(new RouteData(null, this.factory.LIN13, this.factory.LIN10));
        System.out.println(route.toString());

        //Route[] routes = this.factory.routingManager.traceRoutes(this.factory.LIN13, this.factory.ASM);
        //System.out.println(Lists.newArrayList(routes));
    }
}
