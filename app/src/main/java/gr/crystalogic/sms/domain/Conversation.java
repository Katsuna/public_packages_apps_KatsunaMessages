package gr.crystalogic.sms.domain;

public class Conversation {

    private long id;
    private long date;
    private long unreadCount;
    //TODO check if multiple Ids (comma separated) arrive here....
    private long recipientIds;
    private String snippet;
    private long snippetCs;
    private String address;
    private boolean unanswered;

    private String ct_t;

    private Contact contact;

    @Override
    public String toString() {
        return "Conversation: id=" + id + " date=" + date + " unreadCount=" + unreadCount + " "
                + " recipientIds=" + recipientIds + " snippet=" + snippet
                + " snippetCs= " + snippetCs + " ct_t=" + ct_t;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCt_t() {
        return ct_t;
    }

    public void setCt_t(String ct_t) {
        this.ct_t = ct_t;
    }

    public long getRecipientIds() {
        return recipientIds;
    }

    public void setRecipientIds(long recipientIds) {
        this.recipientIds = recipientIds;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public long getSnippetCs() {
        return snippetCs;
    }

    public void setSnippetCs(long snippetCs) {
        this.snippetCs = snippetCs;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isUnanswered() {
        return unanswered;
    }

    public void setUnanswered(boolean unanswered) {
        this.unanswered = unanswered;
    }
}
