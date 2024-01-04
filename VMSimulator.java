// VMSimulator.java
import java.util.List;
import java.util.ArrayList;

public class VMSimulator {
    public static void main(String[] args) {
        // Handle command-line arguments

        if (args.length != 5) {
            System.out.println("Usage: java VMSimulator -n <numframes> -a <opt|clock|lru> <tracefile>");
            System.exit(1);
        }

        int numFrames = Integer.parseInt(args[1]);
        String algorithm = args[3];
        String traceFile = args[4];

        ReplacementAlgorithm replacementAlgorithm = null;

        switch (algorithm) {
            case "opt":
                List<Long> futureAccesses = MemorySimulator.getFutureAccesses(traceFile);
                replacementAlgorithm = new OptimalAlgorithm(futureAccesses,numFrames);
                break;
            case "clock":
                replacementAlgorithm = new ClockAlgorithm(numFrames);
                break;
            case "lru":
                replacementAlgorithm = new LRUCache(numFrames);
                break;
            default:
                System.out.println("Invalid algorithm. Supported algorithms: opt, clock, lru");
                System.exit(1);
        }
        // Call the simulator to handle memory accesses
        MemorySimulator.simulateMemoryAccesses(traceFile, replacementAlgorithm, numFrames);

        // Print summary statistics
        MemorySimulator.printSummaryStatistics(algorithm, numFrames);
    }
}
