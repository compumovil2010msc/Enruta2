package co.edu.javeriana.enrutados.model;

public class Alert {
    private boolean isReply;
    private String name;
    private String text;

    public Alert(boolean isReply, String name, String text) {
        this.isReply = isReply;
        this.name = name;
        this.text = text;
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
}
