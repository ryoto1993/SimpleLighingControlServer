import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by RyotoTomioka on 2017/06/12.
 */
public class SocketServer extends Thread{
    static final int PORT = 44344;
    static private ILS ils;
    private ServerSocket serverSocket;
    private Socket socket;

    public SocketServer(ILS ils) {
        this.ils = ils;
    }
    @Override
    public void run() {
        serverSocket = null;
        System.out.println("Server: listening");

        try {
            serverSocket = new ServerSocket(PORT);
            while(true){
                // listening
                socket = serverSocket.accept();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                ArrayList<String> cmd = new ArrayList<>();

                command(br);
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

    // send message to client
    private void sendMessage(String str) {
        try {
            OutputStream output = socket.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
            bw.write(str);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // error
    private void sendError(String err) {
        String str = "ERROR: ";
        str += err;
        System.out.println("An error has occurred, " + str);
        sendMessage(str);
    }

    // command parser
    public void command(BufferedReader br) {
        String mode = null;
        try {
            mode = br.readLine();

            if(mode == null){

            }else switch(mode){
                case "MANUAL_SIG-ALL":
                    System.out.println("全照明一括 信号値指定調光");
                    manualSigAll(br.readLine());
                    break;
                case "MANUAL_SIG-INDIVIDUAL":
                    System.out.println("全照明独立 信号値指定調光");
                    manualSigIndividual(br.readLine());
                    break;
                case "MANUAL_ID-SIG":
                    System.out.println("照明ID・信号値指定調光");
                    manualIDSig(br.readLine());
                    break;
                case "MANUAL_ID-RELATIVE":
                    System.out.println("照明ID・相対信号値指定調光");
                    break;
                case "DOWNLIGHT_ALL":
                    System.out.println("Downlight: All Control");
                    downlightAll(br.readLine());
                    break;
                case "DOWNLIGHT_INDIVIDUAL":
                    System.out.println("Downlight: Individual control");
                    downlightIndividual(br.readLine());
                    break;
                case "GET_LIGHTS":
                    sendLights();
                    break;
                default:
                    System.out.println("Error: 不明なmode command");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // uniform control by signals
    private void manualSigAll(String sigs) {
        if(sigs == null) {
            sendError("Check for data command format.");
        } else {
            ArrayList<Integer> s = new ArrayList<>();
            String[] buf = sigs.split(",");
            for(String i:buf) s.add(Integer.parseInt(i));
            for(Light l: ils.getLights()) {
                l.setLumPct((double)s.get(0)/255.0*100.0);
                l.setSignal(s.get(0), s.get(1));
            }
        }
        // dimming
        ils.downlightDimmer.send();
        sendMessage("OK. ");

    }

    // all lights individual control by signals
    private void manualSigIndividual(String sigs) {
        if(sigs == null) {
            sendError("Check for data command formant.");
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
        // dimming
        ils.downlightDimmer.send();
        sendMessage("OK");
    }

    // individual control by id and signals
    private void manualIDSig(String sigs) {
        if(sigs == null) {
            sendError("Check for data command format.");
        } else {
            ArrayList<Integer> s = new ArrayList<>();
            String[] buf = sigs.split(",");
            for(String i:buf) s.add(Integer.parseInt(i));
            System.out.println(s.get(0));
            ils.getLight(s.get(0)).setSignal(s.get(1), s.get(2));
        }
        // dimming
        ils.downlightDimmer.send();
        sendMessage("OK");
    }

    /**
     * for downlight uniform control
     * @author Ryoto Tomioka
     * @param cmd data command
     * format: [lumPct (0.5 % inc.)], [color temp.]
     */
    private void downlightAll(String cmd) {
        if(cmd == null) {
            sendError("Check for data command.");
        } else {
            ArrayList<Double> data = new ArrayList<>();
            String[] buf = cmd.split(",");
            for(String i:buf) data.add(Double.parseDouble(i));
            for(Light l: ils.getLights()) {
                l.setLumPct(data.get(0));
                l.setTemperature(data.get(1));
            }
        }
        // dimming
        ils.downlightDimmer.send();
        sendMessage("OK.");
    }

    /**
     * for downlight individual control
     * @author Ryoto Tomioka
     * @param cmd data command
     * format: [ID], [lumPct], [color temp.]*
     */
    private void downlightIndividual(String cmd) {
        if(cmd == null) {
            sendError("Check for data command.");
        } else {
            ArrayList<Integer> id = new ArrayList<>();
            ArrayList<Double> lumPct = new ArrayList<>();
            ArrayList<Integer> temp = new ArrayList<>();

            String[] buf = cmd.split(",");
            if(buf.length % 3 != 0) {
                sendError("invalid number of data.");}

            for(int i=0; i<buf.length/3; i++) {
                int n = i*3;
                try {
                    id.add(Integer.parseInt(buf[n]));
                    lumPct.add(Double.parseDouble(buf[n + 1]));
                    temp.add(Integer.parseInt(buf[n + 2]));
                } catch (Exception e) {
                    sendError(e.getMessage());
                    return;
                }
            }

            while (id.size() > 0) {
                // update light object
                Light light = ils.getLight(id.get(0));
                light.setLumPct(lumPct.get(0));
                light.setTemperature(temp.get(0));

                // remove data from array list
                id.remove(0);
                lumPct.remove(0);
                temp.remove(0);
            }

            // dimming
            ils.downlightDimmer.send();
            sendMessage("OK.");

        }
    }

    /**
     * send light object via JSON
     * @author Ryoto Tomioka
     */
    private void sendLights() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(ils.getLights());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // output
        sendMessage(json);
    }

}
