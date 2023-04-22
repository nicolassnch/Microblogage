package DB;

import server.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBAccess {

    public static Connection connection;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startDB(){
        try {


            Statement stmt = connection.createStatement();

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS FOLLOWUSER (" +
                    "User TEXT," +
                    "Folower TEXT," +
                    "PRIMARY KEY (User,Folower));");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS FOLLOWTAG (" +
                    "User TEXT," +
                    "Tag TEXT," +
                    "PRIMARY KEY (User,Tag));");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS USER(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
                    "USERNAME TEXT NOT NULL," +
                    "CONSTRAINT username UNIQUE(USERNAME)" +
                    ");");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS MESSAGE(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "CONTENT VARCHAR(256)," +
                    "TAGS TEXT," +
                    "REPUBLISHOF INTEGER,"+
                    "AUTHORS TEXT NOT NULL," +
                    "FOREIGN KEY (AUTHORS) REFERENCES USER(USERNAME)"+
                    ");");



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




     public static int publishDB(String content, String tag, String author) throws SQLException {
        Statement statement = connection.createStatement();
         String sql = "INSERT INTO MESSAGE(CONTENT, TAGS, AUTHORS) VALUES (?, ?, ?)";
         PreparedStatement statementPublis = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
         statementPublis.setString(1, content);
         statementPublis.setString(2, tag);
         statementPublis.setString(3, author);

         int rowsAffected = statementPublis.executeUpdate();

        statement.execute(String.format("INSERT INTO USER(USERNAME)\n" +
                "SELECT '%s'\n" +
                "WHERE NOT EXISTS(SELECT 1 FROM USER WHERE USERNAME = '%s');",author,author));

         if (rowsAffected == 0) {
             throw new SQLException("Insertion failed, no rows affected.");
         }
         int messageId;

         try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
             if (generatedKeys.next()) {
                 messageId= generatedKeys.getInt(1);

             } else {
                 throw new SQLException("Insertion failed, no ID obtained.");
             }
         }
         return messageId;
    }


     public static  String getIdDB(String user,String tag,Integer since,int limit) throws SQLException {


        PreparedStatement pstmt = connection.prepareStatement("SELECT id FROM MESSAGE " +
                "WHERE " +
                "(:user IS NULL OR AUTHORS = :user) " +
                "AND (:tag IS NULL OR content LIKE '%' || :tag || '%') " +
                "AND (:since_id IS NULL OR id > :since_id) " +
                "ORDER BY id DESC " +
                "LIMIT :limit;");

        pstmt.setString(1, user);
        pstmt.setString(2, tag);
        if (since != null) {
            pstmt.setObject(3, since);
        } else {
            pstmt.setNull(3, Types.INTEGER);
        }
        pstmt.setInt(4,limit);

        ResultSet rs = pstmt.executeQuery();

        StringBuilder ids = new StringBuilder();

        while (rs.next()) {
            int id = rs.getInt("id");
            ids.append(id);
            ids.append(";");
        }

        rs.close();
        pstmt.close();

        return ids.toString();
    }

    public static String getMsgById(Integer id) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM MESSAGE WHERE ID = ?");

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();
        String content = null;

        if (rs.next()) {
            int messageId = rs.getInt("ID");
            content = rs.getString("CONTENT");
            String tags = rs.getString("TAGS");
            String authors = rs.getString("AUTHORS");
        }

        return content;
    }

    public static void replyDB(String author,int idToReply,String content,String tag) throws SQLException {

        Statement statement = connection.createStatement();
        statement.execute(String.format("INSERT INTO MESSAGE(CONTENT, TAGS, AUTHORS,REPUBLISHOF)\n" +
                "VALUES ('%s', '%s', '%s',%d)",content,tag,author,idToReply));

        statement.execute(String.format("INSERT INTO USER(USERNAME)\n" +
                "SELECT '%s'\n" +
                "WHERE NOT EXISTS(SELECT 1 FROM USER WHERE USERNAME = '%s');",author,author));


    }

    public static void republishDB(String author,int idToReply) throws SQLException {

        Statement statement = connection.createStatement();
        statement.execute(String.format("INSERT INTO MESSAGE(AUTHORS,REPUBLISHOF)\n" +
                "VALUES ('%s',%d)",author,idToReply));

        statement.execute(String.format("INSERT INTO USER(USERNAME)\n" +
                "SELECT '%s'\n" +
                "WHERE NOT EXISTS(SELECT 1 FROM USER WHERE USERNAME = '%s');",author,author));


    }


    public static boolean addFolowingDB(Client client,String follower) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER WHERE USERNAME = ?");

        statement.setString(1, follower);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()){
            statement = connection.prepareStatement("INSERT OR IGNORE INTO FOLLOWUSER(User, Folower) VALUES (?, ?)");

            statement.setString(1, client.getUsername());
            statement.setString(2, follower);

            statement.executeUpdate();

            return true;
        }else {
            return false;
        }

    }

    public static void addTag(Client client,String tag) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT OR IGNORE INTO FOLLOWTAG(User, Tag) VALUES (?, ?)");

        List<String> following = client.getFollowing();
        List<String> tags = client.getTags();

        statement.setString(1, client.getUsername());

        statement.setString(2, tag);
        statement.executeUpdate();
    }


    public static void setFollowingDB(Client client) throws SQLException {
        PreparedStatement selectStatement = connection.prepareStatement("SELECT Folower FROM FOLLOWUSER WHERE User = ?");

        selectStatement.setString(1, client.getUsername());

        ResultSet resultSet = selectStatement.executeQuery();

        List<String> following = new ArrayList<>();

        while (resultSet.next()) {
            following.add(resultSet.getString("Folower"));
        }

        client.setFollowing(following);

    }

    public static void setTagDB(Client client) throws SQLException {
        PreparedStatement selectStatement = connection.prepareStatement("SELECT Tag FROM FOLLOWTAG WHERE User = ?");

        selectStatement.setString(1, client.getUsername());

        ResultSet resultSet = selectStatement.executeQuery();

        List<String> tags = new ArrayList<>();

        while (resultSet.next()) {
            tags.add(resultSet.getString("Tag"));
        }

        client.setTag(tags);
    }

    public static boolean removeTagDB(Client client,String tagToRemove) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM FOLLOWTAG WHERE User = ? AND Tag = ?");

        statement.setString(1, client.getUsername());
        statement.setString(2, tagToRemove);

        int delete = statement.executeUpdate();

        return delete >= 1;
    }

    public static boolean removeFollowerDB(Client client,String followerToRemove) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM FOLLOWUSER WHERE User = ? AND Folower = ?");

        statement.setString(1, client.getUsername());
        statement.setString(2, followerToRemove);

        int delete = statement.executeUpdate();

        return delete >= 1;
    }

    public static void addUserDB(Client client) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(String.format("INSERT INTO USER(USERNAME)\n" +
                "SELECT '%s'\n" +
                "WHERE NOT EXISTS(SELECT 1 FROM USER WHERE USERNAME = '%s');",client.getUsername(),client.getUsername()));
    }

}
