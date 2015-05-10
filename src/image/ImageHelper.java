package image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Helper class for getting pixels from {@link BufferedImage} objects and
 * converting them to a 2D array for easy pixel look up via x,y coordinates.
 * @author miriamzeitz
 */
public class ImageHelper {
    public static int[][] getPixels2DArray(BufferedImage image) {
        byte[] pixels =
                ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        int width = image.getWidth();
        int height = image.getHeight();
        boolean hasAlpha = image.getAlphaRaster() != null;
        int[][] pixels2D = new int[height][width];
        if (hasAlpha) {
            return getArrayAlpha(width, pixels, pixels2D);
        } else {
            return getArray(width, pixels, pixels2D);
        }
    }
    private static int[][] getArray(int width, byte[] pixels, int[][] pixels2D) {
        int pixelLength = 3;
        int row = 0;
        int col = 0;
        for (int pixel = 0; pixel < pixels.length; pixel += pixelLength) {
            // set all to 255 alpha
            int argb = -16777216;
            // get blue
            argb += ((int) pixels[pixel] & 0xff);
            // get green
            argb += (((int) pixels[pixel + 1] & 0xff) << 8);
            // get red
            argb += (((int) pixels[pixel + 2] & 0xff) << 16);
            pixels2D[row][col] = argb;
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
        return pixels2D;
    }
    private static int[][] getArrayAlpha(int width, byte[] pixels,
            int[][] pixels2D) {
        int pixelLength = 4;
        int row = 0;
        int col = 0;
        for (int pixel = 0; pixel < pixels.length; pixel += pixelLength) {
            int argb = 0;
            // get alpha
            argb += (((int) pixels[pixel] & 0xff) << 24);
            // get blue
            argb += ((int) pixels[pixel] & 0xff);
            // get green
            argb += (((int) pixels[pixel + 1] & 0xff) << 8);
            // get red
            argb += (((int) pixels[pixel + 2] & 0xff) << 16);
            pixels2D[row][col] = argb;
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }
        return pixels2D;
    }
}
