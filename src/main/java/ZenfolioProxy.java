import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import sun.nio.cs.StandardCharsets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ZenfolioProxy {


    private final String USER_AGENT = "Mozilla/5.0";

    private Cookies cookies;

    private final String base_url = " http://www.lindseywelchphotography.com/";
    private String layoutUrl;
    private final String key;
    private List<PhotoUrl> photoUrls;


    public ZenfolioProxy(String layoutUrl, String key)throws Exception{
        this.layoutUrl = layoutUrl;
        this.key = key;
        this.photoUrls = new ArrayList<PhotoUrl>();

        openConnection();

    }

    private void openConnection() throws Exception {
        URL obj = new URL(base_url + this.key);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        URL obj1 = new URL(base_url + this.key);
        HttpURLConnection con2 = (HttpURLConnection) obj.openConnection();

        URL obj2 = new URL(base_url + this.key);
        HttpURLConnection con3 = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + base_url + this.key);
        System.out.println("Response Code : " + responseCode);
        this.cookies = new Cookies(con.getHeaderFields().get("Set-Cookie"));

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response);

    }

    private void initializeImages(String path) throws Exception{
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        String fileStr =  new String(encoded, Charset.defaultCharset().name());
        parseImagenames(fileStr);
    }

    private void parseImagenames(String htmlMainPage) throws Exception{

        final Tidy tidy = new Tidy();
        tidy.setMakeClean( true );
        tidy.setXHTML( true );
        tidy.setSmartIndent( true );
        final Document document = tidy.parseDOM( new ByteArrayInputStream( htmlMainPage.getBytes("UTF-8") ) , null );


        NodeList metaTags = document.getElementsByTagName("img");
        for(int i=0; i<metaTags.getLength(); ++i){
            Node metaTag = metaTags.item(i);
            Node style = metaTag.getAttributes().getNamedItem("style");

            if(style != null) {
                String value = style.getNodeValue();
                if (value.contains("jpg")) {
                    this.photoUrls.add(new PhotoUrl(value));
                }
            }
        }

    }

    private JsonNode sendGet() throws Exception {


        URL obj = new URL(layoutUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + layoutUrl);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response.toString());
    }


    public void getPhotos(String htmlFilePath) throws Exception{

        initializeImages(htmlFilePath);

        JsonNode layout =  sendGet();

        ArrayNode objs = (ArrayNode)layout.get("result").get("$root");

        int index = 0;

        List<Photo> photos = new ArrayList<Photo>();

        for(JsonNode r : objs){
            ArrayNode meta = (ArrayNode)r.get("$obj");
            String path1 = this.cookies.path;
            String path2 = meta.get(1).asText();

            int id = meta.get(2).asInt();
            long idl = meta.get(2).asLong();




            String backPath = path2.substring(path2.indexOf("/", 5), path2.length());
            String path = path1.concat(backPath);

            String ts = meta.get(5).asText();
            String tk = meta.get(15).asText();
            if(index < this.photoUrls.size()) {
                Photo photo = new Photo(path, this.photoUrls.get(index).getPhotoUrlPartial(), ts, tk, cookies);
                photos.add(photo);
            }

            ++index;

        }

        for(Photo p : photos){
            p.download();
        }



    }
}
