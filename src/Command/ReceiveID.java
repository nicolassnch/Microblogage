package Command;

import DB.DBAccess;
import server.Client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReceiveID extends Middleware{

    @Override
    public boolean check(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException {
        String commandAccess = "RCV_IDS";

        String[] commandSplit = header.split(" ");



        if (commandSplit[0].equals(commandAccess)) {


            Map<String, String> optionalParams = new HashMap<>();

            for (String optional : commandSplit) {
                String[] split = optional.split(":");
                if (split.length == 2) {
                    optionalParams.put(split[0], split[1]);
                }
            }

            String author = optionalParams.get("author");
            String tag = optionalParams.get("tag");
            Integer since = optionalParams.containsKey("since") ? Integer.parseInt(optionalParams.get("since")) : null;
            int limit = optionalParams.containsKey("limit") ? Integer.parseInt(optionalParams.get("limit")) : 5;

            client.getOut().write("RCV_IDS");
            client.getOut().newLine();
            client.getOut().write(DBAccess.getIdDB(author,tag,since,limit));
            client.getOut().newLine();
            client.getOut().flush();

            return true;
        }
        return checkNext(header,corps,client,clientConnected);
    }
}
