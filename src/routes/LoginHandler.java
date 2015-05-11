package routes;

import ServerMain.ServerMain;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class LoginHandler implements HttpHandler {

    public void handle(HttpExchange t) {
        Map<String, Object> params =
                (Map<String, Object>)t.getAttribute("parameters");

        String action = (String) params.get("action");
        if(action.equals("login")) {
            login(t);
        } else if(action.equals("ping")){
            ping(t);
        }
    }

    private void login(HttpExchange t){
        Map<String, Object> params =
                (Map<String, Object>)t.getAttribute("parameters");
        String response;
        String userName = (String) params.get("username");
        String password = (String) params.get("password");

        if (userName.equals("szabi") && password.equals("asdQWE123")) {
            response = "resp:ok";
            // generate new session id
            Random rand = new Random();
            int randomNumber = rand.nextInt(Integer.MAX_VALUE);
            Headers respHeaders=t.getResponseHeaders();
            java.util.List<String> values = new ArrayList<>();
            values.add("SESSIONID="+randomNumber);
            respHeaders.put("Set-Cookie", values);
            ServerMain.currentSessionId = randomNumber;
            ServerMain.currentIP = t.getRemoteAddress().getAddress();
            System.out.println(ServerMain.currentIP);
        } else {
            response = "resp:error";
        }

        try {
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ping(HttpExchange t){
        Map<String, Object> params =
                (Map<String, Object>)t.getAttribute("parameters");
        String response;

        response = "resp:ok";
        try {
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}