// ReplacementAlgorithm.java

public interface ReplacementAlgorithm {
    int handlePageFault(long pageNumber);
    boolean isDirtyPage(long pageNumber);
    default void updateReferenceBits(PageTableEntry[] RAM) {
        // Default implementation 
    }

    default boolean[] getReferenceBits() {
        // Default implementation 
        return new boolean[0];
    }
}
