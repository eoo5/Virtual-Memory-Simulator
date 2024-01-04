public class ClockAlgorithm implements ReplacementAlgorithm {
    private int hand; // Clock hand pointing to the next frame to consider for replacement
    private boolean[] referencedBits; // Array to store referenced bits

    public ClockAlgorithm(int numFrames) {
        this.hand = 0;
        this.referencedBits = new boolean[numFrames];
    }

    @Override
    public int handlePageFault(long pageNumber) {
        
        int frameIndex = findVictimFrame();
        referencedBits[frameIndex] = true; // Update referenced bit in the array
        return frameIndex;
    }

    @Override
    public boolean isDirtyPage(long pageNumber) {
        // Check if the page is dirty based on the array of referenced bits
        int frameIndex = (int) pageNumber;
        return referencedBits[frameIndex];
    }

    private int findVictimFrame() {
        while (true) {
            if (!referencedBits[hand]) {
                int victimFrame = hand;
                hand = (hand + 1) % referencedBits.length;
                return victimFrame;
            } else {
                referencedBits[hand] = false;
                hand = (hand + 1) % referencedBits.length;
            }
        }
    }
    @Override
    public void updateReferenceBits(PageTableEntry[] RAM) {
        for (int i = 0; i < RAM.length; i++) {
            if (RAM[i].isValid()) {
                referencedBits[i] = RAM[i].getReferenced();
            }
        }
    }
    @Override
    public boolean[] getReferenceBits() {
            return referencedBits;
    }
}
