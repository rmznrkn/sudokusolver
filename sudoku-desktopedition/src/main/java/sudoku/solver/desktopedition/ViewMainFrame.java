package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
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
    private JToggleButton freezeButton = null;
    private JToggleButton unfreezeButton = null;
    private JToggleButton clearMapButton = null;
    private JToggleButton fillMapButton = null;
    private JToggleButton checkMapButton = null;
    private JToggleButton loadPredefinedMap = null;
    int cellWith = 100;
    int cellHeight = 100;
    int border = 10;
    ComponentListener componentListener;
    JMenu formatMenu;
    NumberActionListener actionListener;
    JCheckBox chkTrying;
    JCheckBox chkDeleteNumber;
    JCheckBox cheSimplify;
    JCheckBox cheHighlight;
    private JToggleButton addToolButton(String text){
        JToggleButton button = new JToggleButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 20));
        button.addMouseListener(actionListener);
        numberButtons.add(button);
        formatMenu.add(button);
        numberBar.add(button);
        numberBar.addSeparator();
        return button;
    }

    private JCheckBox addToolCheckbox(String text){
        JCheckBox checkBox = new JCheckBox(text);

        numberButtons.add(checkBox);
        formatMenu.add(checkBox);
        numberBar.add(checkBox);
        numberBar.addSeparator();
        return checkBox;
    }

    DocumentBuilderFactory dbFactory = null;
    DocumentBuilder dBuilder = null;
    Document xmlDoc = null;
    NodeList preDefinedMapList = null;
    String mapNameList[];

    public String [] loadPredefinedMaps() {
        try {
            if(dbFactory == null) {
                dbFactory = DocumentBuilderFactory.newInstance();
                dBuilder = dbFactory.newDocumentBuilder();
                xmlDoc = dBuilder.parse(ViewCellMap.class.getClassLoader().getResourceAsStream("config/maps.xml"));
                xmlDoc.getDocumentElement().normalize();
                preDefinedMapList = xmlDoc.getElementsByTagName("map");
                mapNameList = new String[ preDefinedMapList.getLength()];
                int nameCnt = 0;
                for (int temp = 0; temp < preDefinedMapList.getLength(); temp++) {
                    Node map = preDefinedMapList.item(temp);
                    if (map.getNodeType() == Node.ELEMENT_NODE) {
                        Element eMap = (Element) map;
                        try {
                            mapNameList[nameCnt] =  eMap.getElementsByTagName("name").item(0).getTextContent();
                            nameCnt++;
                        } catch (Exception e){
                            LOGGER.error(e.toString());
                            continue;
                        }
                    }
                }
            }
            return mapNameList;
        } catch (Exception e) {
            LOGGER.error(e);
        }

        String dummyList[] ={""};
        return dummyList;
    }

    public void loadPredefinedMaps(String mapName) {

        try {
            if(dbFactory == null) {
                dbFactory = DocumentBuilderFactory.newInstance();
                dBuilder = dbFactory.newDocumentBuilder();
                xmlDoc = dBuilder.parse(ViewCellMap.class.getClassLoader().getResourceAsStream("config/maps.xml"));
                xmlDoc.getDocumentElement().normalize();
                preDefinedMapList = xmlDoc.getElementsByTagName("map");
            }

            for (int temp = 0; temp < preDefinedMapList.getLength(); temp++) {
                Node map = preDefinedMapList.item(temp);
                if (map.getNodeType() == Node.ELEMENT_NODE) {
                    Element eMap = (Element) map;
                    try {
                        if(eMap.getElementsByTagName("name").item(0).getTextContent().compareToIgnoreCase(mapName) != 0)
                            continue;
                    } catch (Exception e){
                        LOGGER.error(e.toString());
                        continue;
                    }
                    LOGGER.info("Loading predefined map: " + mapName);
                    drawingPanel.getObjectMap().setFreeze(false, false);
                    drawingPanel.clearMap();
                    NodeList cellList = eMap.getElementsByTagName("cell");
                    for (int i = 0; i < cellList.getLength(); i++) {
                        Node cell = cellList.item(i);
                        if(cell.getNodeType() == Node.ELEMENT_NODE){
                            Element eCell = (Element)cell;
                            try {
                                String row = eCell.getElementsByTagName("row").item(0).getTextContent().trim();
                                String col = eCell.getElementsByTagName("column").item(0).getTextContent().trim();
                                String val = eCell.getElementsByTagName("value").item(0).getTextContent().trim();
                                PuzzleCell puzzleCell = drawingPanel.getObjectMap().getCell(Integer.parseInt(row),
                                        Integer.parseInt(col));
                                if(puzzleCell == null || Integer.parseInt(val) < 1 || Integer.parseInt(val) > 9)
                                    continue;

                                LOGGER.info(puzzleCell);

                                puzzleCell.assignValue(Integer.parseInt(val), false);
                            } catch (Exception e){
                                LOGGER.error(e.toString());
                                continue;
                            }
                        }
                    }
                    drawingPanel.getObjectMap().setFreeze(true, true);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
    public ViewMainFrame(int size) {
        super("Sudoku solver");

        drawingPanel = new ViewCellMap(size);

        menuBar = new JMenuBar();

        formatMenu = new JMenu("");
        numberBar = new JToolBar("");
        menuBar.add(formatMenu);

        actionListener = new NumberActionListener();
        for (int i = 0; i < size * size; i++) {
            addToolButton("   " + (i + 1) + "   ");
        }

        clearMapButton = addToolButton("CLEAR");
        undoButton      = addToolButton("UNDO");
        redoButton      = addToolButton("REDO");
        freezeButton    = addToolButton("FREEZE");
        unfreezeButton    = addToolButton("UNFREEZE");

        fillMapButton   = addToolButton("FILL MAP");
        checkMapButton   = addToolButton("CHECK");
        loadPredefinedMap = addToolButton("LOAD MAP");

        chkTrying       = addToolCheckbox("Trial");
        chkDeleteNumber = addToolCheckbox("Delete");
        cheSimplify     = addToolCheckbox("Simplify");
        cheHighlight     = addToolCheckbox("Highlight");

        drawingPanel.setPreferredSize(new Dimension(size * size * cellWith + border * 2, size * size * cellHeight + border * 2));
        drawingPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        this.setJMenuBar(menuBar);
        numberBar.setOrientation(SwingConstants.VERTICAL);
        this.getContentPane().add(numberBar, BorderLayout.EAST);
        this.getContentPane().add(drawingPanel, BorderLayout.CENTER);
        this.pack();


        drawingPanel.setBackground(Color.LIGHT_GRAY);
        drawingPanel.addMouseListener(new PanelMouseListener());


        drawingPanel.setCellWith(cellWith);
        drawingPanel.setCellHeight(cellHeight);
        drawingPanel.setMapTopX(border);
        drawingPanel.setMapTopY(border);
        componentListener = new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                cellHeight = (drawingPanel.getHeight() - border * 2) /
                        (drawingPanel.getSudokuSize() * drawingPanel.getSudokuSize());
                cellWith = (drawingPanel.getWidth() - border * 2) /
                        (drawingPanel.getSudokuSize() * drawingPanel.getSudokuSize());
                drawingPanel.setCellWith(cellWith);
                drawingPanel.setCellHeight(cellHeight);
                drawingPanel.relayout();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        };

        this.addComponentListener(componentListener);

        drawingPanel.createSudoku();

        drawingPanel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if( e.getKeyChar() >= '1' && e.getKeyChar() <= '9'){
                    if(chkDeleteNumber.isSelected())
                        drawingPanel.delete(Integer.parseInt(""+e.getKeyChar()), chkTrying.isSelected());
                    else
                        drawingPanel.setRecValue(Integer.parseInt(""+e.getKeyChar()),
                                chkTrying.isSelected(), cheSimplify.isSelected(), cheHighlight.isSelected());
                }
                setUndoView();
                setRedoView();
                setFreezeButtonView();
                repaint();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP){
                    drawingPanel.changeSelection(-1, 0);
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    drawingPanel.changeSelection(1, 0);
                } else if(e.getKeyCode() == KeyEvent.VK_LEFT){
                    drawingPanel.changeSelection(0, -1);
                } else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                    drawingPanel.changeSelection(0, 1);
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //setKey(e);
            }
        });
        setUndoView();
        setRedoView();
        setFreezeButtonView();

        drawingPanel.setFocusable(true);

        drawingPanel.requestFocusInWindow();

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                drawingPanel.getObjectMap().loadFromFile("sudoku_last_values.txt");
                repaint();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                drawingPanel.getObjectMap().saveToFile("sudoku_last_values.txt");
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        this.setVisible(true);
    }

    private void setRedoView() {
        if (PuzzleCellHistoryCache.getCurrentVersion() -
                PuzzleCellHistoryCache.getUserSelectedVersion() > 0)
            redoButton.setForeground(Color.MAGENTA);
        else
            redoButton.setForeground(Color.DARK_GRAY);
    }

    private void setUndoView() {
        if (PuzzleCellHistoryCache.getUserSelectedVersion() > 0)
            undoButton.setForeground(Color.MAGENTA);
        else
            undoButton.setForeground(Color.DARK_GRAY);
    }

    private void setFreezeButtonView() {
        if (drawingPanel.getObjectMap().getChangeCount() > 0)
            freezeButton.setForeground(Color.MAGENTA);
        else
            freezeButton.setForeground(Color.DARK_GRAY);
    }

    private void setValue(Point p) {
        if (selectedButton == null) {
            drawingPanel.changeSelection(p, cheHighlight.isSelected());
            return;
        }
        if (!chkDeleteNumber.isSelected()) {
            drawingPanel.setRecValue(p, Integer.parseInt(selectedButton.getText().trim()),
                    chkTrying.isSelected(),cheSimplify.isSelected(), cheHighlight.isSelected());
        } else {
            drawingPanel.delete(p, Integer.parseInt(selectedButton.getText().trim()), chkTrying.isSelected());
        }

        setUndoView();
        setRedoView();
        setFreezeButtonView();
    }

    class NumberActionListener implements MouseListener {
        private void changeSelection(JToggleButton selected) {
            if (selected == redoButton) {

                PuzzleCellHistoryCache.selectNextVersion(
                        drawingPanel.getObjectMap().getPuzzleCells());

                setUndoView();
                setRedoView();
                setFreezeButtonView();
                repaint();
                return;
            } else if (selected == undoButton) {
                PuzzleCellHistoryCache.selectPreviousVersion(
                        drawingPanel.getObjectMap().getPuzzleCells());
                setUndoView();
                setRedoView();
                setFreezeButtonView();
                repaint();
                return;
            } else if (selected == freezeButton){
                drawingPanel.getObjectMap().setFreeze(true, false);
                setUndoView();
                setRedoView();
                setFreezeButtonView();
                repaint();
                return;
            } else if(selected == clearMapButton) {
                drawingPanel.clearMap();
                repaint();
                return;
            } else if(selected == fillMapButton){
                drawingPanel.fillPossibleValues(chkTrying.isSelected(),cheSimplify.isSelected());
                repaint();
                return;
            } else if(selected == checkMapButton){
                drawingPanel.check();
                repaint();
                return;
            } else if(selected == unfreezeButton){
                drawingPanel.getObjectMap().setFreeze(false, false);
                repaint();
                return;
            } else if(selected == loadPredefinedMap){
                String[] choices = loadPredefinedMaps();
                String input = (String) JOptionPane.showInputDialog(null, "Choose predefined maps...",
                        "The Choice of Map", JOptionPane.QUESTION_MESSAGE, null,
                        choices,
                        choices[1]);
                if(input != null && input != ""){
                    loadPredefinedMaps(input);
                }
                repaint();
                return;
            }

            if (selectedButton != null) {
                selectedButton.setSelected(false);
                selectedButton.setForeground(Color.BLACK);
            }
            selectedButton = selected;
            if(selectedButton != null) {
                selectedButton.setForeground(Color.RED);
                selectedButton.setSelected(true);
            }
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
            drawingPanel.requestFocusInWindow();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            setValue(e.getPoint());
            drawingPanel.requestFocusInWindow();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            repaint();
            drawingPanel.requestFocusInWindow();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            drawingPanel.requestFocusInWindow();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            drawingPanel.requestFocusInWindow();
        }
    }
}

