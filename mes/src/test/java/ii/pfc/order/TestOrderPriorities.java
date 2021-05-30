package ii.pfc.order;

import com.google.common.collect.Sets;
import ii.pfc.part.PartType;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import junit.framework.TestCase;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.junit.Test;

public class TestOrderPriorities extends TestCase {

    @Test
    public void testUnload() {
        // Unload
        // 1: REMAINING
        // 2: DATE

        UnloadOrder test1 = new UnloadOrder(0, PartType.PART_1, (short) 0, LocalDateTime.now(), 10, 10, 0);
        UnloadOrder test2 = new UnloadOrder(0, PartType.PART_1, (short) 0, LocalDateTime.now(), 12, 12, 0);

        //
        assertEquals(Collections.max(Sets.newHashSet(test1, test2)), test2);

        test1 = new UnloadOrder(0, PartType.PART_1, (short) 0, LocalDateTime.now(), 15, 15, 0);
        test2 = new UnloadOrder(0, PartType.PART_1, (short) 0, LocalDateTime.now(), 10, 10, 0);

        //
        assertEquals(Collections.max(Sets.newHashSet(test1, test2)), test1);

        test1 = new UnloadOrder(0, PartType.PART_1, (short) 0, LocalDateTime.now().minusSeconds(10), 10, 10, 0);
        test2 = new UnloadOrder(0, PartType.PART_1, (short) 0, LocalDateTime.now(), 10, 10, 0);

        //
        assertEquals(Collections.max(Sets.newHashSet(test1, test2)), test1);

        test1 = new UnloadOrder(0, PartType.PART_1, (short) 0, LocalDateTime.now().minusSeconds(10), 10, 10, 0);
        test2 = new UnloadOrder(0, PartType.PART_1, (short) 0, LocalDateTime.now(), 10, 15, 0);

        //
        assertEquals(Collections.max(Sets.newHashSet(test1, test2)), test2);
    }

    @Test
    public void testTransform() {
        // Transform
        // 1 : CPENALTY
        // 2 : REMAINING
        // 3 : PENALTY
        // 4 : DEADLINE

        TransformationOrder test1 = new TransformationOrder(0, PartType.PART_1, PartType.PART_2, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 10, 10, 0, 0, LocalDateTime.now().minusSeconds(55), 22);
        TransformationOrder test2 = new TransformationOrder(0, PartType.PART_1, PartType.PART_2, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 10, 10, 0, 0, LocalDateTime.now().minusSeconds(105), 10);

        assertEquals(Collections.max(Sets.newHashSet(test1, test2)), test1);

        //

        test1 = new TransformationOrder(0, PartType.PART_1, PartType.PART_2, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 10, 10, 0, 0, LocalDateTime.now().plusSeconds(10), 10);
        test2 = new TransformationOrder(0, PartType.PART_1, PartType.PART_2, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 10, 15, 0, 0, LocalDateTime.now().plusSeconds(10), 10);

        assertEquals(Collections.max(Sets.newHashSet(test1, test2)), test2);

        //

        test1 = new TransformationOrder(0, PartType.PART_1, PartType.PART_2, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 10, 10, 0, 0, LocalDateTime.now().plusSeconds(10), 10);
        test2 = new TransformationOrder(0, PartType.PART_1, PartType.PART_2, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 10, 10, 0, 0, LocalDateTime.now().plusSeconds(10), 15);

        assertEquals(Collections.max(Sets.newHashSet(test1, test2)), test2);

        //

        test1 = new TransformationOrder(0, PartType.PART_1, PartType.PART_2, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 10, 10, 0, 0, LocalDateTime.now().plusSeconds(10), 10);
        test2 = new TransformationOrder(0, PartType.PART_1, PartType.PART_2, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), 10, 10, 0, 0, LocalDateTime.now().plusSeconds(15), 10);

        assertEquals(Collections.max(Sets.newHashSet(test1, test2)), test1);

        //
    }

}
