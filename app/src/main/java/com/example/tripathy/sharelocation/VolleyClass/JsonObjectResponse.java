package com.example.tripathy.sharelocation.VolleyClass;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

/**
 * Created by tripathy on 11/23/2015.
 */
public class JsonObjectResponse extends JsonRequest<Integer> {

//    private final Response.Listener<Integer> mListener;
    private String mBody;
    private String mContentType;
    private JSONObject mCustomHeaders;



    public JsonObjectResponse(int method, String url, JSONObject jsonRequest,
                             Response.Listener<Integer> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }


    public JsonObjectResponse(String url, JSONObject jsonRequest, Response.Listener<Integer> listener,
                             Response.ErrorListener errorListener) {
        this(jsonRequest == null ? Request.Method.GET : Request.Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<Integer> parseNetworkResponse(NetworkResponse response) {
            return Response.success(response.statusCode, HttpHeaderParser.parseCacheHeaders(response));
    }
}
