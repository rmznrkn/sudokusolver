package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ramazan
 */
public class PuzzleMap {
    private static final Logger LOGGER = Logger.getLogger(PuzzleMap.class);
    private final List<PuzzleCellGroup> squares;
    private final List<PuzzleCellGroup> rows;
    private final List<PuzzleCellGroup> columns;
    private final int sudokuSize;
    private final int cellCountPerMap;
    private final int cellCountPerGroup;
    private final List<PuzzleCell> puzzleCells;

    public PuzzleMap(int size) {

        sudokuSize = size;
        cellCountPerGroup = size * size;
        cellCountPerMap = cellCountPerGroup * cellCountPerGroup;

        squares = new ArrayList<PuzzleCellGroup>();
        rows = new ArrayList<PuzzleCellGroup>();
        columns = new ArrayList<PuzzleCellGroup>();
        puzzleCells = new ArrayList<PuzzleCell>();


        for (int i = 0; i < cellCountPerGroup; i++) {
            squares.add(new PuzzleCellGroup(cellCountPerGroup));
            rows.add(new PuzzleCellGroup(cellCountPerGroup));
            columns.add(new PuzzleCellGroup(cellCountPerGroup));
        }

        for (int i = 0; i < cellCountPerMap; i++) {
            PuzzleCell puzzleCell = new PuzzleCell(i, sudokuSize, cellCountPerGroup);
            insertCell(puzzleCell);
        }
    }

    public PuzzleCell getCell(int row, int column) {
        if(row * cellCountPerGroup + column >= 0 &&
                row * cellCountPerGroup + column < puzzleCells.size())
            return puzzleCells.get(row * cellCountPerGroup + column);
        else
            return null;
    }

    public void setValue(PuzzleCell puzzleCell, int value) {

        if (puzzleCell.isFriezed() || !puzzleCell.isAddable(puzzleCell, value)) {
            return;
        }

        PuzzleCellHistoryCache.saveVersion(puzzleCells);

        puzzleCell.removeAssignedCell(value);
        puzzleCell.assign(value);
        LOGGER.debug(puzzleCell);

        simplify();

        if(isSatisfied(false)){
            freeze(true, false);
        }

        LOGGER.debug(puzzleCell);

        puzzleCell.setSelected(true);
        puzzleCell.setValueSetByUser(value);

    }

    public void setValue(PuzzleCell puzzleCell, int value, boolean isTry,
                         boolean changeComponentSelection, boolean isSimplify,boolean isHighlight) {
        selectValue(puzzleCell,isHighlight);
        if (puzzleCell.isFriezed() || !puzzleCell.isAddable(puzzleCell, value)) {
            puzzleCell.setSelected(false);
            return;
        }

        PuzzleCellHistoryCache.saveVersion(puzzleCells);

        puzzleCell.assignValue(value, isTry);

        if(isSimplify)
            simplify();

        puzzleCell.setSelected(changeComponentSelection);
        puzzleCell.setValueSetByUser(isTry?Integer.MAX_VALUE:value);
    }

    public void clearCell(PuzzleCell puzzleCell, int value, boolean isTry) {
        if (puzzleCell.isFriezed()) {
            puzzleCell.setSelected(false);
            return;
        }
        PuzzleCellHistoryCache.saveVersion(puzzleCells);

        if(isTry && puzzleCell.getValues().size() > 1)
            puzzleCell.removeValue(value);
        else
            puzzleCell.clear();
        puzzleCell.setSelected(true);
    }

    private boolean isSatisfied(boolean checkPossibleValueCount) {
        boolean isSatisfied = true;
        for (int i = 0; i < cellCountPerGroup; i++) {

            if (!squares.get(i).isSatisfied(checkPossibleValueCount)) {
                isSatisfied = false;
                break;
            }
            /*if (!rows.get(i).isSatisfied()) {
                isSatisfied = false;
                break;
            }
            if (!columns.get(i).isSatisfied()){
                isSatisfied = false;
                break;
            }*/
        }
        return isSatisfied;
    }

    public void fillPossibleValues(PuzzleCell puzzleCell, boolean isTry) {
        if (puzzleCell.getPossibleValueCount() == sudokuSize * sudokuSize ||
                puzzleCell.getPossibleValueCount() == 1) {
            return;
        }

        puzzleCell.fillAllValues();

        puzzleCell.setValueSetByUser(isTry ? Integer.MAX_VALUE : puzzleCell.getValue());
    }

    public void clear() {
        PuzzleCellHistoryCache.saveVersion(puzzleCells);
        for(PuzzleCell cell: puzzleCells){
            if(!cell.isFriezed())
                cell.clear();
        }
    }

