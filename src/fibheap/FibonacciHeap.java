
/**
 * FibonacciHeap
 *
 * An implementation of Fibonacci heap over positive integers.
 *
 */
package fibheap;
import java.util.*;

public class FibonacciHeap
{
	public HeapNode min;
	private int n;               // #elements
  	private final int c;         // cut threshold
  	private int totalLinks;      
  	private int totalCuts;
  	private List<HeapNode> degreeTable;
	private int treeCount;
	
	
	/**
	 *
	 * Constructor to initialize an empty heap.
	 * pre: c >= 2.
	 *
	 */
	public FibonacciHeap(int c)
	{
		if (c < 2) throw new IllegalArgumentException();
    	this.c = c;
    	this.treeCount = this.n = this.totalLinks = this.totalCuts = 0;
    	this.min = null;
	}

	/**
	 * 
	 * pre: key > 0
	 *
	 * Insert (key,info) into the heap and return the newly generated HeapNode.
	 *
	 */
	public HeapNode insert(int key, String info) {    

    	HeapNode x = createNode(key, info);
    	insertNodeIntoRootList(x);
		this.n++;
    	return x;
	}

	/**
	 * 
	 * Return the minimal HeapNode, null if empty.
	 *
	 */
	public HeapNode findMin(){
		return this.min;
	}

	/**
	 * 
	 * Delete the minimal item.
	 * Return the number of links.
	 *
	 */
	public int deleteMin() {
		if (min == null) return 0;
		HeapNode z = min;

		// 1) Move all of z’s children into the root list
		moveChildrenToRootList(z);

		// 2) Pick your new min‐candidate BEFORE you remove z
		HeapNode newMinCandidate = z.next;

		// 3) Unhook z from the root list
		removeNodeFromList(z);

		// 4) Decrement size
		n--;

		// 5) If we’re now empty, reset everything
		if (n == 0) {
			min = null;
			treeCount = 0;
			return 0;
		}

		// 6) Otherwise, set min to that candidate and consolidate
		min = newMinCandidate;
		int linksThisOp = consolidate();
		return linksThisOp;
	}


	/**
	 * 
	 * pre: 0<diff<x.key
	 * 
	 * Decrease the key of x by diff and fix the heap.
	 * Return the number of cuts.
	 * 
	 */
	public int decreaseKey(HeapNode x, int diff){    
		if (diff <= 0) {
			throw new IllegalArgumentException("Invalid decrease amount");
		}
		if (diff >= x.key) {
			if (x.key <= 1) {
				return 0;
			}
			diff = x.key - 1;
		}

		// snapshot cuts so we can compute how many happen here
		int cutsBefore = totalCuts;

		// actually decrease the key
		x.key -= diff;

		// update global min if needed
		if (x.key < min.key) {
			min = x;
		}

		// if heap-order is violated, cut and cascade
		HeapNode parent = x.parent;
		if (parent != null && x.key < parent.key) {
			cut(x, parent);
			cascadingCut(parent);
		}

		// return how many cuts were done in this call
		return totalCuts - cutsBefore;
	}

	/**
	 * 
	 * Delete the x from the heap.
	 * Return the number of links.
	 *
	 */
	public int delete(HeapNode x) {    

		if (x == null) {

			return 0;

		}

		if (x == min) {

			return deleteMin();

		}

		if (min == null) {

			return 0;

		}



		if (x.parent != null) {

			HeapNode parent = x.parent;

			cut(x, parent);

			cascadingCut(parent);

		}



		if (x.key > min.key) {

			int targetKey = Math.max(1, min.key);

			int diff = x.key - targetKey;

			if (diff > 0) {

				decreaseKey(x, diff);

			}

		}



		min = x;

		return deleteMin();

	}





	/**
	 * 
	 * Return the total number of links.
	 * 
	 */
	public int totalLinks(){
		return this.totalLinks;
	}


	/**
	 * 
	 * Return the total number of cuts.
	 * 
	 */
	public int totalCuts(){
		return this.totalCuts;
	}


	/**
	 * 
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2){
		if (heap2 == null || heap2.min == null) return;   // nothing to do

		if (this.min == null) {
			// we’re empty → just take all of heap2’s data
			this.min        = heap2.min;
			this.n          = heap2.n;
			this.treeCount  = heap2.treeCount;
			this.totalLinks = heap2.totalLinks;
			this.totalCuts  = heap2.totalCuts;
		} else {
			// splice the two circular root‐lists together
			HeapNode a = this.min.prev;
			HeapNode b = heap2.min.prev;

			a.next          = heap2.min;
			heap2.min.prev  = a;
			b.next          = this.min;
			this.min.prev   = b;

			// merge counters
			this.n          += heap2.n;
			this.treeCount  += heap2.treeCount;
			this.totalLinks += heap2.totalLinks;
			this.totalCuts  += heap2.totalCuts;

			// update min pointer
			if (heap2.min.key < this.min.key) {
				this.min = heap2.min;
			}
		}

		// --- clear heap2 so it has no references to any nodes ---
		heap2.min         = null;
		heap2.n           = 0;
		heap2.treeCount   = 0;
		heap2.totalLinks  = 0;
		heap2.totalCuts   = 0;
		heap2.degreeTable = null;  		
	}

	/**
	 * 
	 * Return the number of elements in the heap
	 *   
	 */
	public int size(){
		return this.n;
	}


	/**
	 * 
	 * Return the number of trees in the heap.
	 * 
	 */
	public int numTrees(){
		return this.treeCount;
	}


	private HeapNode createNode(int key, String info) {
    	return new HeapNode(key, info);
	}

