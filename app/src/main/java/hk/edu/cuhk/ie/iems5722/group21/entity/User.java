package hk.edu.cuhk.ie.iems5722.group21.entity;

public class User {

    String username;
    int user_id;

    public static User current_user = null;

    public User(String username, int user_id) {
        this.username = username;
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public int getUser_id() {
        return user_id;
    }
}
