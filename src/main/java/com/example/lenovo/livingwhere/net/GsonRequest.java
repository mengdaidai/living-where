package com.example.lenovo.livingwhere.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.android.volley.Response.Listener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * 将服务器返回json直接转化为对象
 */
public class GsonRequest<T> extends Request<T> {

    Gson mGson ;
    Listener<T> mListener;
    Type mType;


    public GsonRequest(int method, String url, Type type,Listener<T> listener,Response.ErrorListener errorlistener) {
        super(method, url, errorlistener);
        mGson = new Gson();
        mListener = listener;
        mType = type;
    }

    public GsonRequest( String url, Type type,Listener<T> listener,Response.ErrorListener errorlistener) {
        this(Method.GET, url, type, listener, errorlistener);
    }



    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String jsonString = new String(networkResponse.data,
                    "utf-8");
            //jsonString = jsonString.replace("\\","");
            System.out.println("jsonString  "+jsonString);
            return (Response<T>) Response.success(mGson.fromJson(jsonString, mType),
                    HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
