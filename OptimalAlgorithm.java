import java.util.List;

public class OptimalAlgorithm implements ReplacementAlgorithm {
    private List<Long> futureAccesses; // List to store future memory accesses
    private Long[] RAM;     // Array to store PageTableEntry instances

    public OptimalAlgorithm(List<Long>futureAccesses, int numFrames) {
        this.futureAccesses = futureAccesses;
        this.RAM = new Long[numFrames];
    }


    @Override
    public int handlePageFault(long pageNumber) {
        int frameIndex = findVictimFrame(pageNumber);
        return frameIndex;
    }

    private int findVictimFrame(long pageNumber) {
    int farthestAccessIndex = Integer.MIN_VALUE;
    int index = 0;
    
    for (int i = 0; i < RAM.length; i++) {
        long virtualAddress = RAM[i];

        if (!isPageInFuture(virtualAddress)) {
            return i; // Page is not gonna be called, kick it out
        } else {
            int accessIndex = futureAccesses.indexOf(virtualAddress);

            // If the accessIndex is greater than the current farthestAccessIndex, update the values
            if (accessIndex > farthestAccessIndex) {
                farthestAccessIndex = accessIndex;
                index = i; // Update the index to the current RAM index
            }
        }
    }

    return index;
}

    private boolean isPageInFuture(long pageNumber) {
        return futureAccesses.contains(pageNumber);
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


}
