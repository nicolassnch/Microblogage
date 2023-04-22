package Command;

import DB.DBAccess;
import server.Client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Subscribe extends Middleware{
    @Override
    public boolean check(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException {
        String commandAccess = "SUBSCRIBE";

        String[] headerSplit = header.split(" ");



        if (headerSplit[0].equals(commandAccess)){

            String[] target = headerSplit[1].split(":");

            if (target[0].equals("user")){
                if (DBAccess.addFolowingDB(client,target[1])){
                    client.addFollowing(target[1]);
                    client.getOut().write("OK");
                }else {
                    client.getOut().write("ERROR");
                }

            }

            if (target[0].equals("tag")){
                DBAccess.addTag(client,target[1]);
                client.addTag(target[1]);

            }

            client.getOut().newLine();
            client.getOut().flush();
            return true;
        }
        return checkNext(header,corps,client,clientConnected );
    }
}
