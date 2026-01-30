package ru.app.call.Cilent;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


public class Record {

    static private int chunkSize = 256;
    static private int numBytesRead;
    static private volatile boolean isRunning = true;
    public final ConcurrentLinkedQueue<byte[]> recordQueue = new ConcurrentLinkedQueue<>();
    public Thread record;

    public Thread init() throws InterruptedException {
//        System.out.println("Recording...");
        return new Thread(
            () -> {
                try {
                    task();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    private void stop() {
        isRunning = false;
    }

    private void task() throws InterruptedException {
        byte[] buffer = new byte[chunkSize];
        int bufferSize = 0;
        byte[] data = new byte[chunkSize];

        AudioFormat audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            16000.0F,  // Частота дискретизации
            16,        // Глубина звука (бит)
            1,         // Количество каналов (моно)
            2,         // Размер.samples в frame (bytes per sample * number of channels)
            16000.0F,  // Frame rate
            false      // Little-endian
        );
        try (TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat)) {
            // Получение доступа к микрофону
            TargetDataLine.Info info = new TargetDataLine.Info(TargetDataLine.class, audioFormat);

            // Открытие и запуск записи
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            short i = 0;
            while (isRunning) {
                numBytesRead = targetDataLine.read(data, 0, data.length);
                if (numBytesRead == -1) {
                    break;
                }
                // Добавляем новые данные в буфер
                if (numBytesRead >= 0) {
                    System.arraycopy(data, 0, buffer, bufferSize, numBytesRead);
                }
                bufferSize += numBytesRead;

                // Проверяем, достиг ли буфер размера куска
                // todo записывать конец массива при остановке
                while (bufferSize >= chunkSize) {
                    // Формируем кусок
                    byte[] chunk = Arrays.copyOfRange(buffer, 0, chunkSize);

                    // Обработка куска
                    recordQueue.add(chunk);

                    // Сдвигаем буфер
                    System.arraycopy(buffer, chunkSize, buffer, 0, bufferSize - chunkSize);
                    bufferSize -= chunkSize;
                }
//                Thread.sleep(500);
//                System.out.println(numBytesRead + " " + recordQueue.size());
                if (i % 512 == 0) {
//                    targetDataLine.drain();
                    i = 0;
//                    System.gc();
                }
                i++;
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}