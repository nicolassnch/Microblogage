package server;

public class Message {
    private final String author;
    private final int id_Msg;
    private final String message;

    public Message(String author, int id_Msg, String message) {
        this.author = author;
        this.id_Msg = id_Msg;
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public int getId_Msg() {
        return id_Msg;
    }

    public String getMessage() {
        return message;
    }
}
