package com.recalot.common.communication;


/**
 * @author Matthäus Schmedding (info@recalot.com)
 */
public class Message  {
    private final String body;
    private Status status;
    private final String title;

    public Message(String title, String body, Status status){
        this.title  = title;
        this.body = body;
        this.status = status;
    }
    public enum Status {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }


    public String getTitle() {
        return title;
    }


    public String getBody() {
        return body;
    }


    public Status getStatus() {
        return status;
    }
}
