import java.util.ArrayList;
import java.util.List;

class SudokuGenerator {

    static int[][] getTestInputBoard() {
        // Create empty sudoku of correct size
        int[][] board = new int[Main.SIZE][Main.SIZE];

        // Create integers 1..SIZE
        List<Integer> permutationList = new ArrayList<>();
        for (int i = 0; i < Main.SIZE; i++) {
            permutationList.add(i + 1);
        }

        // Shuffle integers 1..SIZE
        java.util.Collections.shuffle(permutationList, Main.random);

        // Fill first row with shuffled integers
        for (int col = 0; col < Main.SIZE; col++) {
            board[0][col] = permutationList.get(col);
        }

        for (int row = 1; row < Main.SIZE; row++) {
            for (int col = 0; col < Main.SIZE; col++) {
                // Amount to shift each following row by
                int shift = Main.MINI_SIZE;
                if (row % Main.MINI_SIZE == 0) {
                    shift = 1;
                }

                int wrapIndex = col - shift;
                if (wrapIndex < 0) {
                    wrapIndex = Main.SIZE + wrapIndex;
                }

                board[row][col] = board[row - 1][wrapIndex];
            }
        }

        // board is now completed sudoku so save as solution
        Utils.saveBoardToCSV(board);

        int bestNumSet = Main.SIZE * Main.SIZE;

        for (int attempt = 1; attempt <= Main.DIFFICULTY; attempt++) {
            System.out.println("Attempt # " + attempt + " of " + Main.DIFFICULTY);
            boolean solvable = true;
            do {
                // Get random point
                int row = Main.random.nextInt(Main.SIZE);
                int col = Main.random.nextInt(Main.SIZE);
                int prev = board[row][col];
                // If already removed, try again
                if (prev == 0) {
                    continue;
                }

                // Remove current value at this position
                board[row][col] = 0;
                // Find out if the current board is solvable
                SingleSolver singleSolver = new SingleSolver();
                ReturnStruct returnStruct = singleSolver.trySolve(board, false);
                solvable = returnStruct.solvable;

                // If it was solvable, then keep it that way, otherwise put back the value that was there,
                // increase the number of attempts and restart the loop
                if (solvable) {
//                    System.out.println("Removing row:" + row + " col:" + col + " was solvable.");
                    bestNumSet--;
                } else {
                    System.out.println("Removing row:" + row + " col:" + col + " was not solvable.");
                    board[row][col] = prev;
                }
            } while (solvable);
        }

        System.out.println("Finished creating sudoku with " + bestNumSet + " out of " + (Main.SIZE * Main.SIZE) + " initial elements");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");

        return board;
    }
}
