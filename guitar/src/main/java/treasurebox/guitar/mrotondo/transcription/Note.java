package treasurebox.guitar.mrotondo.transcription;

import treasurebox.guitar.mrotondo.audio.Tone;

public class Note {

    public enum Length {
        THIRTYSECOND, SIXTEENTH, EIGHTH, QUARTER, HALF, WHOLE
    }

    public Tone   tone;
    public Length duration;

    public Note(Tone tone, Length duration) {
        this.tone = tone;
        this.duration = duration;
    }

    public String toString() {
        return tone + ", " + duration;
    }
}
