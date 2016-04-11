package treasurebox.guitar.mrotondo.audio;

import treasurebox.guitar.mrotondo.filter.Filter;

public interface AudioGenerator {
    public boolean shouldSendSamples(int numSamples);

    public double[] getSamples(int numSamples);

    public void start();

    public void addFilter(Filter filter);
}
