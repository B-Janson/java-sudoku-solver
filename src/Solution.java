import java.io.*;
import java.util.*;

public class Solution {

    private static Random random;

    private static final int MINI_SIZE = 3;
    private static final int SIZE = MINI_SIZE * MINI_SIZE;
    private static final String SAVED_INPUT = "data/" + SIZE + ".txt";

    private int numberSet = 0;

    public static void main(String[] args) throws IOException {
        new Solution().run();
    }

    private void run() {
        random = new Random();


        int[][] inputBoard = getTestInputBoard();

        List<List<String>> rows = new ArrayList<>();
        for (int[] row : inputBoard) {
            List<String> list = new ArrayList<>();
            for (int i : row) {
                list.add(i + "");
            }
            rows.add(list);
        }

        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(SAVED_INPUT);

            for (List<String> rowData : rows) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader csvReader;
        int inputVerification[][] = new int[SIZE][SIZE];
        try {
            csvReader = new BufferedReader(new FileReader(SAVED_INPUT));
            String row;
            int r = 0;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                for (int i = 0; i < SIZE; i++) {
                    inputVerification[r][i] = Integer.parseInt(data[i]);
                }
                r++;
            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (inputBoard[i][j] != inputVerification[i][j]) {
                    System.err.println("Mismatch at " + i + " " + j);
                }
            }
        }

        long startTime = System.nanoTime();

        boolean success = trySolve(inputBoard, true);

        long endTime = System.nanoTime();

        trySolveParallel(inputBoard, true);

        System.out.println("Elapsed time: " + (endTime - startTime) / 1_000_000);

        if (success) {
            System.out.println("Completed");
        } else {
            System.out.println("Stopped");
        }
    }

    private boolean trySolve(int[][] inputBoard, boolean doPrint) {
        Tile[][] board = setupBoard(inputBoard);
        int currentFound;
        int numRuns = 0;
        do {
            if (doPrint)
                printBoard(board);
            numRuns++;
            currentFound = numberSet;

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    Tile currTile = board[row][col];
                    if (currTile.getValue() != 0) {
                        continue;
                    }

                    for (int test = 1; test <= SIZE; test++) {
                        if (isValidFor(board, test, row, col)) {
//                            System.out.printf("%d is valid for [%d][%d]\n", test, row, col);
                            if (numRuns == 1) {
                                currTile.addPossibleValue(test);
                            }
                        } else {
                            currTile.removePossibleValue(test);
                        }
                    }

                    if (currTile.getPossibleValues().size() == 1) {
                        setValue(board, row, col, 0);
                    }
                }
            }

            for (int col = 0; col < board.length; col++) {
                for (int test = 1; test <= SIZE; test++) {
                    int count = 0;
                    int lastRow = 0;
                    for (int row = 0; row < board.length; row++) {
                        if (board[row][col].getValue() == test) {
                            count = 0;
                            break;
                        }

                        if (board[row][col].getPossibleValues().contains(test)) {
                            count++;
                            lastRow = row;
                        }
                    }

                    if (count == 1) {
                        setValue(board, lastRow, col, test);
                    }
                }
            }

            for (int row = 0; row < board.length; row++) {
                for (int test = 1; test <= SIZE; test++) {
                    int count = 0;
                    int lastCol = 0;
                    for (int col = 0; col < board.length; col++) {
                        if (board[row][col].getValue() == test) {
                            count = 0;
                            break;
                        }

                        if (board[row][col].getPossibleValues().contains(test)) {
                            count++;
                            lastCol = col;
                        }
                    }

                    if (count == 1) {
                        setValue(board, row, lastCol, test);
                    }
                }
            }

            for (int squareRow = 0; squareRow < MINI_SIZE; squareRow++) {
                for (int squareCol = 0; squareCol < MINI_SIZE; squareCol++) {
                    for (int test = 1; test <= SIZE; test++) {
                        int count = 0;
                        int lastRow = -1;
                        int lastCol = -1;
                        boolean sameRow = true;
                        boolean sameCol = true;

                        for (int i = 0; i < SIZE; i++) {
                            int row = squareRow * MINI_SIZE + (i / MINI_SIZE);
                            int col = squareCol * MINI_SIZE + (i % MINI_SIZE);

                            if (board[row][col].getValue() == test) {
                                count = 0;
                                break;
                            }

                            if (board[row][col].getPossibleValues().contains(test)) {
                                count++;

                                if (lastRow != -1 && row != lastRow) {
                                    sameRow = false;
                                }

                                if (lastCol != -1 && col != lastCol) {
                                    sameCol = false;
                                }

                                lastRow = row;
                                lastCol = col;
                            }
                        }

                        if (count == 0) {
                            continue;
                        }

                        if (sameRow) {
//                            System.out.println("All values of " + test + " occur in the same row for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < SIZE; i++) {
                                if (!(i >= squareCol * MINI_SIZE && i < (squareCol + 1) * MINI_SIZE)) {
                                    board[lastRow][i].removePossibleValue(test);
                                }
                            }
                        }

                        if (sameCol) {
//                            System.out.println("All values of " + test + " occur in the same column for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < SIZE; i++) {
                                if (!(i >= squareRow * MINI_SIZE && i < (squareRow + 1) * MINI_SIZE)) {
                                    board[i][lastCol].removePossibleValue(test);
                                }
                            }
                        }

                        if (count == 1) {
                            setValue(board, lastRow, lastCol, test);
                        }
                    }
                }
            }
        } while (!isComplete() && numberSet != currentFound);
        if (doPrint)
            printBoard(board);
        return isComplete();
    }

