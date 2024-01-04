// MemorySimulator.java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class MemoryAccessEvent {
    private final String eventType;
    private final long address;

    public MemoryAccessEvent(String eventType, long address) {
        this.eventType = eventType;
        this.address = address;
    }

    public String getEventType() {
        return eventType;
    }

    public long getAddress() {
        return address;
    }
}

public class MemorySimulator {
    private static int totalMemoryAccesses = 0;
    private static int totalPageFaults = 0;
    private static int totalWritesToDisk = 0;
    private static int totalPageTableLeaves = 0;
    private static int totalSizeofPageTable = 0;

    private static int numFrames = 0;
    private static TwoLevelPageTable pageTable;


    public static List<Long> getFutureAccesses(String traceFile) {
        List<Long> futureAccesses = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(traceFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                MemoryAccessEvent memAccess = parseMemoryAccessEvent(line);
                if (memAccess.getEventType().equals("INVALID") && memAccess.getAddress() == -1) {
                    continue;
                }

                // Check if it's a future access (Load, Store, or Modify)
                if (memAccess.getEventType().equals("L") || memAccess.getEventType().equals("S") || memAccess.getEventType().equals("M")) {
                    futureAccesses.add(memAccess.getAddress());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return futureAccesses;
    }


    public static void simulateMemoryAccesses(String traceFile, ReplacementAlgorithm algorithm, int numFrames) {
        pageTable = new TwoLevelPageTable(numFrames,algorithm);
        List<Long> futureAccesses = getFutureAccesses(traceFile);
        try (BufferedReader reader = new BufferedReader(new FileReader(traceFile))) {
            String line;
        
            while ((line = reader.readLine()) != null) {
                MemoryAccessEvent memAccess = parseMemoryAccessEvent(line);
                if (memAccess.getEventType().equals("INVALID") && memAccess.getAddress() == -1) {
                    continue;
                }  

                totalMemoryAccesses++;

                //check if address is in a frame.
                long isPageFault = pageTable.translate(memAccess.getAddress());
            
                if (memAccess.getEventType().equals("L") || memAccess.getEventType().equals("S") || memAccess.getEventType().equals("M")) {
                 // Check if the page is present in memory
                    if (isPageFault != -1) {
                        // Page is present
                        PageTableEntry page = pageTable.getRamEntry((int)isPageFault);
                        page.setReferenced(true);
                        

                        if (memAccess.getEventType().equals("S") || memAccess.getEventType().equals("M")) {
                        // For Store or Modify, set the dirty bit if not already set
                            if (!page.getDirty()) {
                                page.setDirty(true);
                            }
                        }
                        pageTable.setRamEntry((int)isPageFault,page);

                    } else {
                        // Page fault
                        totalPageFaults++;
                        int victimPageIndex = pageTable.handlePageFault(memAccess.getAddress(),algorithm);
                        PageTableEntry newPage = pageTable.getRamEntry(victimPageIndex);
                        if (memAccess.getEventType().equals("S") || memAccess.getEventType().equals("M")) {
                            // Set the dirty bit for the newly brought-in page
                            newPage.setDirty(true);
                        }
                        newPage.setVirtualAddress(memAccess.getAddress());
                        newPage.setReferenced(true);
                        pageTable.setRamEntry((int)victimPageIndex,newPage);
                    }
                }
            }    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MemoryAccessEvent parseMemoryAccessEvent(String line) {
        
         if (!line.matches("\\s*[ILMS]\\s+[0-9A-Fa-f]+,\\d+.*")) {
             return new MemoryAccessEvent("INVALID", -1);
        }

        //System.out.println("Line: " + line);
        String trimmedLine = line.trim();
        String[] traceLine = trimmedLine.split("\\s+|,");
        //System.out.println(Arrays.toString(traceLine));
        
        if (traceLine.length < 2) {
            return null;
        }

        String eventType = traceLine[0].trim();
        long address = Long.parseLong(traceLine[1].trim(), 16);
        
        return new MemoryAccessEvent(eventType, address);
       
    }


    public static void printSummaryStatistics(String algorithm, int numFrames) {
        System.out.println("Algorithm: " + algorithm);
        System.out.println("Number of Frames: " + numFrames);
        System.out.println("Total Memory Accesses: " + totalMemoryAccesses);
        System.out.println("Total Page Faults: " + totalPageFaults);
        System.out.println("Total Writes to Disk: " + pageTable.getTotalWritesToDisk());
        System.out.println("Number of page table leaves: " + pageTable.getTotalPageTableLeaves());
        System.out.println("Total size of page table: " + pageTable.getTotalSizeofPageTable());
        
    }
}
