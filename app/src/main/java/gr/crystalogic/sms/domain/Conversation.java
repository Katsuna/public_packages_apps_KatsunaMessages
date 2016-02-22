package gr.crystalogic.sms.domain;

public class Conversation {

    private String id;
    private String ct_t;
    private String threadId;

    @Override
    public String toString() {
        return "Conversation: id=" + id + " ct_t=" + ct_t + " threadId=" + threadId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCt_t() {
        return ct_t;
    }

    public void setCt_t(String ct_t) {
        this.ct_t = ct_t;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }
}
