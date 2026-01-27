package ru.app.call;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Playback {

    public Thread init(){
        return new Thread(
            this::task
        );
    }

    private void task(){
        AudioFormat audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            16000.0F,  // Частота дискретизации
            16,        // Глубина звука (бит)
            1,         // Количество каналов (моно)
            2,         // Размер.samples в frame (bytes per sample * number of channels)
            16000.0F,  // Frame rate
            false      // Little-endian
        );

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        try (SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
            line.open(audioFormat);
            line.start();

            int bufferSize = 256;
            int offset = 0;
            int bytesWritten = 0;
            byte[] audioData;

            while (true) {
                audioData = Server.recordQueue.poll();
                if (audioData != null) {
                    System.out.println("Воспроизведение...");
                    System.out.println(Arrays.toString(audioData));
//                    int bytesToWrite = Math.min(bufferSize, audioData.length - bytesWritten);
                    byte[] buffer = Arrays.copyOfRange(audioData, 0, audioData.length);

                    line.write(buffer, 0, buffer.length);

//                    bytesWritten += bytesToWrite;
//                    offset += bytesToWrite;
                }
//                    line.drain();

//                Thread.sleep(500);
            }
//            line.drain();

//            line.stop();
        } catch (LineUnavailableException e) {
            System.err.println("Не удалось открыть линию для воспроизведения аудио");
        }
    }
}
