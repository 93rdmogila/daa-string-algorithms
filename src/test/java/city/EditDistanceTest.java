package city;

import city.edit.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditDistanceTest {
    static List<EditDistance> versions() {
        return List.of(new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance());
    }

    @ParameterizedTest @MethodSource("versions")
    void referenceValues(EditDistance ed) {
        assertEquals(3, ed.distance("kitten", "sitting"));
        assertEquals(2, ed.distance("flaw", "lawn"));
        assertEquals(5, ed.distance("intention", "execution"));
        assertEquals(3, ed.distance("", "abc"));
        assertEquals(0, ed.distance("abc", "abc"));
    }

    @ParameterizedTest @MethodSource("versions")
    void emptyStrings(EditDistance ed) {
        assertEquals(0, ed.distance("", ""));
        assertEquals(4, ed.distance("abcd", ""));
        assertEquals(4, ed.distance("", "abcd"));
    }

    @Test
    void threeVersionsAgreeOn100RandomShortStrings() {
        Random rnd = new Random(7);
        EditDistance a = new RecursiveEditDistance(), b = new TableEditDistance(), c = new TwoRowEditDistance();
        for (int t = 0; t < 100; t++) {
            String x = randomString(rnd, rnd.nextInt(8)), y = randomString(rnd, rnd.nextInt(8));
            int expected = b.distance(x, y);
            assertEquals(expected, a.distance(x, y), x + " / " + y);
            assertEquals(expected, c.distance(x, y), x + " / " + y);
            assertEquals(expected, b.distance(y, x), "symmetry " + x + " / " + y);
        }
    }

    @Test
    void countersAreMeasuredInsideTheAlgorithms() {
        TableEditDistance t = new TableEditDistance();
        TwoRowEditDistance r = new TwoRowEditDistance();
        t.distance("abcde", "xyz");
        r.distance("abcde", "xyz");
        assertEquals(15, t.operations());
        assertEquals(15, r.operations());
        assertEquals(6 * 4, t.memoryCells());
        assertEquals(2 * 4, r.memoryCells());
    }

    private static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append("abc".charAt(rnd.nextInt(3)));
        return sb.toString();
    }
}
