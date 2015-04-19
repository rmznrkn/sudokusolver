package sudoku.solver.desktopedition;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ramazan
 */
public class PuzzleCellHistoryCache {
    private static final Logger LOGGER = Logger.getLogger(PuzzleCellHistoryCache.class);
    private final static Map<PuzzleCell, Map<Long, String>> versionMap = new HashMap<PuzzleCell, Map<Long, String>>();
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

    private static PuzzleCell getCell(String str){
        ByteArrayInputStream buffer = new ByteArrayInputStream(str.getBytes(Charset.forName("ISO-8859-9")));
        try {
            return (PuzzleCell)SerializerUtil.deserialize(buffer);
        } catch (IOException e) {
            LOGGER.error(e);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e);
        }
        return null;
    }
    private static String getString(PuzzleCell cell){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            SerializerUtil.serialize(cell, buffer);
            return new String(buffer.toByteArray(), Charset.forName("ISO-8859-9"));
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return null;
    }
    private static void updateVersion(PuzzleCell puzzleCell, long newVersion) {
        Map<Long, String> cellVersions = versionMap.get(puzzleCell);
        List<Long> savedVersionNumbers = versionNumberMap.get(puzzleCell);
        if (savedVersionNumbers == null)
            return;
        long lastVersion = savedVersionNumbers.get(savedVersionNumbers.size() - 1);
        if (lastVersion >= newVersion)
            return;

        String data =cellVersions.remove(lastVersion);
        cellVersions.put(newVersion, data);
        savedVersionNumbers.remove(savedVersionNumbers.size() - 1);
        savedVersionNumbers.add(newVersion);
    }

    private static boolean saveVersion(PuzzleCell puzzleCell) {
        Map<Long, String> cellVersions = versionMap.get(puzzleCell);
        List<Long> savedVersionNumbers = versionNumberMap.get(puzzleCell);

        if (cellVersions == null) {
            cellVersions = new HashMap<Long, String>();
            versionMap.put(puzzleCell, cellVersions);
        }
        if (savedVersionNumbers == null) {
            savedVersionNumbers = new ArrayList<Long>();
            versionNumberMap.put(puzzleCell, savedVersionNumbers);
        }

        PuzzleCell lastSavedVersion = null;
        if(savedVersionNumbers.size() > 0){
            lastSavedVersion = getCell(cellVersions.get(savedVersionNumbers.get(savedVersionNumbers.size() - 1)));
        }

        if (puzzleCell.isVersionsEqual(lastSavedVersion)) {
             return false;
        }

        String str = getString(puzzleCell);
        cellVersions.put(version + 1, str);
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
        LOGGER.debug("version: " + version + ", saveCount: " + count);
    }

    private static PuzzleCell getVersion(PuzzleCell puzzleCell, long versionNumber) {
        Map<Long, String> cellVersions = versionMap.get(puzzleCell);
        List<Long> savedVersionNumbers = versionNumberMap.get(puzzleCell);

        long foundVersionNumber = -1;
        for (int i = savedVersionNumbers.size() - 1; i >= 0; i--) {
            if (savedVersionNumbers.get(i) < versionNumber)
                break;
            foundVersionNumber = savedVersionNumbers.get(i);
        }
        return getCell(cellVersions.get(foundVersionNumber));
    }

    synchronized public static void selectPreviousVersion(List<PuzzleCell> clist) {
        if (userIterationCount == 0) {
            saveVersion(clist);
        }

        if (userSelectedVersion < 1)
            return;

        userIterationCount++;
        userSelectedVersion--;
        LOGGER.debug("selectPreviousVersion.userSelectedVersion: " + userSelectedVersion);

        PuzzleCell.resetSelected();

        for (PuzzleCell puzzleCell : clist) {
            PuzzleCell saved = getVersion(puzzleCell, userSelectedVersion);
            if(saved != null)
                puzzleCell.loadVersion(saved);
        }
    }

    synchronized public static void selectNextVersion(List<PuzzleCell> clist) {
        if (version <= userSelectedVersion)
            return;
        userIterationCount++;
        userSelectedVersion++;
        LOGGER.debug("selectNextVersion.userSelectedVersion: " + userSelectedVersion);
        for (PuzzleCell puzzleCell : clist) {
            puzzleCell.loadVersion(getVersion(puzzleCell, userSelectedVersion));
        }
    }

    public static long getUserSelectedVersion() {
        return userSelectedVersion;
    }

    public static long getCurrentVersion() {
        return version;
    }
}
