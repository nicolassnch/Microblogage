package Command;

import DB.DBAccess;
import server.Client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Reply extends Middleware{
    @Override
    public boolean check(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException {

        String commandAccess = "REPLY";


        String[] headerSplit = header.split(" ");


        if (headerSplit[0].equals(commandAccess)){

            String author = headerSplit[1].split(":")[1];
            int id = Integer.parseInt(headerSplit[2].split(":")[1]);
            String message = corps;

            String tag = checkHashtag(message);

            DBAccess.replyDB(author,id,message,tag);

            client.getOut().write("OK");
            client.getOut().newLine();
            client.getOut().newLine();
            client.getOut().flush();

            return true;
        }


        return checkNext(header,corps,client,clientConnected );
    }

    private String checkHashtag(String message){
        String[] words = message.split(" ");
        StringBuilder hashtagChain = new StringBuilder("");

        for (String word:words){
            if (word.startsWith("#")) {
                hashtagChain.append(word);
                hashtagChain.append("%");
            }
        }
        return hashtagChain.toString();
    }
}
