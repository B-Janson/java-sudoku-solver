import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SudokuGenerator {

    static int[][] getTestInputBoard(Random random, int miniSize, String fileName) {
        int size = miniSize * miniSize;

        // Create empty sudoku of correct size
        int[][] board = new int[size][size];

        // Create integers 1..SIZE
        List<Integer> permutationList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            permutationList.add(i + 1);
        }

        // Shuffle integers 1..SIZE
        java.util.Collections.shuffle(permutationList, random);

        // Fill first row with shuffled integers
        for (int col = 0; col < size; col++) {
            board[0][col] = permutationList.get(col);
        }

        for (int row = 1; row < size; row++) {
            for (int col = 0; col < size; col++) {
                // Amount to shift each following row by
                int shift = miniSize;
                if (row % miniSize == 0) {
                    shift = 1;
                }

                int wrapIndex = col - shift;
                if (wrapIndex < 0) {
                    wrapIndex = size + wrapIndex;
                }

                board[row][col] = board[row - 1][wrapIndex];
            }
        }

        // board is now completed sudoku so save as solution
        String completeFile = fileName.substring(0, fileName.length() - 4) + "_complete.txt";
//        String filename = "data/" + miniSize + "_complete.txt";
        Utils.saveBoardToCSV(board, completeFile, miniSize);

        int bestNumSet = size * size;

        for (int attempt = 1; attempt <= Main.DIFFICULTY; attempt++) {
            System.out.println("Attempt # " + attempt + " of " + Main.DIFFICULTY);
            boolean solvable = true;
            do {
                // Get random point
                int row = random.nextInt(size);
                int col = random.nextInt(size);
                int prev = board[row][col];
                // If already removed, try again
                if (prev == 0) {
                    continue;
                }

                // Remove current value at this position
                board[row][col] = 0;
                // Find out if the current board is solvable
                SingleSolver singleSolver = new SingleSolver();
                ParallelSolver parallelSolver = new ParallelSolver(6);
//                ReturnStruct returnStruct = singleSolver.trySolve(board, false, miniSize);
                ReturnStruct returnStruct = parallelSolver.trySolveParallel(board, true, miniSize);
                solvable = returnStruct.solvable;

                // If it was solvable, then keep it that way, otherwise put back the value that was there,
                // increase the number of attempts and restart the loop
                if (solvable) {
//                    System.out.println("Removing row:" + row + " col:" + col + " was solvable.");
                    bestNumSet--;
                } else {
//                    System.out.println("Removing row:" + row + " col:" + col + " was not solvable.");
                    board[row][col] = prev;
                }
            } while (solvable);
        }

        System.out.println("Finished creating sudoku with " + bestNumSet + " out of " + (size * size) + " initial elements");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");

//        filename = "data/" + miniSize + ".txt";
        Utils.saveBoardToCSV(board, fileName, miniSize);

        return board;
    }
}
