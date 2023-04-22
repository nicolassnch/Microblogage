package client;

import java.io.*;
import java.net.*;

public class ClientPublish {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);


        BufferedWriter outServ = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader inClient = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader inServ = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        StringBuilder post= new StringBuilder();

        System.out.println("nom d'utilisateur:");
        String header = "PUBLISH author:" + inClient.readLine();
        post.append(header);
        post.append("\r\n");
        System.out.println("message:");
        String message = inClient.readLine();
        post.append(message);
        post.append("\r\n");
        System.out.println(post);
        outServ.write(post.toString());
        outServ.newLine();
        outServ.flush();

        System.out.println(inServ.readLine());

        outServ.close();
        socket.close();
    }
}
