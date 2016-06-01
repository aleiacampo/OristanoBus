
package com.aleiacampo.oristanobus.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class WebServerHandler {

    public WebServerHandler(){
    }

    public String getJSONData(String url){

        String response = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        HttpEntity httpEntity = null;
        HttpResponse httpResponse = null;
        try{
            httpResponse = httpClient.execute(httpGet);
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
        } catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
