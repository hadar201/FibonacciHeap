package fibheap;


public class PrintHeap {
    // Print the Fibonacci Heap tree structure starting from a given node
    public static void printFibonacciHeap(FibonacciHeap heap) {
        FibonacciHeap.HeapNode min = heap.min;
        if (min == null) {
            System.out.println("(empty heap)");
            return;
        }
        System.out.println("Fibonacci Heap:");
        FibonacciHeap.HeapNode start = min;
        FibonacciHeap.HeapNode curr = min;
        do {
            printTree(curr, "", true);
            curr = curr.next;
        } while (curr != start);
    }

    // Print a single tree rooted at 'root'
    private static void printTree(FibonacciHeap.HeapNode root, String prefix, boolean isLeft) {
        if (root != null) {
            System.out.println(prefix + (isLeft ? "├── " : "└── ") + root.key);
            if (root.child != null) {
                FibonacciHeap.HeapNode child = root.child;
                FibonacciHeap.HeapNode start = child;
                do {
                    printTree(child, prefix + (isLeft ? "│   " : "    "), true);
                    child = child.next;
                } while (child != start);
            }
        }
    }

    public static void main(String[] args) {
        FibonacciHeap heap = new FibonacciHeap(2);
        heap.insert(3, null);
        heap.insert(4, null);
        FibonacciHeap heap2 = new FibonacciHeap(2);
        heap2.insert(5, null);
        heap2.insert(1, null);
        heap2.insert(9, null);
        heap2.insert(7, null);
        heap2.insert(8, null);
        heap2.insert(10, null);
        heap.meld(heap2);
        heap.deleteMin();
        printFibonacciHeap(heap);
        
        System.out.println(heap.min.key);
        heap.insert(12, null);
        // System.out.println(heap.findMin().next.key);
        heap.insert(13, null);
        printFibonacciHeap(heap);
        System.out.println();
        
        System.out.println(heap.deleteMin());
        
        printFibonacciHeap(heap);
    }
}
 