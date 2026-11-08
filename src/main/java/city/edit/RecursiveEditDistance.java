package city.edit;

/**
 * Version A: plain recursion, no memory.
 *
 * DP design:
 *   State:      d(i, j) = edit distance between the first i chars of a and the first j chars of b.
 *   Transition: if a[i-1] == b[j-1] then d(i, j) = d(i-1, j-1)
 *               else d(i, j) = 1 + min( d(i-1, j)      // delete a[i-1]
 *                                     , d(i, j-1)      // insert b[j-1]
 *                                     , d(i-1, j-1) ). // replace a[i-1] by b[j-1]
 *   Base case:  d(0, j) = j and d(i, 0) = i.
 *
 * The same subproblem d(i, j) is solved many times, so the number of calls is exponential.
 * Counter: every call of rec(...) is one unit of work.
 */
public final class RecursiveEditDistance implements EditDistance {
    private long calls;

    @Override
    public int distance(String a, String b) {
        calls = 0;
        return rec(a, b, a.length(), b.length());
    }

    private int rec(String a, String b, int i, int j) {
        calls++;
        if (i == 0) return j;
        if (j == 0) return i;
        if (a.charAt(i - 1) == b.charAt(j - 1)) {
            return rec(a, b, i - 1, j - 1);
        }
        int del = rec(a, b, i - 1, j);
        int ins = rec(a, b, i, j - 1);
        int rep = rec(a, b, i - 1, j - 1);
        return 1 + Math.min(del, Math.min(ins, rep));
    }

    @Override public String name() { return "edit_recursive"; }
    @Override public long operations() { return calls; }
    @Override public long memoryCells() { return 0; } // no table; only the call stack (depth <= n+m)
}
