package client;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ClientRcvIDMsg {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 5000);


        BufferedWriter outServ = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader inClient = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader inServ = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        System.out.println("entree le nom de l'utilissateur");
        String user = inClient.readLine();
        System.out.println();

        outServ.write("RCV_IDS author:"+user);
        outServ.newLine();
        outServ.newLine();
        outServ.flush();


        inServ.readLine();
        String ids_String = inServ.readLine();

        String[] ids_tab = ids_String.split(";");

        for (String id : ids_tab){
            outServ.write("RCV_MSG msg_id:" +id);
            outServ.newLine();
            outServ.newLine();
            outServ.flush();
            String headerMsg = inServ.readLine();
            String message = inServ.readLine();

            System.out.println(id + ": " + message);

        }



        socket.close();

    }

}
