package depthmapweb;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // now test the mesh creation
            MeshCreator creator =
                    new MeshCreator("IMG_20150116_100044.jpg", "test-obj4.obj");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
