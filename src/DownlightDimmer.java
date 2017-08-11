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
    // data
    private ArrayList<Integer> data;
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

    public void send() {
        // byte data
        data = new ArrayList<>();

        // STC
        data.add(0x0F);
        // ADR
        data.add(0x01);
        // SEQ
        data.add(seq);
        // CMD
        data.add(0x02);
        // LEN
        data.add(0x00);
        data.add(0xC8);
        // DATA
        makeData();
        // CB
        makeCheckBit();
        // ENC
        data.add(0xF0);


        // for debug
        for (int i : data) {
            System.out.print(i + ", ");
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

        System.out.println();
    }

    // make data bit from Arraylist<Light>
    private void makeData() {
        if(lights.size() > 64) {
            System.out.println("ERROR! 0x10: Number of lights is too much (over 64).");
            System.exit(0x10);
        }

        for(Light l: lights) {
            // illuminance
            data.add(0x00);
            // color temperature
            data.add(0x00);
            // duration
            data.add(0x00);
        }

        // fill 0x00 in data command section
        for(int i=0; i<200-lights.size()*3; i++) {
            data.add(0x00);
        }
    }

    // calc Check Bit from data
    private void makeCheckBit() {
        int cb = 0x00;

        for (int i=0; i<data.size()-4; i++) {
            cb = data.get(3+i) ^ data.get(3+i + 1);
        }

        data.add(cb);
    }
}

