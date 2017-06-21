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

    public Light() {
        id = id_counter++;
    }

    public void setSignal(int a, int b) {
        signalA = a;
        signalB = b;
    }

    public int getId() {
        return id;
    }

    public void setSignalA(int a) {
        signalA = a;
    }

    public void setSignalB(int b) {
        signalB = b;
    }

    public int[] getSignal() {
        int[] sigs = {signalA, signalB};
        return sigs;
    }
}
