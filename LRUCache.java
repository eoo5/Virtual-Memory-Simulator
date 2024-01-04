import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.LinkedList;

public class LRUCache implements ReplacementAlgorithm {
    private Deque<Long> accessOrderQueue; // Queue to maintain the order of page accesses
    private Long[] RAM;

    public LRUCache(int numFrames) {
        this.accessOrderQueue = new LinkedList<Long>();
        this.RAM= new Long[numFrames];
    }

    @Override
    public int handlePageFault(long pageNumber) {
        int frameIndex = findVictimFrame(pageNumber);
        return frameIndex;
    }

    private int findVictimFrame(long pageNumber) {
        long virtualAddress = accessOrderQueue.poll(); // Remove the least recently used page
        int index = getIndexByVirtualAddress(virtualAddress);

        // Add the new page to the queue and return the index
        accessOrderQueue.offer(pageNumber);
        return index;
    }

    private int getIndexByVirtualAddress(long virtualAddress) {
        for (int i = 0; i < RAM.length; i++) {
            if (RAM[i] == null) {
                return i;
            }
        }
        return 0; // Page not found
    }

    @Override
    public boolean isDirtyPage(long pageNumber) {
        return false;
    }

    @Override
    public void updateReferenceBits(PageTableEntry[] newRAM) {
        for (int i = 0; i < newRAM.length; i++) {
            RAM[i] = newRAM[i].getVirtualAddress();
        }
    }

    @Override
    public boolean[] getReferenceBits() {
        return null;
    }
    public void setAccessQueue(Deque<Long> setAccessQueue) {
        this.accessOrderQueue = setAccessQueue;
    }


    public Long[] getRAMBits() {
        return RAM;
    }
    
}
