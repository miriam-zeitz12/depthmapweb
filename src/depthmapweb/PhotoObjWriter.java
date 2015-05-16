package depthmapweb;
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
        int ct = 0;
        for (Point3D vertex : vertices) {
            // Point3D vertexPlusBase =
            // new Point3D(vertex.getPointID(), vertex.getX(),
            // vertex.getY(), vertex.getZ() + baseDepth);
            addVertex(vertex);
        }
        addBottomVertices(vertices[0], vertices[x - 1], vertices[x * y - 1],
                vertices[x * y - x]);
        addPhotoWorks(x, y);
        addSidesAndBtmFaces(x, y);
        endWrite();
    }
    private void addPhotoWorks(int x, int y) {
        int lastVert = x * y;
        int currVertex = 1;
        int numInRow = x;
        while (currVertex + numInRow + 1 <= lastVert) {
            addFace(currVertex + numInRow, currVertex, currVertex + 1);
            addFace(currVertex + numInRow + 1, currVertex + numInRow,
                    currVertex + 1);
            currVertex++;
        }
    }
    private void addPhotoFace(int x, int y) {
        int rowCount = 0;
        while (rowCount < y - 1) {
            addRow(rowCount, x);
            rowCount++;
        }
    }
    private void addRow(int row, int numInRow) {
        int idxInRow = 1;
        int startVertex = row * numInRow + 1;
        int currVertex = startVertex;
        while (idxInRow < numInRow) {
            addFace(currVertex, currVertex + numInRow, currVertex + 1);
            addFace(currVertex + numInRow, currVertex + numInRow + 1,
                    currVertex + 1);
            currVertex++;
            idxInRow++;
        }
    }
    /*
     * private void computeVertNormals(int cols, int rows, Point3D[] vertices) {
     * double width = Math.abs(vertices[0].getX() - vertices[cols - 1].getX());
     * double height = Math.abs(vertices[0].getY() - vertices[cols - 1].getY());
     * double ax = width / (cols - 1); double ay = height / (rows - 1); int idx
     * = 0; for (Point3D v: vertices){ if (idx - cols > 0){ double Zup =
     * vertices[idx - cols ].getZ(); } }
     */
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
        int vertexD = x * y - x + 1;
        int vertexE = x * y + 1;
        int vertexF = x * y + 2;
        int vertexG = x * y + 3;
        int vertexH = x * y + 4;
        // this side is also correct now!
        addFace(vertexE, vertexF, vertexA);
        addFace(vertexA, vertexF, vertexB);
        addFace(vertexF, vertexG, vertexB);
        addFace(vertexB, vertexG, vertexC);
        addFace(vertexG, vertexH, vertexC);
        addFace(vertexC, vertexH, vertexD);
        addFace(vertexH, vertexE, vertexD);
        addFace(vertexD, vertexE, vertexA);
        // this is correct vertex ordering
        // for side EFGH, where E = top left
        // F = top right, G = bottom right
        // H = bottom left
        addFace(vertexE, vertexG, vertexH);
        addFace(vertexG, vertexE, vertexF);
    }
    private void addBottomVertices(Point3D topA, Point3D topB, Point3D topC,
            Point3D topD) {
        double minZ = Math.min(topA.getZ(), topB.getZ());
        minZ = Math.min(topC.getZ(), minZ);
        minZ = Math.min(topD.getZ(), minZ);
        System.out.println("photo top right: " + topA.toString());
        // System.out.println()
        vertexCount += 1;
        Point3D bottomA =
                new Point3D(vertexCount, topA.getX(), topA.getY(), minZ - 2);
        addVertex(bottomA);
        vertexCount += 1;
        Point3D bottomB =
                new Point3D(vertexCount, topB.getX(), topB.getY(), minZ - 2);
        addVertex(bottomB);
        vertexCount += 1;
        Point3D bottomC =
                new Point3D(vertexCount, topC.getX(), topC.getY(), minZ - 2);
        addVertex(bottomC);
        vertexCount += 1;
        Point3D bottomD =
                new Point3D(vertexCount, topD.getX(), topD.getY(), minZ - 2);
        addVertex(bottomD);
    }
}