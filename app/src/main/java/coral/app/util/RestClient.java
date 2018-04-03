package coral.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class RestClient {
    public static String BASE_URL = "http://192.168.43.181:8000";
    // public static String BASE_URL = "http://bc90a927.ngrok.io";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void setBaseUrl(String url) {
        BASE_URL = url;
    }

    public static void setTimeOut(int value) {
        client.setTimeout(value);
    }

    public static void get(String url, Header[] headers, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        try {
            if (headers != null) {
                client.removeAllHeaders();
                for (Header header : headers) {
                    client.addHeader(header.getName(), header.getValue());
                }
            }
            client.get(getAbsoluteUrl(url), params, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void post(String url, Header[] headers, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        try {
            if (headers != null) {
                client.removeAllHeaders();
                for (Header header : headers) {
                    client.addHeader(header.getName(), header.getValue());
                }
            }
            client.post(getAbsoluteUrl(url), params, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(String url, RequestParams params,
                              AsyncHttpResponseHandler responseHandler) {
        try {
            client.delete(getAbsoluteUrl(url), params, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager != null ? manager.getActiveNetworkInfo() : null;
        return (networkInfo != null && networkInfo.isConnected());
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + "/api/" + relativeUrl;
    }

}
