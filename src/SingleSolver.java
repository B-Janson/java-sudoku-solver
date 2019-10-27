class SingleSolver {

    private int miniSize;
    private int size;

    ReturnStruct trySolve(int[][] inputBoard, boolean debug, int miniSize) {
        Tile[][] board = Utils.setupBoard(inputBoard);
        this.miniSize = miniSize;
        this.size = miniSize * miniSize;
        int currentFound;
        int numRuns = 0;

        do {
            if (debug)
                Utils.printBoard(board);
            numRuns++;
//            System.out.println("Iteration " + numRuns);
            currentFound = Main.numberSet;

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    Tile currTile = board[row][col];
                    if (currTile.getValue() != 0) {
                        continue;
                    }

                    for (int test = 1; test <= size; test++) {
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
                for (int test = 1; test <= size; test++) {
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
                for (int test = 1; test <= size; test++) {
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

            for (int squareRow = 0; squareRow < miniSize; squareRow++) {
                for (int squareCol = 0; squareCol < miniSize; squareCol++) {
                    for (int test = 1; test <= size; test++) {
                        int count = 0;
                        int lastRow = -1;
                        int lastCol = -1;
                        boolean sameRow = true;
                        boolean sameCol = true;

                        for (int i = 0; i <size; i++) {
                            int row = squareRow * miniSize + (i / miniSize);
                            int col = squareCol * miniSize+ (i % miniSize);

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
                            for (int i = 0; i < size; i++) {
                                if (!(i >= squareCol * miniSize && i < (squareCol + 1) * miniSize)) {
                                    board[lastRow][i].removePossibleValue(test);
                                }
                            }
                        }

                        if (sameCol) {
//                            System.out.println("All values of " + test + " occur in the same column for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < size; i++) {
                                if (!(i >= squareRow * miniSize && i < (squareRow + 1) * miniSize)) {
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
        } while (!Utils.isComplete(size) && Main.numberSet != currentFound);
        if (debug) {
            Utils.printBoard(board);
        }
        return new ReturnStruct(Utils.isComplete(size), board);
    }

    private void setValue(Tile[][] board, int row, int col, int value) {
        Tile tile = board[row][col];
        int updatedValue = value > 0 ? value : tile.getPossibleValues().get(0);
        tile.setValue(updatedValue);

//        for (int r = 0; r < board.length; r++) {
//            board[r][col].removePossibleValue(updatedValue);
//        }
//
//        for (int c = 0; c < board.length; c++) {
//            board[row][c].removePossibleValue(updatedValue);
//        }

        Main.numberSet++;

//        System.out.println("row: " + row + " col: " + col + " = " + updatedValue);

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
