/**
 * Created by RyotoTomioka on 2017/06/12.
 */
public class Light {
    static int id_counter = 1;
    private int id;

    private int signalA = 0;  // 白色信号値
    private int signalB = 0;  // 昼白色信号値

    private double luminosity = 0;   // 光度
    private double luminosityA = 0;  // 白色光度
    private double luminosityB = 0;  // 電球色光度

    private double temperature = 4000;  // 色温度
    private double lumPct = 3;        // 光度パーセント

    public Light() {
        id = id_counter++;
    }

    public void setSignal(int a, int b) {
        signalA = a;
        signalB = b;
    }

    public double getLumPct() {
        return lumPct;
    }

    public double getTemperature() {
        return temperature;
    }

    public int[] getSignal() {
        int[] sigs = {signalA, signalB};
        return sigs;
    }
}
