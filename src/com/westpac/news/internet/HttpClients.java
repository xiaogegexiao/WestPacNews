package com.westpac.news.internet;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class HttpClients {
	private HttpClient mHc;
    private HttpGet mGet;
    private Context mContext;
    private HttpResponse mResponse;
    
    public static final int SAVE_FILE_SUCCESS = 0;
    public static final int SAVE_FILE_FAILED = 1;
    
    private static final int TIMEOUT = 20 * 1000;
    private static String mProxyHost;
    private static int mProxyPort = 0;
    
    public HttpClients(Context context){
    	mContext = context;
    	mGet = new HttpGet();
    }
    
    public HttpParams getParams() {
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        if (getProxy() == true) {
            final HttpHost proxy = new HttpHost(mProxyHost, mProxyPort, "http");
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
        return params;
    }

    private boolean getProxy() {
        ConnectivityManager ConnMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = ConnMgr.getActiveNetworkInfo();
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                mProxyPort = 0;
                mProxyHost = null;
            } else {
                mProxyHost = android.net.Proxy.getDefaultHost();
                mProxyPort = android.net.Proxy.getDefaultPort();
            }
            return (!TextUtils.isEmpty(mProxyHost) && mProxyPort != 0);
        }
        return false;
    }

    public void setHeader(HttpPost post) {
    	post.setHeader(HTTP.CONTENT_TYPE, "text/plain");
    	post.setHeader("Accept", "*/*");
    }
    
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
    
    public void disConnect() {
        if (mGet.isAborted() == false) mGet.abort();
    }
    
    public int getResponseCode() {
        int code = -1;
        if (mResponse != null) {
            code = mResponse.getStatusLine().getStatusCode();
        }
        return code;
    }
    
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
