package routes;

import ServerMain.ServerMain;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by Szabolcs on 2015.05.05..
 */
public class SystemHandler implements HttpHandler{
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
                if(action.equals("shutdown")){
                    shutdown();
                } else if (action.equals("restart")){
                    reboot();
                } else if (action.equals("lock")){
                    lock();
                } else if (action.equals("sleep")){
                    sleep();
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

    public void shutdown() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec("shutdown -s -t 0 -f");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reboot() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec("shutdown -r -t 0 -f");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lock() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec("rundll32.exe user32.dll, LockWorkStation");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sleep() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
