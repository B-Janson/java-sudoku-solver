import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Utils {

    static void saveBoardToCSV(int[][] board, String filename) {
        // Add all values to list of list
        List<List<String>> rows = new ArrayList<>();
        for (int[] row : board) {
            List<String> list = new ArrayList<>();
            for (int i : row) {
                list.add(i + "");
            }
            rows.add(list);
        }

        // Write each row to file
        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(filename);

            for (List<String> rowData : rows) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void verifyAgainstCSV(Tile[][] inputBoard, String filename) {
        BufferedReader csvReader;
        int inputVerification[][] = new int[Main.SIZE][Main.SIZE];
        try {
            csvReader = new BufferedReader(new FileReader(filename));
            String row;
            int r = 0;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                for (int i = 0; i < Main.SIZE; i++) {
                    inputVerification[r][i] = Integer.parseInt(data[i]);
                }
                r++;
            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean verified = true;

        for (int i = 0; i < Main.SIZE; i++) {
            for (int j = 0; j < Main.SIZE; j++) {
                if (inputBoard[i][j].getValue() != inputVerification[i][j]) {
                    verified = false;
                    System.err.println("Mismatch at " + i + " " + j);
                }
            }
        }

        if (!verified) {
            System.err.println("Errors found compared to ground truth. See above.");
        }
    }

    static Tile[][] setupBoard(int[][] inputBoard) {
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


    static void printBoard(Tile[][] board) {
        for (Tile[] boardRow : board) {
            for (Tile tile : boardRow) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }

        System.out.println("Number set: " + Main.numberSet + " out of " + (Main.SIZE * Main.SIZE) + " == " + (100.0 * Main.numberSet / (Main.SIZE * Main.SIZE)) + "%");
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

    static boolean isComplete() {
        return Main.numberSet == Main.SIZE * Main.SIZE;
    }
}
