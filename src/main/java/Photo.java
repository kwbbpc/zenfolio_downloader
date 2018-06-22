import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Photo {


    private static final String base_url = "http://www.lindseywelchphotography.com";

    private String path;
    private String name;
    private String ts;
    private String tk;

    private Cookies ck;

    private String url;

    public Photo(String path, String name, String ts, String tk, Cookies ck) {
        this.path = path;
        this.name = name;
        this.ts = ts;
        this.tk = tk;
        this.ck = ck;
        this.url = base_url + path + "/" + name +  "?ts=" + ts + "&tk=" + tk;
    }

    private final String USER_AGENT = "Mozilla/5.0";
    public void download()throws Exception{
/*
        Image image = null;
        try {
            URL url = new URL(this.url);

            image = ImageIO.read(url);
        } catch (IOException e) {
            System.err.println(e);
        }
*/

try {
    URL obj = new URL(this.url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

    // optional default is GET
    con.setRequestMethod("GET");

    //add request header
    con.setRequestProperty("User-Agent", USER_AGENT);
    con.setRequestProperty("Cookie", ck.toString());

    int responseCode = con.getResponseCode();
    System.out.println("\nSending 'GET' request to URL : " + this.url);
    System.out.println("Response Code : " + responseCode);

    BufferedImage image = ImageIO.read(con.getInputStream());
    List<String> parsedName = Arrays.asList(this.name.split("/"));

    File f = new File("C:\\Users\\kybro\\Downloads\\photos\\" + parsedName.get(parsedName.size() - 1));
    ImageIO.write(image, "jpg", f);
}catch (Exception e){
    System.err.println("Failed to download image: " + e);
}
    }
}
