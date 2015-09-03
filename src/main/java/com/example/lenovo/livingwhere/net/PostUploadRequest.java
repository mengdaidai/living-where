package com.example.lenovo.livingwhere.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.example.lenovo.livingwhere.util.FormImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于同时上传图片和文本参数
 */
public class PostUploadRequest extends Request<String> {


    private Response.Listener mListener ;
    private List<FormImage> mListItem ;//图片列表
    private Map mMap;//用于保存文本参数

    private String BOUNDARY = "--------------520-13-14"; //
    private String MULTIPART_FORM_DATA = "multipart/form-data";

    public PostUploadRequest(String url, List<FormImage> listItem,Map<String,String> params,Response.Listener<String> listener,Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mListener = listener ;
        setShouldCache(false);
        mListItem = listItem ;
        mMap = params;

        setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * 解析返回的网络数据
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            System.out.println("Headers!!!!!!!!!!!!"+HttpHeaderParser.parseCharset(response.headers));
            String mString =
                    new String(response.data, "utf-8");
            Log.v("zgy", "====mString===" + mString);

            return Response.success(mString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 设置回掉
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mListItem == null){
            return super.getBody() ;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        int N = mListItem.size() ;
        System.out.println(String.valueOf(N));
        FormImage formImage ;
        for (int i = 0; i < N ;i++){
            formImage = mListItem.get(i) ;
            StringBuffer sb= new StringBuffer() ;
            //`"--" + BOUNDARY + "\r\n"`
            sb.append("--"+BOUNDARY);
            sb.append("\r\n") ;
            //Content-Disposition: form-data; name=""; filename="" + "\r\n"
            sb.append("Content-Disposition: form-data;");
            sb.append(" name=\"");
            sb.append(formImage.getName()) ;
            sb.append("\"") ;
            sb.append("; filename=\"") ;
            sb.append(formImage.getFileName()) ;
            sb.append("\"");
            sb.append("\r\n") ;
            //Content-Type: application/octet-stream + "\r\n"
            //sb.append("Content-Type: ");
            sb.append("Content-Type:application/octet-stream") ;
            sb.append("\r\n") ;
            sb.append("Content-Transfer-Encoding: binary");
            sb.append("\r\n");
            //"\r\n"
            sb.append("\r\n") ;
            try {
                bos.write(sb.toString().getBytes("utf-8"));
                //图片的二进制数据 + "\r\n"
                bos.write(formImage.getValue());
                bos.write("\r\n".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        //传文本参数
        java.util.Iterator it = mMap.entrySet().iterator();
        while(it.hasNext()){
            java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            StringBuffer sb= new StringBuffer() ;
            //`"--" + BOUNDARY + "\r\n"`
            sb.append("--"+BOUNDARY);
            sb.append("\r\n") ;
            //Content-Disposition: form-data; name=""; filename="" + "\r\n"
            sb.append("Content-Disposition: form-data;");
            sb.append(" name=\"");
            sb.append(key) ;
            sb.append("\"") ;
            sb.append("\r\n") ;
            //Content-Type: text/plain; charset=UTF-8 + "\r\n"
            //sb.append("Content-Type: ");
            sb.append("Content-Type: text/plain; charset=UTF-8") ;
            sb.append("\r\n") ;
            //Content-Transfer-Encoding: 8bit
            sb.append("Content-Transfer-Encoding: 8bit");
            sb.append("\r\n");
            //"\r\n"
            sb.append("\r\n") ;
            //文本参数的值
            sb.append(value);
            sb.append("\r\n");
            try {
                bos.write(sb.toString().getBytes("utf-8"));
            }catch(IOException e){
                e.printStackTrace();
            }

        }

        //`"--" + BOUNDARY + "--" + "\r\n"`
        String endLine = "--" + BOUNDARY + "--" + "\r\n" ;
        try {
            bos.write(endLine.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("zgy", "=====formImage====\n" + bos.toString()) ;
        return bos.toByteArray();
    }
    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA+"; boundary="+BOUNDARY;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Charset", "UTF-8");
        //headers.put("Content-Type", "multipart/form-data");
        headers.put("Accept-Encoding", "*");
        return headers;
    }
}
