import java.util.ArrayList;

/**
 * Created by RyotoTomioka on 2017/06/13.
 */
public class ILS {
    private ArrayList<Light> lights = new ArrayList<>();
    private ArrayList<Sensor> sensors = new ArrayList<>();
    public SocketServer socketServer;
    public DownlightDimmer downlightDimmer;

    public ILS() {
        // make dimmer object
        downlightDimmer = new DownlightDimmer(lights);

    }
    public void makeLight(int num) {
        for(int i=0; i<num; i++) {
            lights.add(new Light());
        }
    }
    public void makeSensor(int num) {
        for(int i=0; i<num; i++) {
            sensors.add(new Sensor());
        }
    }

    public void startServer() {
        // start server
        socketServer = new SocketServer(this);
        socketServer.start();
    }
    public void startDimmer() {
        downlightDimmer.start();

    }
    public ArrayList<Light> getLights() {
        return lights;
    }

    public Light getLight(int id) {
        return lights.get(id-1);
    }

    public ArrayList<Sensor> getSensors() {
        return sensors;
    }
}
