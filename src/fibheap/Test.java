package fibheap;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        testEmptyHeap();
        testBasicInsertAndFindMin();
        testDeleteMinAndLinks();
        testDecreaseKeyAndCuts();
        testDeleteNode();
        testMeld();
        testVaryingC();
    }

    private static void testEmptyHeap() {
        System.out.println("=== testEmptyHeap ===");
        FibonacciHeap h = new FibonacciHeap(2);
        assert h.findMin() == null;
        assert h.size() == 0;
        assert h.numTrees() == 0;
        assert h.totalLinks() == 0;
        assert h.totalCuts() == 0;
        System.out.println("OK\n");
    }

    private static void testBasicInsertAndFindMin() {
        System.out.println("=== testBasicInsertAndFindMin ===");
        FibonacciHeap h = new FibonacciHeap(2);
        h.insert(10, "A");
        h.insert(5,  "B");
        h.insert(20, "C");
        assert h.findMin().key == 5;
        assert h.size() == 3;
        assert h.numTrees() == 3;
        System.out.println("OK\n");
    }

    private static void testDeleteMinAndLinks() {
        System.out.println("=== testDeleteMinAndLinks ===");
        FibonacciHeap h = new FibonacciHeap(2);
        h.insert(1,"");
        h.insert(2,"");
        h.insert(3,"");
        // deleteMin should link nothing (all degree 0)
        int links = h.deleteMin();
        assert links == 0;
        assert h.findMin().key == 2;
        assert h.size() == 2;
        System.out.println("OK\n");
    }

    private static void testDecreaseKeyAndCuts() {
        System.out.println("=== testDecreaseKeyAndCuts ===");
        // build a small heap with one child
        FibonacciHeap h = new FibonacciHeap(2);
        FibonacciHeap.HeapNode a = h.insert(10, "");
        FibonacciHeap.HeapNode b = h.insert(20, "");
        h.deleteMin();            // removes 10, b becomes root
        // insert a child under b
        FibonacciHeap.HeapNode c = h.insert(15, "");
        h.deleteMin();            // link c under b
        // now decrease c so it cuts from b
        int cuts = h.decreaseKey(c, 1); // 15->14, violates parent
        assert cuts == 1;
        assert h.totalCuts() == 1;
        System.out.println("OK\n");
    }

    private static void testDeleteNode() {
        System.out.println("=== testDeleteNode ===");
        FibonacciHeap h = new FibonacciHeap(2);
        List<FibonacciHeap.HeapNode> nodes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            nodes.add(h.insert(i, ""));
        }
        // delete the middle node (key=3)
        FibonacciHeap.HeapNode toDelete = nodes.get(2);
        int links = h.delete(toDelete);
        assert h.size() == 4;
        assert h.findMin().key == 1;
        assert links >= 0;
        System.out.println("OK\n");
    }

    private static void testMeld() {
        System.out.println("=== testMeld ===");
        FibonacciHeap h1 = new FibonacciHeap(2);
        FibonacciHeap h2 = new FibonacciHeap(2);
        h1.insert(1, "A");
        h1.insert(4, "B");
        h2.insert(2, "C");
        h2.insert(3, "D");
        h1.meld(h2);
        // after meld, h2 should be empty:
        assert h2.size() == 0;
        assert h2.findMin() == null;
        // h1 should contain [1,2,3,4], min = 1
        assert h1.size() == 4;
        assert h1.findMin().key == 1;
        System.out.println("OK\n");
    }

    private static void testVaryingC() {
        System.out.println("=== testVaryingC ===");
        int[] cs = {2,3,5,10};
        for (int c : cs) {
            FibonacciHeap h = new FibonacciHeap(c);
            int n = 1000;
            for (int i = n; i >= 1; i--) 
                h.insert(i, "");
            // n random decreaseKey ops
            Random rnd = new Random(0);
            for (int i = 0; i < 100; i++) {
                // pick a non-root
                FibonacciHeap.HeapNode x = h.findMin().next; 
                h.decreaseKey(x, 1);
            }
            // some deleteMins
            for (int i = 0; i < 100; i++) 
                h.deleteMin();
            System.out.println("c=" + c 
                + " size=" + h.size() 
                + " totalLinks=" + h.totalLinks() 
                + " totalCuts=" + h.totalCuts() 
                + " numTrees=" + h.numTrees());
        }
        System.out.println("OK\n");
    }
}
