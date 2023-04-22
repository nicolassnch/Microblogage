package Command;

import DB.DBAccess;
import server.Client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class Republish extends Middleware{


    @Override
    public boolean check(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException {
        String commandAccess = "REPUBLISH";

        String[] headerSplit = header.split(" ");


        if (headerSplit[0].equals(commandAccess)){

            String author = headerSplit[1].split(":")[1];
            int id =Integer.parseInt(headerSplit[2].split(":")[1]);

            DBAccess.republishDB(author,id);

            client.getOut().write("OK");
            client.getOut().newLine();
            client.getOut().newLine();
            client.getOut().flush();

            return true;
        }


        return checkNext(header,corps,client,clientConnected );
    }
}