	private void insertNodeIntoRootList(HeapNode x) {
		if (min == null) {
			// first node in heap
			min = x;
		} 
		else {
			// splice x into the circular root list, just before min
			x.next = min;
			x.prev = min.prev;
			min.prev.next = x;
			min.prev = x;
			// if x is new minimum, update pointer
			if (x.key < min.key) {
				min = x;
			}
		}
		// you’ll need a field `treeCount` to track size:
		this.treeCount++;
	}



	private void removeNodeFromList(HeapNode x) {
		x.prev.next = x.next;
		x.next.prev = x.prev;
		x.next = x.prev = x;
	}

		/**
	 * Detach every child of z and splice each one into the root list.
	 * Clears z.child, and for each moved child: parent→null.
	 */
	private void moveChildrenToRootList(HeapNode z) {
		if (z.child == null) return;
		HeapNode start = z.child;
		HeapNode curr  = start;
		do {
			HeapNode next = curr.next;
			// unlink from child-list
			removeNodeFromList(curr);
			// reset parent pointer
			curr.parent = null;
			// splice into roots
			insertNodeIntoRootList(curr);
			curr = next;
		} while (curr != start);
		z.child = null;
	}

		/**
	 * Make y a child of x (x.key ≤ y.key).  
	 * – remove y from root list,  
	 * – set y.parent = x, splice y into x.child list,  
	 * – x.rank++, y.lostChildren = 0,  
	 * – increment both the global totalLinks counter.
	 */
	private void link(HeapNode y, HeapNode x) {
		// 1) remove y from root list
		removeNodeFromList(y);
		// 2) set parent and splice into x.child
		y.parent = x;
		if (x.child == null) {
			x.child = y;
			y.next = y.prev = y;
		} else {
			// insert before existing child
			y.next = x.child;
			y.prev = x.child.prev;
			x.child.prev.next = y;
			x.child.prev = y;
		}
		// 3) update ranks & counters
		x.rank++;
		y.lostChildren = 0;
		totalLinks++;
	}



		/**
	 * “Degree‐collision” pass after deleteMin:
	 *   - link together any two roots of equal rank,
	 *   - rebuild the root list,
	 *   - update 'min' & 'treeCount'.
	 * Returns how many links were performed in this pass.
	 */
	private int consolidate() {
		int maxD = (int)(Math.log(n) / Math.log((1 + Math.sqrt(5)) / 2)) + 1;
		int needed = Math.max(1, maxD + 1);
		ensureDegreeTableSize(needed);
		if (!degreeTable.isEmpty()) {
			Collections.fill(degreeTable, null);
		}

		List<HeapNode> roots = new ArrayList<>();
		HeapNode w = min;
		if (w != null) {
			HeapNode stop = min;
			do {
				roots.add(w);
				w = w.next;
			} while (w != stop);
		}

		treeCount = 0;
		min = null;
		int linksThisOp = 0;

		for (HeapNode x : roots) {
			if (x == null) {
				continue;
			}
			x.next = x.prev = x;
			int d = x.rank;
			ensureDegreeTableSize(d + 1);
			while (degreeTable.get(d) != null) {
				HeapNode y = degreeTable.get(d);
				degreeTable.set(d, null);
				if (x.key > y.key) {
					HeapNode tmp = x; x = y; y = tmp;
				}
				link(y, x);
				linksThisOp++;
				d = x.rank;
				ensureDegreeTableSize(d + 1);
			}
			degreeTable.set(d, x);
		}

		for (int i = 0; i < degreeTable.size(); i++) {
			HeapNode r = degreeTable.get(i);
			if (r != null) {
				r.next = r.prev = r;
				insertNodeIntoRootList(r);
			}
		}
		return linksThisOp;
	}




	//    Cut x from its parent’s child list and move it to the root list.
	//    Updates totalCuts and treeCount via insertNodeIntoRootList.
	private void cut(HeapNode x, HeapNode parent) {
		// 1) unlink x from parent’s child list
		removeNodeFromList(x);
		if (parent.child == x) {
			parent.child = (x.next != x) ? x.next : null;
		}
		parent.rank--;
		
		// 2) move x to root list
		x.parent = null;
		insertNodeIntoRootList(x);

		// 3) reset its “lostChildren” counter
		x.lostChildren = 0;

		// 4) record the cut
		totalCuts++;
	}

	//If a node y has lost ≥ c children, cut it too (and recurse upwards).
	private void cascadingCut(HeapNode y) {
		HeapNode parent = y.parent;
		if (parent != null) {
			y.lostChildren++;
			if (y.lostChildren >= c) {
				cut(y, parent);
				cascadingCut(parent);
			}
		}
	}

		/**
	 * Ensure degreeTable.size() ≥ needed.
	 * Extends with nulls if too small.
	 */
	private void ensureDegreeTableSize(int needed) {
		if (degreeTable == null) {
			degreeTable = new ArrayList<>(needed);
		}
		// add nulls up to needed
		for (int i = degreeTable.size(); i < needed; i++) {
			degreeTable.add(null);
		}
	}




	/**
	 * Class implementing a node in a Fibonacci Heap.
	 *  
	 */
	public static class HeapNode{
		public int key;
		public String info;
		public HeapNode child;
		public HeapNode next;
		public HeapNode prev;
		public HeapNode parent;
		public int rank;
		int lostChildren;

		public HeapNode(int key, String info){
			this.key = key;
        	this.info = info;
        	this.child = null;
        	this.parent = null;
        	this.rank = 0;
        	this.lostChildren = 0;
			this.next = this;
        	this.prev = this;

		}
	}


}
