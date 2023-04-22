import Command.*;
import DB.DBAccess;
import server.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MicroblogCentral {

    private Map<String, Client> clientConnected = new HashMap<>();


    public static void main(String[] args) {
        MicroblogCentral microblogCentral = new MicroblogCentral();
        microblogCentral.start();
    }


    public void start() {
        final Executor executor;
        try (ServerSocket ss = new ServerSocket(5000)) {
            System.out.println("Server started. Listening to port 5000.");
            DBAccess.startDB();
            System.out.println("//////////// DB START ////////////");

            executor = Executors.newFixedThreadPool(10);

            while (true) {
                executor.execute(new ClientWorker(ss.accept()));
            }
        } catch (IOException e) {
            System.err.println("IO error");
        }
    }

    private class ClientWorker implements Runnable {

        private Socket socket;
        Client client = null;
        boolean clientIsSET = false;
        public static Middleware middleware = Middleware.link(new Publish(), new ReceiveID(), new ReceiveMsg(), new Reply(), new Republish(), new Subscribe(), new UnSubscribe());

        public ClientWorker(Socket socket) {
            this.socket = socket;
        }


        @Override
        public void run() {
            try {

                System.out.println("server.Client connected.");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


                while (true) {

                    String header = in.readLine();
                    System.out.println("header:" + header);

                    if (!header.equals("EXIT")) {

                        String corps = in.readLine();

                        if (!clientIsSET) {

                            if (header.split(" ")[0].equals("CONNECT")) {
                                String clientName = header.split(" ")[1].split(":")[1];
                                client = new Client(clientName, out);
                                if (!clientConnected.containsKey(clientName)) {
                                    clientConnected.putIfAbsent(clientName, client);
                                    out.write("OK");
                                    clientIsSET = true;

                                    DBAccess.setFollowingDB(client);
                                    DBAccess.setTagDB(client);
                                    DBAccess.addUserDB(client);

                                } else {
                                    out.write("ERROR");
                                }
                                out.newLine();
                                out.flush();
                            } else {
                                client = new Client(null, out);
                                clientIsSET = true;
                            }
                        }

                        middleware.check(header, corps, client, clientConnected);

                        for (Client client : clientConnected.values()) {
                            client.sendAllMessage();
                        }


                        header = null;
                        corps = null;
                    } else {
                        System.out.println("EXIT");
                        break;
                    }


                }
                if (client != null) {
                    clientConnected.remove(client);
                }
                socket.close();

            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }


        }
    }


}
