import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PhotoUrl {

    String name;
    String s;
    String v;

    public PhotoUrl(String photoUrl) {
        List<String> breakdown = Arrays.asList(photoUrl.split("/|\\)"));

        this.name = breakdown.get(10).split("-")[0];
        this.s = breakdown.get(8);
        this.v = breakdown.get(9);
    }


    public String getPhotoUrlPartial(){
        return this.s + "/" + this.v + "/" + this.name + "-6.jpg";
    }
}
