package city.exp;
import city.edit.*;
import city.search.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
public final class Experiment{
    static final String DNA = "ACGT";
    static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    static final String LOG_PATTERN = "ERROR disk full code=7";

    static final int[] ED_SMALL = {3, 6, 9, 11, 12};
    static final int[] ED_LARGE = {250, 500, 1000, 2000, 4000};
    static final int[] SEARCH_N = {50_000, 100_000, 200_000, 400_000, 800_000, 1_600_000};
    static final int[] SEARCH_M = {10, 25, 50, 200, 400};
    static final int M_FIXED = 100;
    static final int N_FOR_M_SWEEP = 400_000;

    static final String[] ED_INPUTS = {"random4", "identical", "disjoint"};
    static final String[] SEARCH_INPUTS = {"random26", "dna4", "log_lines", "best_nomatch", "worst_aaab", "all_match"};

    private final Map<String, Object> agreement = new HashMap<>();
    public static void main(String[] args) throws IOException {
        Path out = Path.of(args.length > 0 ? args[0] : "results/results.csv");
        if(out.getParent() != null) Files.createDirectories(out.getParent());
        Experiment e = new Experiment();
        e.warmUp();
        try(PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))){
            w.println("family,algorithm,input,n,m,time_ms,ops,mem_cells,result");
            e.editDistance(w);
            e.stringSearch(w);
        }
        System.out.println("written " + out);
    }

    private void editDistance(PrintWriter w){
        List<EditDistance> all = List.of(new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance());
        List<EditDistance> dp = List.of(new TableEditDistance(), new TwoRowEditDistance());
        for(int n : ED_SMALL){
			for(String in : ED_INPUTS){
            	String[] ab = editInput(in, n);
            	for(EditDistance ed : all){
					runEdit(w, ed, in, n, ab[0], ab[1], n >= 11 ? 3 : 5);
				}
        	}
		}
        for(int n : ED_LARGE){
			for(String in : ED_INPUTS){
            	String[] ab = editInput(in, n);
            	for(EditDistance ed : dp){
					runEdit(w, ed, in, n, ab[0], ab[1], 5);
				}
			}
        }
    }

    static String[] editInput(String type, int n){
        long salt = type.hashCode() * 31L + n;
        switch(type){
            case "random4":
				return new String[]{new Gen(salt).random(n, DNA), new Gen(salt + 1).random(n, DNA)};
            case "identical":
				{String a = new Gen(salt).random(n, DNA); return new String[]{a, a};}
            case "disjoint":
				return new String[]{Gen.repeat('a', n), Gen.repeat('b', n)};
            default:
				throw new IllegalArgumentException(type);
        }
    }

    private void runEdit(PrintWriter w, EditDistance ed, String input, int n, String a, String b, int reps){
        if(n <= 9)ed.distance(a, b);
        double[] t = new double[reps];
        int result = 0;
        for(int r = 0; r < reps; r++){
            long s = System.nanoTime();
            result = ed.distance(a, b);
            t[r] = (System.nanoTime() - s) / 1e6;
        }
        check("edit|" + input + "|" + n, result);
        w.printf(Locale.ROOT, "edit,%s,%s,%d,%d,%.4f,%d,%d,%d%n",
                ed.name(), input, n, b.length(), median(t), ed.operations(), ed.memoryCells(), result);
        System.out.printf(Locale.ROOT, "%-15s %-9s n=%-5d %10.3f ms  ops=%d%n", ed.name(), input, n, median(t), ed.operations());
    }

    private void stringSearch(PrintWriter w){
        List<StringSearch> algs = List.of(new NaiveSearch(), new KmpSearch());
        for(int n : SEARCH_N){
			for(String in : SEARCH_INPUTS){
            	String[] tp = searchInput(in, n, M_FIXED);
            	for(StringSearch s : algs){
					runSearch(w, s, in, n, tp[0], tp[1]);
				}
			}
        }
        for(int m : SEARCH_M){
            String[] tp = searchInput("worst_aaab", N_FOR_M_SWEEP, m);
            for(StringSearch s : algs){
				runSearch(w, s, "worst_aaab", N_FOR_M_SWEEP, tp[0], tp[1]);
			}
        }
    }

    static String[] searchInput(String type, int n, int m){
        long salt = type.hashCode() * 31L + n * 7L + m;
        switch(type){
            case "random26":
				{ Gen g = new Gen(salt); return new String[]{g.random(n, LETTERS), g.random(m, LETTERS)}; }
            case "dna4":
				{ Gen g = new Gen(salt); return new String[]{g.random(n, DNA), g.random(m, DNA)}; }
            case "log_lines":
				return new String[]{new Gen(salt).logText(n), LOG_PATTERN};
            case "best_nomatch":
				return new String[]{Gen.repeat('a', n), "b" + Gen.repeat('a', m - 1)};
            case "worst_aaab":
				return new String[]{Gen.repeat('a', n), Gen.repeat('a', m - 1) + "b"};
            case "all_match":
				return new String[]{Gen.repeat('a', n), Gen.repeat('a', m)};
            default:
				throw new IllegalArgumentException(type);
        }
    }

    private void runSearch(PrintWriter w, StringSearch s, String input, int n, String text, String pat){
        final int reps = 5;
        s.search(text, pat);
        double[] t = new double[reps];
        List<Integer> res = null;
        for(int r = 0; r < reps; r++){
            long st = System.nanoTime();
            res = s.search(text, pat);
            t[r] = (System.nanoTime() - st) / 1e6;
        }
        check("search|" + input + "|" + n + "|" + pat.length(), res.hashCode() * 31 + res.size());
        w.printf(Locale.ROOT, "search,%s,%s,%d,%d,%.4f,%d,%d,%d%n",
                s.name(), input, n, pat.length(), median(t), s.comparisons(), s.memoryCells(), res.size());
        System.out.printf(Locale.ROOT, "%-13s %-12s n=%-8d m=%-4d %9.3f ms  cmp=%d%n",
                s.name(), input, n, pat.length(), median(t), s.comparisons());
    }

    private void check(String key, Object value){
        Object old = agreement.putIfAbsent(key, value);
        if(old != null && !old.equals(value))throw new IllegalStateException("algorithms disagree on " + key);
    }

    static double median(double[] x){
        double[] c = x.clone();
        Arrays.sort(c);
        return c[c.length / 2];
    }

    private void warmUp(){
        EditDistance[] eds = {new TableEditDistance(), new TwoRowEditDistance()};
        StringSearch[] ss = {new NaiveSearch(), new KmpSearch()};
        String a = new Gen(1).random(300, DNA), b = new Gen(2).random(300, DNA);
        String t = new Gen(3).random(20_000, DNA);
        for(int i = 0; i < 40; i++){
            for(EditDistance e : eds){
				e.distance(a, b);
			}
            for(StringSearch s : ss){
				s.search(t, "ACGTAC");
			}
        }
    }
}
