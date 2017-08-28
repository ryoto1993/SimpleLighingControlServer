import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by RyotoTomioka on 2017/06/12.
 */
public class SocketServer extends Thread{
    static final int PORT = 44344;
    static private ILS ils;

    public SocketServer(ILS ils) {
        this.ils = ils;
    }
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        System.out.println("Server: listening");

        try {
            serverSocket = new ServerSocket(PORT);
            boolean runFlag = true;
            while(runFlag){
                // System.out.println("Listening...");
                // 接続があるまでブロック
                Socket socket = serverSocket.accept();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                ArrayList<String> cmd = new ArrayList<>();
                String in;
                while( (in = br.readLine()) != null ){
                    // exitという文字列を受け取ったら終了する
                    if( "exit".equals(in)){
                        runFlag = false;
                    }
                    // コマンドに追加
                    cmd.add(in);
                }
                command(cmd);
                if( socket != null){
                    socket.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if( serverSocket != null){
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 動作コマンドの実装
    public void command(ArrayList<String> cmd) {
        String mode = cmd.get(0);
        if(mode == null){

        }else switch(mode){
            case "MANUAL_SIG-ALL":
                System.out.println("全照明一括 信号値指定調光");
                manualSigAll(cmd.get(1));
                break;
            case "MANUAL_SIG-INDIVIDUAL":
                System.out.println("全照明独立 信号値指定調光");
                manualSigIndividual(cmd.get(1));
                break;
            case "MANUAL_ID-SIG":
                System.out.println("照明ID・信号値指定調光");
                manualIDSig(cmd.get(1));
                break;
            case "MANUAL_ID-RELATIVE":
                System.out.println("照明ID・相対信号値指定調光");
                break;
            default:
                System.out.println("Error: 不明なmode command");
        }
    }

    // 全照明一括 信号値指定調光
    private void manualSigAll(String sigs) {
        if(sigs == null) {
            System.out.println("信号値のフォーマットを確認してください");
        } else {
            ArrayList<Integer> s = new ArrayList<>();
            String[] buf = sigs.split(",");
            for(String i:buf) s.add(Integer.parseInt(i));
            for(int i=0; i<ils.getLights().size(); i++) {
                ils.getLights().get(i).setSignal(s.get(0), s.get(1));
            }
        }
        // 調光
        ils.downlightDimmer.send();
    }

    // 全照明独立 信号値指定調光
    private void manualSigIndividual(String sigs) {
        if(sigs == null) {
            System.out.println("信号値のフォーマットを確認してください");
        } else {
            ArrayList<Integer> s = new ArrayList<>();
            String[] buf = sigs.split(",");
            for(String i:buf) s.add(Integer.parseInt(i));
            for(int i=0; i<ils.getLights().size(); i++) {
                ils.getLights().get(i).setSignal(s.get(0), s.get(1));
                s.remove(0);
                s.remove(0);
            }
        }
        ils.downlightDimmer.send();
    }

    // 照明ID・信号値指定調光
    private void manualIDSig(String sigs) {
        if(sigs == null) {
            System.out.println("信号値のフォーマットを確認してください");
        } else {
            ArrayList<Integer> s = new ArrayList<>();
            String[] buf = sigs.split(",");
            for(String i:buf) s.add(Integer.parseInt(i));
            System.out.println(s.get(0));
            ils.getLight(s.get(0)).setSignal(s.get(1), s.get(2));
        }
        ils.downlightDimmer.send();
    }


}
