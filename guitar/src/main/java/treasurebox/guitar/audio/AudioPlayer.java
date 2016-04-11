/**
 */
package treasurebox.guitar.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import treasurebox.guitar.model.music.Pitch;

/**
 * @author shuo.qius
 * @version $Id: Player.java, v 0.1 Apr 11, 2016 3:27:45 PM qiushuo Exp $
 */
public class AudioPlayer {
    public static final int                 SAMPLES_PER_SECOND    = 44100;
    public static final double              DEFAULT_NOTE_DURATION = 1;
    /**value: data for 1s*/
    private static final Map<Pitch, byte[]> dataMap               = new HashMap<Pitch, byte[]>();
    static {
        for (Pitch t : Pitch.PITCHS_GUITAR_RANGE) {
            dataMap.put(t, generateData(t.getFrequency(), DEFAULT_NOTE_DURATION));
        }
        dataMap.put(null, generateMute(1));
    }

    private static byte[] generateMute(double duration) {
        byte[] bytes = new byte[(int) (duration * SAMPLES_PER_SECOND)];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = 0;
        }
        return bytes;
    }

    private static byte[] generateData(double frequency, double duration) {
        byte[] bytes = new byte[(int) (duration * SAMPLES_PER_SECOND)];

        for (int i = 0; i < bytes.length; ++i) {
            byte amplitude = (byte) (Math.sin(i * 2 * Math.PI / SAMPLES_PER_SECOND * frequency) * 127);
            bytes[i] = amplitude;
        }
        return bytes;
    }

    private final AudioFormat              format;
    private final List<SourceDataLine>     dataLines;
    private final BlockingQueue<NotesTask> toWrite = new LinkedBlockingQueue<AudioPlayer.NotesTask>();
    private final Thread                   thread;

    public AudioPlayer() throws LineUnavailableException {
        this.format = new AudioFormat((float) SAMPLES_PER_SECOND, 8, 1, true, true);
        this.dataLines = new ArrayList<SourceDataLine>(1);
        this.thread = new Thread(() -> {
            run();
        });
    }

    private SourceDataLine getSourceDataLine(int index) throws LineUnavailableException {
        if (index >= this.dataLines.size()) {
            for (int i = this.dataLines.size() - 1; i < index; ++i) {
                SourceDataLine dataLine = AudioSystem.getSourceDataLine(format);
                dataLine.open();
                dataLine.start();
                this.dataLines.add(dataLine);
            }
        }
        return this.dataLines.get(index);
    }

    public void start() {
        this.thread.start();
    }

    public void stop() {
        this.thread.interrupt();
    }

    public NotesTask submitTask(NotesTask task) {
        try {
            getSourceDataLine(task.getTones().length - 1);
            toWrite.put(task);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return task;
    }

    private void run() {
        for (; !Thread.currentThread().isInterrupted();) {

            NotesTask task = null;
            try {
                task = toWrite.take();
                final CountDownLatch latch = new CountDownLatch(task.getTones().length);
                List<byte[]> datas = new ArrayList<byte[]>(3);
                for (Pitch t : task.getTones()) {
                    byte[] b = dataMap.get(t);
                    datas.add(b);
                }
                int len = (int) (task.getDuration() * SAMPLES_PER_SECOND);

                for (int i = 0; i < datas.size(); ++i) {
                    final byte[] data = datas.get(i);
                    final int length = len = Math.min(len, data.length);
                    final SourceDataLine line = getSourceDataLine(i);
                    new Thread(() -> {
                        line.write(data, 0, length);
                        latch.countDown();
                    }).start();
                }
                latch.await();
            } catch (InterruptedException ee) {
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    task.finishPlay();
                } catch (Exception e2) {
                }
            }
        }

        for (SourceDataLine line : this.dataLines) {
            line.stop();
            line.close();
        }
    }

    /**
     * @author shuo.qius
     * @version $Id: Player.java, v 0.1 Apr 11, 2016 4:05:17 PM qiushuo Exp $
     */
    public static class NotesTask {
        private final Pitch[]        tones;
        /** in seconds */
        private final double         duration;
        private final CountDownLatch latch = new CountDownLatch(1);

        /**
         * @param tones
         * @param duration no more than 1s
         */
        public NotesTask(double duration, Pitch... tones) {
            super();
            this.tones = tones;
            this.duration = duration;
        }

        public void waitForFinishPlay() throws InterruptedException {
            latch.await();
        }

        public void finishPlay() {
            try {
                Thread.sleep((long) (duration * 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
            latch.countDown();
        }

        /**
         * Getter method for property <tt>tones</tt>.
         * 
         * @return property value of tones
         */
        public Pitch[] getTones() {
            return tones;
        }

        /**
         * Getter method for property <tt>duration</tt>.
         * 
         * @return property value of duration
         */
        public double getDuration() {
            return duration;
        }
    }

    public static void main(String[] args) throws Throwable {
        AudioPlayer sut = new AudioPlayer();
        sut.start();
        for (int i = 0; i < 1; ++i) {
            //            int tonic = Constant.PITCHS_GUITAR_RANGE.get(0).getPitch() + new Random().nextInt(24);
            //            int mid = (new Random().nextInt(2) == 0 ? 3 : 4);
            //            System.out.println(mid);
            //            sut.submitTask(
            //                new NotesTask(DEFAULT_NOTE_DURATION, new Pitch(tonic), new Pitch(tonic + 7), new Pitch(mid + tonic)))
            //                .waitForFinishPlay();
            sut.submitTask(new NotesTask(DEFAULT_NOTE_DURATION, new Pitch(39 - 3 + 12 + 12)))
                .waitForFinishPlay();
        }
        sut.stop();
    }
}
