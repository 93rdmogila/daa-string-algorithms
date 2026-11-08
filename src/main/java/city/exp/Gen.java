package city.exp;

import java.util.Random;

/** Seeded generator: the same salt always gives the same strings, on every machine. */
public final class Gen {
    public static final long SEED = 42;
    private final Random rnd;

    public Gen(long salt) {
        this.rnd = new Random(SEED * 1_000_003L + salt);
    }

    public String random(int n, String alphabet) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        return sb.toString();
    }

    public static String repeat(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    private static final String[] LINES = {
        "INFO request ok id=%d\n",
        "INFO user login id=%d\n",
        "WARN slow query ms=%d\n",
        "DEBUG cache hit key=%d\n",
        "INFO route search q=%d\n",
        "ERROR disk full code=%d\n"
    };

    /** Fake server log of length n. About 1 line in 6 is an ERROR line. */
    public String logText(int n) {
        StringBuilder sb = new StringBuilder(n + 64);
        while (sb.length() < n) {
            sb.append(String.format(LINES[rnd.nextInt(LINES.length)], rnd.nextInt(10)));
        }
        sb.setLength(n);
        return sb.toString();
    }
}
