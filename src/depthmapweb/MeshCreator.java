package depthmapweb;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import obj.Point3D;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.xmp.impl.Base64;

/**
 * Builds the 3D mesh and writes a .OBJ file. Created by Vinushka on 5/4/2015.
 */
public class MeshCreator {
    final Logger logger = LoggerFactory.getLogger(MeshCreator.class);
    /**
     * Offset used to generate the "base" of the object.
     */
    private static final double OFFSET = 0.05;
    /**
     * The depth map image to be processed.
     */
    private BufferedImage storeImg;
    private double near;
    private double far;
    // private String outputFile;
//    /**
//     * Creates the 3D mesh from the depth map at inFilePath, and writes an .OBJ
//     * file at outFilePath.
//     * @param inFilePath - the path to the depth map as a Uri
//     * @throws IOException - if there's issues reading the depth map
//     */
//    public MeshCreator(String inFilePath)
//            throws IOException {
//        // first get the data
//        File imageFile = new File(inFilePath);
//        DataExtractor extract = new DataExtractor(imageFile);
//        byte[] data = extract.getDepthData();
//        if (data == null) {
//            throw new IllegalArgumentException(
//                    "Did not provide an image with depth map information.");
//        }
//        near = extract.getNear();
//        far = extract.getFar();
//        logger.info("Found data: near: {}, far: {}", Double.toString(near),
//                Double.toString(far));
//        logger.info("Base64: {}", new String(data));
//        // now make an image out of it!
//        logger.info("Length of data: {}", Integer.toString(data.length));
//        logger.info("First byte in data: {}", Byte.toString(data[0]));
//        logger.info("Last byte in data: {}",
//                Byte.toString(data[data.length - 1]));
//        getImageFromBase64(data);
//    }

    /**
     * Creates the mesh from pre-extracted data. Used for the server.
     * @param base64Data - the base64 string representing the depth map,
     * as a byte array.
     * @param n - the GDepth:Near value
     * @param f - the GDepth:Far value
     * @throws IOException - if there's issues opening files for writing and stuff
     */
    public MeshCreator(String base64Data, double n, double f) throws IOException {
        near = n;
        far = f;
        getImageFromBase64(base64Data.getBytes());
    }

    /**
     * Turns the base64 data into an image that we can read with IOUtils.
     * @param base64Data - base64 string as a byte array.
     * @throws IOException - if there's issues reading the file
     */
    private void getImageFromBase64(byte[] base64Data) throws IOException {
        File outFile = new File("out-tmp-"+java.util.UUID.randomUUID()+".png");
        byte[] imgData = Base64.decode(base64Data);
        //write to PNG so we can use ImageIO to figure out how this works
        ByteArrayInputStream in = new ByteArrayInputStream(imgData);
        FileOutputStream out = new FileOutputStream(outFile);
        IOUtils.copy(in, out);
        // read it back in
        storeImg = ImageIO.read(outFile);
        outFile.delete(); //delete it after it's in memory, we don't need it after.
        //System.out.println(outFile.delete());
    }

    /**
     * Extracts the mesh and writes it out to the file at outFilePath.
     * @param outFilePath - the path to write the mesh OBJ to.
     */
    public void extractAndWriteMesh(String outFilePath) {
        //get the points
        //List<Point3D> points = getPoints();
        Point3D[] points = getPoints();
        int s = 6;
        int height = storeImg.getHeight();
        int width = storeImg.getWidth();
        int w = width / s;
        int h = height / s;
        logger.info("Image is null: {}", Boolean.toString(storeImg == null));
        // now spawn a PhotoObjWriter
        PhotoObjWriter writer = new PhotoObjWriter(outFilePath);
        writer.writePhotoObj(w, h, points);
    }

//    public byte[] getMesh
    /**
     * Returns the width of the image to be processed.
     * @return The width of the image to be processed.
     */
    public int getWidth() {
        return storeImg.getWidth();
    }
    /**
     * Returns the height of the image to be processed.
     * @return The height of the image to be processed.
     */
    public int getHeight() {
        return storeImg.getHeight();
    }
    /**
     * Creates 3D points based on the depth data.
     * @param far - the "far" value in GDepth:Far.
     * @param near the "near" value in GDepth:Near.
     * @param image - the Bitmap object holding the depth map itself.
     * @return The list consisting of the "point cloud" created from the depth
     *         map.
     */
    public Point3D[] getPoints() {
        // we have the bitmap so let's first get height and width
        // List<Point3D> returnPoints = new ArrayList<Point3D>();
        int s = 6;
        int height = storeImg.getHeight();
        int width = storeImg.getWidth();
        int w = width / s;
        int h = height / s;
        Point3D[] returnPoints = new Point3D[h * w];
        byte[] pixels =
                ((DataBufferByte) storeImg.getRaster().getDataBuffer())
                        .getData();
        // int[][] pixelArray = ImageHelper.getPixels2DArray(storeImg);
        // now make the points.
        int counter = 1; // counts the vectorIDs. Replace this with a static int
                         // in point3D.
        double minZ = Float.POSITIVE_INFINITY; // find the minimal as we go
                                               // along
        double ar = height / width;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                // I think ?? you want x & y revered like this?
                // now make the point

                // if (z < minZ)
                // minZ = z;
                double newX = (x - 0.5 * w) / w;
                double newY = (y - 0.5 * h) / h;
                int p =
                        (int) (Math.round(((-newY + .5)) * (height - 1))
                                * width * 3 + Math.round(((newX + .5))
                                * (width - 1)) * 3);
                double dn = pixels[p]; // this PROBABLY
                // works. First
                // debug to check
                // what these values
                // look like?
                dn = dn / 255.;
                double rd = (far * near) / (far - dn * (far - near)); // see the
                // Android
                // depth
                // map
                // algorithm
                // link
                double newZ = -rd;
                newX *= rd * 1;
                newY *= rd * ar;
                Point3D newPoint = new Point3D(counter, newX, newY, newZ);
                returnPoints[counter - 1] = newPoint;
                counter++;
            }
        }
        // now do it again, but for the "base" layer
        // Commented out since Miriam does this in ObjWriter!
        // for (int x = 0; x < width; x++) {
        // for (int y = 0; y < height; y++) {
        // Point3D newPoint = new Point3D(x,y,minZ-OFFSET,counter);
        // returnPoints.add(newPoint);
        // counter++;
        // }
        // }
        // Now we've got the points, so just return them.
        return returnPoints;
    }
}