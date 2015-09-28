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
    private final List<PuzzleCell> cellList;
    private final Map<Integer, Set<PuzzleCell>> valueToCell;
    private boolean selected = false;

    public PuzzleCellGroup(int valueCount) {
        indexToCell = new HashMap<Integer, PuzzleCell>();
        valueToCell = new HashMap<Integer, Set<PuzzleCell>>();
        cellList = new ArrayList<>();
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
        cellList.add(puzzleCell);
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
    boolean reduced = false;
    public boolean isSelected(){
        return selected;
    }
    final Set<Integer> set = new HashSet<>();
    public  void simplifyCom2(){
        int n = cellList.size();
        for(int i = 0 ; i < n; i++){
            for(int j = i; j < n; j++){
                if(i != j){
                    set.clear();
                    set.addAll(cellList.get(i).getValues());
                    set.addAll(cellList.get(j).getValues());
                    if(set.size() == 2){
                        for (Integer value : set) {
                            for (int c = 0; c < cellList.size();c++) {
                                if (i  != c && j  != c) {
                                    if(cellList.get(c).remove(value))
                                        reduced = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void simplifyCom3(){
        int n = cellList.size();
        for(int i = 0 ; i < n; i++){
            for(int j = i ; j < n; j++){
                for(int k = j; k < n; k++) {
                    if (i != j && i != k && j != k ) {
                        set.clear();
                        set.addAll(cellList.get(i).getValues());
                        set.addAll(cellList.get(j).getValues());
                        set.addAll(cellList.get(k).getValues());
                        if(set.size() == 3){
                            for (Integer value : set) {
                                for (int key = 0; key <  cellList.size(); key++) {
                                    if (i  != key &&
                                            j  != key &&
                                            k  != key) {
                                        if(cellList.get(key).remove(value))
                                            reduced = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void simplifyCom4() {
        int n = cellList.size();
        for (int i1 = 0; i1 < n; i1++)
            for (int i2 = i1; i2 < n; i2++)
                for (int i3 = i2; i3 < n; i3++)
                    for (int i4 = i3; i4 < n; i4++)
                        if(i1 != i2 && i1 != i3 && i1 != i4
                                && i2 != i3 && i2 != i4
                                && i3 != i4
                                ){
                            set.clear();
                            set.addAll(cellList.get(i1).getValues());
                            set.addAll(cellList.get(i2).getValues());
                            set.addAll(cellList.get(i3).getValues());
                            set.addAll(cellList.get(i4).getValues());
                            if(set.size() == 4){
                                for (Integer value : set) {
                                    for (int key = 0; key <  cellList.size(); key++) {
                                        if (
                                                i1  != key &&
                                                        i2  != key &&
                                                        i3  != key &&
                                                        i4  != key) {

                                            if(cellList.get(key).remove(value))
                                                reduced = true;
                                        }
                                    }
                                }
                            }
                        }


    }


    public void simplifyCom5() {
        int n = cellList.size();
        for (int i1 = 0; i1 < n; i1++)
            for (int i2 = i1; i2 < n; i2++)
                for (int i3 = i2; i3 < n; i3++)
                    for (int i4 = i3; i4 < n; i4++)
                        for (int i5 = i4; i5 < n; i5++)
                            if(i1 != i2 && i1 != i3 && i1 != i4 && i1 != i5
                                    && i2 != i3 && i2 != i4 && i2 != i5
                                    && i3 != i4 && i3 != i5
                                    && i4 != i5
                                    ){
                                set.clear();

                                set.addAll(cellList.get(i1).getValues());
                                set.addAll(cellList.get(i2).getValues());
                                set.addAll(cellList.get(i3).getValues());
                                set.addAll(cellList.get(i4).getValues());
                                set.addAll(cellList.get(i5).getValues());

                                if(set.size() == 5){
                                    for (Integer value : set) {
                                        for (int key = 0; key <  cellList.size(); key++) {
                                            if (i1  != key &&
                                                    i2  != key &&
                                                    i3  != key &&
                                                    i4  != key &&
                                                    i5  != key) {

                                                if(cellList.get(key).remove(value))
                                                    reduced = true;
                                            }
                                        }
                                    }
                                }
                            }


    }

    public void simplifyCom6() {
        int n = cellList.size();
        for (int i1 = 0; i1 < n; i1++)
            for (int i2 = i1; i2 < n; i2++)
                for (int i3 = i2; i3 < n; i3++)
                    for (int i4 = i3; i4 < n; i4++)
                        for (int i5 = i4; i5 < n; i5++)
                            for (int i6 = i5; i6 < n; i6++)
                                if(i1 != i2 && i1 != i3 && i1 != i4 && i1 != i5 && i1 != i6
                                        && i2 != i3 && i2 != i4 && i2 != i5 && i2 != i6
                                        && i3 != i4 && i3 != i5 && i3 != i6
                                        && i4 != i5 && i4 != i6
                                        && i5 != i6
                                        ){
                                    set.clear();

                                    set.addAll(cellList.get(i1).getValues());
                                    set.addAll(cellList.get(i2).getValues());
                                    set.addAll(cellList.get(i3).getValues());
                                    set.addAll(cellList.get(i4).getValues());
                                    set.addAll(cellList.get(i5).getValues());
                                    set.addAll(cellList.get(i6).getValues());
                                    if(set.size() == 6){
                                        for (Integer value : set) {
                                            for (int key = 0; key <  cellList.size(); key++) {
                                                if (i1  != key &&
                                                        i2  != key &&
                                                        i3  != key &&
                                                        i4  != key &&
                                                        i5  != key &&
                                                        i6  != key) {

                                                    if(cellList.get(key).remove(value))
                                                        reduced = true;
                                                }
                                            }
                                        }
                                    }
                                }


    }

    public void simplifyCom7() {
        int n = cellList.size();
        for (int i1 = 0; i1 < n; i1++)
            for (int i2 = i1; i2 < n; i2++)
                for (int i3 = i2; i3 < n; i3++)
                    for (int i4 = i3; i4 < n; i4++)
                        for (int i5 = i4; i5 < n; i5++)
                            for (int i6 = i5; i6 < n; i6++)
                                for (int i7 = i6; i7 < n; i7++)
                                    if(i1 != i2 && i1 != i3 && i1 != i4 && i1 != i5 && i1 != i6 && i1 != i7
                                            && i2 != i3 && i2 != i4 && i2 != i5 && i2 != i6 && i2 != i7
                                            && i3 != i4 && i3 != i5 && i3 != i6 && i3 != i7
                                            && i4 != i5 && i4 != i6 && i4 != i7
                                            && i5 != i6 && i5 != i7
                                            && i6 != i7){
                                        set.clear();

                                        set.addAll(cellList.get(i1).getValues());
                                        set.addAll(cellList.get(i2).getValues());
                                        set.addAll(cellList.get(i3).getValues());
                                        set.addAll(cellList.get(i4).getValues());
                                        set.addAll(cellList.get(i5).getValues());
                                        set.addAll(cellList.get(i6).getValues());
                                        set.addAll(cellList.get(i7).getValues());
                                        if(set.size() == 7){
                                            for (Integer value : set) {
                                                for (int key = 0; key <  cellList.size(); key++) {
                                                    if (i1  != key &&
                                                            i2  != key &&
                                                            i3  != key &&
                                                            i4  != key &&
                                                            i5  != key &&
                                                            i6  != key &&
                                                            i7  != key) {

                                                        if(cellList.get(key).remove(value))
                                                            reduced = true;
                                                    }
                                                }
                                            }
                                        }
                                    }


    }

    public void simplifyCom8() {
        int n = cellList.size();
        for (int i1 = 0; i1 < n; i1++)
            for (int i2 = i1; i2 < n; i2++)
                for (int i3 = i2; i3 < n; i3++)
                    for (int i4 = i3; i4 < n; i4++)
                        for (int i5 = i4; i5 < n; i5++)
                            for (int i6 = i5; i6 < n; i6++)
                                for (int i7 = i6; i7 < n; i7++)
                                    for (int i8 = i7; i8 < n; i8++)
                                        if(i1 != i2 && i1 != i3 && i1 != i4 && i1 != i5 && i1 != i6 && i1 != i7 && i1 != i8
                                                && i2 != i3 && i2 != i4 && i2 != i5 && i2 != i6 && i2 != i7 && i2 != i8
                                                && i3 != i4 && i3 != i5 && i3 != i6 && i3 != i7 && i3 != i8
                                                && i4 != i5 && i4 != i6 && i4 != i7 && i4 != i8
                                                && i5 != i6 && i5 != i7 && i5 != i8
                                                && i6 != i7 && i6 != i8
                                                && i7 != i8){
                                            set.clear();

                                            set.addAll(cellList.get(i1).getValues());
                                            set.addAll(cellList.get(i2).getValues());
                                            set.addAll(cellList.get(i3).getValues());
                                            set.addAll(cellList.get(i4).getValues());
                                            set.addAll(cellList.get(i5).getValues());
                                            set.addAll(cellList.get(i6).getValues());
                                            set.addAll(cellList.get(i7).getValues());
                                            set.addAll(cellList.get(i8).getValues());
                                            if(set.size() == 8){
                                                for (Integer value : set) {
                                                    for (int key = 0; key <  cellList.size(); key++) {
                                                        if (i1  != key &&
                                                                i2  != key &&
                                                                i3  != key &&
                                                                i4  != key &&
                                                                i5  != key &&
                                                                i6  != key &&
                                                                i7  != key &&
                                                                i8  != key) {

                                                            if(cellList.get(key).remove(value))
                                                                reduced = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
    }

    public boolean simplify() {

        boolean simplified = false;
        /*int determinedCellCount = 0;
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
                    //System.out.println("Something strange: n = " + n + " equalList == null");
                    break;
                }

                if (equalList.size() > n) {
                    //System.out.println("Something strange: n = " + n + " equalList.size() = " + equalList.size());
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
        */

        cellList.clear();

        reduced = false;
        for (Integer i : indexToCell.keySet()) {
            PuzzleCell puzzleCell = indexToCell.get(i);
            if (puzzleCell.getPossibleValueCount() == 1) {
                int value = puzzleCell.getValue();
                if (remove(value) > 1)
                    reduced = true;
                puzzleCell.assign(value);
            }
        }

        for (Integer i : indexToCell.keySet()) {
            PuzzleCell puzzleCell = indexToCell.get(i);
            if (puzzleCell.getPossibleValueCount() != 1) {
                cellList.add(puzzleCell);
            }
        }

        if(cellList.size() > 2)
            simplifyCom2();
        if(cellList.size() > 3)
            simplifyCom3();
        if(cellList.size() > 4)
            simplifyCom4();
        if(cellList.size() > 5)
            simplifyCom5();
        if(cellList.size() > 6)
            simplifyCom6();
        if(cellList.size() > 7)
            simplifyCom7();
        if(cellList.size() > 8)
            simplifyCom8();


        return reduced;
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
