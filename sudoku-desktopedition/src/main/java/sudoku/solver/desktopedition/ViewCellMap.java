package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

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
        for (int n = 0; n < sudokuSize * sudokuSize; n++) {
            for (int m = 0; m < sudokuSize * sudokuSize; m++) {
                addRec(objectMap.getCell(m, n),
                        n * cellWith + mapTopX, m * cellHeight + mapTopY, cellWith, cellHeight,
                        Color.LIGHT_GRAY, 1,
                        Color.WHITE, Color.orange,Color.blue,"Courier New");
            }
        }
        int with = sudokuSize * cellWith;
        int height = sudokuSize * cellHeight;
        for (int i = 0; i < sudokuSize; i++) {
            for (int j = 0; j < sudokuSize; j++) {
                addRec(null,
                        i * with + mapTopX, j * height + mapTopY, with, height,
                        Color.BLACK, 3,
                        Color.LIGHT_GRAY, Color.orange,Color.blue,"Courier New");
            }
        }
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

    public void delete(Point point) {
        int c = (point.x - mapTopX) / cellWith;
        int r = (point.y - mapTopY) / cellHeight;

        PuzzleCell puzzleCell = objectMap.getCell(r, c);
        if (puzzleCell == null) {
            System.out.println("PuzzleCell == null; Point:" + point.x + "," + point.y + "; (r,c):(" + r + "," + c + ")");
            return;
        }
        objectMap.deleteValue(puzzleCell);
    }

    public void setRecValue(Point point, int value) {
        int c = (point.x - mapTopX) / cellWith;
        int r = (point.y - mapTopY) / cellHeight;

        PuzzleCell puzzleCell = objectMap.getCell(r, c);
        if (puzzleCell == null) {
            System.out.println("PuzzleCell == null; Point:" + point.x + "," + point.y + "; (r,c):(" + r + "," + c + ")");
            return;
        }

        objectMap.setValue(puzzleCell, value);
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
