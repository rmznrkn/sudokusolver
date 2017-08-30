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
    private static PuzzleCell selectedCell;
    private boolean freeze = false;
    private boolean lastClicked;
    private boolean hitByUser = false;
    private int valueSetByUser = Integer.MAX_VALUE;
    private boolean highlight = false;
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException{
        ois.defaultReadObject();
        values = (Set<Integer>)ois.readObject();
        setLastClicked(ois.readBoolean());
        freeze = ois.readBoolean();
        setValueSetByUser(ois.readInt());
    }

    private void writeObject(ObjectOutputStream oos) throws IOException{
        oos.defaultWriteObject();
        oos.writeObject(values);
        oos.writeBoolean(isLastClicked());
        oos.writeBoolean(freeze);
        oos.writeInt(valueSetByUser);
    }

    public PuzzleCell(PuzzleCell sell) {

    }

    public PuzzleCell(){

    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public static PuzzleCell getSelectedCell() {
        return selectedCell;
    }

    public boolean isHitByUser() {
        return hitByUser;
    }

    public void setHitByUser(boolean hitByUser) {
        this.hitByUser = hitByUser;
    }

    public int getValueSetByUser() {
        return valueSetByUser;
    }

    public void setValueSetByUser(int valueSetByUser) {
        this.valueSetByUser = valueSetByUser;
    }

    public boolean isSetByUser(){
        return valueSetByUser != Integer.MAX_VALUE && valueSetByUser == getValue();
    }

    public void clear() {
        values.clear();
        setHitByUser(false);
        resetSelected();
        freeze = false;
        setLastClicked(false);
        setValueSetByUser(Integer.MAX_VALUE);
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

        /*for (int i = 0; i < sudokuSize * sudokuSize; i++) {
            values.add(i + 1);
        }*/
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

    protected void setSelected(boolean modifyComponents){
        if (selectedCell == this)
            return;

        if(selectedCell != null){
            selectedCell.row.setSelected(false);
            selectedCell.column.setSelected(false);
            selectedCell.square.setSelected(false);
            selectedCell.setLastClicked(false);
        }

        if(modifyComponents) {
            row.setSelected(true);
            column.setSelected(true);
            square.setSelected(true);
        }

        setLastClicked(true);
        selectedCell = this;
    }

    public boolean isSelected(){
        return (row.isSelected() || column.isSelected() || square.isSelected() || this == selectedCell);
    }

    //Inserts
    protected boolean assign(int value) {
        if(isFriezed())
            return false;

        values.clear();
        values.add(value);
        return true;
    }

    protected void assignAll(PuzzleCell otherThenThis, int value) {
        row.assignAll(otherThenThis, value);
        column.assignAll(otherThenThis,value);
        square.assignAll(otherThenThis, value);
    }

    public void assignValue(int value, boolean isTry){
        if(!isTry)
            values.clear();
        if (!values.contains(value)) {
            values.add(value);
        }
    }

    public void removeValue(int value){
        if (values.contains(value)) {
            values.remove(value);
        }
    }

    protected void fillAllValues() {
        if(isFriezed())
            return;
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
            setSelected(true);
        }
        freeze = other.isFriezed();
        setValueSetByUser(other.getValueSetByUser());
    }

    //Removes    
    protected boolean remove(int tobeRemovedValue) {
        if(isFriezed())
            return false;

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
        boolean  presentInRow = row.isValueAssigned(otherThenThis, value);
        boolean  presentInColumn = column.isValueAssigned(otherThenThis,value);
        boolean  presentInSquare = square.isValueAssigned(otherThenThis,value);

        if(isFriezed())
            setHitByUser(true);

        return !isFriezed() &&
                !presentInRow &&
                !presentInColumn &&
                !presentInSquare;
    }

    public boolean isVersionsEqual(PuzzleCell other){
        return (other != null &&
                freeze == other.isFriezed() &&
                valueSetByUser == other.getValueSetByUser() &&
                isSelected() == other.isSelected() &&
                values.size() == other.getValues().size() &&
                values.containsAll(other.getValues()));
    }

    @Override
    public void validateObject() throws InvalidObjectException {
        if(values == null) throw new InvalidObjectException("values can not be null");
    }

    public void setFreeze(boolean freeze, boolean checkPossibleValueCount) {
        if(freeze) {
            if (checkPossibleValueCount && getPossibleValueCount() == 1) {
                setValueSetByUser(getValue());
            }

            if (isSetByUser())
                this.freeze = true;
        } else
            this.freeze = freeze;
    }

    public boolean isFriezed(){
        return freeze;
    }
}
