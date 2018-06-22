public class App {

    public static void main(String args[])throws Exception{


        String layoutUrl = "http://www.lindseywelchphotography.com/zf/layout/layout.asmx?request=%7B%22method%22%3A%22GetPhotoRange%22%2C%22params%22%3A%5B23880217%2C606963593%2C0%2C100%2C%22qTAH0I3moqV4ayC2JUcLZLhHnmLaMVqN_PhFBtH7-r8%3D%22%2C%22EzctVg3fsx2zrPRu%2BthnmGEq%22%2C74%5D%2C%22id%22%3A0%7D&__cv=64663";

        ZenfolioProxy proxy = new ZenfolioProxy(layoutUrl, "kristenmaternity");
        proxy.getPhotos("C:\\Users\\kybro\\Downloads\\photos\\maternity.html");


    }
}
