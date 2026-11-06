package city.search;
import java.util.ArrayList;
import java.util.List;
public final class KmpSearch implements StringSearch{
    private long comparisons;
    private long mem;
    public static int[] lps(String p){
        return new KmpSearch().buildLps(p);
    }

    private int[] buildLps(String p){
        int m = p.length();
        int[] lps = new int[m];
        int len = 0;
        for(int i = 1; i < m; i++){
            while(true){
                comparisons++;
                if(p.charAt(i) == p.charAt(len)){
					len++; break;
				}
                if(len == 0) break;
                len = lps[len - 1];
            }
            lps[i] = len;
        }
        return lps;
    }

    @Override
    public List<Integer> search(String text, String pattern){
        if(pattern.isEmpty()) throw new IllegalArgumentException("pattern must not be empty");
        comparisons = 0;
        List<Integer> result = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        int[] lps = buildLps(pattern);
        mem = m;
        if(m > n) return result;
        int j = 0; // how many pattern chars are matched
        for(int i = 0; i < n; i++){
            char c = text.charAt(i);
            while(true){
                comparisons++;
                if(pattern.charAt(j) == c){
					j++;
					break;
				}
                if(j == 0) break;
                j = lps[j - 1];
            }
            if(j == m){
                result.add(i - m + 1);
                j = lps[m - 1];
            }
        }
        return result;
    }

    @Override
	public String name(){
		return "search_kmp";
	}
    @Override
	public long comparisons(){
		return comparisons;
	}
    @Override
	public long memoryCells(){
		return mem;
	}
}
