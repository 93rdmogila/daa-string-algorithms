package city.search;
import java.util.List;
public interface StringSearch {
    List<Integer> search(String text, String pattern);
    String name();
    long comparisons();
    long memoryCells();
}
