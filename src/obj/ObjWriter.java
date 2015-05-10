package obj;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ObjWriter {
    protected FileOutputStream outputStream;
    // should work on ensuring .obj extension in name
    protected String fileName;
    protected int vertexCount = 0;
    protected OutputStreamWriter writer;
    protected final String commentTag = "# ";
    protected String fileHeader = "Default header";
    private String toWrite;
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
            writer = new OutputStreamWriter(outputStream);
            writeHeader();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    // not sure of the best way to ensure that this method is called
    public void endWrite() {
        try {
            writer.write(toWrite);
            writer.flush();
            writer.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeHeader() {
        toWrite += commentTag;
        toWrite += fileHeader + "\n";
    }
    public void addVertex(Point3D vertex) {
        String vertexString =
                "v " + vertex.getX() + " " + vertex.getY() + " "
                        + vertex.getZ() + "\n";
        toWrite += vertexString;
        vertexCount++;
    }
    public void addFace(int vertexA, int vertexB, int vertexC, int vertexD) {
        toWrite +=
                "f " + vertexA + " " + vertexB + " " + vertexC + " " + vertexD
                        + "\n";
    }
    public void addComment(String comment) {
        toWrite += commentTag + comment + "\n";
    }
}
