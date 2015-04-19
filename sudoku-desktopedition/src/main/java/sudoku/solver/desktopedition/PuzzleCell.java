/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku.solver.desktopedition;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author ramazan
 */
public class PuzzleCell implements Serializable, ObjectInputValidation {
    private static final Logger LOGGER = Logger.getLogger(PuzzleCell.class);
    private Set<Integer> values;
    private int index;
    private int rowIndex;
    private int columnIndex;
    private int squareIndex;
    private int sudokuSize;
    private ViewCell rec;
    private PuzzleCellGroup row;
    private PuzzleCellGroup column;
    private PuzzleCellGroup square;
    private boolean lastClicked;
    private static PuzzleCell selectedCell;
    public PuzzleCell(){

    }
    public static void resetSelected(){
        if(selectedCell != null){
            selectedCell.row.setSelected(false);
            selectedCell.column.setSelected(false);
            selectedCell.square.setSelected(false);
            selectedCell.setLastClicked(false);
        }

        selectedCell = null;
    }
    public PuzzleCell(int index, int sudokuSize, int cellGoupCount) {
        this.index = index;
        this.sudokuSize = sudokuSize;

        rowIndex = index / cellGoupCount;
        columnIndex = index % cellGoupCount;
        squareIndex = ((int) (rowIndex / sudokuSize)) * sudokuSize + (int) (columnIndex / sudokuSize);

        values = new HashSet<Integer>();

        for (int i = 0; i < sudokuSize * sudokuSize; i++) {
            values.add(i + 1);
        }
        rec = null;
    }

    public boolean isLastClicked() {
        return lastClicked;
    }

    public void setLastClicked(boolean lastClicked) {
        this.lastClicked = lastClicked;
    }

    public PuzzleCellGroup getRow() {
        return row;
    }

    public void setRow(PuzzleCellGroup row) {
        this.row = row;
    }

    public PuzzleCellGroup getColumn() {
        return column;
    }

    public void setColumn(PuzzleCellGroup column) {
        this.column = column;
    }

    public PuzzleCellGroup getSquare() {
        return square;
    }

    public void setSquare(PuzzleCellGroup square) {
        this.square = square;
    }

    protected void removeAssignedCell(int value){
        row.removeAssignedCell(value);
        column.removeAssignedCell(value);
        square.removeAssignedCell(value);
    }

    protected void setSelected(){
        if (selectedCell == this)
            return;

        if(selectedCell != null){
            selectedCell.row.setSelected(false);
            selectedCell.column.setSelected(false);
            selectedCell.square.setSelected(false);
            selectedCell.setLastClicked(false);
        }

        row.setSelected(true);
        column.setSelected(true);
        square.setSelected(true);
        setLastClicked(true);
        selectedCell = this;
    }

    public boolean isSelected(){
        return (row.isSelected() || column.isSelected() || square.isSelected());
    }

    //Inserts
    protected void assign(int value) {
        values.clear();
        values.add(value);
    }

    protected void assignAll(PuzzleCell otherThenThis, int value) {
        row.assignAll(otherThenThis, value);
        column.assignAll(otherThenThis,value);
        square.assignAll(otherThenThis, value);
    }

    protected void fillAllValues() {
        values.clear();
        for (int i = 0; i < sudokuSize * sudokuSize; i++) {
            if(isAddable(this, i+1))
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

    protected void loadVersion(PuzzleCell other) {
        if (other == null) return;
        values.clear();
        values.addAll(other.getValues());
        if(other.isLastClicked()) {
            setSelected();
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
        return (values.size() != 1) ? Integer.MAX_VALUE : values.iterator().next();
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

    public String toString() {
        String str = String.format("Cell[ %2d,%2d,%2d] = ",rowIndex, columnIndex, squareIndex);
        for (int i = 0; i < sudokuSize*sudokuSize; i++) {
            str += (values.contains(i+1))?String.format("%d", i+1):" ";
        }
        return str;
    }

    public boolean isAddable(PuzzleCell otherThenThis, int value) {
        return !row.isValueAssigned(otherThenThis, value) || !column.isValueAssigned(otherThenThis,value) ||  !square.isValueAssigned(otherThenThis,value);
    }

    public boolean isVersionsEqual(PuzzleCell other){
        return (other != null &&
                isSelected() == other.isSelected() &&
                values.size() == other.getValues().size() &&
                values.containsAll(other.getValues()));
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException{
        ois.defaultReadObject();
        values = (Set<Integer>)ois.readObject();
        setLastClicked(ois.readBoolean());
    }

    private void writeObject(ObjectOutputStream oos) throws IOException{
        oos.defaultWriteObject();
        oos.writeObject(values);
        oos.writeBoolean(isLastClicked());
    }

    @Override
    public void validateObject() throws InvalidObjectException {
        if(values == null) throw new InvalidObjectException("values can not be null");
    }
}
