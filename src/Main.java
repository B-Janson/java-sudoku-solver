import java.io.*;
import java.util.*;

public class Main {

    static Random random;

    static final int DIFFICULTY = 100;
    static int MINI_SIZE = 10;
    static int SIZE = MINI_SIZE * MINI_SIZE;

    static int numberSet = 0;

    public static void main(String[] args) throws IOException {
        boolean generateOnly = false;
        if (args.length > 0 && args[0].equalsIgnoreCase("generate")) {
            generateOnly = true;
        }
        new Main().run(generateOnly);
    }

    private void run(boolean generateOnly) {
        random = new Random(1);

        if (generateOnly) {
            for (int i = 2; i <= 10; i++) {
                MINI_SIZE = i;
                SIZE = MINI_SIZE * MINI_SIZE;
                SudokuGenerator.getTestInputBoard(random, i);
            }
            return;
        }

        int[][] inputBoardSingle = SudokuGenerator.getTestInputBoard(random, MINI_SIZE);
        int[][] inputBoardParallel = Arrays.stream(inputBoardSingle).map(int[]::clone).toArray(int[][]::new);

        String filename = "data/" + MINI_SIZE + "_complete.txt";

        SingleSolver singleSolver = new SingleSolver();

        // Single-Threaded
        System.out.println("Solving single threaded");
        long startTimeSingle = System.nanoTime();
        ReturnStruct success = singleSolver.trySolve(inputBoardSingle, false);
        long endTimeSingle = System.nanoTime();
        Utils.verifyAgainstCSV(success.returnBoard, filename);

        ParallelSolver parallelSolver = new ParallelSolver();

        // Multi-Threaded
        System.out.println("Solving parallel");
        long startTimeParallel = System.nanoTime();
        success = parallelSolver.trySolveParallel(inputBoardParallel, false);
        long endTimeParallel = System.nanoTime();
        Utils.verifyAgainstCSV(success.returnBoard, filename);

        System.out.println("Elapsed time single: " + (endTimeSingle - startTimeSingle) / 1_000_000.0);
        System.out.println("Elapsed time parallel: " + (endTimeParallel - startTimeParallel) / 1_000_000.0);
    }
}
