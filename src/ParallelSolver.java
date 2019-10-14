class ParallelSolver {

    private class MultiThreadingDemo implements Runnable {

        private int row, numRuns;
        private Tile[][] board;

        private MultiThreadingDemo(int row, int numRuns, Tile[][] board) {
            this.row = row;
            this.numRuns = numRuns;
            this.board = board;
        }

        @Override
        public void run() {
            try {
                for (int col = 0; col < Main.SIZE; col++) {
                    Tile currTile = board[row][col];
                    if (currTile.getValue() != 0) {
                        continue;
                    }

//                    System.out.println("Row: " + row + " Col: " + col);

                    for (int test = 1; test <= Main.SIZE; test++) {
                        if (isValidFor(board, test, row, col)) {
                            System.out.printf("%d is valid for [%d][%d]\n", test, row, col);
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
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    ReturnStruct trySolveParallel(int[][] inputBoard, boolean debug) {
        Tile[][] board = setupBoard(inputBoard);
        int currentFound;
        int numRuns = 0;
        int numThreads = 9;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new MultiThreadingDemo(i, numRuns, board));
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        Utils.printBoard(board);

        return new ReturnStruct(false, board);
    }

    private static Tile[][] setupBoard(int[][] inputBoard) {
        Main.numberSet = 0;
        Tile[][] board = new Tile[Main.SIZE][Main.SIZE];
        for (int row = 0; row < Main.SIZE; row++) {
            for (int col = 0; col < Main.SIZE; col++) {
                board[row][col] = new Tile();
                if (inputBoard[row][col] != 0) {
                    board[row][col].setValue(inputBoard[row][col]);
                    Main.numberSet++;
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

        Main.numberSet++;

//        System.out.println("row: " + row + " col: " + col + " = " + updatedValue);

        tile.clearPossibleValues();
    }

    private boolean isValidFor(Tile[][] board, int test, int given_row, int given_col) {
        for (int col = 0; col < Main.SIZE; col++) {
            if (board[given_row][col].getValue() == test) {
                return false;
            }
        }

        for (Tile[] aBoard : board) {
            if (aBoard[given_col].getValue() == test) {
                return false;
            }
        }

        int boxRow = given_row / Main.MINI_SIZE;
        int boxCol = given_col / Main.MINI_SIZE;

        for (int row = boxRow * Main.MINI_SIZE; row < boxRow * Main.MINI_SIZE + Main.MINI_SIZE; row++) {
            for (int col = boxCol * Main.MINI_SIZE; col < boxCol * Main.MINI_SIZE + Main.MINI_SIZE; col++) {
                if (board[row][col].getValue() == test) {
                    return false;
                }
            }
        }

        return true;
    }
}
