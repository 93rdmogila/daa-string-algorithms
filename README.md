# DAA Assignment 4: City Search & Spell-Check

Edit Distance (recursive, table, two rows), Naive and KMP string search, counters, a seeded experiment, 8 plots and a report.

## Requirements
JDK 17+ and Maven 3.8+ (JUnit 5 is downloaded by Maven). Python 3 with `matplotlib` only for the plots.

## Build and test (one command)
```
mvn test
```

## Run the experiment (writes `results/results.csv`, about 15 s)
```
mvn -q compile exec:java -Dexec.args="results/results.csv"
```
The generator is seeded (`Gen.SEED = 42`): counters and results are identical on every run; only `time_ms` depends on the machine.

## Draw the plots (writes `results/plots/*.png`)
```
python3 scripts/plot.py
```

## Without Maven (plain JDK)
```
mkdir -p out/main && javac -d out/main $(find src/main -name '*.java')
java -cp out/main city.exp.Experiment results/results.csv
```

## Layout
```
src/main/java/city/edit/     EditDistance interface + Recursive, Table, TwoRow versions
src/main/java/city/search/   StringSearch interface + NaiveSearch, KmpSearch (public static lps)
src/main/java/city/exp/      Gen (seeded generator), Experiment (runner, writes results.csv)
src/test/java/city/          JUnit 5 tests (reference values, edge cases, random cross-checks)
scripts/plot.py              plots from results.csv
results/results.csv          one row per run: family,algorithm,input,n,m,time_ms,ops,mem_cells,result
results/plots/               P1 to P8 as PNG
REPORT.md                    Experiment Design Card, plots, prediction vs result, short answers
```

`ops` = recursive calls (recursive Edit Distance), DP cells evaluated (table, two rows) or character comparisons (search). `mem_cells` = ints allocated for the DP storage / the LPS array. `result` = edit distance or number of matches.

## Git
Work on `main`, final version tagged `v1.0`. GitHub repository: <PUT THE LINK HERE>

## AI use acknowledgement
This assignment was prepared with the help of an AI assistant (Claude by Anthropic): it produced the code, the tests, the experiment, the plots and the report text, and ran them. I read and checked the code and the results, and I can explain every part of them.