//    private class MultiThreadingDemo implements Runnable {
//
//        private int start, end;
//
//        private MultiThreadingDemo(int start, int end) {
//            this.start = start;
//            this.end = end;
//        }
//
//        @Override
//        public void run() {
//            try {
//                for (int row = start; row < end; row++) {
//                    for (int col = 0; col < SIZE; col++) {
//                        Tile currTile = board[row][col];
//                        if (currTile.getValue() != 0) {
//                            continue;
//                        }
//
//                        for (int test = 1; test <= SIZE; test++) {
//                            if (isValidFor(board, test, row, col)) {
////                            System.out.printf("%d is valid for [%d][%d]\n", test, row, col);
//                                if (numRuns == 1) {
//                                    currTile.addPossibleValue(test);
//                                }
//                            } else {
//                                currTile.removePossibleValue(test);
//                            }
//                        }
//
//                        if (currTile.getPossibleValues().size() == 1) {
//                            setValue(board, row, col, 0);
//                        }
//                    }
//                }
//            } catch (Exception  e) {
//                System.out.println(e);
//            }
//        }
//    }

    private boolean trySolveParallel(int[][] inputBoard, boolean doPrint) {
        Tile[][] board = setupBoard(inputBoard);
        int currentFound;
        int numRuns = 0;

//        for (int i = 0; i < 8; i++) {
//            Thread object = new Thread(new MultiThreadingDemo());
//            object.start();
//        }





        do {
            if (doPrint)
                printBoard(board);
            numRuns++;
            currentFound = numberSet;

            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    Tile currTile = board[row][col];
                    if (currTile.getValue() != 0) {
                        continue;
                    }

                    for (int test = 1; test <= SIZE; test++) {
                        if (isValidFor(board, test, row, col)) {
//                            System.out.printf("%d is valid for [%d][%d]\n", test, row, col);
                            if (numRuns == 1) {
                                currTile.addPossibleValue(test);
                            }
                        } else {
                            currTile.removePossibleValue(test);
                        }
                    }

                    if (currTile.getPossibleValues().size() == 1) {
                        setValue(board, row, col, 0);
                    }
                }
            }

            for (int col = 0; col < board.length; col++) {
                for (int test = 1; test <= SIZE; test++) {
                    int count = 0;
                    int lastRow = 0;
                    for (int row = 0; row < board.length; row++) {
                        if (board[row][col].getValue() == test) {
                            count = 0;
                            break;
                        }

                        if (board[row][col].getPossibleValues().contains(test)) {
                            count++;
                            lastRow = row;
                        }
                    }

                    if (count == 1) {
                        setValue(board, lastRow, col, test);
                    }
                }
            }

            for (int row = 0; row < board.length; row++) {
                for (int test = 1; test <= SIZE; test++) {
                    int count = 0;
                    int lastCol = 0;
                    for (int col = 0; col < board.length; col++) {
                        if (board[row][col].getValue() == test) {
                            count = 0;
                            break;
                        }

                        if (board[row][col].getPossibleValues().contains(test)) {
                            count++;
                            lastCol = col;
                        }
                    }

                    if (count == 1) {
                        setValue(board, row, lastCol, test);
                    }
                }
            }

            for (int squareRow = 0; squareRow < MINI_SIZE; squareRow++) {
                for (int squareCol = 0; squareCol < MINI_SIZE; squareCol++) {
                    for (int test = 1; test <= SIZE; test++) {
                        int count = 0;
                        int lastRow = -1;
                        int lastCol = -1;
                        boolean sameRow = true;
                        boolean sameCol = true;

                        for (int i = 0; i < SIZE; i++) {
                            int row = squareRow * MINI_SIZE + (i / MINI_SIZE);
                            int col = squareCol * MINI_SIZE + (i % MINI_SIZE);

                            if (board[row][col].getValue() == test) {
                                count = 0;
                                break;
                            }

                            if (board[row][col].getPossibleValues().contains(test)) {
                                count++;

                                if (lastRow != -1 && row != lastRow) {
                                    sameRow = false;
                                }

                                if (lastCol != -1 && col != lastCol) {
                                    sameCol = false;
                                }

                                lastRow = row;
                                lastCol = col;
                            }
                        }

                        if (count == 0) {
                            continue;
                        }

                        if (sameRow) {
//                            System.out.println("All values of " + test + " occur in the same row for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < SIZE; i++) {
                                if (!(i >= squareCol * MINI_SIZE && i < (squareCol + 1) * MINI_SIZE)) {
                                    board[lastRow][i].removePossibleValue(test);
                                }
                            }
                        }

                        if (sameCol) {
//                            System.out.println("All values of " + test + " occur in the same column for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < SIZE; i++) {
                                if (!(i >= squareRow * MINI_SIZE && i < (squareRow + 1) * MINI_SIZE)) {
                                    board[i][lastCol].removePossibleValue(test);
                                }
                            }
                        }

                        if (count == 1) {
                            setValue(board, lastRow, lastCol, test);
                        }
                    }
                }
            }
        } while (!isComplete() && numberSet != currentFound);
        if (doPrint)
            printBoard(board);
        return isComplete();
    }

