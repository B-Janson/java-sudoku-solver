import java.io.*;
import java.util.*;

public class Solution {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        new Solution().run();
    }

    private int inputBoard[][] = {
            {0,0,0,4,1,0,0,6,0},
            {4,0,0,0,8,0,0,9,2},
            {0,0,8,0,0,2,0,0,4},
            {0,0,0,8,0,0,0,0,0},
            {0,6,3,0,0,0,9,2,0},
            {0,0,0,0,0,5,0,0,0},
            {7,0,0,2,0,0,1,0,0},
            {9,4,0,0,5,0,0,0,7},
            {0,5,0,0,7,6,0,0,0}
    };

    private Tile board[][] = new Tile[9][9];

    private int numberSet = 0;

    private void setupBoard() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = new Tile();
                if (inputBoard[row][col] != 0) {
                    board[row][col].setValue(inputBoard[row][col]);
                    numberSet++;
                }
            }
        }
    }

    private void setValue(Tile tile, int row, int col, int value) {
        int updatedValue = value > 0 ? value : tile.getPossibleValues().get(0);
        tile.setValue(updatedValue);

        for (int r = 0; r < board.length; r++) {
            board[r][col].removePossibleValue(updatedValue);
        }

        for (int c = 0; c < board.length; c++) {
            board[row][c].removePossibleValue(updatedValue);
        }

        numberSet++;

        System.out.println("row: " + row + " col: " + col + " = " + updatedValue);

        tile.clearPossibleValues();
    }

    private void run() {
        setupBoard();

        int numRuns = 0;

        while (!isComplete() && numRuns < 10) {
            printBoard();
            numRuns++;

            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[row].length; col++) {
                    Tile currTile = board[row][col];
                    if (currTile.getValue() != 0) {
                        continue;
                    }

                    for (int test = 1; test <= 9; test++) {
                        if (isValidFor(test, row, col)) {
                            System.out.printf("%d is valid for [%d][%d]\n", test, row, col);
                            if (numRuns == 1) {
                                currTile.addPossibleValue(test);
                            }
                        } else {
                            currTile.removePossibleValue(test);
                        }
                    }

                    if (currTile.getPossibleValues().size() == 1) {
                        setValue(currTile, row, col, 0);
                    }
                }
            }

            for (int col = 0; col < board.length; col++) {
                for (int test = 1; test <= 9; test++) {
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
                        setValue(board[lastRow][col], lastRow, col, test);
                    }
                }
            }

            for (int row = 0; row < board.length; row++) {
                for (int test = 1; test <= 9; test++) {
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
                        setValue(board[row][lastCol], row, lastCol, test);
                    }
                }
            }

            for (int squareRow = 0; squareRow < 3; squareRow++) {
                for (int squareCol = 0; squareCol < 3; squareCol++) {
                    for (int test = 1; test <= 9; test++) {
                        int count = 0;
                        int lastRow = -1;
                        int lastCol = -1;
                        boolean sameRow = true;
                        boolean sameCol = true;

                        for (int i = 0; i < 9; i++) {
                            int row = squareRow * 3 + (i / 3);
                            int col = squareCol * 3 + (i % 3);

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
                            System.out.println("All values of " + test + " occur in the same row for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < 9; i++) {
                                if (!(i >= squareCol * 3 && i < (squareCol + 1) * 3)) {
                                    board[lastRow][i].removePossibleValue(test);
                                }
                            }
                        }

                        if (sameCol) {
                            System.out.println("All values of " + test + " occur in the same column for [" + lastRow + "][" + lastCol + "]");
                            for (int i = 0; i < 9; i++) {
                                if (!(i >= squareRow * 3 && i < (squareRow + 1) * 3)) {
                                    board[i][lastCol].removePossibleValue(test);
                                }
                            }
                        }

                        if (count == 1) {
                            setValue(board[lastRow][lastCol], lastRow, lastCol, test);
                        }
                    }
                }
            }

//            int
        }

        if (isComplete()) {
            System.out.println("Completed in " + numRuns + " iterations.");
        } else {
            System.out.println("Stopped after " + numRuns + " iterations.");
        }

        printBoard();
    }

    private boolean isValidFor(int test, int given_row, int given_col) {
        for (int col = 0; col < board[given_row].length; col++) {
            if (board[given_row][col].getValue() == test) {
                return false;
            }
        }

        for (Tile[] aBoard : board) {
            if (aBoard[given_col].getValue() == test) {
                return false;
            }
        }

        int boxRow = given_row / 3;
        int boxCol = given_col / 3;

        for (int row = boxRow * 3; row < boxRow * 3 + 3; row++) {
            for (int col = boxCol * 3; col < boxCol * 3 + 3; col++) {
                if (board[row][col].getValue() == test) {
                    return false;
                }
            }
        }

        return true;
    }

    private void printBoard() {
        for (Tile[] boardRow : board) {
            for (Tile tile : boardRow) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }

        System.out.println();

        for (int row = 0; row < board.length; row++) {
            int count = 0;
            for (int col = 0; col < board[row].length; col++) {
                Tile tile = board[row][col];
                if (tile.getValue() == 0) {
                    System.out.printf("[%d][%d]: ", row, col);
                    tile.printPossibleValues();
                    count++;
                }
            }
            if (count != 0) {
                System.out.println();
            }
        }
    }

    private boolean isComplete() {
        return numberSet == 9 * 9;
    }
}
