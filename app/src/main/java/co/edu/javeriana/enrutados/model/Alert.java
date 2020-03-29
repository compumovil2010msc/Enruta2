package co.edu.javeriana.enrutados.model;

public class Alert {
    private boolean isReply;
    private String name;
    private String text;
    private String time;
    private int alertType;
    public static final int MESSAGE = 1;
    public static final int EVENT = 2;

    public Alert(boolean isReply, String name, String text, String time) {
        this.isReply = isReply;
        this.name = name;
        this.text = text;
        this.time = time;
        this.alertType = MESSAGE;
    }

    public Alert(String text, String time) {
        this.alertType = EVENT;
        this.text = text;
        this.time = time;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public int getAlertType() {
        return alertType;
    }
}
