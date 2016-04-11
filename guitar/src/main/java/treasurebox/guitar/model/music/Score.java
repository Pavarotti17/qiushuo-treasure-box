/**
 */
package treasurebox.guitar.model.music;

/**
 * 
 * @author shuo.qius
 * @version $Id: Score.java, v 0.1 Apr 11, 2016 4:34:00 PM qiushuo Exp $
 */
public class Score {
    private final KeySignature  keySign;
    private final TimeSignature timeSign;
    private final Tempo         tempo;

    /**
     * @param keySign
     * @param timeSign
     * @param tempo
     */
    public Score(KeySignature keySign, TimeSignature timeSign, Tempo tempo) {
        super();
        this.keySign = keySign;
        this.timeSign = timeSign;
        this.tempo = tempo;
    }

    /**
     * Getter method for property <tt>keySign</tt>.
     * 
     * @return property value of keySign
     */
    public KeySignature getKeySign() {
        return keySign;
    }

    /**
     * Getter method for property <tt>timeSign</tt>.
     * 
     * @return property value of timeSign
     */
    public TimeSignature getTimeSign() {
        return timeSign;
    }

    /**
     * Getter method for property <tt>tempo</tt>.
     * 
     * @return property value of tempo
     */
    public Tempo getTempo() {
        return tempo;
    }

    // add staff

}
