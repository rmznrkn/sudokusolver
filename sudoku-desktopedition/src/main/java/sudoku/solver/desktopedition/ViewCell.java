package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ViewCell implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(ViewCell.class);
    private final PuzzleCell puzzleCell;
    private final Rectangle cellRectangle;
    private final Map<Integer, Rectangle> valueToRectangle;
    private final Map<Integer, Rectangle> singleValueToRectangle;
    private Color lineColor;
    private Color fillColor;
    private Color textColorSingleValue;
    private Color textColorMultiValue;
    private String textFont;
    private int lineSize;
    private Font multiValueFont;
    private Font singleValueFont;
    private Color textColorFreezedValue = Color.DARK_GRAY;
    private Font freezedFont;

    public ViewCell(PuzzleCell puzzleCell,
                    int x, int y, int w, int h,
                    Color lineColor, int lineSize,
                    Color fillColor, Color textColorSingleValue,
                    Color textColorMultiValue,
                    String textFont,
                    Graphics graphics) {
        this.puzzleCell = puzzleCell;
        cellRectangle = new Rectangle(x, y, w, h);
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.textColorSingleValue = textColorSingleValue;
        this.textColorMultiValue = textColorMultiValue;
        this.textFont = textFont;
        this.lineSize = lineSize;

        if(this.puzzleCell == null) {
            valueToRectangle = null;
            singleValueToRectangle = null;
            return;
        }

        valueToRectangle = new HashMap<Integer, Rectangle>();
        singleValueToRectangle = new HashMap<Integer, Rectangle>();

        int size = (w > h) ? h : w;
        singleValueFont = new Font(textFont, Font.CENTER_BASELINE, size-2);
        freezedFont = new Font("Courier New",Font.CENTER_BASELINE|Font.BOLD, size - 2 );
        w = cellRectangle.width / puzzleCell.getSudokuSize();
        h = cellRectangle.height / puzzleCell.getSudokuSize();

        size = (w > h) ? h : w;
        multiValueFont = new Font(textFont, Font.CENTER_BASELINE, size-2);

        Font holdFont = graphics.getFont();

        for (int i = 0; i  < puzzleCell.getSudokuSize() * puzzleCell.getSudokuSize(); i++) {
            String strValue = String.format("%d",i+1);

            graphics.setFont(singleValueFont);
            FontMetrics fm = graphics.getFontMetrics();

            x = cellRectangle.x + (cellRectangle.width - fm.stringWidth(strValue)) / 2;
            y = cellRectangle.y + (fm.getAscent() + (cellRectangle.height - (fm.getAscent() + fm.getDescent())) / 2);

            singleValueToRectangle.put(i+1, new Rectangle(x, y, cellRectangle.width, cellRectangle.height));

            graphics.setFont(multiValueFont);
            fm = graphics.getFontMetrics();

            x = cellRectangle.x + ((int) i % puzzleCell.getSudokuSize()) * w;
            y = cellRectangle.y + ((int) i / puzzleCell.getSudokuSize()) * h;

            x = x + (w - fm.stringWidth(String.format("%d",i+1))) / 2;
            y = y + (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);

            valueToRectangle.put(i+1,  new Rectangle(x, y, w, h));
        }

        graphics.setFont(holdFont);
    }

    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        if (puzzleCell != null){
            g.setColor(fillColor);
            if(!puzzleCell.isSelected())
                g2.fillRect(cellRectangle.x, cellRectangle.y, cellRectangle.width, cellRectangle.height);
        }

        g2.setStroke(new BasicStroke(lineSize));
        g.setColor(lineColor);
        g.drawRect(cellRectangle.x, cellRectangle.y, cellRectangle.width, cellRectangle.height);

        if(puzzleCell == null)
            return;

        Integer vlist[] = puzzleCell.getValueList();

        if(vlist == null)
            return;

        if (puzzleCell.isSetByUser()) {
            Integer v = puzzleCell.getValue();
            if(puzzleCell.isFriezed())
                paintText(g, v.toString(), singleValueToRectangle.get(v), freezedFont, textColorFreezedValue);
            else
                paintText(g, v.toString(), singleValueToRectangle.get(v), singleValueFont, textColorSingleValue);
        } else {
            for (Integer v : vlist) {
                paintText(g, v.toString(), valueToRectangle.get(v), multiValueFont,  textColorMultiValue);
            }
        }
    }

    private void paintText(Graphics g, String text, Rectangle r, Font font,  Color textColor) {
        g.setFont(font);
        g.setColor(textColor);
        g.drawString(text, r.x, r.y);
    }
}
