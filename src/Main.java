/**
 * Created by RyotoTomioka on 2017/06/12.
 */
public class Main {
    public static void main(String[] args) {
        ILS ils = new ILS();
        ils.makeLight();
        ils.startDimmer();
        ils.startServer();
    }
}
