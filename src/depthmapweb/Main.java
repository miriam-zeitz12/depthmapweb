package depthmapweb;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Main {
    /**
     * Stores the open port on this server. Assumed to be 9999.
     */
    public static final int DEFAULT_PORT = 9999;
    public static void main(String[] args) {
        // temp debug opt to test mesh w/o server
        if (args[0] == "mesh") {
            MeshCreator creator;
            try {
                creator = new MeshCreator("IMG_20150116_143419.jpg");
                creator.extractAndWriteMesh("newtemp.obj");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // otherwise, run full program
        else {
            runSparkServer();
        }
    }
    private static void runSparkServer() {
        Spark.setPort(DEFAULT_PORT);
        Spark.post("/create",
                (request, response) -> handleMeshCreation(request, response));
    }
    /**
     * Creates the mesh, and feeds it back to the user via an OutputStream
     * @param req - the HTTP Request
     * @param res - The HTTP response
     * @return nothing, we're feeding an outputstream
     */
    private static Object handleMeshCreation(Request req, Response res) {
        // first get the JSON object representing our image and the near, far
        // values
        res.status(200);
        System.out.println("Received request.");
        String imageJson = req.body(); // the entire request is the AJAX
        // now parse it in a format Java won't choke on
        JsonReader jsonReader = Json.createReader(new StringReader(imageJson));
        JsonObject imageData = jsonReader.readObject();
        System.out.println("Read the data");
        jsonReader.close();
        // Java's JSON interface is kind of strange
        System.out.println("Trying to get image.");
        double near = imageData.getJsonNumber("near").doubleValue();
        double far = imageData.getJsonNumber("far").doubleValue();
        String imgBytes =
                DataExtractor.fixString(imageData.getString("imageData"));
        System.out.println("Got image successfully.");
        // write imgBytes to a file before we do anything because Android sucks
        try {
            FileOutputStream outTest = new FileOutputStream("android-test.txt");
            outTest.write(imgBytes.getBytes(), 0, imgBytes.getBytes().length);
            outTest.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // create a UUID file as temp so we don't have collisions
        String tempFile = "out-temp-" + java.util.UUID.randomUUID() + ".obj";
        File readFile = new File(tempFile);
        try {
            // Create the mesh
            MeshCreator creator = new MeshCreator(imgBytes, near, far);
            // write to obj file because we don't want to deal with refactoring
            // the entire code
            creator.extractAndWriteMesh(tempFile);
            // Content type must be application/octet-stream
            res.raw().setContentType("application/octet-stream");
            res.raw().setCharacterEncoding("UTF-8");
            int size = (int) readFile.length();
            // res.status(200);
            System.out.println("About to write to response.");
            // avoid chunked encoding because it leads to strange exceptions
            res.raw().setContentLength(size);
            // write directly to the response outputstream
            BufferedOutputStream responseOutput =
                    new BufferedOutputStream(res.raw().getOutputStream(), size);
            BufferedInputStream buf =
                    new BufferedInputStream(new FileInputStream(readFile));
            // could use IOUtils.copy but we get less control over it if we do
            byte[] buffer = new byte[1024];
            int len;
            while ((len = buf.read(buffer)) > 0) {
                responseOutput.write(buffer, 0, len);
            }
            responseOutput.flush();
            responseOutput.close();
            buf.close();
        } catch (IOException e) {
            // we should actually fail here honestly, but we can fail silently!
            res.status(500); // internal server error
            System.out.println("HERE3");
            e.printStackTrace();
        } finally {
            // clean up after ourselves
            System.out.println("Wrote response.");
            readFile.delete();
        }
        return res.raw();
    }
}
