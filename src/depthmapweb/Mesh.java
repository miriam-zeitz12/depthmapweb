package depthmapweb;
import java.util.List;
import obj.Point3D;

public class Mesh {
    private final int width;
    private final int height;
    private List<Point3D> vertices;
    private List<Point3D> vertexNormals;
    public Mesh(int w, int h, List<Point3D> points) {
        width = w;
        height = h;
        vertices = points;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public List<Point3D> getVertices() {
        return vertices;
    }
    private void computeVertexNormals() {
        int ax;
        int ay;
        double planeWidth =
                Math.abs(vertices.get(0).getX() - vertices.get(width).getX());
    }
}
