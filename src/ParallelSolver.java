class ParallelSolver {

    private Tile[][] board;

    private class MultiThreadingDemo implements Runnable {

        private int row;

        private MultiThreadingDemo(int row) {
            this.row = row;
        }

        @Override
        public void run() {
            try {
                for (int col = 0; col < Main.SIZE; col++) {
                    Tile currTile = board[row][col];
                    if (currTile.getValue() != 0) {
                        continue;
                    }

                    for (int test = 1; test <= Main.SIZE; test++) {
                        if (!isValidFor(board, test, row, col)) {
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

    private class MultiThreadingDemo2 implements Runnable {

        private int threadNumber;

        private MultiThreadingDemo2(int threadNumber) {
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            for (int test = 1; test <= Main.SIZE; test++) {
                int rowCount = 0;
                int lastRow = 0;
                for (int row = 0; row < Main.SIZE; row++) {
                    if (board[row][threadNumber].getValue() == test) {
                        rowCount = 0;
                        break;
                    }

                    if (board[row][threadNumber].getPossibleValues().contains(test)) {
                        rowCount++;
                        lastRow = row;
                    }
                }

                if (rowCount == 1) {
                    setValue(board, lastRow, threadNumber, test);
                }

                int colCount = 0;
                int lastCol = 0;
                for (int col = 0; col < Main.SIZE; col++) {
                    if (board[threadNumber][col].getValue() == test) {
                        colCount = 0;
                        break;
                    }

                    if (board[threadNumber][col].getPossibleValues().contains(test)) {
                        colCount++;
                        lastCol = col;
                    }
                }

                if (colCount == 1) {
                    setValue(board, threadNumber, lastCol, test);
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
            for (int squareCol = 0; squareCol < Main.MINI_SIZE; squareCol++) {
                for (int test = 1; test <= Main.SIZE; test++) {
                    int count = 0;
                    int lastRow = -1;
                    int lastCol = -1;
                    boolean sameRow = true;
                    boolean sameCol = true;

                    for (int i = 0; i < Main.SIZE; i++) {
                        int row = squareRow * Main.MINI_SIZE + (i / Main.MINI_SIZE);
                        int col = squareCol * Main.MINI_SIZE + (i % Main.MINI_SIZE);
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
//                            System.out.println("All values of " + test + " occur in the same row for [" + lastRow + "][" + lastCol + "]");
                        for (int i = 0; i < Main.SIZE; i++) {
                            if (!(i >= squareCol * Main.MINI_SIZE && i < (squareCol + 1) * Main.MINI_SIZE)) {
                                board[lastRow][i].updatePossibleValue(test, false);
                            }
                        }
                    }

                    if (sameCol) {
//                            System.out.println("All values of " + test + " occur in the same column for [" + lastRow + "][" + lastCol + "]");
                        for (int i = 0; i < Main.SIZE; i++) {
                            if (!(i >= squareRow * Main.MINI_SIZE && i < (squareRow + 1) * Main.MINI_SIZE)) {
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

    ReturnStruct trySolveParallel(int[][] inputBoard, boolean debug) {
        board = Utils.setupBoard(inputBoard);
        int currentFound;
        int numThreads = Main.SIZE;
        Thread[] threads = new Thread[numThreads];

        do {
            currentFound = Main.numberSet;

            // Add all possible values to grid
            for (int row = 0; row < Main.SIZE; row++) {
                for (int col = 0; col < Main.SIZE; col++) {
                    Tile currTile = board[row][col];
                    if (currTile.getValue() != 0) {
                        continue;
                    }

                    for (int test = 1; test <= Main.SIZE; test++) {
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

            for (int i = 0; i < numThreads; i++) {
                threads[i] = new Thread(new MultiThreadingDemo(i));
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

            for (int i = 0; i < numThreads; i++) {
                threads[i] = new Thread(new MultiThreadingDemo2(i));
                threads[i].start();
            }

            for (int i = 0; i < numThreads; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < Main.MINI_SIZE; i++) {
                threads[i] = new Thread(new MultiThreadingDemo3(i));
                threads[i].start();
            }

            for (int i = 0; i < numThreads; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

//            Utils.printBoard(board);
        } while (!Utils.isComplete() && Main.numberSet != currentFound);


//        Utils.printBoard(board);

        return new ReturnStruct(Utils.isComplete(), board);
    }

    private void setValue(Tile[][] board, int row, int col, int value) {
        Tile tile = board[row][col];
        int updatedValue = value > 0 ? value : tile.getPossibleValues().get(0);
        tile.setValue(updatedValue);

        for (int r = 0; r < board.length; r++) {
            board[r][col].updatePossibleValue(updatedValue, false);
        }

        for (int c = 0; c < board.length; c++) {
            board[row][c].updatePossibleValue(updatedValue, false);
        }

        int squareRow = row / Main.MINI_SIZE;
        int squareCol = col / Main.MINI_SIZE;

        for (int i = 0; i < Main.SIZE; i++) {
            int r = squareRow * Main.MINI_SIZE + (i / Main.MINI_SIZE);
            int c = squareCol * Main.MINI_SIZE + (i % Main.MINI_SIZE);

            board[r][c].updatePossibleValue(updatedValue, false);
        }

        Main.numberSet++;

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
