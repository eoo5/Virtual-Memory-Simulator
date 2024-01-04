/**
 * Represents a two-level page table entry.
 */
public class PageTableEntry {

    // Fields
    private int frame;
    private long virtualAddress;
    private boolean valid;
    private boolean dirty;
    private boolean referenced;

    // Constructors
    /**
     * Constructs a new PageTableEntry with default values.
     */
    public PageTableEntry() {
        this.frame = -1;
        this.virtualAddress = -1;
        this.valid = false;  // Set as invalid by default
        this.dirty = false;
        this.referenced = false;
    }

    /**
     * Constructs a new PageTableEntry by copying another entry.
     *
     * @param cp The PageTableEntry to copy.
     */
    public PageTableEntry(PageTableEntry cp) {
        this.frame = cp.frame;
        this.virtualAddress = cp.virtualAddress;
        this.valid = cp.valid;
        this.dirty = cp.dirty;
        this.referenced = cp.referenced;
    }

    // Accessor Methods
    /**
     * Checks if the page table entry is valid.
     *
     * @return True if the entry is valid, false otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Sets the validity of the page table entry.
     *
     * @param valid True to mark the entry as valid, false to mark it as invalid.
     */
    public void setValid(boolean valid) {
        this.valid = valid;

    }

     /**
     * Gets the reference bit associated with the page table entry.
     *
     * @return The bit number.
     */
    public boolean getReferenced() {
        return referenced;
    }

     /**
     * Sets the reference of the page table entry.
     *
     * @param valid True to mark the entry as reference, false to mark it as reference.
     */
    public void setReferenced(boolean reference) {
        this.referenced = reference;
    }
    
    /**
     * Gets the dirty bit associated with the page table entry.
     *
     * @return The frame number.
     */
    public boolean getDirty() {
        return dirty;
    }

    /**
     * Gets the dirty bit associated with the page table entry.
     *
     * @return The dirty bit.
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Gets the frame number associated with the page table entry.
     *
     * @return The frame number.
     */
    public int getFrame() {
        return frame;
    }

  /**
     * Sets the validity of the page table entry.
     *
     * @param valid True to mark the entry as valid, false to mark it as invalid.
     */
    public void setFrame(int Frame) {
        this.frame = Frame;

    }

    public long getVirtualAddress() {
        return virtualAddress;
    }

    public void setVirtualAddress(long virtualAddress) {
        this.virtualAddress = virtualAddress;
    }
}
