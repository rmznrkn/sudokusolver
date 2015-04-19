package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

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
        return puzzleCells.get(row * cellCountPerGroup + column);
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

        if(isSatisfied()){
            freeze();
        }

        LOGGER.debug(puzzleCell);

        puzzleCell.setSelected();
        puzzleCell.setValueSetByUser(value);

    }

    private boolean isSatisfied() {
        boolean isSatisfied = true;
        for (int i = 0; i < cellCountPerGroup; i++) {

            if (!squares.get(i).isSatisfied()) {
                isSatisfied = false;
                break;
            }
            if (!rows.get(i).isSatisfied()) {
                isSatisfied = false;
                break;
            }
            if (!columns.get(i).isSatisfied()){
                isSatisfied = false;
                break;
            }
        }
        return isSatisfied;
    }

    public void deleteValue(PuzzleCell puzzleCell) {
        if (puzzleCell.getPossibleValueCount() == sudokuSize * sudokuSize) {
            puzzleCell.setSelected();
            return;
        }

        PuzzleCellHistoryCache.saveVersion(puzzleCells);

        if(puzzleCell.getValues().size() == 1)
            puzzleCell.assignAll(puzzleCell, puzzleCell.getValue());

        puzzleCell.fillAllValues();

        LOGGER.debug(puzzleCell);

        simplify();

        LOGGER.debug(puzzleCell);

        puzzleCell.setSelected();

        puzzleCell.setValueSetByUser(Integer.MAX_VALUE);
        if(isSatisfied()){
            freeze();
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

    public void freeze(){
        for(PuzzleCell cell: puzzleCells){
            cell.freeze();
        }
    }
}
