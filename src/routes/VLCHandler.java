package routes;

import ServerMain.ServerMain;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Szabolcs on 2015.05.05..
 */
public class VLCHandler implements HttpHandler {

    int speed = 1;

    public void handle(HttpExchange t) {
        Headers reqHeaders=t.getRequestHeaders();
        java.util.List<String> cookies = reqHeaders.get("Cookie");

        int requestSessionId = -1;

        String[] cookieDatas = cookies.get(0).split("; ");
        for(String cookieData: cookieDatas){
            String key = cookieData.split("=")[0];
            String value = cookieData.split("=")[1];
            if(key.equals("SESSIONID")){
                requestSessionId = Integer.parseInt(value);
                break;
            }
        }

        if(ServerMain.currentSessionId == -1 || ServerMain.currentSessionId != requestSessionId){
            try {
                t.sendResponseHeaders(401, -1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        Map<String, Object> params =
                (Map<String, Object>)t.getAttribute("parameters");
        String action = (String) params.get("action");
        (new Thread(new Runnable() {
            @Override
            public void run() {
                if(action.equals("playPause")){
                    playPause();
                } else if(action.equals("stop")){
                    stop();
                } else if(action.equals("volumeUp")){
                    volumeUp();
                } else if(action.equals("volumeDown")){
                    volumeDown();
                } else if(action.equals("speedPlus")){
                    speedPlus();
                } else if(action.equals("speedMin")){
                    speedMinus();
                } else if(action.equals("volumeOff")){
                    volumeOff();
                } else if(action.equals("subDelMin")){
                    subDelMin();
                } else if(action.equals("subDelPlus")){
                    subDelPlus();
                }
            }
        })).start();

        // send response
        String response = "resp:ok";
        try {
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subDelMin() {
        System.out.println("subtitle delay -");
    }

    private void subDelPlus() {
        System.out.println("subtitle delay +");
    }

    public void playPause() {
        // new connection
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/requests/status.xml?command=pl_pause");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // set timeout (in case of server not responding)
            urlConnection.setConnectTimeout(5000);
            InputStream is = urlConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        // new connection
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/requests/status.xml?command=pl_stop");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // set timeout (in case of server not responding)
            urlConnection.setConnectTimeout(5000);
            InputStream is = urlConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void volumeUp() {
        // new connection
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/requests/status.xml?command=volume&val=+10");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // set timeout (in case of server not responding)
            urlConnection.setConnectTimeout(5000);
            InputStream is = urlConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void volumeDown() {
        // new connection
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/requests/status.xml?command=volume&val=-10");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // set timeout (in case of server not responding)
            urlConnection.setConnectTimeout(5000);
            InputStream is = urlConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void speedPlus() {
        System.out.println("speed video up");
    }

    public void speedMinus() {
        System.out.println("slow video down");
    }

    public void volumeOff() {
        // new connection
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/requests/status.xml?command=volume&val=0");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // set timeout (in case of server not responding)
            urlConnection.setConnectTimeout(5000);
            InputStream is = urlConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
