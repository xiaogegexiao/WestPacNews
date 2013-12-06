package com.westpac.news.internet;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;

/**
 * HttpClients class for http requests
 * @author xiao
 * */

public class HttpClients {
	/* httpclient */
	private HttpClient mHc;
	/* mGet instance */
    private HttpGet mGet;
    private Context mContext;
    
    /* http response */
    private HttpResponse mResponse;
    
    /* set connection timeout and socket timeout to 20 seconds */
    private static final int TIMEOUT = 20 * 1000;
    
    public HttpClients(Context context){
    	mContext = context;
    	mGet = new HttpGet();
    }
    
    /**
     * set timeout for http connection and socket connection
     * set socket buffer to 8192
     */
    public HttpParams getParams() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        return params;
    }
    
    /**
     * replace some invalid character int urlpath
     * @param urlPath
     */
    public void setURL(String urlPath) {
        try {
            String urlPathFixed = urlPath.replace(" ", "");
            urlPathFixed = urlPathFixed.replace("\n", "*");
            urlPathFixed = urlPathFixed.replace("\t", "");
            URI url = new URI(urlPathFixed);
            if (mGet.isAborted() == true) {
            	mGet = new HttpGet();
            }
            mGet.setURI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * get responde code of http responde
     * @return
     */
    public int getResponseCode() {
        int code = -1;
        if (mResponse != null) {
            code = mResponse.getStatusLine().getStatusCode();
        }
        return code;
    }
    
    /**
     * get json result from http response with charset utf-8
     * @return
     */
    public String getJSONResult(){
    	String strResult = null;
    	try {
			strResult = EntityUtils.toString(
					mResponse.getEntity(), HTTP.UTF_8);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return strResult;
    }
    
    /**
     * open the connection to server
     * @return
     */
    public HttpResponse openConnection() {
        mHc = new DefaultHttpClient();
        try {
            mResponse = mHc.execute(mGet);
        } catch(UnknownHostException e) {
            e.printStackTrace();
            mResponse = null;
        } catch (HttpHostConnectException e){
            e.printStackTrace();
            mResponse = null;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            mResponse = null;
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            mResponse = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            mResponse = null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            mResponse = null;
        } catch (AbstractMethodError e) {
            e.printStackTrace();
            mResponse = null;
		 } catch (IOException e) {
            e.printStackTrace();
            mResponse = null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            mResponse = null;
		  } catch (Exception e) {
            e.printStackTrace();
            mResponse = null;
        }
        return mResponse;
    }
}
