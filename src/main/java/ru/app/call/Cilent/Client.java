package ru.app.call.Cilent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//клиентское udp соединение с rtp
public class Client {
    private final int SERVER_PORT;
    private final int BUFFER_SIZE;
    private final String ADDRESS;
    private final int CLIENT_PORT;

    public Client(int serverPort, int bufferSize, String address, int clientPort){
        this.SERVER_PORT = serverPort;
        this.BUFFER_SIZE = bufferSize;
        this.ADDRESS = address;
        this.CLIENT_PORT = clientPort;
    }

    public void run() throws Exception {
        Listen listen = new Listen();
        Send send = new Send();
        Playback playback = new Playback();

        Thread tListen = listen.init(this.SERVER_PORT, this.BUFFER_SIZE);
        Thread tSend = send.init(this.ADDRESS, this.CLIENT_PORT);
        Thread tPlayback = playback.init();

        tListen.start();
        tSend.start();
        tPlayback.start();

        tListen.join();
        tSend.join();
        tPlayback.join();
    }

}
