package routes;

import ServerMain.ServerMain;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class MouseHandler implements HttpHandler {

    public MouseHandler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket serverSocket = new DatagramSocket(8000);
                    byte[] receiveData = new byte[1024];
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        if(ServerMain.currentIP == null || !ServerMain.currentIP.equals(receivePacket.getAddress())) continue;
                        String sentence = new String(Arrays.copyOfRange(receiveData, 0, receivePacket.getLength()));
                        String coord[] = sentence.split(",");
                        int dx = Integer.parseInt(coord[0]);
                        int dy = Integer.parseInt(coord[1]);
                        move(dx, dy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

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
        // click with left btn request
        (new Thread(new Runnable() {
            @Override
            public void run() {
                if(action.equals("clickLeftBtn")) {
                    clickLeftBtn(t);
                } else if(action.equals("clickRightBtn")) {
                    clickRightBtn(t);
                } else if(action.equals("scrollUp")) {
                    scrollUp(t);
                } else if(action.equals("scrollDown")) {
                    scrollDown(t);
                } else if(action.equals("zoom")) {
                    zoom(t);
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

    private void zoom(HttpExchange t) {
        Robot robot = null;
        Map<String, Object> params =
                (Map<String, Object>)t.getAttribute("parameters");
        try {
            String direction = (String) params.get("direction");
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_WINDOWS);
            if(direction.equals("out")){
                robot.keyPress(KeyEvent.VK_SUBTRACT);
                robot.keyRelease(KeyEvent.VK_SUBTRACT);
            } else {
                robot.keyPress(KeyEvent.VK_ADD);
                robot.keyRelease(KeyEvent.VK_ADD);
            }
            robot.keyRelease(KeyEvent.VK_WINDOWS);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void move(int dx, int dy){
        try {
            Point currPos = MouseInfo.getPointerInfo().getLocation();

            Robot robot = new Robot();
            robot.mouseMove(currPos.x + dx, currPos.y + dy);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clickLeftBtn(HttpExchange t){
        Map<String, Object> params =
                (Map<String, Object>)t.getAttribute("parameters");
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clickRightBtn(HttpExchange t){
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON3_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_MASK);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void scrollUp(HttpExchange t){
        try {
            Robot robot = new Robot();
            robot.mouseWheel(-1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void scrollDown(HttpExchange t){
        try {
            Robot robot = new Robot();
            robot.mouseWheel(1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}