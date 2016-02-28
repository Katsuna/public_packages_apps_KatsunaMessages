package gr.crystalogic.sms.domain;

import org.joda.time.DateTime;

public class Message {

    private long id;
    private long threadId;
    private long date;
    private String address;
    private String body;
    private int type;
    private int read;
    private Contact contact;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getDateFormatted() {
        DateTime dateTime = new DateTime(date);
        DateTime startOfToday = DateTime.now().toLocalDate().toDateTimeAtStartOfDay();

        String output;
        if (dateTime.isBefore(startOfToday)) {
            output = dateTime.toString("dd/MM/yyyy");
        } else {
            output = dateTime.toString("HH:mm");
        }

        return output;
    }
}
