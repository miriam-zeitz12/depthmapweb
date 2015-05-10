package depthmapweb;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import com.adobe.xmp.impl.Base64;

public class Main {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        DataExtractor extract;
        try {
            extract = new DataExtractor((new File("IMG_20150116_143419.jpg")));
            System.out.println(extract.getNear());
            System.out.println(extract.getFar());
            byte[] data = extract.getDepthData();
            System.out.println(data.length);
            if (data != null) {
                System.setProperty("file.encoding", "UTF-8");
                OutputStreamWriter writer =
                        new OutputStreamWriter(new FileOutputStream(new File(
                                "out-java.txt")));
                writer.write(new String(data));
                writer.close();
                System.out
                        .println("First: " + data[0] + " Second:  " + data[1]);
                System.out.println("length-1: " + data[data.length - 2]
                        + " End: " + data[data.length - 1]);
                byte[] imgData = Base64.decode(data);
                ByteArrayInputStream in = new ByteArrayInputStream(imgData);
                FileOutputStream out =
                        new FileOutputStream(new File("out.png"));
                DataExtractor.copy(in, out, 1024);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
