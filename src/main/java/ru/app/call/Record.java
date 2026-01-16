package ru.app.call;

import java.io.ByteArrayInputStream;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class Record {

    static private int numBytesRead;
    static private volatile boolean isRunning = true;

    public static void main(String[] args) {
        // Настройка формата аудио
        AudioFormat audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            16000.0F,  // Частота дискретизации
            16,        // Глубина звука (бит)
            1,         // Количество каналов (моно)
            2,         // Размер.samples в frame (bytes per sample * number of channels)
            16000.0F,  // Frame rate
            false      // Little-endian
        );

        try (TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat);) {
            // Получение доступа к микрофону
            TargetDataLine.Info info = new TargetDataLine.Info(TargetDataLine.class, audioFormat);


            // Открытие и запуск записи
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            System.out.println("Press Enter to start recording...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            System.out.println("Recording...");

            // Буфер для хранения аудиоданных
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            int numBytesRead;
            byte[] data = new byte[targetDataLine.getBufferSize() / 5];

            Thread newWriter = new Thread(
                () -> {
                    while (isRunning) {
                        numBytesRead = targetDataLine.read(data, 0, data.length);
                        if (numBytesRead == -1) {
                            break;
                        }
                        byteArrayOutputStream.write(data, 0, numBytesRead);
                    }
                }
            );
            newWriter.start();
            System.out.println("Press Enter to stop recording...");
            scanner.nextLine();

            //остановка потока записи
            isRunning = false;

            // Остановка и закрытие линии
            targetDataLine.stop();
//            targetDataLine.close();

            // Сохранение аудиоданных в файл
            byte[] audioData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());

            // Указываем путь для сохранения аудиофайла
            File audioFile = new File("output.wav");
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);

            System.out.println("Audio recorded and saved to: " + audioFile.getAbsolutePath());
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}