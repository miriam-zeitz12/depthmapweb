package depthmapweb;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;

import org.apache.commons.io.IOUtils;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Main {
    /**
     * Stores the open port on this server. Assumed to be 9999.
     */
    public static final int DEFAULT_PORT = 9999;
    public static void main(String[] args) {
                //now test the mesh creation
//                MeshCreator creator = new MeshCreator("IMG_20150116_143419.jpg");
//                creator.extractAndWriteMesh("newtemp.obj");
            runSparkServer();
    }
    
    private static void runSparkServer() {
        Spark.setPort(DEFAULT_PORT);
        Spark.post("/create", (request, response) -> handleMeshCreation(request, response));
    }


    /**
     * Creates the mesh, and feeds it back to the user via an OutputStream
     * @param req - the HTTP Request
     * @param res - The HTTP response
     * @return nothing, we're feeding an outputstream
     */
    private static Object handleMeshCreation(Request req, Response res) {
        //first get the JSON object representing our image and the near, far values
        res.raw().setStatus(200);
        System.out.println("Received request.");
        String image = req.body(); //the entire request is the AJAX
        ByteArrayInputStream x = new ByteArrayInputStream(image.getBytes());
        String tempFile = "out-temp-"+java.util.UUID.randomUUID()+".obj";
        File readFile = new File(tempFile);
        try{
        DataExtractor extractor = new DataExtractor(x);
        //now parse it in a format Java won't choke on

//        try{
//        JsonReader jsonReader = Json.createReader(
//                new StringReader(imageJson));
//        JsonObject imageData = jsonReader.readObject();
//        System.out.println("Read the data");
//        jsonReader.close();
//        //Java's JSON interface is kind of strange
//        System.out.println("Trying to get image.");


        String imgBytes = new String(extractor.getDepthData());
        //ORDER IS IMPORTANT
        double near = extractor.getNear();
        double far = extractor.getFar();
        
        System.out.println("Got image successfully.");
        
        //create a UUID file as temp so we don't have collisions

            //Create the mesh
            MeshCreator creator = new MeshCreator(imgBytes, near, far);
            //write to obj file because we don't want to deal with refactoring the entire code
            creator.extractAndWriteMesh(tempFile);
            //Content type must be application/octet-stream
            res.raw().setContentType("application/octet-stream");
            res.raw().setCharacterEncoding("UTF-8");
            int size = (int) readFile.length();
            //res.status(200);
            System.out.println("About to write to response.");
            //avoid chunked encoding because it leads to strange exceptions
            res.raw().setContentLength(size);
            //write directly to the response outputstream
            BufferedOutputStream responseOutput = new BufferedOutputStream(res.raw().getOutputStream(), size);
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(readFile));
            //could use IOUtils.copy but we get less control over it if we do
            byte[] buffer = new byte[1024];
            int len;
            while ((len = buf.read(buffer)) > 0) {
                responseOutput.write(buffer,0,len);
            }
            responseOutput.flush();
            responseOutput.close();
            buf.close();
        } catch (IOException e) {
            //we should actually fail here honestly, but we can fail silently!
            res.raw().setStatus(200); //internal server error
            System.out.println("HERE3");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //clean up after ourselves
            res.raw().setStatus(200);
            System.out.println("Wrote response.");
            readFile.delete();
        }
        return res.raw();
    }
}
