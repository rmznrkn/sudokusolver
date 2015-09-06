package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ViewCellMap extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(ViewCellMap.class);
    private final List<ViewCell> unindexedRecs;
    private final PuzzleMap objectMap;
    private final int sudokuSize;
    private int mapTopX;
    private int mapTopY;
    private int cellHeight;
    private int cellWith;

    public ViewCellMap(int sudokuSize) {
        this.sudokuSize = sudokuSize;
        unindexedRecs = new ArrayList<ViewCell>();
        objectMap = new PuzzleMap(sudokuSize);
    }

    public void createSudoku() {
        org.ini4j.Wini ini = null;
        String font = "Courier New";
        Color cellBorderLine = Color.LIGHT_GRAY;
        Color cellFill = Color.WHITE;
        Integer cellLineTickness = 1;
        Color singleValue = Color.ORANGE;
        Color multiValue = Color.BLUE;
        Color squareFill = Color.LIGHT_GRAY;
        Color squareBorderLine = Color.BLACK;
        Integer squareLineTickness = 3;

        try {
            ini = new Wini(ViewCellMap.class.getClassLoader().getResourceAsStream("config/sudoku-desktopedition.ini"));
            Section section = ini.get("sudokumap");

            font = section.get("font");

            cellBorderLine = new Color(Integer.parseInt(section.get("cellBorderLine"),16));
            cellFill = new Color(Integer.parseInt(section.get("cellFill"),16));
            cellLineTickness = Integer.parseInt(section.get("cellLineTickness"),16);
            singleValue = new Color(Integer.parseInt(section.get("singleValue"),16));
            multiValue = new Color(Integer.parseInt(section.get("multiValue"),16));
            squareFill = new Color(Integer.parseInt(section.get("squareFill"),16));
            squareBorderLine = new Color(Integer.parseInt(section.get("squareBorderLine"),16));
            squareLineTickness =Integer.parseInt(section.get("squareLineTickness"),16);

        }catch (Exception e){
            LOGGER.error(e.toString());
            LOGGER.error(e.getMessage());
        }

        for (int n = 0; n < sudokuSize * sudokuSize; n++) {
            for (int m = 0; m < sudokuSize * sudokuSize; m++) {
                addRec(objectMap.getCell(m, n),
                        n * cellWith + mapTopX, m * cellHeight + mapTopY, cellWith, cellHeight,
                        cellBorderLine, cellLineTickness,
                        cellFill, singleValue, multiValue,font);
            }
        }
        int with = sudokuSize * cellWith;
        int height = sudokuSize * cellHeight;
        for (int i = 0; i < sudokuSize; i++) {
            for (int j = 0; j < sudokuSize; j++) {
                addRec(null,
                        i * with + mapTopX, j * height + mapTopY, with, height,
                        squareBorderLine, squareLineTickness,
                        squareFill, singleValue,multiValue,font);
            }
        }
    }

    public void relayout(){
        unindexedRecs.clear();
        createSudoku();
    }
    public int getMapTopX() {
        return mapTopX;
    }

    public void setMapTopX(int mapTopX) {
        this.mapTopX = mapTopX;
    }

    public int getMapTopY() {
        return mapTopY;
    }

    public void setMapTopY(int mapTopY) {
        this.mapTopY = mapTopY;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getCellWith() {
        return cellWith;
    }

    public void setCellWith(int cellWith) {
        this.cellWith = cellWith;
    }

    public void addRec(PuzzleCell puzzleCell,
                       int x, int y, int w, int h,
                       Color lineColor, int lineSize,
                       Color fillColor, Color textColorSingleValue,
                       Color textColorMultiValue,
                       String textFont) {

        ViewCell rec = new ViewCell(puzzleCell, x, y, w, h,
                lineColor,lineSize,
                fillColor,textColorSingleValue,
                textColorMultiValue,
                textFont, this.getGraphics());

        if (puzzleCell == null)
            unindexedRecs.add(rec);
        else {
            puzzleCell.setRec(rec);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (PuzzleCell puzzleCell : objectMap.getPuzzleCells()) {
            puzzleCell.getRec().paint(g);
        }

        for (ViewCell rec : unindexedRecs) {
            rec.paint(g);
        }
    }

    private PuzzleCell getPuzzleCell(Point point){
        int c = (point.x - mapTopX) / cellWith;
        int r = (point.y - mapTopY) / cellHeight;

        return objectMap.getCell(r, c);
    }

    public void delete(Point point, int value, boolean isTry) {
        PuzzleCell puzzleCell = getPuzzleCell(point);
        if (puzzleCell == null) {
            LOGGER.warn(point);
            LOGGER.warn(puzzleCell);
            return;
        }
        objectMap.clearCell(puzzleCell, value, isTry);
    }

    public void delete(int value, boolean isTry) {
        PuzzleCell puzzleCell = PuzzleCell.getSelectedCell();
        if (puzzleCell == null) {
            return;
        }
        objectMap.clearCell(puzzleCell, value, isTry);
    }

    public void clearMap() {
        objectMap.clear();
    }

    public void fillPossibleValues(boolean isTry, boolean isSimplify) {
        objectMap.fillPossibleValues(isTry, isSimplify);
    }

    public void check() {
        objectMap.check();
    }

    public void setRecValue(Point point, int value, boolean isTry, boolean isSimplify, boolean isHighlight) {
        PuzzleCell puzzleCell = getPuzzleCell(point);
        if (puzzleCell == null) {
            LOGGER.warn(point);
            LOGGER.warn(puzzleCell);
            return;
        }

        objectMap.setValue(puzzleCell, value, isTry, true, isSimplify, isHighlight);
    }

    public void setRecValue(int value, boolean isTry, boolean isSimplify,  boolean isHighlight) {
        PuzzleCell puzzleCell = PuzzleCell.getSelectedCell();
        if (puzzleCell == null) {
            return;
        }
        objectMap.setValue(puzzleCell, value, isTry, false, isSimplify, isHighlight);
    }

    public void changeSelection(int deltaRow, int deltaColumn) {

        PuzzleCell puzzleCell = PuzzleCell.getSelectedCell();
        if (puzzleCell == null) {
            return;
        }

        puzzleCell = objectMap.getCell(puzzleCell.getRowIndex()+deltaRow, puzzleCell.getColumnIndex()+deltaColumn);

        if(puzzleCell != null)
            puzzleCell.setSelected(false);
    }

    public void changeSelection(Point point, boolean isHighlight) {

        PuzzleCell puzzleCell = getPuzzleCell(point);
        if (puzzleCell == null) {
            return;
        }

        if(puzzleCell != null) {
            objectMap.selectValue(puzzleCell, isHighlight);
            puzzleCell.setSelected(false);
        }
    }

    public List<ViewCell> getUnindexedRecs() {
        return unindexedRecs;
    }

    public PuzzleMap getObjectMap() {
        return objectMap;
    }

    public int getSudokuSize() {
        return sudokuSize;
    }

}
