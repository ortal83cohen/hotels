package com.etb.app.etbapi;

/**
 * @author alex
 * @date 2015-10-02
 */
public class ResponseException extends Throwable {

    public ResponseException(String detailMessage) {
        super(detailMessage);
    }
}
