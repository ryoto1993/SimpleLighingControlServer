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
 *
 * NOTE:
 * By use this class as thread,
 * you can send signals every 1 second.
 * If you want to send signal from out side of thread
 * you should use this class as non-thread class.
 *
 */

public class DownlightDimmer extends Thread{
    // Light object
    private ArrayList<Light> lights;
    // data
    private ArrayList<Integer> data;
    // sequence number
    private static int SEQ = 0x00;
    // COM port name
    private static String com = "COM5";
    // COM port
    private CommPortIdentifier portId;
    private SerialPort port;
    private OutputStream out;

    // send signals every 1 seconds.
    public void run() {
        while (true) {
            // send
            send();

            // sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    }

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

    synchronized public void send() {
        // byte data
        data = new ArrayList<>();

        // STC
        data.add(0x0F);
        // ADR
        data.add(0x01);
        // SEQ
        data.add(SEQ);
        // CMD
        data.add(0x02);
        // LEN
        data.add(0x00);
        data.add(0xC0);
        // DATA
        makeData();
        // CB
        makeCheckBit();
        // ENC
        data.add(0xF0);

        // for debug
        for (int i : data) {
            // ToDo: for debug
            String hex = String.format("%02x", i);
            System.out.print(hex + ", ");
        }

        // send data into RS485
        try{
            out = port.getOutputStream();
            for(int i:data) {
                out.write(i);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        // ToDo: for debug
        System.out.println();

        SEQ++;
        if(SEQ>0xFF) {
            SEQ = 0x00;
        }
    }

    // make data bit from Arraylist<Light>
    private void makeData() {
        if(lights.size() > 64) {
            System.out.println("ERROR! 0x10: Number of lights is too much (over 64).");
            System.exit(0x10);
        }

        for(Light l: lights) {
            // illuminance
            data.add(convertLumToHex(l.getLumPct()));
            // color temperature
            data.add(convertTmpToHex(l.getTemperature()));
            // duration
            data.add(0x0A);
        }

        /**
         *
         * Ryoto Tomioka, 2017/08/24
         * Deleted extra 8 byte 0xFF
         *
         **/
    }

    // calc Check Bit from data
    private void makeCheckBit() {
        int cb = 0x00;

        for (int i=0; i<data.size()-3; i++) {
            cb = data.get(3+i) ^ cb;
        }

        data.add(cb);
    }

    // convert luminous percent to hex
    private int convertLumToHex(double luminance_signal) {
        return (int)(luminance_signal / 0.5 * 0x01);
    }

    // convert color temperature to hex sig
    private int convertTmpToHex(double temp) {
        return (int)((temp-1550)/50 * 0x01);
    }
}