//    private boolean trySolve() {
//        int numRuns = 0;
//        while (!isComplete() && numRuns < 10) {
//            printBoard();
//            numRuns++;
//            int currentFound = numberSet;
//
//            for (int row = 0; row < SIZE; row++) {
//                for (int col = 0; col < SIZE; col++) {
//                    Tile currTile = board[row][col];
//                    if (currTile.getValue() != 0) {
//                        continue;
//                    }
//
//                    for (int test = 1; test <= SIZE; test++) {
//                        if (isValidFor(test, row, col)) {
////                            System.out.printf("%d is valid for [%d][%d]\n", test, row, col);
//                            if (numRuns == 1) {
//                                currTile.addPossibleValue(test);
//                            }
//                        } else {
//                            currTile.removePossibleValue(test);
//                        }
//                    }
//
//                    if (currTile.getPossibleValues().size() == 1) {
//                        setValue(currTile, row, col, 0);
//                    }
//                }
//            }
//
//            for (int col = 0; col < board.length; col++) {
//                for (int test = 1; test <= SIZE; test++) {
//                    int count = 0;
//                    int lastRow = 0;
//                    for (int row = 0; row < board.length; row++) {
//                        if (board[row][col].getValue() == test) {
//                            count = 0;
//                            break;
//                        }
//
//                        if (board[row][col].getPossibleValues().contains(test)) {
//                            count++;
//                            lastRow = row;
//                        }
//                    }
//
//                    if (count == 1) {
//                        setValue(board[lastRow][col], lastRow, col, test);
//                    }
//                }
//            }
//
//            for (int row = 0; row < board.length; row++) {
//                for (int test = 1; test <= SIZE; test++) {
//                    int count = 0;
//                    int lastCol = 0;
//                    for (int col = 0; col < board.length; col++) {
//                        if (board[row][col].getValue() == test) {
//                            count = 0;
//                            break;
//                        }
//
//                        if (board[row][col].getPossibleValues().contains(test)) {
//                            count++;
//                            lastCol = col;
//                        }
//                    }
//
//                    if (count == 1) {
//                        setValue(board[row][lastCol], row, lastCol, test);
//                    }
//                }
//            }
//
//            for (int squareRow = 0; squareRow < MINI_SIZE; squareRow++) {
//                for (int squareCol = 0; squareCol < MINI_SIZE; squareCol++) {
//                    for (int test = 1; test <= SIZE; test++) {
//                        int count = 0;
//                        int lastRow = -1;
//                        int lastCol = -1;
//                        boolean sameRow = true;
//                        boolean sameCol = true;
//
//                        for (int i = 0; i < SIZE; i++) {
//                            int row = squareRow * MINI_SIZE + (i / MINI_SIZE);
//                            int col = squareCol * MINI_SIZE + (i % MINI_SIZE);
//
//                            if (board[row][col].getValue() == test) {
//                                count = 0;
//                                break;
//                            }
//
//                            if (board[row][col].getPossibleValues().contains(test)) {
//                                count++;
//
//                                if (lastRow != -1 && row != lastRow) {
//                                    sameRow = false;
//                                }
//
//                                if (lastCol != -1 && col != lastCol) {
//                                    sameCol = false;
//                                }
//
//                                lastRow = row;
//                                lastCol = col;
//                            }
//                        }
//
//                        if (count == 0) {
//                            continue;
//                        }
//
//                        if (sameRow) {
////                            System.out.println("All values of " + test + " occur in the same row for [" + lastRow + "][" + lastCol + "]");
//                            for (int i = 0; i < SIZE; i++) {
//                                if (!(i >= squareCol * MINI_SIZE && i < (squareCol + 1) * MINI_SIZE)) {
//                                    board[lastRow][i].removePossibleValue(test);
//                                }
//                            }
//                        }
//
//                        if (sameCol) {
////                            System.out.println("All values of " + test + " occur in the same column for [" + lastRow + "][" + lastCol + "]");
//                            for (int i = 0; i < SIZE; i++) {
//                                if (!(i >= squareRow * MINI_SIZE && i < (squareRow + 1) * MINI_SIZE)) {
//                                    board[i][lastCol].removePossibleValue(test);
//                                }
//                            }
//                        }
//
//                        if (count == 1) {
//                            setValue(board[lastRow][lastCol], lastRow, lastCol, test);
//                        }
//                    }
//                }
//            }
//
//            if (numberSet == currentFound) {
//                System.out.println("No changes made during iteration, exiting.");
//                break;
//            }
//        }
//        return isComplete();
//    }

    private int[][] getTestInputBoard() {
        int[][] board = new int[SIZE][SIZE];
        List<Integer> permutationList = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            permutationList.add(i + 1);
        }

        java.util.Collections.shuffle(permutationList, random);

        for (int i = 0; i < SIZE; i++) {
            board[0][i] = permutationList.get(i);
        }

        for (int i = 1; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int shift = MINI_SIZE;
                if (i % (int) Math.sqrt(SIZE) == 0) {
                    shift = 1;
                }
                int wrapIndex = j - shift;
                if (wrapIndex < 0) {
                    wrapIndex = SIZE + wrapIndex;
                }
                board[i][j] = board[i-1][wrapIndex];
            }
        }

        int bestNumSet = SIZE * SIZE;

        for (int attempts = 1; attempts <= 100; attempts++) {
            System.out.println("Attempt # " + attempts + " of 100");
            boolean solvable = true;
            do {
                int i = random.nextInt(SIZE);
                int j = random.nextInt(SIZE);
                int prev = board[i][j];
                if (prev == 0) {
                    continue;
                }
                board[i][j] = 0;
                solvable = trySolve(board, false);
                if (solvable) {
                    System.out.println("Removing i:" + i + " j:" + j + " was solvable.");
                    bestNumSet--;
                } else {
                    System.out.println("Removing i:" + i + " j:" + j + " was not solvable.");
                    board[i][j] = prev;
                }
            } while (solvable);
        }

        System.out.println("Finished creating sudoku with " + bestNumSet + " out of " + (SIZE*SIZE) + " initial elements");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");

