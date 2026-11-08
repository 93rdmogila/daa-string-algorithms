package city;

import city.search.KmpSearch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class LpsTest {
    @Test
    void referenceArrays() {
        assertArrayEquals(new int[]{0, 1, 2, 0, 1, 2, 3, 3}, KmpSearch.lps("AAACAAAA"));
        assertArrayEquals(new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, KmpSearch.lps("ABABCABAB"));
    }

    @Test
    void smallCases() {
        assertArrayEquals(new int[]{0}, KmpSearch.lps("A"));
        assertArrayEquals(new int[]{0, 0, 0}, KmpSearch.lps("ABC"));
        assertArrayEquals(new int[]{0, 1, 2, 3}, KmpSearch.lps("AAAA"));
    }
}
