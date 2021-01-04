package com.lexor.service.until;

import org.codehaus.jettison.json.JSONException;

public interface IQBRequest {
    Object callAPIGET(String url, String accessToken) throws JSONException;
}
