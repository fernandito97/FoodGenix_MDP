package com.genix.foodgenix;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EDWSRequest
{
    public static final String APIKEY="417ebb5ba57f1379ddc9d66311b91278";
    public static final String SERVER_URL="http://10.0.2.2/edws/";
    public static String Request(String requestMethod,String urlPath, HashMap<String,String> params){
        String response = "";
        try
        {
            String parameter = "";
            for(Map.Entry<String,String> item:params.entrySet()){
                parameter+=(parameter.equals("")?"":"&")+item.getKey()+"="+item.getValue();
            }
            URL url = null;
            if(requestMethod.equals("GET")){
                url = new URL(SERVER_URL+"/"+urlPath+"?"+parameter); //untuk emulator
            }else{
                url = new URL(SERVER_URL+"/"+urlPath); //untuk emulator
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("connection", "close");
            conn.setRequestProperty("APIKEY",APIKEY);
            if(!requestMethod.equals("GET")){
                conn.setRequestMethod(requestMethod);

                OutputStream outputstream = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputstream,"UTF-8"));

                writer.write(parameter);
                writer.flush();
                writer.close();
                outputstream.close();
            }

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                String line ="";
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while((line = reader.readLine())!= null)
                {
                    response += line;
                }
                reader.close();
            }
            else
            {
                response = "Gagal konek ke server";
            }

        }catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return response;
    }
    public EDWSRequest(String requestMethod, String urlPath, HashMap<String,String> params) throws IOException {

    }

}