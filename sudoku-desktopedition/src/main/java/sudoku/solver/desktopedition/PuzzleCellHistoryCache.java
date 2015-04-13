package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ramazan
 */
public class PuzzleCellHistoryCache {
    private static final Logger LOGGER = Logger.getLogger(PuzzleCellHistoryCache.class);
    private final static Map<PuzzleCell, Map<Long, List<Integer>>> versionMap = new HashMap<PuzzleCell, Map<Long, List<Integer>>>();
    private final static Map<PuzzleCell, List<Long>> versionNumberMap = new HashMap<PuzzleCell, List<Long>>();
    private static long version = 0;
    private static long userSelectedVersion = 0;
    private static int userIterationCount = 0;

    public PuzzleCellHistoryCache() {
    }

    private static void printList(String str, List<Integer> v) {
        System.out.print(str + " = [");
        for (int i = 0; i < v.size(); i++) {
            if (i != 0)
                System.out.print(", ");
            System.out.print("" + v.get(i));
        }
        System.out.print("]\n");
    }

    private static void updateVersion(PuzzleCell puzzleCell, long newVersion) {
        Map<Long, List<Integer>> cellVersions = versionMap.get(puzzleCell);
        List<Long> savedVersionNumbers = versionNumberMap.get(puzzleCell);
        if (savedVersionNumbers == null)
            return;
        long lastVersion = savedVersionNumbers.get(savedVersionNumbers.size() - 1);
        if (lastVersion >= newVersion)
            return;

        List<Integer> data = cellVersions.remove(lastVersion);
        cellVersions.put(newVersion, data);
        savedVersionNumbers.remove(savedVersionNumbers.size() - 1);
        savedVersionNumbers.add(newVersion);
    }

    private static boolean saveVersion(PuzzleCell puzzleCell) {
        Map<Long, List<Integer>> cellVersions = versionMap.get(puzzleCell);
        List<Long> savedVersionNumbers = versionNumberMap.get(puzzleCell);

        if (cellVersions == null) {
            cellVersions = new HashMap<Long, List<Integer>>();
            versionMap.put(puzzleCell, cellVersions);
        }
        if (savedVersionNumbers == null) {
            savedVersionNumbers = new ArrayList<Long>();
            versionNumberMap.put(puzzleCell, savedVersionNumbers);
        }


        List<Integer> lastSavedVersion
                = (savedVersionNumbers.size() <= 0) ? null : cellVersions.get(savedVersionNumbers.get(savedVersionNumbers.size() - 1));

        if (lastSavedVersion != null) {
            if (puzzleCell.getValues().size() == lastSavedVersion.size()) {
                if (puzzleCell.getValues().containsAll(lastSavedVersion)) {
                    return false;
                }
            }
        }

        lastSavedVersion = new ArrayList<Integer>();
        puzzleCell.copyValues(lastSavedVersion);
        cellVersions.put(version + 1, lastSavedVersion);
        savedVersionNumbers.add(version + 1);

        return true;
    }

    synchronized public static void saveVersion(List<PuzzleCell> clist) {
        int count = 0;
        for (PuzzleCell puzzleCell : clist) {
            if (saveVersion(puzzleCell))
                count++;
        }
        if (count > 0) {
            version++;
            for (PuzzleCell puzzleCell : clist) {
                updateVersion(puzzleCell, version);
            }
        }
        userIterationCount = 0;
        userSelectedVersion = version;
        System.out.println("version: " + version + ", saveCount: " + count);
    }

    private static List<Integer> getVersion(PuzzleCell puzzleCell, long versionNumber) {
        Map<Long, List<Integer>> cellVersions = versionMap.get(puzzleCell);
        List<Long> savedVersionNumbers = versionNumberMap.get(puzzleCell);

        long foundVersionNumber = -1;
        for (int i = savedVersionNumbers.size() - 1; i >= 0; i--) {
            if (savedVersionNumbers.get(i) < versionNumber)
                break;
            foundVersionNumber = savedVersionNumbers.get(i);
        }
        return cellVersions.get(foundVersionNumber);
    }

    synchronized public static void selectPreviousVersion(List<PuzzleCell> clist) {
        if (userIterationCount == 0) {
            saveVersion(clist);
        }

        if (userSelectedVersion <= 1)
            return;

        userIterationCount++;
        userSelectedVersion--;
        System.out.println("selectPreviousVersion.userSelectedVersion: " + userSelectedVersion);
        for (PuzzleCell puzzleCell : clist) {
            puzzleCell.loadVersion(getVersion(puzzleCell, userSelectedVersion));
        }
    }

    synchronized public static void selectNextVersion(List<PuzzleCell> clist) {
        if (version <= userSelectedVersion)
            return;
        userIterationCount++;
        userSelectedVersion++;
        System.out.println("selectNextVersion.userSelectedVersion: " + userSelectedVersion);
        for (PuzzleCell puzzleCell : clist) {
            puzzleCell.loadVersion(getVersion(puzzleCell, userSelectedVersion));
        }
    }

    public static long getUserSelectedVersion() {
        return userSelectedVersion;
    }

    public static long getCurrentVersion() {
        return userSelectedVersion;
    }
}
