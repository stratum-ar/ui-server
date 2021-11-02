package com.stratum.uiserver;

import com.stratum.uiserver.connection.RequestReader;
import com.stratum.uiserver.framebuffer.*;
import com.stratum.uiserver.graphics.ColorUtil;
import com.stratum.uiserver.graphics.Graphics;
import com.stratum.uiserver.graphics.Surface;
import com.stratum.uiserver.graphics.types.Color;
import com.stratum.uiserver.graphics.types.Gradient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UIServerApp {

//     public static void testDraw(IFramebuffer framebuffer) {
//         Surface surf = new Surface();
//         Graphics g = surf.getGraphics();

//         g.fillRect(
//                 0, 0, 240, 240, new Gradient(0, 240, Color.WHITE, Color.BLACK, true)
//         );
//         g.setBlendingFunction((bg, fg) -> (short)(bg ^ fg));

//         g.fillEllipse(40, 40, 80, 50, Color.YELLOW);
//         g.fillRect(60, 50, 120, 100, Color.BLUE);
//         g.fillRect(80, 70, 120, 100, Color.RED);
//         g.drawLine(100, 20, 200, 50, Color.WHITE);
//         g.drawPolygon(new int[]{100, 200, 60}, new int[]{100, 200, 200}, Color.RED);
//         g.drawQuadratic(20, 100, 100, 100, 100, 200, Color.WHITE);

    public static void testDraw(IFramebuffer framebuffer, Surface surf) {
//        Surface surf = new Surface();
//        Graphics g = surf.getGraphics();
//
//        g.fillRect(40, 40, 80, 50, ColorUtil.pack(1.f, 1.f, 0.f));
//        g.fillRect(120, 90, 60, 60, ColorUtil.pack(1.f, 0.f, 1.f));


        framebuffer.write(surf);
    }

    public static void main(String[] argv) {
        try {
            if (argv.length == 1) {
                IFramebuffer framebuffer;

                if (argv[0].equals("fb1")) {
                    framebuffer = new RPiFramebuffer();
                } else if (argv[0].equals("swing")) {
                    framebuffer = new SwingFramebuffer();
                } else {
                    System.out.println("Invalid mode.");
                    return;
                }

                System.out.println(43);
//                testDraw(framebuffer);
                start(50666, framebuffer);
            } else {
                System.out.println("Usage: opal [mode]");
                System.out.println("Modes:");
                System.out.println("\tswing - display framebuffer in a window");
                System.out.println("\tfb1 - send framebuffer to /dev/fb1 (RPi only)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //example: echo -e '\x2\x0\x0f\x12\x20\x40\x40\x40\x0F\xFF' | nc localhost 50666
    public static void start(int port, IFramebuffer framebuffer) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream((clientSocket.getOutputStream()));

                byte[] request = in.readAllBytes();
                Surface surface = new RequestReader(request).readRequest();

                testDraw(framebuffer, surface);

                System.out.println(Arrays.toString(request));
                out.write("dostalem".getBytes(StandardCharsets.UTF_8));
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
