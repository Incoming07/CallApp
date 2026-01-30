package ru.app.call.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import ru.app.call.Cilent.Record;

public class Server {
    // Порт для приема RTP-пакетов

    public void init(int serverPort, int bufferSize) {
        Thread tServ = new Thread(
            () -> {
                try {
                    this.listen(serverPort, bufferSize);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    public static final ConcurrentLinkedQueue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<>();

    private void listen(int serverPort, int bufferSize) throws IOException, InterruptedException {
        // Создание UDP сокета для приема данных
        try (DatagramSocket socket = new DatagramSocket(serverPort)) {
            System.out.println("RTP Сервер запущен на порту " + serverPort);
            while (true) {
                // Создание DatagramPacket для приема данных
                byte[] buffer = new byte[bufferSize];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Прием пакета
                socket.receive(packet);
                packetQueue.add(packet);
                // Обработка полученного пакета
//                this.processRtpPacket(packet);
//                Thread.sleep(500);
            }
        }
    }

    private void send(String address, int port) throws IOException, InterruptedException {
        // Создание UDP сокета
        Record rec = new Record();
        Thread threadRec = rec.init();
        threadRec.join();
        threadRec.start();

        try (DatagramSocket socket = new DatagramSocket()) {
            // Адрес сервера
            InetAddress serverIp = InetAddress.getByName(address);

            while (true) {
                // Данные, которые будут отправлены (например, аудио данные)
//                byte[] data = "Hello, RTP!".getBytes();
                byte[] data = rec.recordQueue.poll();

                if (data != null) {

                    // Объединение заголовка и данных
                    // Создание пакета
//                    DatagramPacket packet = new DatagramPacket(packetData, packetData.length, serverIp, port);

                    // Отправка пакета
                    socket.send(packetQueue.poll());
//                    System.out.println("RTP packet sent to " + SEND_ADRESS + ":" + SEND_PORT);
                }
//                Thread.sleep(500);
            }
        }
    }


}
