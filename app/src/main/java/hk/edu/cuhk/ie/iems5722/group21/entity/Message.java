package hk.edu.cuhk.ie.iems5722.group21.entity;

public class Message {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String text;
    private String name;
    private int use_id;
    private String timestamp;
    private int type;

    public Message(String text, String timestamp,int type,String name, int use_id){
        this.text = text;
        this.timestamp = timestamp;
        this.type = type;
        this.use_id = use_id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getUse_id() {
        return use_id;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", name='" + name + '\'' +
                ", use_id=" + use_id +
                ", type=" + type +
                '}';
    }
}
