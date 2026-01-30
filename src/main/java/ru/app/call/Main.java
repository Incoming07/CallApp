package ru.app.call;

import java.util.Scanner;
import ru.app.call.Cilent.Client;
import ru.app.call.Cilent.Playback;
import ru.app.call.Server.Server;

class Main {
    private static final int SERVER_PORT = 5005;
    private static final int BUFFER_SIZE = 512;
    private static final String ADDRESS = "localhost";
    private static final int CLIENT_PORT = 5005;

    public static void main(String[] args) throws Exception {

        Client cli = new Client(SERVER_PORT, BUFFER_SIZE, ADDRESS, CLIENT_PORT);
        Server srv = new Server();
        Playback pbk = new Playback();

        cli.run();
//        Thread tSrv = srv.init();
//        tSrv.start();
//        tSrv.join();

        System.out.println("Press Enter to end recording...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

//        tCli.interrupt();
//        tSrv.interrupt();

    }
}
