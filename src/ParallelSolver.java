import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class ParallelSolver {

    private int NUM_THREADS;

    private Tile[][] board;

    private int miniSize;
    private int size;

    public ParallelSolver(int numThreads) {
        this.NUM_THREADS = numThreads;
    }

    private class MultiThreadingDemo implements Runnable {

        private int threadNo;


        private MultiThreadingDemo(int threadNo) {
            this.threadNo = threadNo;
        }

        @Override
        public void run() {
            for (int row = threadNo; row < board.length; row += NUM_THREADS) {
                for (int col = 0; col < board.length; col++) {
                    Tile currTile = board[row][col];
                    if (currTile.getValue() != 0) {
                        continue;
                    }

                    for (int test = 1; test <= board.length; test++) {
                        if (!isValidFor(board, test, row, col)) {
                            currTile.removePossibleValue(test);
                        }
                    }

                    if (currTile.getPossibleValues().size() == 1) {
                        setValue(board, row, col, 0);
                    }
                }

            }
        }
    }

    private class MultiThreadingDemo2 implements Runnable {

        private int threadNumber;

        private MultiThreadingDemo2(int threadNumber) {
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            for (int trial = threadNumber; trial < board.length; trial += NUM_THREADS) {
                for (int test = 1; test <= board.length; test++) {
                    int rowCount = 0;
                    int lastRow = 0;
                    for (int row = 0; row < board.length; row++) {
                        if (board[row][trial].getValue() == test) {
                            rowCount = 0;
                            break;
                        }

                        if (board[row][trial].getPossibleValues().contains(test)) {
                            rowCount++;
                            lastRow = row;
                        }
                    }

                    if (rowCount == 1) {
                        setValue(board, lastRow, trial, test);
                    }

                    int colCount = 0;
                    int lastCol = 0;
                    for (int col = 0; col < board.length; col++) {
                        if (board[trial][col].getValue() == test) {
                            colCount = 0;
                            break;
                        }

                        if (board[trial][col].getPossibleValues().contains(test)) {
                            colCount++;
                            lastCol = col;
                        }
                    }

                    if (colCount == 1) {
                        setValue(board, trial, lastCol, test);
                    }
                }
            }
        }
    }

    private class MultiThreadingDemo3 implements Runnable {

        private int squareRow;

        private MultiThreadingDemo3(int threadNumber) {
            this.squareRow = threadNumber;
        }

        @Override
        public void run() {
            for (int trial = squareRow; trial < miniSize; trial += NUM_THREADS) {
                for (int squareCol = 0; squareCol < miniSize; squareCol++) {
                    for (int test = 1; test <= size; test++) {
                        int count = 0;
                        int lastRow = -1;
                        int lastCol = -1;
                        boolean sameRow = true;
                        boolean sameCol = true;

                        for (int i = 0; i < size; i++) {
                            int row = trial * miniSize+ (i / miniSize);
                            int col = squareCol * miniSize + (i % miniSize);
                            Tile currTile = board[row][col];

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
//                            System.out.println("All values of " + test + " occur in the same threadNo for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < size; i++) {
                                if (!(i >= squareCol * miniSize && i < (squareCol + 1) * miniSize)) {
                                    board[lastRow][i].updatePossibleValue(test, false);
                                }
                            }
                        }

                        if (sameCol) {
//                            System.out.println("All values of " + test + " occur in the same column for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < size; i++) {
                                if (!(i >= trial * miniSize && i < (trial + 1) * miniSize)) {
                                    board[i][lastCol].updatePossibleValue(test, false);
                                }
                            }
                        }

                        if (count == 1) {
                            setValue(board, lastRow, lastCol, test);
                        }
                    }
                }
            }
        }
    }

    ReturnStruct trySolveParallel(int[][] inputBoard, boolean debug, int miniSize) {
        System.out.println("NUMBER OF THREADS: " + NUM_THREADS);
        board = Utils.setupBoard(inputBoard);
        int currentFound;
        Thread[] threads = new Thread[NUM_THREADS];
        this.miniSize = miniSize;
        this.size = miniSize * miniSize;

        // Add all possible values to grid
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile currTile = board[row][col];
                if (currTile.getValue() != 0) {
                    continue;
                }

                for (int test = 1; test <= size; test++) {
                    if (isValidFor(board, test, row, col)) {
                        currTile.addPossibleValue(test);
                    }
                }

                if (currTile.getPossibleValues().size() == 1) {
                    setValue(board, row, col, 0);
                }
            }
        }

//        Utils.printBoard(board);

        do {
            currentFound = Main.numberSet;

//            List<Callable<Object>> calls = new ArrayList<>();

            for (int i = 0; i < NUM_THREADS; i++) {
//                calls.add(Executors.callable(new MultiThreadingDemo(i)));
//                executorService.execute(new MultiThreadingDemo(1));
                threads[i] = new Thread(new MultiThreadingDemo(i));
                threads[i].start();
            }

            for (int i = 0; i < NUM_THREADS; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            Utils.printBoard(board);

            for (int i = 0; i < NUM_THREADS; i++) {
//                calls.add(Executors.callable(new MultiThreadingDemo2(i)));
//                executorService.execute(new MultiThreadingDemo2(i));
                threads[i] = new Thread(new MultiThreadingDemo2(i));
                threads[i].start();
            }

            for (int i = 0; i < NUM_THREADS; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < NUM_THREADS; i++) {
//                calls.add(Executors.callable(new MultiThreadingDemo3(i)));
//                executorService.execute(new MultiThreadingDemo3(i));
                threads[i] = new Thread(new MultiThreadingDemo3(i));
                threads[i].start();
            }

            for (int i = 0; i < NUM_THREADS; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            Utils.printBoard(board);
        } while (!Utils.isComplete(size) && Main.numberSet != currentFound);


//        Utils.printBoard(board);

        return new ReturnStruct(Utils.isComplete(size), board);
    }

    private void setValue(Tile[][] board, int row, int col, int value) {
        Tile tile = board[row][col];
        int updatedValue = value > 0 ? value : tile.getPossibleValues().get(0);
        tile.setValue(updatedValue);

        Main.numberSet++;

        tile.clearPossibleValues();
    }

    private boolean isValidFor(Tile[][] board, int test, int given_row, int given_col) {
        for (int col = 0; col < size; col++) {
            if (board[given_row][col].getValue() == test) {
                return false;
            }
        }

        for (Tile[] aBoard : board) {
            if (aBoard[given_col].getValue() == test) {
                return false;
            }
        }

        int boxRow = given_row / miniSize;
        int boxCol = given_col / miniSize;

        for (int row = boxRow * miniSize; row < boxRow * miniSize + miniSize; row++) {
            for (int col = boxCol * miniSize; col < boxCol * miniSize + miniSize; col++) {
                if (board[row][col].getValue() == test) {
                    return false;
                }
            }
        }

        return true;
    }
}
