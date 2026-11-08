package city.edit;

/**
 * Version C: the same recurrence, but only the previous and the current row are kept.
 * Time O(n*m), extra memory O(m).
 *
 * DP design:
 *   State:      prev[j] = d(i-1, j) and cur[j] = d(i, j): only two rows of the table are alive.
 *   Transition: cur[j] = prev[j-1]                                       if a[i-1] == b[j-1]
 *               cur[j] = 1 + min(prev[j], cur[j-1], prev[j-1])           otherwise;
 *               after each row: swap(prev, cur).
 *   Base case:  prev[j] = j for row 0, and cur[0] = i at the start of every row i.
 *
 * Counter: every evaluation of an inner cell is one unit of work (same unit as in version B).
 */
public final class TwoRowEditDistance implements EditDistance {
    private long cells;
    private long mem;

    @Override
    public int distance(String a, String b) {
        int n = a.length(), m = b.length();
        cells = 0;
        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];
        mem = 2L * (m + 1);
        for (int j = 0; j <= m; j++) prev[j] = j;
        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            for (int j = 1; j <= m; j++) {
                cells++;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    cur[j] = prev[j - 1];
                } else {
                    cur[j] = 1 + Math.min(prev[j], Math.min(cur[j - 1], prev[j - 1]));
                }
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m]; // after the last swap, prev holds row n (for n == 0 it holds row 0)
    }

    @Override public String name() { return "edit_two_rows"; }
    @Override public long operations() { return cells; }
    @Override public long memoryCells() { return mem; }
}
