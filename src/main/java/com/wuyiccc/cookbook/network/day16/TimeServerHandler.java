package com.wuyiccc.cookbook.network.day16;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wuyiccc
 * @date 2025/2/6 08:55
 */
public class TimeServerHandler implements Runnable {
    
    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        BufferedReader in = null;
        PrintWriter out = null;
        
        try {
            
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            
            String currentTime = null;
            String body = null;
            
            while (true) {

                char[] data = new char[1024];
                int len = in.read(data);

                if (len == -1) {
                    in.close();
                }
                System.out.println("receive: " + body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
                
                out.println(currentTime);
            }
        } catch (Exception e) {
            
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    System.err.println("in 关闭失败");
                }
            }

            if (out != null) {
                out.close();
            }

            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException ex) {
                    System.err.println("socket 关闭失败");
                }
            }

        }
        
    }
}
