// VMSimulator.java
package vmsim;

public class VMSimulator {
    public static void main(String[] args) {
        // Handle command-line arguments

        if (args.length != 4) {
            System.out.println("Usage: java VMSimulator -n <numframes> -a <opt|clock|lru> <tracefile>");
            System.exit(1);

        int numFrames = Integer.parseInt(args[1]);
        String algorithm = args[3];
        String traceFile = args[4];

        System.out.println("Number of Frames: " + numFrames);
        System.out.println("Algorithm: " + algorithm);
        System.out.println("Trace File: " + traceFile);
        }

    }
}
