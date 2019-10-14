//import java.io.*;
//import java.util.*;
//
//public class Solution {
//
//    private static Random random;
//
//    private static final int MINI_SIZE = 3;
//    private static final int SIZE = MINI_SIZE * MINI_SIZE;
//    private static final String SAVED_INPUT = "data/" + SIZE + ".txt";
//    private static final int DIFFICULTY = 100;
//
//    private int numberSet = 0;
//
//    public static void main(String[] args) throws IOException {
//        new Solution().run();
//    }
//
//    private void run() {
//        random = new Random();
//
//        int[][] inputBoardSingle = getTestInputBoard();
//        int[][] inputBoardParallel = Arrays.stream(inputBoardSingle).map(int[]::clone).toArray(int[][]::new);
//
//        // Single-Threaded
//        long startTimeSingle = System.nanoTime();
//        FunctionReturner success = trySolve(inputBoardSingle, false);
//        long endTimeSingle = System.nanoTime();
//        verifyAgainstCSV(success.returnBoard);
//
//        // Multi-Threaded
//        long startTimeParallel = System.nanoTime();
//        success = trySolve(inputBoardParallel, false);
//        long endTimeParallel = System.nanoTime();
//        verifyAgainstCSV(success.returnBoard);
////        trySolveParallel(inputBoardSingle, true);
//
//        System.out.println("Elapsed time: " + (endTimeSingle - startTimeSingle) / 1_000_000.0);
//        System.out.println("Elapsed time: " + (endTimeParallel - startTimeParallel) / 1_000_000.0);
//    }
//
//    private FunctionReturner trySolve(int[][] inputBoard, boolean debug) {
//        Tile[][] board = setupBoard(inputBoard);
//        int currentFound;
//        int numRuns = 0;
//        do {
//            if (debug)
//                printBoard(board);
//            numRuns++;
//            currentFound = numberSet;
//
//            for (int row = 0; row < SIZE; row++) {
//                for (int col = 0; col < SIZE; col++) {
//                    Tile currTile = board[row][col];
//                    if (currTile.getValue() != 0) {
//                        continue;
//                    }
//
//                    for (int test = 1; test <= SIZE; test++) {
//                        if (isValidFor(board, test, row, col)) {
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
//                        setValue(board, row, col, 0);
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
//                        setValue(board, lastRow, col, test);
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
//                        setValue(board, row, lastCol, test);
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
//                            setValue(board, lastRow, lastCol, test);
//                        }
//                    }
//                }
//            }
//        } while (!isComplete() && numberSet != currentFound);
//        if (debug) {
//            printBoard(board);
//        }
//        return new FunctionReturner(isComplete(), board);
//    }
//
//    private class MultiThreadingDemo implements Runnable {
//
//        private int row, numRuns;
//        private Tile[][] board;
//
//        private MultiThreadingDemo(int row, int numRuns, Tile[][] board) {
//            this.row = row;
//            this.numRuns = numRuns;
//            this.board = board;
//        }
//
//        @Override
//        public void run() {
//            try {
//                for (int col = 0; col < SIZE; col++) {
//                    Tile currTile = board[row][col];
//                    if (currTile.getValue() != 0) {
//                        continue;
//                    }
//
//                    for (int test = 1; test <= SIZE; test++) {
//                        if (isValidFor(board, test, row, col)) {
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
//                        setValue(board, row, col, 0);
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//        }
//    }
//
//    private boolean trySolveParallel(int[][] inputBoard, boolean doPrint) {
//        Tile[][] board = setupBoard(inputBoard);
//        int currentFound;
//        int numRuns = 0;
//        int numThreads = 8;
//
////        for (int i = 0; i < numThreads; i++) {
////            Thread object = new Thread(new MultiThreadingDemo());
////            object.start();
////        }
//
//
//        return isComplete();
//    }
//
//    private int[][] getTestInputBoard() {
//        // Create empty sudoku of correct size
//        int[][] board = new int[SIZE][SIZE];
//
//        // Create integers 1..SIZE
//        List<Integer> permutationList = new ArrayList<>();
//        for (int i = 0; i < SIZE; i++) {
//            permutationList.add(i + 1);
//        }
//
//        // Shuffle integers 1..SIZE
//        java.util.Collections.shuffle(permutationList, random);
//
//        // Fill first row with shuffled integers
//        for (int col = 0; col < SIZE; col++) {
//            board[0][col] = permutationList.get(col);
//        }
//
//        for (int row = 1; row < SIZE; row++) {
//            for (int col = 0; col < SIZE; col++) {
//                // Amount to shift each following row by
//                int shift = MINI_SIZE;
//                if (row % MINI_SIZE == 0) {
//                    shift = 1;
//                }
//
//                int wrapIndex = col - shift;
//                if (wrapIndex < 0) {
//                    wrapIndex = SIZE + wrapIndex;
//                }
//
//                board[row][col] = board[row - 1][wrapIndex];
//            }
//        }
//
//        // board is now completed sudoku so save as solution
//        saveBoardToCSV(board);
//
//        int bestNumSet = SIZE * SIZE;
//
//        for (int attempt = 1; attempt <= DIFFICULTY; attempt++) {
//            System.out.println("Attempt # " + attempt + " of " + DIFFICULTY);
//            boolean solvable = true;
//            do {
//                // Get random point
//                int row = random.nextInt(SIZE);
//                int col = random.nextInt(SIZE);
//                int prev = board[row][col];
//                // If already removed, try again
//                if (prev == 0) {
//                    continue;
//                }
//
//                // Remove current value at this position
//                board[row][col] = 0;
//                // Find out if the current board is solvable
//                FunctionReturner fr = trySolve(board, false);
//                solvable = fr.solvable;
//
//                // If it was solvable, then keep it that way, otherwise put back the value that was there,
//                // increase the number of attempts and restart the loop
//                if (solvable) {
//                    System.out.println("Removing row:" + row + " col:" + col + " was solvable.");
//                    bestNumSet--;
//                } else {
//                    System.out.println("Removing row:" + row + " col:" + col + " was not solvable.");
//                    board[row][col] = prev;
//                }
//            } while (solvable);
//        }
//
//        System.out.println("Finished creating sudoku with " + bestNumSet + " out of " + (SIZE * SIZE) + " initial elements");
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
//
//        return board;
//    }
//
//    private void saveBoardToCSV(int[][] board) {
//        // Add all values to list of list
//        List<List<String>> rows = new ArrayList<>();
//        for (int[] row : board) {
//            List<String> list = new ArrayList<>();
//            for (int i : row) {
//                list.add(i + "");
//            }
//            rows.add(list);
//        }
//
//        // Write each row to file
//        FileWriter csvWriter;
//        try {
//            csvWriter = new FileWriter(SAVED_INPUT);
//
//            for (List<String> rowData : rows) {
//                csvWriter.append(String.join(",", rowData));
//                csvWriter.append("\n");
//            }
//
//            csvWriter.flush();
//            csvWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Tile[][] setupBoard(int[][] inputBoard) {
//        numberSet = 0;
//        Tile[][] board = new Tile[SIZE][SIZE];
//        for (int row = 0; row < SIZE; row++) {
//            for (int col = 0; col < SIZE; col++) {
//                board[row][col] = new Tile();
//                if (inputBoard[row][col] != 0) {
//                    board[row][col].setValue(inputBoard[row][col]);
//                    numberSet++;
//                }
//            }
//        }
//        return board;
//    }
//
//    private void setValue(Tile[][] board, int row, int col, int value) {
//        Tile tile = board[row][col];
//        int updatedValue = value > 0 ? value : tile.getPossibleValues().get(0);
//        tile.setValue(updatedValue);
//
//        for (int r = 0; r < board.length; r++) {
//            board[r][col].removePossibleValue(updatedValue);
//        }
//
//        for (int c = 0; c < board.length; c++) {
//            board[row][c].removePossibleValue(updatedValue);
//        }
//
//        numberSet++;
//
////        System.out.println("row: " + row + " col: " + col + " = " + updatedValue);
//
//        tile.clearPossibleValues();
//    }
//
//
//    private boolean isValidFor(Tile[][] board, int test, int given_row, int given_col) {
//        for (int col = 0; col < SIZE; col++) {
//            if (board[given_row][col].getValue() == test) {
//                return false;
//            }
//        }
//
//        for (Tile[] aBoard : board) {
//            if (aBoard[given_col].getValue() == test) {
//                return false;
//            }
//        }
//
//        int boxRow = given_row / MINI_SIZE;
//        int boxCol = given_col / MINI_SIZE;
//
//        for (int row = boxRow * MINI_SIZE; row < boxRow * MINI_SIZE + MINI_SIZE; row++) {
//            for (int col = boxCol * MINI_SIZE; col < boxCol * MINI_SIZE + MINI_SIZE; col++) {
//                if (board[row][col].getValue() == test) {
//                    return false;
//                }
//            }
//        }
//
//        return true;
//    }
//
//    private void printBoard(Tile[][] board) {
////        for (Tile[] boardRow : board) {
////            for (Tile tile : boardRow) {
////                System.out.print(tile + " ");
////            }
////            System.out.println();
////        }
//
//        System.out.println("Number set: " + numberSet + " out of " + (SIZE * SIZE) + " == " + (100.0 * numberSet / (SIZE * SIZE)) + "%");
//        System.out.println();
//
////        for (int row = 0; row < board.length; row++) {
////            int count = 0;
////            for (int col = 0; col < board[row].length; col++) {
////                Tile tile = board[row][col];
////                if (tile.getValue() == 0) {
////                    System.out.printf("[%d][%d]: ", row, col);
////                    tile.printPossibleValues();
////                    count++;
////                }
////            }
////            if (count != 0) {
////                System.out.println();
////            }
////        }
//    }
//
//    private boolean isComplete() {
//        return numberSet == SIZE * SIZE;
//    }
//
//    private void verifyAgainstCSV(Tile[][] inputBoard) {
//        BufferedReader csvReader;
//        int inputVerification[][] = new int[SIZE][SIZE];
//        try {
//            csvReader = new BufferedReader(new FileReader(SAVED_INPUT));
//            String row;
//            int r = 0;
//            while ((row = csvReader.readLine()) != null) {
//                String[] data = row.split(",");
//                for (int i = 0; i < SIZE; i++) {
//                    inputVerification[r][i] = Integer.parseInt(data[i]);
//                }
//                r++;
//            }
//            csvReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        for (int i = 0; i < SIZE; i++) {
//            for (int j = 0; j < SIZE; j++) {
//                if (inputBoard[i][j].getValue() != inputVerification[i][j]) {
//                    System.err.println("Mismatch at " + i + " " + j);
//                }
//            }
//        }
//    }
//
//    private class FunctionReturner {
//        private boolean solvable;
//        private Tile[][] returnBoard;
//
//        private FunctionReturner(boolean solvable, Tile[][] returnBoard) {
//            this.solvable = solvable;
//            this.returnBoard = returnBoard;
//        }
//    }
//}
