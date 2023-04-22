package client;

import java.awt.desktop.OpenURIEvent;
import java.io.*;
import java.net.Socket;

public class MicroblogClient {





    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 5000);

        BufferedWriter outServ = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader inClient = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader inServ = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread inManager =  new InputManager(inServ);
        inManager.start();

        System.out.println("CONNECT: \n\n");
        System.out.println("nom d'utilisateur:");

        StringBuilder post= new StringBuilder();
        String header = null;

        String userconnected = inClient.readLine();

        header = "CONNECT user:" + userconnected;
        post.append(header);
        post.append("\r\n");
        post.append("");
        post.append("\r\n");
        outServ.write(post.toString());
        outServ.flush();



        while (true) {



            post= new StringBuilder();
            header = null;



            System.out.println("1: publish");
            System.out.println("2: subscribe");
            System.out.println("3: unsubscribe");
            System.out.println("4: EXIT");

            int input = Integer.parseInt(inClient.readLine());

            if (input == 1){
                System.out.println("PUBLISH:\n\n");


                header = "PUBLISH author:" + userconnected;
                post.append(header);
                post.append("\r\n");
                System.out.println("message:");
                String message = inClient.readLine();
                post.append(message);
                post.append("\r\n");
                System.out.println(post);
                outServ.write(post.toString());
                outServ.flush();


            }
            if (input ==2){
                System.out.println("SUBSCRIBE:\n\n");

                System.out.println("1:User");
                System.out.println("2:Tag");
                int inputMenu2 = Integer.parseInt(inClient.readLine());

                if (inputMenu2 == 1){
                    System.out.println("nom d'utilisateur:");
                    header = "SUBSCRIBE user:" + inClient.readLine();
                    post.append(header);
                    post.append("\r\n");
                    post.append("");
                    post.append("\r\n");
                    outServ.write(post.toString());
                    outServ.flush();
                }
                if (inputMenu2 == 2){
                    System.out.println("nom tag:");
                    header = "SUBSCRIBE tag:" + inClient.readLine();
                    post.append(header);
                    post.append("\r\n");
                    post.append("");
                    post.append("\r\n");
                    outServ.write(post.toString());
                    outServ.flush();
                }

            }
            if (input == 3){
                System.out.println("UNSUBSCRIBE:\n\n");

                System.out.println("1:User");
                System.out.println("2:Tag");
                int inputMenu2 = Integer.parseInt(inClient.readLine());

                if (inputMenu2 == 1){
                    System.out.println("nom d'utilisateur:");
                    header = "UNSUBSCRIBE user:" + inClient.readLine();
                    post.append(header);
                    post.append("\r\n");
                    post.append("");
                    post.append("\r\n");
                    outServ.write(post.toString());
                    outServ.flush();
                }
                if (inputMenu2 == 2){
                    System.out.println("nom tag:");
                    header = "UNSUBSCRIBE tag:" + inClient.readLine();
                    post.append(header);
                    post.append("\r\n");
                    post.append("");
                    post.append("\r\n");
                    outServ.write(post.toString());
                    outServ.flush();
                }
            }
            if (input == 4){
                System.out.println("EXIT \n\n");

                header = "EXIT";
                post.append(header);
                post.append("\r\n");
                post.append("");
                post.append("\r\n");
                outServ.write(post.toString());
                outServ.flush();

                socket.close();
                inManager.stop();

                break;

            }
        }




    }


    private static class InputManager extends Thread {

        BufferedReader inServ;

        public InputManager(BufferedReader inServ) {
            this.inServ = inServ;
        }

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    System.out.println(inServ.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}



