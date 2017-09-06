import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        try {
            File file = new File("light_layout.csv");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Light light = new Light();
                light.setPosX(Double.parseDouble(data[0]));
                light.setPosY(Double.parseDouble(data[1]));
                lights.add(light);
            }
        } catch (IOException e) {}

        for(int i=0; i<num; i++) {

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
