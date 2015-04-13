/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku.solver.desktopedition;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author ramazan
 */
public class PuzzleCell {
    private static final Logger LOGGER = Logger.getLogger(PuzzleCell.class);
    private final Set<Integer> values;
    private final int index;
    private final int rowIndex;
    private final int columnIndex;
    private final int squareIndex;
    private int sudokuSize;
    private ViewCell rec;


    public PuzzleCell(int index, int sudokuSize, int cellGoupCount) {
        this.index = index;
        this.sudokuSize = sudokuSize;

        rowIndex = index / cellGoupCount;
        columnIndex = index % cellGoupCount;
        squareIndex = ((int) (rowIndex / sudokuSize)) * 3 + (int) (columnIndex / sudokuSize);

        values = new HashSet<Integer>();

        for (int i = 0; i < sudokuSize * sudokuSize; i++) {
            values.add(i + 1);
        }
        rec = null;
    }

    //Inserts     
    protected void assign(int value) {
        values.clear();
        values.add(value);
    }

    protected void fillAllValues() {
        values.clear();
        for (int i = 0; i < sudokuSize * sudokuSize; i++) {
            values.add(i + 1);
        }
    }

    protected void loadVersion(List<Integer> version) {
        if (version == null) return;
        values.clear();
        for (Integer v : version) {
            values.add(v);
        }
    }

    //Removes    
    protected boolean remove(int tobeRemovedValue) {
        if (values.contains(tobeRemovedValue)) {
            values.remove(tobeRemovedValue);
            return true;
        }
        return false;
    }

    //ReadOnly
    public int getIndex() {
        return index;
    }

    public Set<Integer> getValues() {
        return values;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getSquareIndex() {
        return squareIndex;
    }

    public boolean isPossible(int value) {
        return values.contains(value);
    }

    public int getValue() {
        return (values.size() != 1) ? Integer.MAX_VALUE : getValueList()[0];
    }

    public int getPossibleValueCount() {
        return values.size();
    }

    public Integer[] getValueList() {
        return (Integer[]) values.toArray(new Integer[0]);
    }

    public boolean isEqual(PuzzleCell other) {
        if (getPossibleValueCount() != other.getPossibleValueCount())
            return false;
        for (Integer i : values) {
            if (!other.isPossible(i))
                return false;
        }

        return true;
    }

    public ViewCell getRec() {
        return rec;
    }

    public void setRec(ViewCell rec) {
        this.rec = rec;
    }

    public int getSudokuSize() {
        return sudokuSize;
    }

    public void setSudokuSize(int sudokuSize) {
        this.sudokuSize = sudokuSize;
    }

    public void copyValues(List<Integer> cvlist) {
        for (Integer v : values) {
            cvlist.add(v);
        }
    }
}
