package depthmapweb;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
        String imageJson = req.body(); //the entire request is the AJAX
        //now parse it in a format Java won't choke on
        JsonReader jsonReader = Json.createReader(
                new StringReader(imageJson));
        JsonObject imageData = jsonReader.readObject();
        jsonReader.close();
        //Java's JSON interface is kind of strange
        double near = imageData.getJsonNumber("near").doubleValue();
        double far = imageData.getJsonNumber("far").doubleValue();
        String imgBytes = imageData.getString("imageData");
        try {
            MeshCreator creator = new MeshCreator(imgBytes, near, far);
            String tempFile = "out-temp-"+java.util.UUID.randomUUID()+".obj";
            creator.extractAndWriteMesh(tempFile);
          //  Path path = FileSystems.getDefault().getPath(tempFile);
            File readFile = new File(tempFile);
            //FileInputStream responseStream = new FileInputStream(readFile);

            //res.raw().setContentLength((int)readFile.length());
            //res.raw().setHeader("Content-Disposition", "attachment; filename="+tempFile);
            res.raw().setContentType("application/octet-stream");
            res.raw().setCharacterEncoding("UTF-8");
            int size = (int) readFile.length();
            res.raw().setContentLength(size);
            //res.raw().setHeader(CUSTOM_OUTPUT_LENGTH, Long.toString(readFile.length()));
            BufferedOutputStream responseOutput = new BufferedOutputStream(res.raw().getOutputStream(), size);
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(readFile));

         //   DataExtractor.copy(responseStream, responseOutput, 256);

       //     res.raw().setContentLength(4);
            //res.body("test");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = buf.read(buffer)) > 0) {
                responseOutput.write(buffer,0,len);
            }
            buf.close();
            responseOutput.flush();
            responseOutput.close();
            //res.raw().setContentType("application/octet-stream");

          //  res.raw().getWriter().write("test");
//            res.raw().getWriter().flush();
//            res.raw().getWriter().close();

            System.out.println("HERE2");
//            byte[] fileBytes = Files.readAllBytes(path);
//            responseOutput.write(fileBytes);
//            //IOUtils.copy(responseStream, responseOutput);
//            System.out.println("HERE");
//            res.status(200);
//            readFile.delete(); //clean up after ourselves
//            responseOutput.flush();
//            responseOutput.close();
//            return res.raw();
        } catch (IOException e) {
            //we should actually fail here honestly, but we can fail silently!
            res.status(500); //internal server error
            System.out.println("HERE3");
            e.printStackTrace();
        }
        return res.raw();
    }
}
