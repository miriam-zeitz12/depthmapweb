package depthmapweb;
import java.util.List;
import obj.ObjWriter;
import obj.Point3D;

public class PhotoObjWriter extends ObjWriter {
    private double baseDepth = 5.0;
    public PhotoObjWriter(String file) {
        super(file);
        // will change to a better header
        // headers are opt in .obj, though
        // so its just what we decide is best
        setHeader("3D Photo Model");
    }
    public void setBaseDepth(double depth) {
        baseDepth = depth;
    }
    /**
     * Generates a .obj file representation of a photo, using an ArrayList of
     * vertices representing each pixel
     * @param x the width of the photo in pixels (vertices)
     * @param y the height of the photo in pixels (vertices
     * @param vertices
     */
    public void writePhotoObj(int x, int y, Point3D[] vertices) {
        beginWrite();
        for (Point3D vertex : vertices) {
//            Point3D vertexPlusBase =
//                    new Point3D(vertex.getPointID(), vertex.getX(),
//                            vertex.getY(), vertex.getZ() + baseDepth);
            addVertex(vertex);
        }
//        addBottomVertices(vertices.get(0), vertices.get(x - 1),
//                vertices.get(x * y - 1), vertices.get(x * (y - 1)));
//        addPhotoFace(x, y);
//        addSidesAndBtmFaces(x, y);
        endWrite();
    }
    private void addPhotoFace(int x, int y) {
        int rowCount = 0;
        while (rowCount < y) {
            addRow(rowCount, x);
            rowCount++;
        }
    }
    private void addRow(int row, int numInRow) {
        int idxInRow = 0;
        int startVertex = row * numInRow;
        int currVertex = startVertex;
        while (idxInRow < numInRow) {
            addFace(currVertex, currVertex + 1, currVertex + numInRow + 1,
                    currVertex + numInRow);
            currVertex++;
            idxInRow++;
        }
    }
    /**
     * adds the non-photo sides to the model. assumes that all photo vertices
     * have some base z > 0
     * @param x
     * @param y
     */
    private void addSidesAndBtmFaces(int x, int y) {
        int vertexA = 1;
        int vertexB = x;
        int vertexC = x * y;
        int vertexD = (x * (y - 1)) + 1;
        int vertexE = x * y + 1;
        int vertexF = x * y + 2;
        int vertexG = x * y + 3;
        int vertexH = x * y + 4;
        addFace(vertexE, vertexF, vertexB, vertexA);
        addFace(vertexB, vertexF, vertexG, vertexC);
        addFace(vertexC, vertexD, vertexH, vertexG);
        addFace(vertexD, vertexH, vertexE, vertexA);
        addFace(vertexH, vertexG, vertexC, vertexD);
    }
    private void addBottomVertices(Point3D topA, Point3D topB, Point3D topC,
            Point3D topD) {
        Point3D bottomA =
                new Point3D(vertexCount + 1, topA.getX(), topA.getY(), 0);
        addVertex(bottomA);
        Point3D bottomB =
                new Point3D(vertexCount + 1, topB.getX(), topB.getY(), 0);
        addVertex(bottomB);
        Point3D bottomC =
                new Point3D(vertexCount + 1, topC.getX(), topC.getY(), 0);
        addVertex(bottomC);
        Point3D bottomD =
                new Point3D(vertexCount + 1, topD.getX(), topD.getY(), 0);
        addVertex(bottomD);
    }
}