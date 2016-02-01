package com.easytobook.api.model;

/**
 * @author alex
 * @date 2015-04-19
 */
public class CancelRequest {

    private String explanation = "Canceled from the Android app";

    public CancelRequest(String confirmationId, int reason) {
    }
}
