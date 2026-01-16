/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2026. All rights reserved.
 */
package ru.app.call;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//клиентское udp соединение с rtp
public class Client {

    public static void main(String[] args) throws Exception {
        // Параметры для соединения
        String serverAddress = "localhost";
        int serverPort = 5004; // Порт сервера

        // Создание UDP сокета
        try (DatagramSocket socket = new DatagramSocket()) {
            // Адрес сервера
            InetAddress serverIp = InetAddress.getByName(serverAddress);

            // Данные, которые будут отправлены (например, аудио данные)
            byte[] data = "Hello, RTP!".getBytes();

            // Формирование RTP заголовка
            byte[] rtpHeader = createRtpHeader(/*добавивть инкремент идентификатора пакета*/);

            // Объединение заголовка и данных
            byte[] packetData = new byte[rtpHeader.length + data.length];
            System.arraycopy(rtpHeader, 0, packetData, 0, rtpHeader.length);
            System.arraycopy(data, 0, packetData, rtpHeader.length, data.length);

            // Создание пакета
            DatagramPacket packet = new DatagramPacket(packetData, packetData.length, serverIp, serverPort);

            // Отправка пакета
            socket.send(packet);
            System.out.println("RTP packet sent to " + serverAddress + ":" + serverPort);
        }
    }

    private static byte[] createRtpHeader() {
        // Структура RTP заголовка:
        // В этом примере формируем минимальный заголовок
        // В реальном приложении необходимо правильно устанавливать все поля
        byte[] header = new byte[12]; // 12 байт для базового заголовка без CSRC

        // Поля заголовка RTP
        // В примере используются фиктивные значения, которые нужно заменить на реальные
        // Флаги (F)
        header[0] = (byte) 0x80; // Версия 2, 0 флагов, тип полезной нагрузки 0

        // Номер пакета (SEQ)
        // Должен инкрементироваться для каждого пакета
        int sequenceNumber = 1;
        header[2] = (byte) (sequenceNumber >> 8);
        header[3] = (byte) (sequenceNumber & 0xFF);

        // Таймстамп (TIMESTAMP)
        // Текущее время в единицах RTP (например, 90 kHz)
        long timestamp = System.currentTimeMillis() * 90;
        header[4] = (byte) (timestamp >> 24);
        header[5] = (byte) (timestamp >> 16 & 0xFF);
        header[6] = (byte) (timestamp >> 8 & 0xFF);
        header[7] = (byte) (timestamp & 0xFF);

        // SSRC (Synchronization Source Identifier)
        // Уникальный идентификатор источника
        int ssrc = 0x12345678;
        header[8] = (byte) (ssrc >> 24);
        header[9] = (byte) (ssrc >> 16 & 0xFF);
        header[10] = (byte) (ssrc >> 8 & 0xFF);
        header[11] = (byte) (ssrc & 0xFF);

        return header;
    }
}
