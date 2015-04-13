package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

import java.awt.*;

public class ViewCell {
    private static final Logger LOGGER = Logger.getLogger(ViewCell.class);
    private PuzzleCell puzzleCell;
    private Point p1, p2, p3, p4, sP, pCenter;
    private Rectangle rectangle;
    private int with, height;
    private boolean deleted;
    private Color c;
    private Rectangle CenterRec;
    private Rectangle r1, r2, r3, r4;

    public ViewCell() {
        deleted = false;
    }

    public ViewCell(int x, int y, int w, int h) {
        deleted = false;
        rectangle = new Rectangle(x, y, w, h);
        with = w;
        height = h;
        sP = new Point(x, y);
        p1 = new Point(x + w / 2, y);
        p2 = new Point(x + w, y + h / 2);
        p3 = new Point(x + w / 2, y + h);
        p4 = new Point(x, y + h / 2);
        pCenter = new Point(x + w / 2, y + h / 2);
    }

    public PuzzleCell getPuzzleCell() {
        return puzzleCell;
    }

    public void setPuzzleCell(PuzzleCell puzzleCell) {
        this.puzzleCell = puzzleCell;
    }

    public Point getsP() {
        return sP;
    }

    public void setsP(Point sP) {
        this.sP = sP;
    }

    public Point getpCenter() {
        return pCenter;
    }

    public void setpCenter(Point pCenter) {
        this.pCenter = pCenter;
    }

    public int getWith() {
        return with;
    }

    public void setWith(int with) {
        this.with = with;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Color getC() {
        return c;
    }

    public void setC(Color c) {
        this.c = c;
    }

    public Rectangle getCenterRec() {
        return CenterRec;
    }

    public void setCenterRec(Rectangle CenterRec) {
        this.CenterRec = CenterRec;
    }

    public Point getPCent() {
        return pCenter;
    }

    public void setPCent(Point p) {
        pCenter = p;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p) {
        p1 = p;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p) {
        p2 = p;
    }

    public Point getP3() {
        return p3;
    }

    public void setP3(Point p) {
        p3 = p;
    }

    public Point getP4() {
        return p4;
    }

    public void setP4(Point p) {
        p4 = p;
    }

    public Point getSP() {
        return sP;
    }

    public void setSP(Point p) {
        sP = p;
    }

    public Color getClr() {
        return c;
    }

    public void setClr(Color c1) {
        c = c1;
    }

    public Rectangle getCr() {
        return CenterRec;
    }

    public Rectangle getR1() {
        return r1;
    }

    public Rectangle getR2() {
        return r2;
    }

    public Rectangle getR3() {
        return r3;
    }

    public Rectangle getR4() {
        return r4;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(int x, int y, int w, int h) {
        rectangle = new Rectangle(x, y, w, h);
        with = w;
        height = h;
        sP = new Point(x, y);
        p1 = new Point(x + w / 2, y);
        p2 = new Point(x + w, y + h / 2);
        p3 = new Point(x + w / 2, y + h);
        p4 = new Point(x, y + h / 2);
        pCenter = new Point(x + w / 2, y + h / 2);
    }

    public boolean isDel() {
        return deleted;
    }

    public void setPoints(int x, int y)//start points
    {
        setSP(new Point(x, y));
        setP1(new Point(x + with / 2, y));
        setP2(new Point(x + with, y + height / 2));
        setP3(new Point(x + with / 2, y + height));
        setP4(new Point(x, y + height / 2));
        setPCent(new Point(x + with / 2, y + height / 2));
    }

    public void setSize(int w, int h)//start points
    {
        with = w;
        height = h;
        setPoints(sP.x, sP.y);
    }

    public void delete() {
        deleted = true;
    }

    public void createRecs() {
        CenterRec = new Rectangle(pCenter.x - 2, pCenter.y - 2, 5, 5);
        r1 = new Rectangle(p1.x - 2, p1.y - 2, 5, 5);
        r2 = new Rectangle(p2.x - 2, p2.y - 2, 5, 5);
        r3 = new Rectangle(p3.x - 2, p3.y - 2, 5, 5);
        r4 = new Rectangle(p4.x - 2, p4.y - 2, 5, 5);
        rectangle = new Rectangle(sP.x, sP.y, with, height);
    }

    public int getW() {
        return with;
    }

    public int getH() {
        return height;
    }

    public boolean isContains(Point p) {
        return rectangle.contains(p);
    }

    public boolean isCRec(Point p) {
        return CenterRec.contains(p);
    }

    public boolean isR1Con(Point p) {
        return r1.contains(p);
    }

    public boolean isR2Con(Point p) {
        return r2.contains(p);
    }

    public boolean isR3Con(Point p) {
        return r3.contains(p);
    }

    public boolean isR4Con(Point p) {
        return r4.contains(p);
    }
}
