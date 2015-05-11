package ServerMain;

import com.sun.net.httpserver.*;
import routes.LoginHandler;
import routes.MouseHandler;
import filters.ParameterFilter;
import routes.SystemHandler;
import routes.VLCHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by Szabolcs on 2015.04.21..
 */
public class ServerMain {

    public static int currentSessionId = -1;
    public static InetAddress currentIP  = null;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        HttpContext contextLogin = server.createContext("/login", new LoginHandler());
        HttpContext contextMouse = server.createContext("/mouse", new MouseHandler());
        HttpContext contextSystem = server.createContext("/system", new SystemHandler());
        HttpContext contextVLC = server.createContext("/vlc", new VLCHandler());
        contextLogin.getFilters().add(new ParameterFilter());
        contextMouse.getFilters().add(new ParameterFilter());
        contextSystem.getFilters().add(new ParameterFilter());
        contextVLC.getFilters().add(new ParameterFilter());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

}
