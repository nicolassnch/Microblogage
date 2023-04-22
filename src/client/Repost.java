package client;

import java.io.*;
import java.net.Socket;

public class Repost {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 5000);

        BufferedWriter outServ = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader inClient = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader inServ = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String authorToRepublish = inClient.readLine();

        String[] authorToRepublishTab = authorToRepublish.split(" ");

        System.out.println("nom utilisateur");
        String author = inClient.readLine();

        for (String authors :authorToRepublishTab) {

            outServ.write("RCV_IDS author:" + authors + " limit:999");
            outServ.newLine();
            outServ.newLine();
            outServ.flush();


            inServ.readLine();
            String ids_String = inServ.readLine();

            String[] ids_tab = ids_String.split(";");

            for (String id : ids_tab) {
                outServ.write("REPUBLISH author:" +author+ " msg_id:" + id);
                outServ.newLine();
                outServ.newLine();
                outServ.flush();
                String headerMsg = inServ.readLine();

                System.out.println(id + ": " + headerMsg);
            }



        }

    }
}
