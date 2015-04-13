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
                addRec(objectMap.getCell(m, n), n * cellWith + mapTopX, m * cellHeight + mapTopY, cellWith, cellHeight, Color.LIGHT_GRAY);
            }
        }
        int squareSize = sudokuSize * cellWith;
        for (int i = 0; i < sudokuSize; i++) {
            for (int j = 0; j < sudokuSize; j++) {
                addRec(null, i * squareSize + mapTopX, j * squareSize + mapTopY, squareSize, squareSize, Color.BLACK);
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

    public void addRec(PuzzleCell puzzleCell, int x, int y, int w, int h, Color c) {
        if (puzzleCell != null && puzzleCell.getRec() != null)
            return;

        ViewCell rec = new ViewCell(x, y, w, h);
        rec.setClr(c);
        rec.createRecs();
        rec.setClr(c);
        rec.setPuzzleCell(puzzleCell);

        if (puzzleCell == null)
            unindexedRecs.add(rec);
        else {
            puzzleCell.setRec(rec);
        }
    }

    private void paintRec(Graphics g, ViewCell rec) {
        int x = rec.getRectangle().x;
        int y = rec.getRectangle().y;
        int w = rec.getRectangle().width;
        int h = rec.getRectangle().height;
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(rec.getClr());
        g2.setStroke(new BasicStroke(3));
        g.drawRect(x, y, w, h);
    }

    private void paintCell(Graphics g, PuzzleCell puzzleCell) {

        ViewCell rec = puzzleCell.getRec();

        int x = rec.getRectangle().x;
        int y = rec.getRectangle().y;
        int w = rec.getRectangle().width;
        int h = rec.getRectangle().height;
        Graphics2D g2 = (Graphics2D) g;

        g.setColor(Color.WHITE);
        g2.fillRect(x + 1, y + 1, w - 2, h - 2);
        g2.setStroke(new BasicStroke(1));
        g.setColor(rec.getClr());
        g.drawRect(x, y, w, h);

        if (puzzleCell.getPossibleValueCount() == 1) {
            paintText(puzzleCell.getValue() + "", g, x, y, Color.orange, w, h);
        } else {
            Integer vlist[] = puzzleCell.getValueList();
            for (Integer v : vlist) {
                int tw = w / puzzleCell.getSudokuSize();
                int th = h / puzzleCell.getSudokuSize();
                int textX = x + ((int) (v - 1) % puzzleCell.getSudokuSize()) * tw;
                int textY = y + ((int) (v - 1) / puzzleCell.getSudokuSize()) * th;
                paintText(v + "", g, textX, textY, Color.blue, tw, th);
            }
        }
    }

    private void paintText(String str, Graphics g, int x, int y, Color textColor, int w, int h) {
        Font holdFont = g.getFont();
        int size = (w > h) ? h : w;
        Font font = new Font("Coruer new", Font.CENTER_BASELINE, size);
        g.setFont(font);
        g.setColor(textColor);
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(str)) / 2;
        int ty = y + (fm.getAscent() + (w - (fm.getAscent() + fm.getDescent())) / 2);
        g.drawString(str, tx, ty);
        g.setFont(holdFont);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (PuzzleCell puzzleCell : objectMap.getPuzzleCells()) {
            paintCell(g, puzzleCell);
        }

        for (ViewCell rec : unindexedRecs) {
            paintRec(g, rec);
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
