package library;

public class UserAccount {

    private int id;
    private String username;
    private String mail;

    public UserAccount(int id, String username, String mail) {
        this.id = id;
        this.username = username;
        this.mail = mail;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getMail() {
        return mail;
    }

    @Override
    public String toString() {
        return id + " | " + username + " | " + mail;
    }
}