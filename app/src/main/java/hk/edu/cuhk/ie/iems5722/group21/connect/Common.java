package hk.edu.cuhk.ie.iems5722.group21.connect;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Common {

    // GET
    public static String Get_getJson(String Url) {
        InputStream is = null;
        String results = "";
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); // 10,000 milliseconds
            conn.setConnectTimeout(15000); // 15,000 milliseconds
            conn.setRequestMethod("GET"); // Use the GET method
            conn.setDoInput(true);
            // Start the query
            conn.connect();
            int response = conn.getResponseCode(); // This will be 200 if successful
            if (response == 200) {
                is = conn.getInputStream();
                // Convert the InputStream into a string
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    results += line;
                }
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try{
                    is.close(); // Close the InputStream when done
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
    // END GET


    //POST
    public static String Post_Json(String Url,int chatroom_id, int user_id, String name, String message){
        String status = null;
        String api = "chatroom_id="+ chatroom_id +
                "&" + "user_id=" + user_id + "&"+ "name="+ name + "&" + "message="+ message;
        Log.v("api",api);
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            // 设置接收数据的格式:json格式数据
            conn.setRequestProperty("Accept", "application/json");
            //设置请求数据类型
            // conn.setRequestProperty("Content-Type","text/html");
            //链接并发送
            conn.connect();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(api);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 通过连接对象获取一个输入流，向远程读取
                InputStream inputStream=conn.getInputStream();
                byte[] data=new byte[1024];
                StringBuffer sb1=new StringBuffer();
                int length=0;
                while ((length=inputStream.read(data))!=-1){
                    String s=new String(data, 0,length);
                    sb1.append(s);
                }
                status=sb1.toString();
                inputStream.close();
            }
            //关闭连接
            conn.disconnect();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
    // END POST


    // Sign_up
    public static String Post_Signup(String Url, int user_id, String name, String password){
        String status = null;
        String api = "user_id=" + user_id + "&"+ "username="+ name + "&" + "password="+ password;
        Log.v("api",api);
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            // 设置接收数据的格式:json格式数据
            conn.setRequestProperty("Accept", "application/json");
            //设置请求数据类型
            // conn.setRequestProperty("Content-Type","text/html");
            //链接并发送
            conn.connect();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(api);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 通过连接对象获取一个输入流，向远程读取
                InputStream inputStream=conn.getInputStream();
                byte[] data=new byte[1024];
                StringBuffer sb1=new StringBuffer();
                int length=0;
                while ((length=inputStream.read(data))!=-1){
                    String s=new String(data, 0,length);
                    sb1.append(s);
                }
                status=sb1.toString();
                inputStream.close();
            }
            //关闭连接
            conn.disconnect();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
    // END Sign_up

    // Login
    public static String Post_Login(String Url, String name, String password){
        String status = null;
        String api = "username="+ name + "&" + "password="+ password;
        Log.v("api",api);
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            // 设置接收数据的格式:json格式数据
            conn.setRequestProperty("Accept", "application/json");
            //设置请求数据类型
            // conn.setRequestProperty("Content-Type","text/html");
            //链接并发送
            conn.connect();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(api);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 通过连接对象获取一个输入流，向远程读取
                InputStream inputStream=conn.getInputStream();
                byte[] data=new byte[1024];
                StringBuffer sb1=new StringBuffer();
                int length=0;
                while ((length=inputStream.read(data))!=-1){
                    String s=new String(data, 0,length);
                    sb1.append(s);
                }
                status=sb1.toString();
                inputStream.close();
            }
            //关闭连接
            conn.disconnect();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
    // END Login

    // POST General
    public static String Post_Gen(String Url, String api){
        String status = null;
        Log.v("api",api);
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            // 设置接收数据的格式:json格式数据
            conn.setRequestProperty("Accept", "application/json");
            //设置请求数据类型
            // conn.setRequestProperty("Content-Type","text/html");
            //链接并发送
            conn.connect();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(api);
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 通过连接对象获取一个输入流，向远程读取
                InputStream inputStream=conn.getInputStream();
                byte[] data=new byte[1024];
                StringBuffer sb1=new StringBuffer();
                int length=0;
                while ((length=inputStream.read(data))!=-1){
                    String s=new String(data, 0,length);
                    sb1.append(s);
                }
                status=sb1.toString();
                inputStream.close();
            }
            //关闭连接
            conn.disconnect();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
    // END POST General

}
