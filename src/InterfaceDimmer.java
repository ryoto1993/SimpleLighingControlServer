import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by RyotoTomioka on 2017/06/12.
 * 調光インターフェース用 InterfaceDimmer
 */
public class InterfaceDimmer {
    static int NUM_INTERFACE = 2;  // 調光インターフェイスの数
    static int NUM_SIGNAL = 2;     // 1台の照明に送る信号の数（昼白色と電球色なら2）
    static String com = "COM1";
    //static String com = "/dev/tty.usbserial";

    CommPortIdentifier portId;
    SerialPort port;
    OutputStream out;
    ArrayList<Light> lights;

    public InterfaceDimmer(ArrayList<Light> lights) {
        this.lights = lights;
        // シリアル通信設定とポートOpen
        try{
            portId = CommPortIdentifier.getPortIdentifier(com);
            port = (SerialPort)portId.open("serial", 2000);
            port.setSerialPortParams(38400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void send() {
        // バイト列を作成
        int[] data = new int[10 + NUM_INTERFACE*10 + 1];

        // 調光インターフェイス（MODEL-LC3155）の通信プロトコル
        data[0] = 0x81;  // ヘッダー
        data[1] = 0x7E;  // ヘッダー
        data[2] = 0xFF;  // 送信先（ブロードキャスト）
        data[3] = 0x00;  // 送信元（親機）
        data[4] = (NUM_INTERFACE*10+4)%256;  // インタフェース数チェック
        data[5] = (NUM_INTERFACE*10+4)/256;  // インタフェース数チェック
        data[6] = 0x41;  // 機能コード
        data[7] = 0x00;  // 機能コード
        data[8] = 0x00;  // 機能コード
        data[9] = 0x00;  // 機能コード

        // 信号値を格納
        for(int i = 0; i<lights.size(); i++) {
            int[] sig = lights.get(i).getSignals();
            if(NUM_SIGNAL==1) {
                data[10 + i] = sig[0];
            } else if(NUM_SIGNAL==2) {
                data[10 + 2 * i] = sig[0];
                data[11 + 2 * i] = sig[1];
            }
        }

        // BCCを計算して格納
        int bcc = data[2];
        for(int i=1; i<10+NUM_INTERFACE*10-1; i++) {
            bcc = bcc ^ data[2 + i];
        }
        data[10 + NUM_INTERFACE*10] = bcc;


        // for debug
        for(int i:data) {
            System.out.print(i + ", ");
        }

        // 送信
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

}
