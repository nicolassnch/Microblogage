package Command;

import DB.DBAccess;
import server.Client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class UnSubscribe extends Middleware{
    @Override
    public boolean check(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException {
        String commandAccess = "UNSUBSCRIBE";

        String[] headerSplit = header.split(" ");

        if (headerSplit[0].equals(commandAccess)){

            String[] target = headerSplit[1].split(":");


            if (target[0].equals("user")){
                if (DBAccess.removeFollowerDB(client,target[1])){
                    client.removeFollower(target[1]);
                    client.getOut().write("OK");
                }else {
                    client.getOut().write("ERROR");
                }


            }

            if (target[0].equals("tag")){
                if (DBAccess.removeTagDB(client,target[1])){
                    client.removeTag(target[1]);
                    client.getOut().write("OK");
                }else {
                    client.getOut().write("ERROR");
                }


            }

            client.getOut().newLine();
            client.getOut().flush();

            return true;
        }

        return false;
    }
}
