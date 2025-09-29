package fibheap;

/**
 Compile:
  javac -cp . src/fibheap/FibonacciHeap.java src/fibheap/FibonacciHeapTester.java
 Run:
  java -cp . fibheap.FibonacciHeapTester
  Good luck!
 */
public class FibonacciHeapTester {
    public static void main(String[] args) {
        // 1) insert & findMin
        FibonacciHeap h1 = new FibonacciHeap(2);
        h1.insert(10, "ten");
        h1.insert(5, "five");
        h1.insert(7, "seven");
        if (h1.findMin().key != 5) {
            throw new RuntimeException("insert/findMin test failed");
        }
        System.out.println("insert/findMin OK");

        // 2) size & numTrees
        if (h1.size() != 3 || h1.numTrees() != 3) {
            throw new RuntimeException("size/numTrees test failed");
        }
        System.out.println("size/numTrees OK");

        // 3) deleteMin
        int links = h1.deleteMin();
        if (h1.size() != 2 || h1.findMin().key != 7) {
            throw new RuntimeException("deleteMin test failed");
        }
        System.out.println("deleteMin OK (links=" + links + ")");

        // 4) decreaseKey & cuts: build small tree to force a cut
        FibonacciHeap h2 = new FibonacciHeap(2);
        FibonacciHeap.HeapNode na = h2.insert(1, "one");
        FibonacciHeap.HeapNode nb = h2.insert(2, "two");
        FibonacciHeap.HeapNode nc = h2.insert(3, "three");
        h2.deleteMin();                  // links 2 & 3 into one tree
        int cuts = h2.decreaseKey(nc, 2); // decrease 3->1, should cut once
        if (cuts != 1 || h2.findMin().key != 1) {
            throw new RuntimeException("decreaseKey test failed");
        }
        System.out.println("decreaseKey OK (cuts=" + cuts + ")");

        // 5) delete(x)
        FibonacciHeap hDel = new FibonacciHeap(2);
        FibonacciHeap.HeapNode dx1 = hDel.insert(4, "four");
        FibonacciHeap.HeapNode dx2 = hDel.insert(6, "six");
        int linksDel = hDel.delete(dx2);
        if (hDel.size() != 1 || hDel.findMin().key != 4) {
            throw new RuntimeException("delete(x) test failed");
        }
        System.out.println("delete(x) OK (links=" + linksDel + ")");

        // 6) totalLinks/totalCuts initial
        FibonacciHeap h0 = new FibonacciHeap(3);
        if (h0.totalLinks() != 0 || h0.totalCuts() != 0) {
            throw new RuntimeException("totalLinks/totalCuts initial test failed");
        }
        System.out.println("totalLinks/totalCuts initial OK");

        // 7) meld
        FibonacciHeap ha = new FibonacciHeap(2);
        FibonacciHeap hb = new FibonacciHeap(2);
        ha.insert(100, "100");
        hb.insert(50,  "50");
        hb.insert(150, "150");
        ha.meld(hb);
        if (ha.size() != 3 || ha.findMin().key != 50) {
            throw new RuntimeException("meld test failed");
        }
        System.out.println("meld OK");

        System.out.println("All tests passed!");
    }
}
