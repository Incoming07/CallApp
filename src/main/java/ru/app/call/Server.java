package ru.app.call;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server {
    private static final int BUFFER_SIZE = 512;
    private static final int SERVER_PORT = 5004; // Порт для приема RTP-пакетов
    public static final ConcurrentLinkedQueue<byte[]>  recordQueue = new ConcurrentLinkedQueue<>();

    public Thread init() throws Exception {
        return new Thread(
            () -> {
                try {
                    this.task();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    private void task() throws IOException, InterruptedException {
        // Создание UDP сокета для приема данных
        try (DatagramSocket socket = new DatagramSocket(SERVER_PORT)) {
            System.out.println("RTP Сервер запущен на порту " + SERVER_PORT);
            while (true) {
                // Создание DatagramPacket для приема данных
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Прием пакета
                socket.receive(packet);
                // Обработка полученного пакета
                this.processRtpPacket(packet);
//                Thread.sleep(500);
            }
        }
    }

    private void processRtpPacket(DatagramPacket packet) {
        // Получение данных из пакета
        byte[] data = packet.getData();
        int length = packet.getLength();

        // Парсинг RTP заголовка
        if (data.length >= 12) { // Минимальный размер RTP-заголовка
            // Извлечение RTP заголовка
            int version = (data[0] >> 6) & 0x03;
            int payloadType = data[0] & 0x7F;
            boolean marker = (data[1] >> 7) == 1;
            int sequenceNumber = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
            long timestamp = ((data[4] & 0xFFL) << 24) |
                ((data[5] & 0xFFL) << 16) |
                ((data[6] & 0xFFL) << 8) |
                (data[7] & 0xFFL);
            int ssrc = (int) (((data[8] & 0xFFL) << 24) |
                ((data[9] & 0xFFL) << 16) |
                ((data[10] & 0xFFL) << 8) |
                (data[11] & 0xFFL));

            byte[] cleanData = new byte[data.length - 11];

            recordQueue.add(
                Arrays.copyOfRange(data, 12, packet.getLength())
            );

            // Вывод информации о пакете
//            System.out.println("Получен RTP-пакет:");
//            System.out.println("Версия: " + version);
//            System.out.println("Тип полезной нагрузки: " + payloadType);
//            System.out.println("Флаг: " + marker);
//            System.out.println("Номер пакета: " + sequenceNumber);
//            System.out.println("Таймстамп: " + timestamp);
//            System.out.println("SSRC: " + ssrc);
//            System.out.println("Длина: " + length);
//            System.out.println("Данные: " + Arrays.toString(data));
//            System.out.println("------------------------");
        } else {
            System.out.println("Пакет слишком мал для RTP.");
        }
    }
}
