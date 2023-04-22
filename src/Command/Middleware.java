package Command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import server.Client;

public abstract class Middleware {
    private Middleware next;

    public static Middleware link(Middleware first, Middleware... chain) {
        Middleware head = first;
        for (Middleware nextInChain: chain) {
            head.next = nextInChain;
            head = nextInChain;
        }
        return first;
    }

    public abstract boolean check(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException;

    protected boolean checkNext(String header, String corps, Client client, Map<String, Client> clientConnected) throws SQLException, IOException {
        if (next == null) {
            return true;
        }
        return next.check(header,corps, client,clientConnected);
    }





}
