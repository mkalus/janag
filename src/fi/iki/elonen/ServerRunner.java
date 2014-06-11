package fi.iki.elonen;

import java.io.IOException;

public class ServerRunner {
    private static boolean finished = false;

    public static void run(Class serverClass) {
        try {
            executeInstance((NanoHTTPD) serverClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeInstance(NanoHTTPD server) {
        try {
            server.start();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

        // add shutdown handler
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHandler(server)));

        System.out.println("Server started...\n");

        while (!finished) {
            try {
                Thread.sleep(1000);
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Shutdown hook executable
     */
    private static class ShutdownHandler implements Runnable
    {
        private NanoHTTPD server;

        public ShutdownHandler(NanoHTTPD server) {
            this.server = server;
        }

        public void run()
        {
            finished = true;
            this.server.stop();
            System.out.println("Server stopped.\n");
        }
    }
}
