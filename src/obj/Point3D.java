package obj;
public class Point3D {
    /**
     * ID of the point, which will represent its ordering in the .OBJ file.
     */
    private int pointID;
    /**
     * X-coordinate of the point.
     */
    private double x;
    /**
     * Y-coordinate of the point.
     */
    private double y;
    /**
     * Z-coordinate of the point.
     */
    private double z;
    public Point3D(int id, double newX, double newY, double newZ) {
        x = newX;
        y = newY;
        z = newZ;
        pointID = id;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public int getPointID() {
        return pointID;
    }
    @Override
    public String toString() {
        return pointID + ":[" + x + "," + y + "," + z + "]";
    }
}