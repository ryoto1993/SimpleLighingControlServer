import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by RyotoTomioka on 2017/08/11.
 *
 # Panasonic Downlight InterfaceDimmer
 # using LS/PD converter Plus
 #
 # coded by Ryoto Tomioka @20th
 # 2017/08/07
 */

public class DownlightDimmer {
    // Light object
    private ArrayList<Light> lights;
    // Byte data
    private int[] data;
    // sequence number
    private int seq = 0x00;
    // COM port name
    private static String com = "COM1";
    // COM port
    private CommPortIdentifier portId;
    private SerialPort port;
    private OutputStream out;

    public DownlightDimmer(ArrayList<Light> lights) {
        this.lights = lights;
        // config serial and open port
        try {
            portId = CommPortIdentifier.getPortIdentifier(com);
            port = (SerialPort) portId.open("serial", 2000);
            port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_ODD);
            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

