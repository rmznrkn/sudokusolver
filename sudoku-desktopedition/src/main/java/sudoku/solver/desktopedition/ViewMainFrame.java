package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class ViewMainFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(ViewMainFrame.class);
    private static final long serialVersionUID = 1L;
    private final ViewCellMap drawingPanel;
    private final List<JToggleButton> numberButtons = new ArrayList<JToggleButton>();
    private final JToolBar numberBar;
    public JMenuBar menuBar;
    private JToggleButton selectedButton = null;
    private JToggleButton undoButton = null;
    private JToggleButton redoButton = null;

    public ViewMainFrame(int size) {
        super("Sudoku solver");

        drawingPanel = new ViewCellMap(size);

        menuBar = new JMenuBar();

        JMenu formatMenu = new JMenu("");
        numberBar = new JToolBar("");
        menuBar.add(formatMenu);

        NumberActionListener actionListener = new NumberActionListener();
        for (int i = 0; i < size * size; i++) {
            JToggleButton button = new JToggleButton("" + (i + 1));
            button.addMouseListener(actionListener);
            numberButtons.add(button);
            formatMenu.add(button);
            numberBar.add(button);
            numberBar.addSeparator();
        }

        JToggleButton button = new JToggleButton("Delete");
        button.addMouseListener(actionListener);
        numberButtons.add(button);
        formatMenu.add(button);
        numberBar.add(button);
        numberBar.addSeparator();

        undoButton = button = new JToggleButton("UNDO");
        button.addMouseListener(actionListener);
        numberButtons.add(button);
        formatMenu.add(button);
        numberBar.add(button);
        numberBar.addSeparator();

        redoButton = button = new JToggleButton("REDO");
        button.addMouseListener(actionListener);
        numberButtons.add(button);
        formatMenu.add(button);
        numberBar.add(button);
        numberBar.addSeparator();

        numberBar.setMaximumSize(drawingPanel.getSize());

        int cellWith = 50;
        int cellHeight = 50;
        int border = 10;

        drawingPanel.setPreferredSize(new Dimension(size * size * cellWith + border * 2, size * size * cellHeight + border * 2));
        drawingPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        this.setJMenuBar(menuBar);
        this.getContentPane().add(numberBar, BorderLayout.NORTH);
        this.getContentPane().add(drawingPanel, BorderLayout.CENTER);
        this.pack();
        this.setMinimumSize(this.getSize());
        this.setMaximumSize(this.getSize());

        drawingPanel.setMinimumSize(drawingPanel.getSize());
        drawingPanel.setMaximumSize(drawingPanel.getSize());
        drawingPanel.setBackground(Color.LIGHT_GRAY);
        drawingPanel.addMouseListener(new PanelMouseListener());


        drawingPanel.setCellWith(cellWith);
        drawingPanel.setCellHeight(cellHeight);
        drawingPanel.setMapTopX(border);
        drawingPanel.setMapTopY(border);

        drawingPanel.createSudoku();

        setUndoView();
        setRedoView();

        this.setVisible(true);
    }

    private void setRedoView() {
        if (PuzzleCellHistoryCache.getCurrentVersion() -
                PuzzleCellHistoryCache.getUserSelectedVersion() > 0)
            redoButton.setForeground(Color.GREEN);
        else
            redoButton.setForeground(Color.DARK_GRAY);
    }

    private void setUndoView() {
        if (PuzzleCellHistoryCache.getUserSelectedVersion() > 0)
            undoButton.setForeground(Color.GREEN);
        else
            undoButton.setForeground(Color.DARK_GRAY);
    }

    private void setValue(Point p) {
        if (selectedButton == null)
            return;
        if (selectedButton.getText().equalsIgnoreCase("Delete")) {

        } else {
            drawingPanel.setRecValue(p, Integer.parseInt(selectedButton.getText()));
        }

        setUndoView();
        setRedoView();
    }

    class NumberActionListener implements MouseListener {
        private void changeSelection(JToggleButton selected) {
            if (selected == redoButton) {

                PuzzleCellHistoryCache.selectNextVersion(
                        drawingPanel.getObjectMap().getPuzzleCells());

                setUndoView();
                setRedoView();
                repaint();
                return;
            } else if (selected == undoButton) {
                PuzzleCellHistoryCache.selectPreviousVersion(
                        drawingPanel.getObjectMap().getPuzzleCells());
                setUndoView();
                setRedoView();
                repaint();
                return;
            }

            if (selectedButton != null) {
                selectedButton.setSelected(false);
                selectedButton.setForeground(Color.BLACK);
            }
            selectedButton = selected;
            selectedButton.setForeground(Color.RED);
            selectedButton.setSelected(true);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            changeSelection((JToggleButton) e.getSource());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private class PanelMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            setValue(e.getPoint());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            setValue(e.getPoint());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}

