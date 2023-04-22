package Command;

import DB.DBAccess;
import server.Client;
import server.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class Publish extends Middleware {


    @Override
    public boolean check(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException {

        String commandAccess = "PUBLISH";


        String[] headerSplit = header.split(" ");


        if (headerSplit[0].equals(commandAccess)) {

            String autheur = headerSplit[1].split(":")[1];
            String message = corps;

            String tag = checkHashtag(message);

            int id_Msg = DBAccess.publishDB(corps, tag, autheur);

            Message messageObject = new Message(autheur,id_Msg,message);

            if (clientConnected.size() != 0) {
                sendToFlux(tag,autheur, clientConnected,messageObject);
            }


            client.getOut().write("OK");
            client.getOut().newLine();
            client.getOut().flush();
            return true;
        }

        return checkNext(header, corps, client, clientConnected);
    }


    private String checkHashtag(String message) {
        String[] words = message.split(" ");
        StringBuilder hashtagChain = new StringBuilder("");

        for (String word : words) {
            if (word.startsWith("#")) {
                hashtagChain.append(word);
                hashtagChain.append("%");
            }
        }
        return hashtagChain.toString();
    }

    private void sendToFlux(String tag,String author, Map<String, Client> clientConnected,Message message) {

        outerLoop:

        if (!tag.equals("")) {
            String[] allTag = tag.split("%");
            for (Client client : clientConnected.values()) {
                for (String tagTab:allTag){
                    if (client.getTags().contains(tagTab) && !client.contain((message))) {
                        client.addMessage(message);
                        break outerLoop;
                    }
                }
            }
        }

        for (Client client : clientConnected.values()) {
            if (client.getFollowing().contains(author) && !client.contain(message)) {
                client.addMessage(message);
            }
        }


    }
}