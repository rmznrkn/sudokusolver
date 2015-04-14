package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author ramazan
 */
public class PuzzleCellGroup {
    private static final Logger LOGGER = Logger.getLogger(PuzzleCellGroup.class);
    private final Map<Integer, PuzzleCell> indexToCell;
    private final Map<Integer, Set<PuzzleCell>> valueToCell;

    public PuzzleCellGroup(int valueCount) {
        indexToCell = new HashMap<Integer, PuzzleCell>();
        valueToCell = new HashMap<Integer, Set<PuzzleCell>>();
        for (int i = 0; i < valueCount; i++) {
            Set<PuzzleCell> set = new HashSet<PuzzleCell>();
            valueToCell.put(i + 1, set);
        }
    }

    private void countValueHits() {

        for (Integer i : valueToCell.keySet()) {
            valueToCell.get(i).clear();
        }

        for (Integer i : indexToCell.keySet()) {
            PuzzleCell puzzleCell = indexToCell.get(i);
            for (Integer v : puzzleCell.getValues()) {
                Set<PuzzleCell> set = valueToCell.get(v);
                if (!set.contains(puzzleCell)) {
                    set.add(puzzleCell);
                }
            }
        }
    }

    public Map<Integer, PuzzleCell> getCells() {
        return indexToCell;
    }

    public HashMap<Integer, Set<PuzzleCell>> getValueToCell() {
        return (HashMap<Integer, Set<PuzzleCell>>) valueToCell;
    }

    public void add(PuzzleCell puzzleCell) {
        indexToCell.put(puzzleCell.getIndex(), puzzleCell);
    }

    private int remove(int value) {
        int removedCount = 0;
        for (Integer key : indexToCell.keySet()) {
            PuzzleCell puzzleCell = indexToCell.get(key);
            if (puzzleCell.remove(value))
                removedCount++;
        }
        return removedCount;
    }

    public boolean isSatisfied() {
        boolean satisfied = true;
        for (Integer key : indexToCell.keySet()) {
            if (indexToCell.get(key).getPossibleValueCount() > 1) {
                satisfied = false;
                break;
            }
        }
        return satisfied;
    }

    void debug(String str, List<PuzzleCell> puzzleCells) {
        if (puzzleCells == null) return;
        LOGGER.debug(str);
        for (PuzzleCell c : puzzleCells) {
            LOGGER.debug("   " + c.toString());
        }
    }

    public boolean simplify() {

        boolean simplified = false;

        for (Integer i : indexToCell.keySet()) {
            PuzzleCell puzzleCell = indexToCell.get(i);
            if (puzzleCell.getPossibleValueCount() == 1) {
                int value = puzzleCell.getValue();
                if (remove(value) > 1)
                    simplified = true;
                puzzleCell.assign(value);
            }
        }

        countValueHits();

        for (Integer key : valueToCell.keySet()) {
            Set<PuzzleCell> value = valueToCell.get(key);
            if (value.size() == 1) {
                Iterator<PuzzleCell> iterator = value.iterator();
                PuzzleCell puzzleCell = iterator.next();
                if (puzzleCell.getPossibleValueCount() > 1) {
                    puzzleCell.assign(key);
                    simplified = true;
                }
            }
        }

        for (int n = 2; n < indexToCell.size(); n++) {
            LOGGER.debug("n = " + n);
            List<PuzzleCell> havingNitem = getItemsThatHasNItem(n);
            List<List<PuzzleCell>> equals = null;

            while (havingNitem != null) {

                debug("havingNitem=", havingNitem);

                PuzzleCell firstPuzzleCell = havingNitem.get(0);

                List<PuzzleCell> equalList = getEquals(firstPuzzleCell, havingNitem);

                if (equalList == null) {
                    System.out.println("Something strange: n = " + n + " equalList == null");
                    break;
                }

                debug("equalList=", equalList);

                if (equalList.size() > n) {
                    System.out.println("Something strange: n = " + n + " equalList.size() = " + equalList.size());
                    break;
                }

                if (equalList.size() == n) {
                    if (equals == null) equals = new ArrayList<List<PuzzleCell>>();
                    equals.add(equalList);
                }

                havingNitem = getDistjointSet(equalList, havingNitem);
            }

            if (equals != null) {
                for (List<PuzzleCell> sublist : equals) {
                    for (Integer value : sublist.get(0).getValueList()) {
                        if (removeFromOthers(sublist, value))
                            simplified = true;
                    }
                }
            }
        }

        return simplified;
    }

    private boolean removeFromOthers(List<PuzzleCell> subcells, int value) {
        int removedCount = 0;
        for (Integer key : indexToCell.keySet()) {
            if (!subcells.contains(indexToCell.get(key))) {
                PuzzleCell puzzleCell = indexToCell.get(key);
                if (puzzleCell.remove(value)) {
                    removedCount++;
                }
            }
        }
        return (removedCount != 0);
    }

    private List<PuzzleCell> getEquals(PuzzleCell puzzleCell, List<PuzzleCell> refList) {
        List<PuzzleCell> equalList = null;
        for (PuzzleCell pair : refList) {
            if (puzzleCell.isEqual(pair)) {
                if (equalList == null)
                    equalList = new ArrayList<PuzzleCell>();
                equalList.add(pair);
            }
        }
        return equalList;
    }

    private List<PuzzleCell> getDistjointSet(List<PuzzleCell> subset, List<PuzzleCell> wholeSet) {
        List<PuzzleCell> distinction = null;
        for (PuzzleCell puzzleCell : wholeSet) {
            if (!subset.contains(puzzleCell)) {
                if (distinction == null)
                    distinction = new ArrayList<PuzzleCell>();
                distinction.add(puzzleCell);
            }
        }
        return distinction;
    }

    private List<PuzzleCell> getItemsThatHasNItem(int n) {
        List<PuzzleCell> pairs = null;
        for (Integer key : indexToCell.keySet()) {
            if (indexToCell.get(key).getPossibleValueCount() == n) {
                if (pairs == null)
                    pairs = new ArrayList<PuzzleCell>();

                pairs.add(indexToCell.get(key));
            }
        }
        return pairs;
    }
}
