import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class TwoLevelPageTable {

    private Map<Integer, Map<Integer, Map <Integer, PageTableEntry>>> pageTable;
    private PageTableEntry[] RAM;
    private int numFrames;
    private Deque<Long> accessQueue;  // Queue to store the order of accesses


    private static int totalPageFaults = 0;
    private static int totalWritesToDisk = 0;
    private static int totalPageTableLeaves = 0;
    private static int totalSizeofPageTable = 1;

    private ReplacementAlgorithm replacementAlgorithm;

    public TwoLevelPageTable(int numFrames, ReplacementAlgorithm algorithm) {
        this.numFrames = numFrames;
        pageTable = new HashMap<>(512);
         accessQueue = new ArrayDeque<>();

        for (int i = 0; i < 512; i++) {
            pageTable.put(i, new HashMap<>());
        }

        RAM = new PageTableEntry[numFrames];
        for (int i = 0; i < numFrames; i++) {
            RAM[i] = new PageTableEntry();
        }

        this.replacementAlgorithm = algorithm;
    }

    public long translate(long virtualAddress) {
        int rootLevelIndex = (int) ((virtualAddress >> 23) & 0x1FF);
        int secondLevelIndex = (int) ((virtualAddress >> 14) & 0x1FF);
        int offset = (int) (virtualAddress & 0x3FFF);

        if (pageTable.containsKey(rootLevelIndex)) {
            Map<Integer, Map<Integer, PageTableEntry>> secondLevelTable = pageTable.get(rootLevelIndex);
            if (secondLevelTable.containsKey(secondLevelIndex)) {
                Map<Integer, PageTableEntry> Page = secondLevelTable.get(secondLevelIndex);
                if (Page.containsKey(offset)) {
                    PageTableEntry PTE = Page.get(offset);
                    if (PTE.isValid()) {
                        // Calculate physical address using the frame and offset (hypothetical since we dont have a ram)
                        int physicalAddress = PTE.getFrame();
                        return physicalAddress;
                    }
                }
            }
        }

        return -1L;
    }

    public void mapPage(long virtualPage, int physicalFrame) {
        int rootLevelIndex = (int) ((virtualPage >> 23) & 0x1FF);
        int secondLevelIndex = (int) ((virtualPage >> 14) & 0x1FF);
        int offset = (int) (virtualPage & 0x3FFF);

        if (!pageTable.containsKey(rootLevelIndex)) {
            pageTable.put(rootLevelIndex, new HashMap<>());
        }

        Map<Integer, Map<Integer, PageTableEntry>> secondLevelTable = pageTable.get(rootLevelIndex);
        if (!secondLevelTable.containsKey(secondLevelIndex)) {
            secondLevelTable.put(secondLevelIndex, new HashMap<>());
            totalPageTableLeaves++;
            totalSizeofPageTable++;
        }

        Map<Integer, PageTableEntry> page = secondLevelTable.get(secondLevelIndex);
        PageTableEntry newEntry = new PageTableEntry();
        newEntry.setFrame(physicalFrame);
        newEntry.setVirtualAddress(physicalFrame);
        newEntry.setValid(true);
        newEntry.setReferenced(true);

        page.put(offset, newEntry);
        accessQueue.offer(virtualPage);
        RAM[physicalFrame].setFrame(physicalFrame);
        RAM[physicalFrame].setVirtualAddress(virtualPage);
        RAM[physicalFrame].setValid(true);
        RAM[physicalFrame].setReferenced(true);
        
    }


    public PageTableEntry getRamEntry(int index) {
        if (index >= 0 && index < numFrames) {
            return RAM[index];
        } else {
            throw new IllegalArgumentException("Invalid index for RAM array");
        }
    }

    public PageTableEntry[] getRAM() {
        return RAM;
    }

    public void updateReferenceBits(boolean[] referencedBits) {
        for (int i = 0; i < RAM.length; i++) {
            RAM[i].setReferenced(referencedBits[i]);
            }
    }
    public void updateRAM(Long[] OptRAM) {
        for (int i = 0; i < OptRAM.length; i++) {
            RAM[i].setVirtualAddress(OptRAM[i]);
            }
    }

   public int handlePageFault(long virtualPage, ReplacementAlgorithm replacementAlgorithm) {
        // Call the replacement algorithm to handle the page fault and get the victim page index
        totalPageFaults++;

        if (replacementAlgorithm instanceof ClockAlgorithm) {
            ClockAlgorithm clockAlgorithm = (ClockAlgorithm) replacementAlgorithm;

            // Update reference bits 
            clockAlgorithm.updateReferenceBits(getRAM());
            replacementAlgorithm = clockAlgorithm;
        }

        if (replacementAlgorithm instanceof OptimalAlgorithm) {
            OptimalAlgorithm optimalAlgorithm = (OptimalAlgorithm) replacementAlgorithm;
            optimalAlgorithm.updateReferenceBits(getRAM());
            replacementAlgorithm = optimalAlgorithm;
        }
        if (replacementAlgorithm instanceof LRUCache) {
            LRUCache lruCache = (LRUCache) replacementAlgorithm;
            lruCache.updateReferenceBits(getRAM());
            accessQueue.offer(virtualPage);
            lruCache.setAccessQueue(accessQueue); 
            replacementAlgorithm = lruCache;
            
        }

        int victimPageIndex = replacementAlgorithm.handlePageFault(virtualPage);

        if (replacementAlgorithm instanceof ClockAlgorithm) {
            this.updateReferenceBits(replacementAlgorithm.getReferenceBits());
        }
    
        if (getRAM()[victimPageIndex].getDirty()){
            totalWritesToDisk++;
        }
        // Map the virtual page to the victim page index
        mapPage(virtualPage, victimPageIndex);
        
        // Update the referenced bit of the victim page
        getRAM()[victimPageIndex].setReferenced(true);

        if (replacementAlgorithm instanceof ClockAlgorithm) {
        replacementAlgorithm.updateReferenceBits(getRAM());
        }

        
        // Return the victim page index
        return victimPageIndex;
    }   

    public void setRamEntry(int index, PageTableEntry entry) {
        if (index >= 0 && index < numFrames) {
            RAM[index] = entry;
        } else {
            throw new IllegalArgumentException("Invalid index for RAM array");
        }
    }

    public static int getTotalWritesToDisk() {
         return totalWritesToDisk;
    }

    public static int getTotalPageTableLeaves() {
        return totalPageTableLeaves;
    }

    public static int getTotalSizeofPageTable() {
        return totalSizeofPageTable;
    }
    public static int getTotalPageFaults() {
        return totalPageFaults;
    }
     public void updateOptimalAlgorithm(OptimalAlgorithm optimalAlgorithm) {
        optimalAlgorithm.updateReferenceBits(getRAM());
    }
}
