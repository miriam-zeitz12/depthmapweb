package depthmapweb;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // now test the mesh creation
            MeshCreator creator = new MeshCreator("leaf.jpg", "test-mesh3.obj");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
