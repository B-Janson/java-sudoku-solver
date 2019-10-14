import java.util.ArrayList;
import java.util.List;

public class Tile {

    private int value;
    private List<Integer> possibleValues;

    Tile() {
        value = 0;
        possibleValues = new ArrayList<>();
    }

    void printPossibleValues() {
        for (int i : possibleValues) {
            System.out.print(i + " ");
        }
    }

    List<Integer> getPossibleValues() {
        return possibleValues;
    }

    void clearPossibleValues() {
        possibleValues.clear();
    }

    synchronized void updatePossibleValue(int i, boolean add) {
        if (add) {
            addPossibleValue(i);
        } else {
            removePossibleValue(i);
        }
    }

    void addPossibleValue(int i) {
        if (!possibleValues.contains(i)) {
            possibleValues.add(i);
        }
    }

    void removePossibleValue(int i) {
        if (possibleValues.contains(i)) {
            possibleValues.remove((Integer) i);
        }
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%3d", value);
    }
}
