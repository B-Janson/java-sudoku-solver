import java.io.*;
import java.util.*;

public class Main {

    static Random random;

    static final int DIFFICULTY = 100;
    static final int MINI_SIZE = 3;
    static final int SIZE = MINI_SIZE * MINI_SIZE;
    static final String SAVED_INPUT = "data/" + SIZE + ".txt";

    static int numberSet = 0;

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    private void run() {
        random = new Random(1);

        int[][] inputBoardSingle = SudokuGenerator.getTestInputBoard();
        int[][] inputBoardParallel = Arrays.stream(inputBoardSingle).map(int[]::clone).toArray(int[][]::new);

        SingleSolver singleSolver = new SingleSolver();

        // Single-Threaded
        System.out.println("Solving single threaded");
        long startTimeSingle = System.nanoTime();
        ReturnStruct success = singleSolver.trySolve(inputBoardSingle, false);
        long endTimeSingle = System.nanoTime();
        Utils.verifyAgainstCSV(success.returnBoard);

        ParallelSolver parallelSolver = new ParallelSolver();

        // Multi-Threaded
        System.out.println("Solving parallel");
        long startTimeParallel = System.nanoTime();
        success = parallelSolver.trySolveParallel(inputBoardParallel, false);
        long endTimeParallel = System.nanoTime();
        Utils.verifyAgainstCSV(success.returnBoard);

        System.out.println("Elapsed time single: " + (endTimeSingle - startTimeSingle) / 1_000_000.0);
        System.out.println("Elapsed time parallel: " + (endTimeParallel - startTimeParallel) / 1_000_000.0);
    }
}
