import java.io.*;
import java.util.*;

public class Main {

    static Random random;

    static final int DIFFICULTY = 5;
    static final int MINI_SIZE = 10;
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
        long startTimeSingle = System.nanoTime();
        ReturnStruct success = singleSolver.trySolve(inputBoardSingle, false);
        long endTimeSingle = System.nanoTime();
        Utils.verifyAgainstCSV(success.returnBoard);

        ParallelSolver parallelSolver = new ParallelSolver();

        // Multi-Threaded
        long startTimeParallel = System.nanoTime();
        success = parallelSolver.trySolveParallel(inputBoardParallel, false);
        long endTimeParallel = System.nanoTime();
//        Utils.verifyAgainstCSV(success.returnBoard);

        System.out.println("Elapsed time: " + (endTimeSingle - startTimeSingle) / 1_000_000.0);
        System.out.println("Elapsed time: " + (endTimeParallel - startTimeParallel) / 1_000_000.0);
    }
}
