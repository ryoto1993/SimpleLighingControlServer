/**
 * Created by RyotoTomioka on 2017/06/12.
 */
public class Manager {
    public static void main(String[] args) {
        ILS ils = new ILS();
        ils.makeLight(36);
        ils.startDimmer();
        ils.startServer();
    }
}
