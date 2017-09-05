/**
 * Created by RyotoTomioka on 2017/06/12.
 */
public class Light {
    static int id_counter = 1;
    private int id;

    private int signalA = 0;  // white signal
    private int signalB = 0;  // natural signal

    private double temperature = 4000;  // color temperature
    private double lumPct = 50.0;       // luminosity percentage

    public Light() {
        id = id_counter++;
    }

    public void setSignals(int[] signals) {
        signalA = signals[0];
        signalB = signals[1];
    }

    public void setSignals(int sig_a, int sig_b) {
        signalA = sig_a;
        signalB = sig_b;
    }

    public void setLumPct(double pct) {this.lumPct = pct;}

    public void setTemperature(double tmp) {this.temperature = tmp;}

    public int getId() { return id; }

    public double getLumPct() {
        return lumPct;
    }

    public double getTemperature() {
        return temperature;
    }

    public int[] getSignals() {
        int[] signals = {signalA, signalB};
        return signals;
    }
}
