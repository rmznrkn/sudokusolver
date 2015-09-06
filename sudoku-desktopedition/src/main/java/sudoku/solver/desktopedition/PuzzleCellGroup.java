package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * @author ramazan
 */
public class PuzzleCellGroup implements Serializable {
    private static final Logger LOGGER = Logger.getLogger(PuzzleCellGroup.class);
    private final Map<Integer, PuzzleCell> indexToCell;
    private final Map<Integer, Set<PuzzleCell>> valueToCell;
    private boolean selected = false;
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

    public boolean isSatisfied(boolean checkPossibleValueCount) {
        boolean satisfied = true;
        for (Integer key : indexToCell.keySet()) {
            PuzzleCell cell = indexToCell.get(key);
            if (!cell.isSetByUser()) {
                if(!checkPossibleValueCount || cell.getPossibleValueCount() != 1)
                    return  false;
            }
        }
        return satisfied;
    }

    public boolean removeAssignedCell(int value) {
        for (Integer key : indexToCell.keySet()) {
            PuzzleCell cell = indexToCell.get(key);
            if(cell.getValue() == value) {
                LOGGER.debug(cell);
                cell.assignAll(cell, value);
                cell.fillAllValues();
                LOGGER.debug(cell);
                return true;
            }
        }
        return false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected(){
        return selected;
    }

    public boolean simplify() {

        boolean simplified = false;
        int determinedCellCount = 0;
        for (Integer i : indexToCell.keySet()) {
            PuzzleCell puzzleCell = indexToCell.get(i);
            if (puzzleCell.getPossibleValueCount() == 1) {
                int value = puzzleCell.getValue();
                if (remove(value) > 1)
                    simplified = true;
                puzzleCell.assign(value);
                determinedCellCount++;
            }
        }

        if(determinedCellCount == indexToCell.size())
            return false;

        countValueHits();

        for (Integer key : valueToCell.keySet()) {
            Set<PuzzleCell> value = valueToCell.get(key);
            if (value.size() == 1) {
                Iterator<PuzzleCell> iterator = value.iterator();
                PuzzleCell puzzleCell = iterator.next();
                if (puzzleCell.getPossibleValueCount() > 1) {
                    if(puzzleCell.assign(key))
                        simplified = true;
                }
            }
        }

        for (int n = 2; n < indexToCell.size(); n++) {
            List<PuzzleCell> havingNitem = getItemsThatHasNItem(n);
            List<List<PuzzleCell>> equals = null;

            while (havingNitem != null) {

                PuzzleCell firstPuzzleCell = havingNitem.get(0);

                List<PuzzleCell> equalList = getEquals(firstPuzzleCell, havingNitem);

                if (equalList == null) {
                    System.out.println("Something strange: n = " + n + " equalList == null");
                    break;
                }

                //LOGGER.debug("Equal List = ");
                //LOGGER.debug(equalList);

                if (equalList.size() > n) {
                    System.out.println("Something strange: n = " + n + " equalList.size() = " + equalList.size());
                    break;
                }

                if (equalList.size() == n) {
                    if (equals == null) equals = new ArrayList<List<PuzzleCell>>();
                    equals.add(equalList);
                }

                havingNitem = getDisjointSet(equalList, havingNitem);
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

    private List<PuzzleCell> getDisjointSet(List<PuzzleCell> subset, List<PuzzleCell> wholeSet) {
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

    public void assignAll(PuzzleCell otherThenThis, int value) {
        for (Integer key : indexToCell.keySet()) {
            PuzzleCell cell = indexToCell.get(key);
            if(cell != otherThenThis){
                if(cell.isAddable(otherThenThis, value))
                   cell.getValues().add(value);
            }
        }
    }

    public boolean isValueAssigned(PuzzleCell otherThenThis, int value) {
        for (Integer key : indexToCell.keySet()) {
            PuzzleCell cell = indexToCell.get(key);
            if(cell != otherThenThis){
                if(cell.getValue() ==  value && cell.isSetByUser()) {
                    cell.setHitByUser(true);
                    return true;
                }
            }
        }
        return false;
    }
}
