package treasurebox.guitar.mrotondo.filter;

import java.util.LinkedList;

import treasurebox.guitar.mrotondo.audio.AudioGenerator;

public class FilterFactory {

    private static LinkedList<Filter> filters = new LinkedList<Filter>();

    public static void addFilterPrototype(Filter filter) {
        filters.add(filter);
    }

    public static void applyFilters(AudioGenerator toneWriter) {
        for (Filter filter : filters) {
            Filter clone = filter.clone();
            clone.start();
            toneWriter.addFilter(clone);
        }
    }

}
