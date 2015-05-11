package depthmapweb;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
                //now test the mesh creation
                MeshCreator creator = new MeshCreator("IMG_20150116_143419.jpg","test-obj.obj");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
