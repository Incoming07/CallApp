package ru.app.call;

import java.util.Scanner;

class Main {
    public static void main(String[] args) throws Exception {
        Client cli = new Client();
        Server srv = new Server();
        Playback pbk = new Playback();

        Thread tCli = cli.init();
        Thread tSrv = srv.init();
        Thread tPbk = pbk.init();

        tCli.start();
        tSrv.start();
        tPbk.start();

        tCli.join();
        tSrv.join();
        tPbk.join();

        System.out.println("Press Enter to end recording...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

//        tCli.interrupt();
//        tSrv.interrupt();

    }
}