//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                if (random.nextInt(100) < 20) {
//                    board[i][j] = 0;
//                }
//            }
//        }

        return board;
    }

    private Tile[][] setupBoard(int[][] inputBoard) {
        numberSet = 0;
        Tile[][] board = new Tile[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = new Tile();
                if (inputBoard[row][col] != 0) {
                    board[row][col].setValue(inputBoard[row][col]);
                    numberSet++;
                }
            }
        }
        return board;
    }



    private void setValue(Tile[][] board, int row, int col, int value) {
        Tile tile = board[row][col];
        int updatedValue = value > 0 ? value : tile.getPossibleValues().get(0);
        tile.setValue(updatedValue);

        for (int r = 0; r < board.length; r++) {
            board[r][col].removePossibleValue(updatedValue);
        }

        for (int c = 0; c < board.length; c++) {
            board[row][c].removePossibleValue(updatedValue);
        }

        numberSet++;

//        System.out.println("row: " + row + " col: " + col + " = " + updatedValue);

        tile.clearPossibleValues();
    }



    private boolean isValidFor(Tile[][] board, int test, int given_row, int given_col) {
        for (int col = 0; col < SIZE; col++) {
            if (board[given_row][col].getValue() == test) {
                return false;
            }
        }

        for (Tile[] aBoard : board) {
            if (aBoard[given_col].getValue() == test) {
                return false;
            }
        }

        int boxRow = given_row / MINI_SIZE;
        int boxCol = given_col / MINI_SIZE;

        for (int row = boxRow * MINI_SIZE; row < boxRow * MINI_SIZE + MINI_SIZE; row++) {
            for (int col = boxCol * MINI_SIZE; col < boxCol * MINI_SIZE + MINI_SIZE; col++) {
                if (board[row][col].getValue() == test) {
                    return false;
                }
            }
        }

        return true;
    }

    private void printBoard(Tile[][] board) {
//        for (Tile[] boardRow : board) {
//            for (Tile tile : boardRow) {
//                System.out.print(tile + " ");
//            }
//            System.out.println();
//        }

        System.out.println("Number set: " + numberSet + " out of " + (SIZE * SIZE) + " == " + (100.0 * numberSet / (SIZE * SIZE)) + "%");
        System.out.println();

//        for (int row = 0; row < board.length; row++) {
//            int count = 0;
//            for (int col = 0; col < board[row].length; col++) {
//                Tile tile = board[row][col];
//                if (tile.getValue() == 0) {
//                    System.out.printf("[%d][%d]: ", row, col);
//                    tile.printPossibleValues();
//                    count++;
//                }
//            }
//            if (count != 0) {
//                System.out.println();
//            }
//        }
    }

    private boolean isComplete() {
        return numberSet == SIZE * SIZE;
    }
}
