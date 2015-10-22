package com.recalot.kaggle.recruit.helper;

/**
 * Created with IntelliJ IDEA.
 * User: matthaeus.gdaniec
 * Date: 14.11.13
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class WebResponse {
    private int responseCode;
    private String body;
    private String contentType;

    public WebResponse(int responseCode, String body, String contentType){

        this.responseCode = responseCode;
        this.body = body;
        this.contentType = contentType;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
