package city.search;

import java.util.List;

public interface StringSearch {
    /**
     * Start indices (from 0) of all occurrences of pattern in text, increasing, overlaps included.
     * @throws IllegalArgumentException if the pattern is empty
     */
    List<Integer> search(String text, String pattern);

    String name();

    /** Counter of the LAST call: character comparisons text[..] vs pattern[..] (KMP: LPS build + search). */
    long comparisons();

    /** Extra ints allocated by the last call (Naive: 0, KMP: the LPS array of length m). */
    long memoryCells();
}
