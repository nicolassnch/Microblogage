package Command;

import DB.DBAccess;
import server.Client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ReceiveMsg extends Middleware{

    @Override
    public boolean check(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException {
        String commandAccess = "RCV_MSG";

        String[] headerSplit = header.split(" ");


        if (headerSplit[0].equals(commandAccess)){


            String message =  DBAccess.getMsgById(Integer.parseInt(headerSplit[1].substring(7)));

            client.getOut().write("MSG");
            client.getOut().newLine();
            client.getOut().write(message);
            client.getOut().newLine();
            client.getOut().flush();

            return true;
        }
        return checkNext(header,corps,client,clientConnected );
    }
}
