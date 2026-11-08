package city.edit;

/** One signature for all three versions, so the experiment is a single loop. */
public interface EditDistance {
    /** Minimal number of insertions, deletions and replacements turning a into b. */
    int distance(String a, String b);

    String name();

    /** Counter of the LAST call: recursive calls (A) or evaluated DP cells (B, C). */
    long operations();

    /** Counter of the LAST call: number of ints allocated on the heap for the DP storage. */
    long memoryCells();
}
