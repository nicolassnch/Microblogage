package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {

    private String username;
    private List<String> tag;
    private List<String> following;
    private ConcurrentLinkedQueue<Message> messageQueue;
    private BufferedWriter out;

    public Client(String username,BufferedWriter out) {
        this.username = username;
        this.tag = new ArrayList<>();
        this.following = new ArrayList<>();
        this.messageQueue = new ConcurrentLinkedQueue<Message>();
        this.out = out;


    }

    public Boolean contain(Message message){
        return messageQueue.contains(message);
    }

    public String getUsername(){
        return username;
    }

    public List<String> getTags() {
        return tag;
    }

    public List<String> getFollowing() {
        return following;
    }


    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public void addTag(String tag) {
        this.tag.add(tag);
    }

    public void addFollowing(String follower) {
        this.following.add(follower);
    }

    public void removeTag(String tag){
        this.tag.remove(tag);
    }

    public void removeFollower(String follower){
        this.following.remove(follower);
    }


    public void sendAllMessage() throws IOException {

        while (!messageQueue.isEmpty()) {
            Message messageToSend = messageQueue.poll();

            out.newLine();
            out.write("MSG author:" + messageToSend.getAuthor() + " msg_id:" + messageToSend.getId_Msg());
            out.newLine();
            out.write(messageToSend.getMessage());
            out.newLine();
            out.flush();
        }
    }

    public void addMessage(Message message){
        messageQueue.add(message);
    }

    public boolean hasMessage(){
        return !messageQueue.isEmpty();
    }


    @Override
    public String toString() {
        return "server.Client{" +
                "username='" + username + '\'' +
                ", tag=" + tag +
                ", following=" + following +
                ", messageQueue=" + messageQueue +
                ", out=" + out +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return username.equals(client.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public BufferedWriter getOut() {
        return out;
    }
}
