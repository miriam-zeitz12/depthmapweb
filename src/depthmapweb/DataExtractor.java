package depthmapweb;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;

/**
 * Extracts data from depth maps. Ideally this would just be static methods, but
 * we also need to store near and far values as opposed to just the depth
 * information, and thanks to the "magic" of static typing, we can't exactly do
 * that.
 * @author Vinushka
 */
public class DataExtractor {
    // We specify UTF-8 in the constructor.
    private static byte[] OPEN_ARR;
    private static byte[] CLOSE_ARR;
    private static byte[] OPEN_DATA;
    private static byte[] CLOSE_DATA;
    private static byte[] OPEN_NEAR;
    private static byte[] OPEN_FAR;
    private static byte[] CLOSE_DEPTH_RANGE;
    /**
     * "Near" value, the nearest point in the depth map.
     */
    private double near;
    /**
     * "Far" value, the farthest point in the depth map.
     */
    private double far;
    /**
     * The input data.
     */
    private byte[] inputData;
    /**
     * Holds the depth data.
     */
    byte[] depthData;
    /**
     * Creates a DataExtractor. Creating the object, also extracts the data.
     * @param in - File of the depth map.
     * @throws IOException - If there's issues reading the file
     */
    public DataExtractor(File imageFile) throws IOException {
        OPEN_ARR = "<x:xmpmeta".getBytes("UTF-8");
        CLOSE_ARR = "</x:xmpmeta>".getBytes("UTF-8");
        OPEN_DATA = "GDepth:Data=\"".getBytes("UTF-8");
        CLOSE_DATA = "\"/>".getBytes("UTF-8");
        OPEN_NEAR = "GDepth:Near=\"".getBytes("UTF-8");
        OPEN_FAR = "GDepth:Far=\"".getBytes("UTF-8");
        CLOSE_DEPTH_RANGE = "\"".getBytes("UTF-8");
        inputData = IOUtils.toByteArray(new FileInputStream(imageFile));
        depthData = findDepthData();
    }
    /**
     * The "near" value in GDepth:Near. Used to determine minimum depth.
     * @return - the "near" value in depth units.
     */
    public double getNear() {
        return near;
    }
    /**
     * The far value in GDepth:far. Used to determine the maximum depth.
     * @return - The "far" value in depth units.
     */
    public double getFar() {
        return far;
    }
    /**
     * Actually gets the depth data.
     * @return - the depth data as a Base64-encoded String.
     */
    public byte[] getDepthData() {
        return depthData;
    }
    public static void copy(InputStream in, OutputStream out, int bufferSize)
            throws IOException {
        byte[] buf = new byte[bufferSize];
        int bytesRead = in.read(buf);
        while (bytesRead != -1) {
            out.write(buf, 0, bytesRead);
            bytesRead = in.read(buf);
        }
        in.close();
        out.flush();
    }
    private static int indexOf(byte[] arr, byte[] sub, int start) {
        int subIdx = 0;
        for (int x = start; x < arr.length; x++) {
            if (arr[x] == sub[subIdx]) {
                if (subIdx == sub.length - 1) {
                    return x - subIdx;
                }
                subIdx++;
            } else {
                subIdx = 0;
            }
        }
        return -1;
    }
    private static String fixString(String str) {
        int idx = 0;
        StringBuilder buf = new StringBuilder(str);
        while ((idx = buf.indexOf("http")) >= 0) {
            buf.delete(idx - 4, idx + 75);
        }
        return buf.toString();
    }
    private byte[] findDepthData() throws IOException {
        // get the Near and Far data first, since there's only one of them in
        // the file
        int nearStart = indexOf(inputData, OPEN_NEAR, 0) + OPEN_NEAR.length;
        int farStart = indexOf(inputData, OPEN_FAR, 0) + OPEN_FAR.length;
        int nearEnd = indexOf(inputData, CLOSE_DEPTH_RANGE, nearStart);
        int farEnd = indexOf(inputData, CLOSE_DEPTH_RANGE, farStart);
        // now get near and far for it
        if (nearStart >= 0 && farStart >= 0) {
            Double nearVal =
                    Double.parseDouble(new String(Arrays.copyOfRange(inputData,
                            nearStart, nearEnd)));
            Double farVal =
                    Double.parseDouble(new String(Arrays.copyOfRange(inputData,
                            farStart, farEnd)));
            near = nearVal;
            far = farVal;
        } else {
            return null;
        }
        int openIdx = indexOf(inputData, OPEN_ARR, 0);
        while (openIdx >= 0) {
            int closeIdx =
                    indexOf(inputData, CLOSE_ARR, openIdx + 1)
                            + CLOSE_ARR.length;
            byte[] segArr = Arrays.copyOfRange(inputData, openIdx, closeIdx);
            // instead of relying on the slow XMP parser, parse the bytes
            // ourselves.
            int dataStart = indexOf(segArr, OPEN_DATA, 0);
            int dataEnd = indexOf(segArr, CLOSE_DATA, dataStart + 1);
            String test = "";
            if (dataStart != -1) {
                test =
                        new String(Arrays.copyOfRange(segArr, dataStart + 13,
                                dataEnd));
                return fixString(test).getBytes("UTF-8");
            }
            openIdx = indexOf(inputData, OPEN_ARR, closeIdx + 1);
        }
        return null;
    }
}