    public void fillPossibleValues(boolean isTry, boolean isSimplify) {
        PuzzleCellHistoryCache.saveVersion(puzzleCells);
        for(PuzzleCell cell: puzzleCells){
            if(!cell.isFriezed())
                fillPossibleValues(cell, isTry);
        }

        for(PuzzleCell cell: puzzleCells){
            cell.setHitByUser(false);
        }

        if(isSimplify)
            simplify();
    }

    public void selectValue(PuzzleCell selectedCell, boolean isHighlight) {

        for(PuzzleCell cell: puzzleCells){
            cell.getColumn().setSelected(false);
            cell.getRow().setSelected(false);
            cell.getSquare().setSelected(false);
            cell.setHitByUser(false);
            cell.setHighlight(false);
        }

        if(!isHighlight)
            return;

        if(!selectedCell.isFriezed()){
            return;
        }

        selectedCell.setHitByUser(true);

        for(PuzzleCell cell: puzzleCells){
            if(cell.getPossibleValueCount() != 1)
                continue;

            if(cell.getValue() == selectedCell.getValue()){
                cell.setHitByUser(true);
                cell.getColumn().setSelected(true);
                cell.getRow().setSelected(true);
                cell.getSquare().setSelected(true);
            } else {
                cell.setHighlight(isHighlight);
            }
        }
    }

    public void check() {
        PuzzleCellHistoryCache.saveVersion(puzzleCells);
        if(isSatisfied(true))
            freeze(true, true);
    }


    public void saveToFile(String fileName) {
        File file = new File(fileName);
        file.deleteOnExit();
        FileOutputStream  out;
        ObjectOutputStream oos;
        try {
            file.createNewFile();
            out = new FileOutputStream (fileName);
            oos = new ObjectOutputStream(out);
        } catch (IOException e) {
            LOGGER.info(e);
            return;
        }

        for(PuzzleCell cell: puzzleCells){
            try {
                SerializerUtil.serialize(cell, oos);
            } catch (IOException e) {
                file.deleteOnExit();
                LOGGER.error(e);
                return;
            }
        }
    }

    public void loadFromFile(String fileName) {
        File file = new File(fileName);
        FileInputStream inputStream;
        ObjectInputStream ois;
        try {
            inputStream = new FileInputStream(fileName);
            ois = new ObjectInputStream(inputStream);
        } catch (IOException e) {
            LOGGER.error(e);
            return;
        }

        try {
            for (PuzzleCell cell : puzzleCells) {
                try {
                    cell.loadVersion((PuzzleCell) SerializerUtil.deserialize(ois));
                    LOGGER.info("LOADED:" + cell);
                } catch (IOException e) {
                    LOGGER.error(e);
                    return;
                } catch (ClassNotFoundException e) {
                    LOGGER.error(e);
                    return;
                }
            }
        }finally {
            try {
                ois.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    public int getCellCountPerMap() {
        return cellCountPerMap;
    }

    public int getCellCountPerGroup() {
        return cellCountPerGroup;
    }


    public void simplify() {

        int doAgaint;
        do {
            doAgaint = 0;
            for (int i = 0; i < cellCountPerGroup; i++) {

                if (squares.get(i).simplify())
                    doAgaint++;
                if (rows.get(i).simplify())
                    doAgaint++;
                if (columns.get(i).simplify())
                    doAgaint++;
            }
        } while (doAgaint > 0);
    }

    public List<PuzzleCellGroup> getSquares() {
        return squares;
    }

    public List<PuzzleCellGroup> getRows() {
        return rows;
    }

    public List<PuzzleCellGroup> getColumns() {
        return columns;
    }

    public int getSudokuSize() {
        return sudokuSize;
    }

    public int getCellCount() {
        return cellCountPerMap;
    }

    public int getCellGoupCount() {
        return cellCountPerGroup;
    }

    public List<PuzzleCell> getPuzzleCells() {
        return puzzleCells;
    }

    private void insertCell(PuzzleCell puzzleCell) {
        puzzleCells.add(puzzleCell);
        PuzzleCellGroup container = rows.get(puzzleCell.getRowIndex());
        container.add(puzzleCell);
        puzzleCell.setRow(container);

        container = squares.get(puzzleCell.getSquareIndex());
        container.add(puzzleCell);
        puzzleCell.setSquare(container);

        container = columns.get(puzzleCell.getColumnIndex());
        container.add(puzzleCell);
        puzzleCell.setColumn(container);
    }

    public int getChangeCount() {
        int cnt = 0;
        for(PuzzleCell cell : puzzleCells){
            if(!cell.isFriezed() && cell.getValues().size() == 1)
                cnt++;
        }
        return cnt;
    }

    public void freeze(boolean value, boolean checkPossibleValueCount){
        for(PuzzleCell cell: puzzleCells){
            cell.freeze(value, checkPossibleValueCount);
        }
    }
}
