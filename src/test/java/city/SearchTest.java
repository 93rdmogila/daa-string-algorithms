package city;

import city.search.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {
    static List<StringSearch> algorithms() {
        return List.of(new NaiveSearch(), new KmpSearch());
    }

    @ParameterizedTest @MethodSource("algorithms")
    void referenceMatches(StringSearch s) {
        assertEquals(List.of(10), s.search("ABABDABACDABABCABAB", "ABABCABAB"));
        assertEquals(List.of(0, 1, 2, 3), s.search("AAAAA", "AA"));
        assertEquals(List.of(0, 2, 4), s.search("ABABABA", "ABA"));
    }

    @ParameterizedTest @MethodSource("algorithms")
    void noMatchGivesEmptyList(StringSearch s) {
        assertEquals(List.of(), s.search("ABCDEF", "XYZ"));
        assertEquals(List.of(), s.search("", "A"));
    }

    @ParameterizedTest @MethodSource("algorithms")
    void emptyPatternThrows(StringSearch s) {
        assertThrows(IllegalArgumentException.class, () -> s.search("ABC", ""));
        assertThrows(IllegalArgumentException.class, () -> s.search("", ""));
    }

    @ParameterizedTest @MethodSource("algorithms")
    void patternLongerThanTextGivesEmptyList(StringSearch s) {
        assertEquals(List.of(), s.search("AB", "ABC"));
    }

    @Test
    void naiveAndKmpAgreeOn100RandomTexts() {
        Random rnd = new Random(11);
        NaiveSearch naive = new NaiveSearch();
        KmpSearch kmp = new KmpSearch();
        for (int t = 0; t < 100; t++) {
            String text = randomString(rnd, rnd.nextInt(60));
            String pat = randomString(rnd, 1 + rnd.nextInt(5));
            assertEquals(naive.search(text, pat), kmp.search(text, pat), text + " / " + pat);
        }
    }

    @Test
    void kmpNeverMovesBackwardsInTheText() {
        // Each text character is compared at most twice, so total <= 2n (+ 2m for the LPS build).
        KmpSearch kmp = new KmpSearch();
        String text = "A".repeat(10_000);
        String pat = "A".repeat(99) + "B";
        kmp.search(text, pat);
        assertTrue(kmp.comparisons() <= 2L * text.length() + 2L * pat.length(), "comparisons=" + kmp.comparisons());
        NaiveSearch naive = new NaiveSearch();
        naive.search(text, pat);
        assertTrue(naive.comparisons() > 50L * text.length() / 2, "naive does ~m work per start");
    }

    private static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append("AB".charAt(rnd.nextInt(2)));
        return sb.toString();
    }
}
