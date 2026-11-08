package city.edit;

/**
 * Version B: bottom-up with a full (n+1) x (m+1) table. Time O(n*m), memory O(n*m).
 *
 * DP design:
 *   State:      d[i][j] = edit distance between a[0..i) and b[0..j).
 *   Transition: d[i][j] = d[i-1][j-1]                                    if a[i-1] == b[j-1]
 *               d[i][j] = 1 + min(d[i-1][j], d[i][j-1], d[i-1][j-1])     otherwise.
 *   Base case:  d[0][j] = j (insert j chars), d[i][0] = i (delete i chars).
 *
 * Counter: every evaluation of an inner cell d[i][j], i>=1, j>=1, is one unit of work.
 */
public final class TableEditDistance implements EditDistance {
    private long cells;
    private long mem;

    @Override
    public int distance(String a, String b) {
        int n = a.length(), m = b.length();
        cells = 0;
        int[][] d = new int[n + 1][m + 1];
        mem = (long) (n + 1) * (m + 1);
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                cells++;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    d[i][j] = 1 + Math.min(d[i - 1][j], Math.min(d[i][j - 1], d[i - 1][j - 1]));
                }
            }
        }
        return d[n][m];
    }

    @Override public String name() { return "edit_table"; }
    @Override public long operations() { return cells; }
    @Override public long memoryCells() { return mem; }
}
