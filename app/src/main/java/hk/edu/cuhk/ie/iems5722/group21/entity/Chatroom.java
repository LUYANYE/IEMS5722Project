package hk.edu.cuhk.ie.iems5722.group21.entity;

public class Chatroom {

    private int id;
    private String room_name;

    public Chatroom(String room_name, int id){

        this.room_name = room_name;
        this.id = id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "id=" + id +
                ", room_name='" + room_name + '\'' +
                '}';
    }
}
