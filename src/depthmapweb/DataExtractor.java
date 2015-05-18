package depthmapweb;
import java.io.*;
import java.util.*;

import com.adobe.xmp.*;

public class DataExtractor
{
    // An encoding should really be specified here, and for other uses of getBytes!
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

    private InputStream i;
    public DataExtractor(InputStream is) throws IOException, XMPException {
        OPEN_ARR = "<x:xmpmeta".getBytes("UTF-8");
        CLOSE_ARR = "</x:xmpmeta>".getBytes("UTF-8");
        OPEN_DATA = "GDepth:Data=\"".getBytes("UTF-8");
        CLOSE_DATA = "\"/>".getBytes("UTF-8");
        OPEN_NEAR = "GDepth:Near=\"".getBytes("UTF-8");
        OPEN_FAR = "GDepth:Far=\"".getBytes("UTF-8");
        CLOSE_DEPTH_RANGE = "\"".getBytes("UTF-8");
        i = is;
        depthData = findDepthData().getBytes("UTF-8");
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
    public byte[] getDepthData() {
        return depthData;
    }
    private static void copy(InputStream in, OutputStream out) throws IOException
    {
        int len = -1;
        byte[] buf = new byte[1024];
        while((len = in.read(buf)) >= 0)
        {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
    }

    private static int indexOf(byte[] arr, byte[] sub, int start)
    {
        int subIdx = 0;

        for(int x = start;x < arr.length;x++)
        {
            if(arr[x] == sub[subIdx])
            {
                if(subIdx == sub.length - 1)
                {
                    return x - subIdx;
                }
                subIdx++;
            }
            else
            {
                subIdx = 0;
            }
        }

        return -1;
    }

    private static String fixString(String str)
    {
        int idx = 0;
        StringBuilder buf = new StringBuilder(str);
        while((idx = buf.indexOf("http")) >= 0)
        {
            buf.delete(idx - 4, idx + 75);
        }

        return buf.toString();
    }

    public String findDepthData() throws IOException, XMPException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        copy(i, out);
        byte[] fileData = out.toByteArray();
        // the file
        int nearStart = indexOf(fileData, OPEN_NEAR, 0) + OPEN_NEAR.length;
        int farStart = indexOf(fileData, OPEN_FAR, 0) + OPEN_FAR.length;
        int nearEnd = indexOf(fileData, CLOSE_DEPTH_RANGE, nearStart);
        int farEnd = indexOf(fileData, CLOSE_DEPTH_RANGE, farStart);
        // now get near and far for it
        if (nearStart >= 0 && farStart >= 0) {
            Double nearVal =
                    Double.parseDouble(new String(Arrays.copyOfRange(fileData,
                            nearStart, nearEnd)));
            Double farVal =
                    Double.parseDouble(new String(Arrays.copyOfRange(fileData,
                            farStart, farEnd)));
            near = nearVal;
            far = farVal;
        } else {
            return null;
        }
        int openIdx = indexOf(fileData, OPEN_ARR, 0);
        while(openIdx >= 0)
        {
            int closeIdx = indexOf(fileData, CLOSE_ARR, openIdx + 1) + CLOSE_ARR.length;

            byte[] segArr = Arrays.copyOfRange(fileData, openIdx, closeIdx);
            XMPMeta meta = XMPMetaFactory.parseFromBuffer(segArr);

            String str = meta.getPropertyString("http://ns.google.com/photos/1.0/depthmap/", "Data");

            if(str != null)
            {
                return fixString(str);
            }

            openIdx = indexOf(fileData, OPEN_ARR, closeIdx + 1);
        }

        return null;
    }

//    public static void main(String[] args) throws Exception
//    {
//        long startTime = System.nanoTime();
//        String data = findDepthData(new File("IMG_20150116_143419.jpg"));
//        System.out.println("Finding data took: " + ((System.nanoTime() - startTime)*1000000000) + " ns");
//        if(data != null)
//        {
//            byte[] imgData = Base64.decode(data.getBytes());
//            ByteArrayInputStream in = new ByteArrayInputStream(imgData);
//            FileOutputStream out = new FileOutputStream(new File("out.png"));
//            copy(in, out);
//        }
//    }
}