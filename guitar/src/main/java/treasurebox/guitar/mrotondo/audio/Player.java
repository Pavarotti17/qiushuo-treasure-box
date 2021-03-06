package treasurebox.guitar.mrotondo.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

public class Player {

    public static final float SAMPLES_PER_SECOND       = 44100;
    static final double       VOLUME                   = 1.0;

    // These are "per-instrument" variables
    static double             maxAmplitude             = 120.0; // This is in "bytes" or something, but should be in decibels?!?
    static double             attackTimeMs             = 0;//1000;//10.0;
    static double             decayTimeMs              =0;//10.0;
    static double             relativeSustainAmplitude = 0.1;  // This is in "bytes" or something, but should be in decibels?!?
    static double             releaseTimeMs            = 0.0;

    public static void main(String[] args) throws Throwable {
        //        for(int i=0;i<100;++i)
        playToneInternal(Tone.MIDDLE_C, 2);
    }

    public static void playTone(Tone tone, double duration) throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLES_PER_SECOND, 8, 1, true, true);
        DataLine.Info clipInfo = new DataLine.Info(Clip.class, format);
        Clip clip = (Clip) AudioSystem.getLine(clipInfo);

        double fullDuration = duration + releaseTimeMs / 1000; //TODO: This logic needs to be moved into getToneBytes so others can call it correctly

        byte[] data = getToneBytes(tone, fullDuration);
        clip.open(format, data, 0, data.length);
        clip.start();

        try {
            Thread.sleep((int) (fullDuration * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clip.stop();
        clip.flush();
        clip.close();
    }

    private static void playToneInternal(Tone tone, double duration)
                                                                    throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLES_PER_SECOND, 8, 1, true, true);
        DataLine.Info clipInfo = new DataLine.Info(Clip.class, format);
        Clip clip = (Clip) AudioSystem.getLine(clipInfo);
        Clip clip2 = (Clip) AudioSystem.getLine(clipInfo);

        double fullDuration = duration + releaseTimeMs / 1000; //TODO: This logic needs to be moved into getToneBytes so others can call it correctly

        byte[] data = getClickBytes(1);//getToneBytes(tone, fullDuration);
        byte[] data2 =getClickBytes(1);// getToneBytes(new Tone(tone.pitch+7), fullDuration);
        clip.open(format, data, 0, data.length);
        clip.start();
        clip2.open(format, data2, 0, data.length);
        clip2.start();

        try {
            Thread.sleep((int) (fullDuration * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clip.stop();
        clip.flush();
        clip.close();
        clip2.stop();
        clip2.flush();
        clip2.close();
    }

    /**
     * @param tone
     * @param duration in second
     * @return
     */
    public static byte[] getToneBytes(Tone tone, double duration) {
        byte[] bytes = new byte[(int) (SAMPLES_PER_SECOND * duration)];
        System.out.println(bytes.length);

        int attackSamples = (int) (attackTimeMs / 1000 * SAMPLES_PER_SECOND);
        int decaySamples = (int) (decayTimeMs / 1000 * SAMPLES_PER_SECOND);
        int releaseSamples = (int) (releaseTimeMs / 1000 * SAMPLES_PER_SECOND);
        int attackDecayReleaseSamples = attackSamples + decaySamples + releaseSamples;
        int sustainSamples = (int) (bytes.length - attackDecayReleaseSamples);

        System.out.println("attackSamples: " + attackSamples);

        int signalPosition = 0;
        for (int i = 0; i < attackSamples; i++) {
            byte amplitude = (byte) (Math.sin(((signalPosition * 2 * Math.PI) / SAMPLES_PER_SECOND)
                                              * tone.getFrequency())
                                     * maxAmplitude * VOLUME);
            double multiplier = (double) i / attackSamples;
            bytes[signalPosition] = (byte) (amplitude * multiplier);
            signalPosition++;
        }
        System.out.println("After attack, signalPosition is " + signalPosition);
        for (int i = 0; i < decaySamples; i++) {
            byte amplitude = (byte) (Math.sin(((signalPosition * 2 * Math.PI) / SAMPLES_PER_SECOND)
                                              * tone.getFrequency())
                                     * maxAmplitude * VOLUME);
            double multiplier = relativeSustainAmplitude + (1.0 - (double) i / decaySamples)
                                * (1.0 - relativeSustainAmplitude);
            bytes[signalPosition] = (byte) (amplitude * multiplier);
            signalPosition++;
        }
        System.out.println("After decay, signalPosition is " + signalPosition);
        for (int i = 0; i < sustainSamples; i++) {
            byte amplitude = (byte) (Math.sin(((signalPosition * 2 * Math.PI) / SAMPLES_PER_SECOND)
                                              * tone.getFrequency())
                                     * maxAmplitude * relativeSustainAmplitude * VOLUME);
            bytes[signalPosition] = amplitude;
            signalPosition++;
        }
        System.out.println("After sustain, signalPosition is " + signalPosition);
        for (int i = 0; i < releaseSamples; i++) {
            byte amplitude = (byte) (Math.sin(((signalPosition * 2 * Math.PI) / SAMPLES_PER_SECOND)
                                              * tone.getFrequency())
                                     * maxAmplitude * relativeSustainAmplitude * VOLUME);
            double multiplier = (1.0 - (double) i / releaseSamples);
            bytes[signalPosition] = (byte) (amplitude * multiplier);
            signalPosition++;
        }
        System.out.println("After release, signalPosition is " + signalPosition);
        return bytes;
    }

    public static byte[] getToneBytesWithSpeed(Tone tone, double duration, double clockRate) {
        byte[] bytes = new byte[(int) (SAMPLES_PER_SECOND * duration)];
        for (int i = 0; i < bytes.length; i++) {
            byte amplitude = (byte) (Math.sin(((i * clockRate * 2 * Math.PI) / SAMPLES_PER_SECOND)
                                              * tone.getFrequency())
                                     * maxAmplitude * VOLUME);
            bytes[i] = amplitude;
        }
        return bytes;
    }

    // TODO: parameterize this bitch
    public static void click(double duration) throws LineUnavailableException {
        AudioFormat format = new AudioFormat(SAMPLES_PER_SECOND, 8, 1, true, true);
        DataLine.Info clipInfo = new DataLine.Info(Clip.class, format);
        Clip clip = (Clip) AudioSystem.getLine(clipInfo);

        byte[] data = getClickBytes(duration);
        clip.open(format, data, 0, data.length);
        clip.start();

        try {
            Thread.sleep((int) (duration * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clip.stop();
        clip.flush();
        clip.close();
    }

    public static byte[] getClickBytes(double duration) {
        byte[] bytes = new byte[(int) (SAMPLES_PER_SECOND * duration)];
        //Random r = new Random();
        //r.nextBytes(bytes);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (Math.sin(((i * 2 * Math.PI) / SAMPLES_PER_SECOND) * 500.0)
                               * maxAmplitude * VOLUME);
            //bytes[i] = (byte) (bytes[i] * (double) Math.pow(2, -Math.pow(-(i / 5000.0 - 2), 2)));
            bytes[i] = (byte) (bytes[i] * (1.0 / Math.pow((double) i / bytes.length + 1, 100)));
        }
        return bytes;
    }
}