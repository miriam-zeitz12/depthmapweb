package obj;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ObjWriter {
    protected FileOutputStream outputStream;
    // should work on ensuring .obj extension in name
    protected String fileName;
    protected int vertexCount = 0;
    protected PrintWriter writer;
    protected final String commentTag = "# ";
    protected String fileHeader = "Default header";
    protected int vertexNormalCount = 0;
    // private String toWrite;
    public ObjWriter(String file) {
        fileName = file;
    }
    public void setHeader(String header) {
        fileHeader = header;
    }
    public String getHeader() {
        return fileHeader;
    }
    public int getVertexCount() {
        return vertexCount;
    }
    public void beginWrite() {
        try {
            outputStream = new FileOutputStream(fileName);
            writer = new PrintWriter(outputStream);
            writeHeader();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    // not sure of the best way to ensure that this method is called
    public void endWrite() {
        try {
            writer.flush();
            writer.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeHeader() {
        writer.print(commentTag);
        writer.println(fileHeader);
    }
    public void addVertex(Point3D vertex) {
        String vertexString =
                "v " + vertex.getX() + " " + vertex.getY() + " "
                        + vertex.getZ();
        writer.println(vertexString);
        vertexCount++;
    }
    public void addVertexNormal(Point3D vertexNormal) {
        String vertexNormalString =
                "vn " + vertexNormal.getX() + " " + vertexNormal.getY() + " "
                        + vertexNormal.getZ();
        writer.println(vertexNormalString);
        vertexNormalCount++;
    }
    public void addFace(int vertexA, int vertexB, int vertexC) {
        writer.println("f " + vertexA + " " + vertexB + " " + vertexC);
    }
    public void addComment(String comment) {
        writer.println(commentTag + comment);
    }
}
