import java.util.List;

public class Cookies {

    String path;
    List<String> originalcookie;

    public Cookies(List<String> cookieStr){
        this.originalcookie = cookieStr;
        String[] pathArg = cookieStr.get(3).split(";");
        String[] pathArgParsed = pathArg[1].split("=");
        this.path = pathArgParsed[1];
    }

    public List<String> getCookies(){
        return this.originalcookie;
    }

    @Override
    public String toString(){
        String arr = String.join(";", this.originalcookie);
        return arr.substring(1, arr.length()-1);
    }
}
