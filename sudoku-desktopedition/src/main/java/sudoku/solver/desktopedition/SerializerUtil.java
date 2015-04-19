package sudoku.solver.desktopedition;

import java.io.*;

/**
 * Created by ramazan on 4/19/2015.
 */
public class SerializerUtil {
    public static Object deserialize(InputStream inputStream) throws IOException,
            ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }
    public static void serialize(Object obj, OutputStream outputStream)
            throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(obj);
    }
}
