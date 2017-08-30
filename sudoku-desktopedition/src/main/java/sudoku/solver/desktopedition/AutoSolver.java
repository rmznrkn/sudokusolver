package sudoku.solver.desktopedition;

import sun.misc.Queue;

import java.util.*;

public class AutoSolver {
    private static PriorityQueueEntry copy(final PuzzleMap map) {
        Set<Integer> cells[][];

        int size = map.getRows().size();

        cells = new Set[size][];
        int cnt = 0;
        for (int i = 0; i < size; i++) {
            cells[i] = new Set[size];
            for (int j = 0;  j < size; j++) {
                cells[i][j] = new HashSet<>();
                cells[i][j].addAll(map.getCell(i, j).getValues());
                cnt += cells[i][j].size();
            }
        }
        PriorityQueueEntry entry = new PriorityQueueEntry();
        entry.map = cells;
        entry.possibleValueCount = cnt;
        return entry;
    }

    private static PriorityQueueEntry copy(PriorityQueueEntry src) {
        Set<Integer> cells[][];

        int size = src.map.length;

        cells = new Set[size][];

        for (int i = 0; i < size; i++) {
            cells[i] = new Set[size];
            for (int j = 0;  j < size; j++) {
                cells[i][j] = new HashSet<>();
                cells[i][j].addAll(src.map[i][j]);
            }
        }

        PriorityQueueEntry entry = new PriorityQueueEntry();
        entry.map = cells;
        entry.possibleValueCount = src.possibleValueCount;

        return entry;
    }

    private static void copy(final Set<Integer>[][] src, final PuzzleMap dst, boolean isTry) {
        Set<Integer> cells[][];

        int size = src.length;

        cells = new Set[size][];

        dst.clear();

        for (int i = 0; i < size; i++) {
            cells[i] = new Set[size];
            for (int j = 0;  j < size; j++) {
                for (Integer v : src[i][j]) {
                    dst.getCell(i, j).assignValue(v, isTry && src[i][j].size() > 1);
                }
            }
        }
    }

    private static boolean done(Set<Integer>[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].size() != 1) return false;
            }
        }
        return true;
    }


    private static boolean isOk(Set<Integer>[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].size() <= 0)
                    return false;
            }
        }
        return true;
    }

    public static PuzzleMap simplifier =  new PuzzleMap(3);

    public static void addNewClones(PriorityQueue<PriorityQueueEntry> queue, PriorityQueueEntry entry, int r, int c) {

        Set<Integer> cells[][] = entry.map;
        Set<Integer> cell = cells[r][c];
        int sqr = (int) Math.sqrt(cells.length);
        for (Integer v : cell) {
            PriorityQueueEntry cloneEntry = copy(entry);
            Set<Integer> [][] clone = cloneEntry.map;
            cloneEntry.possibleValueCount -= clone[r][c].size();
            clone[r][c].clear();
            clone[r][c].add(v);
            cloneEntry.possibleValueCount++;

            simplifier.clear();
            copy(cloneEntry.map, simplifier, true);
            simplifier.simplify();
            PriorityQueueEntry newEntry = copy(simplifier);
            if (isOk(newEntry.map)) {
                queue.add(newEntry);
            }
        }
    }

    public static Set<Integer>[][] getSolved(PriorityQueue<PriorityQueueEntry> queue) {
        PriorityQueueEntry entry = queue.poll();

        if (entry == null || done(entry.map)) {
            return entry.map;
        }

        for (int i = 0; i < entry.map.length; i++) {
            for (int j = 0; j < entry.map[i].length; j++) {
                if (entry.map[i][j].size() > 1) {
                    addNewClones(queue, entry, i, j);
                }
            }
        }

        return null;
    }

    public static boolean solve(final PuzzleMap map) {
        PriorityQueue<PriorityQueueEntry> queue = new PriorityQueue<>(new Comparator<PriorityQueueEntry>() {
            @Override
            public int compare(PriorityQueueEntry o1, PriorityQueueEntry o2) {
                return o1.possibleValueCount - o2.possibleValueCount;
            }
        });

        map.fillPossibleValues(true, true);

        queue.add(copy(map));

        while (!queue.isEmpty()) {
            Set<Integer>[][] solved = getSolved(queue);
            if (solved != null) {
                print(solved);
                copy(solved, map, false);
                return true;
            }
        }
        return false;
    }

    public static void print(Set<Integer>[][] map) {
        int max = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j].size() > max) {
                    max = map[i][j].size();
                }
            }
        }
        System.out.println("=========");
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {

                System.out.print("(");
                int pcnt = 0;
                for (Integer v : map[i][j]) {
                    System.out.print(v + " ");
                    pcnt++;
                }

                while (pcnt < max) {
                    System.out.print("  ");
                    pcnt++;
                }

                System.out.print(")");
            }
            System.out.println("");
        }
    }
}
