import java.io.*;
import java.util.*;

public class Main {

    enum ProgramChoice {
        Both,
        Single,
        Parallel,
        Generate
    }

    static Random random;

    static final int DIFFICULTY = 100;
//    static int MINI_SIZE = 5;
//    static int SIZE = MINI_SIZE * MINI_SIZE;

    static int numberSet = 0;

    public static void main(String[] args) throws IOException, InterruptedException {
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(20);
        }

        if (args.length < 2) {
            System.err.println("You must specify which option to run: [filename] -single | -parallel (#threads) | -both | -generate");
            System.exit(0);
            return;
        }

        String textInput = args[0];
        ProgramChoice programChoice;
        int numThreads = 6;
        int size = 3;

        if (args[1].equalsIgnoreCase("-generate")) {
            programChoice = ProgramChoice.Generate;

            if (args.length == 3) {
                size = Integer.parseInt(args[2]);
            }
        } else if (args[1].equalsIgnoreCase("-single")) {
            programChoice = ProgramChoice.Single;
        } else if (args[1].equalsIgnoreCase("-parallel")) {
            programChoice = ProgramChoice.Parallel;

            if (args.length == 3) {
                numThreads = Integer.parseInt((args[2]));
            }
        } else {
            programChoice = ProgramChoice.Both;
        }

        new Main().run(programChoice, textInput, numThreads, size);
    }

    private void run(ProgramChoice programChoice, String textInput, int numThreads, int size) {
        random = new Random();

        if (programChoice == ProgramChoice.Generate) {
//            for (int i = 2; i <= 10; i++) {
//                MINI_SIZE = size;
//                SIZE = MINI_SIZE * MINI_SIZE;
                SudokuGenerator.getTestInputBoard(random, size, textInput);
//            }
            return;
        }

        int[][] inputBoardSingle = Utils.readFromCSV(textInput);
        int[][] inputBoardParallel = Arrays.stream(inputBoardSingle).map(int[]::clone).toArray(int[][]::new);

        size = (int) Math.sqrt(inputBoardParallel.length);

//        int[][] inputBoardSingle = SudokuGenerator.getTestInputBoard(random, MINI_SIZE);
//        int[][] inputBoardParallel = Arrays.stream(inputBoardSingle).map(int[]::clone).toArray(int[][]::new);

        String completeFile = textInput.substring(0, textInput.length() - 4) + "_complete.txt";

        if (programChoice == ProgramChoice.Single || programChoice == ProgramChoice.Both) {
            SingleSolver singleSolver = new SingleSolver();

            // Single-Threaded
            System.out.println("Solving single threaded");
            long startTimeSingle = System.nanoTime();
            ReturnStruct success = singleSolver.trySolve(inputBoardSingle, false, size);
            long endTimeSingle = System.nanoTime();
            Utils.verifyAgainstCSV(success.returnBoard, completeFile);

            System.out.println("Elapsed time single: " + (endTimeSingle - startTimeSingle) / 1_000_000.0);
        }

        if (programChoice == ProgramChoice.Parallel || programChoice == ProgramChoice.Both) {
            ParallelSolver parallelSolver = new ParallelSolver(numThreads);

            // Multi-Threaded
            System.out.println("Solving parallel");
            long startTimeParallel = System.nanoTime();
            ReturnStruct success = parallelSolver.trySolveParallel(inputBoardParallel, false, size);
            long endTimeParallel = System.nanoTime();
            Utils.verifyAgainstCSV(success.returnBoard, completeFile);

            System.out.println("Elapsed time parallel: " + (endTimeParallel - startTimeParallel) / 1_000_000.0);
        }
    }
}
