/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2026. All rights reserved.
 */
package ru.app.call;

import java.util.Scanner;

class Main {
    public static void main(String[] args) throws Exception {
        Client cli = new Client();
        Server srv = new Server();

        Thread tCli = cli.init();
        Thread tSrv = srv.init();

        tCli.start();
        tSrv.start();

        tCli.join();
        tSrv.join();

        System.out.println("Press Enter to end recording...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

//        tCli.interrupt();
//        tSrv.interrupt();

    }
}
