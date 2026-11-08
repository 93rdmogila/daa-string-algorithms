#!/usr/bin/env python3
"""Draws all plots from results/results.csv into results/plots/. Usage: python3 scripts/plot.py"""
import csv, os, textwrap
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
rows = list(csv.DictReader(open(os.path.join(ROOT, "results", "results.csv"))))
OUT = os.path.join(ROOT, "results", "plots")
os.makedirs(OUT, exist_ok=True)
for r in rows:
    r["n"], r["m"], r["ops"], r["mem_cells"] = int(r["n"]), int(r["m"]), int(r["ops"]), int(r["mem_cells"])
    r["time_ms"] = float(r["time_ms"])

def sel(**kw):
    return sorted([r for r in rows if all(str(r[k]) == str(v) for k, v in kw.items())], key=lambda r: r["n"])

COL = {"disjoint": "tab:red", "random4": "tab:blue", "identical": "tab:green",
       "random26": "tab:blue", "dna4": "tab:purple", "log_lines": "tab:green",
       "best_nomatch": "tab:gray", "worst_aaab": "tab:red", "all_match": "tab:orange"}

def finish(fig, ax, name, title, xlabel, ylabel, caption):
    ax.set_title(title, fontsize=11, fontweight="bold")
    ax.set_xlabel(xlabel); ax.set_ylabel(ylabel)
    ax.grid(True, which="both", alpha=0.3)
    ax.legend(fontsize=8)
    fig.text(0.5, 0.015, textwrap.fill("Takeaway: " + caption, 95), ha="center", va="bottom", fontsize=9, style="italic")
    fig.subplots_adjust(bottom=0.24, left=0.12, right=0.97, top=0.92)
    fig.savefig(os.path.join(OUT, name), dpi=150)
    plt.close(fig)

def newfig():
    fig, ax = plt.subplots(figsize=(7.2, 5.2))
    return fig, ax

# P1 recursive calls
fig, ax = newfig()
for inp in ("disjoint", "random4", "identical"):
    d = sel(algorithm="edit_recursive", input=inp)
    ax.plot([r["n"] for r in d], [r["ops"] for r in d], "o-", color=COL[inp], label=f"recursive, {inp}")
d = sel(algorithm="edit_table", input="disjoint", n=12)
ns = [3, 6, 9, 11, 12]
ax.plot(ns, [n * n for n in ns], "s--", color="black", label="table / two rows: n*n cells")
ax.set_yscale("log")
finish(fig, ax, "p1_recursive_calls.png", "P1. Recursive edit distance: number of calls",
       "string length n (both strings)", "counted operations (calls or cells), log scale",
       "On disjoint strings the calls grow about 5.6x per extra character (377 million at n=12); identical strings need only n+1 calls, and the table never needs more than n*n cells.")

# P2 recursive time
fig, ax = newfig()
for inp in ("disjoint", "random4", "identical"):
    d = sel(algorithm="edit_recursive", input=inp)
    ax.plot([r["n"] for r in d], [r["time_ms"] for r in d], "o-", color=COL[inp], label=f"recursive, {inp}")
d = sel(algorithm="edit_table", input="disjoint"); d = [r for r in d if r["n"] <= 12]
ax.plot([r["n"] for r in d], [r["time_ms"] for r in d], "s--", color="black", label="table, disjoint")
ax.set_yscale("log")
finish(fig, ax, "p2_recursive_time.png", "P2. Recursive vs table: measured time",
       "string length n (both strings)", "time, ms (median of 3-5 runs), log scale",
       "Recursion on disjoint strings grows from about 0.001 ms to 820 ms between n=3 and n=12, while the table stays below 0.004 ms: the clock agrees with the counter.")

# P3 table vs two rows time (log-log)
fig, ax = newfig()
for alg, mk in (("edit_table", "o"), ("edit_two_rows", "s")):
    for inp, ls in (("random4", "-"), ("disjoint", ":")):
        d = [r for r in sel(algorithm=alg, input=inp) if r["n"] >= 250]
        ax.plot([r["n"] for r in d], [r["time_ms"] for r in d], marker=mk, ls=ls, color=COL[inp] if alg == "edit_table" else "tab:orange",
                label=f"{alg[5:]}, {inp}")
base = [r for r in sel(algorithm="edit_two_rows", input="random4") if r["n"] == 250][0]["time_ms"]
xs = [250, 500, 1000, 2000, 4000]
ax.plot(xs, [base * (x / 250) ** 2 for x in xs], "k--", lw=1, label="reference: time ~ n^2")
ax.set_xscale("log"); ax.set_yscale("log")
finish(fig, ax, "p3_dp_time_loglog.png", "P3. Table vs two rows: time against n",
       "string length n (both strings), log scale", "time, ms (median of 5 runs), log scale",
       "Both versions run roughly parallel to the n^2 line (n doubles, time about x4), and the table is slower than two rows at every size.")

# P4 ns per cell
fig, ax = newfig()
for alg, mk in (("edit_table", "o"), ("edit_two_rows", "s")):
    for inp, ls in (("random4", "-"), ("disjoint", ":"), ("identical", "--")):
        d = [r for r in sel(algorithm=alg, input=inp) if r["n"] >= 250]
        ax.plot([r["n"] for r in d], [r["time_ms"] * 1e6 / r["ops"] for r in d], marker=mk, ls=ls,
                color="tab:blue" if alg == "edit_table" else "tab:orange", label=f"{alg[5:]}, {inp}")
ax.set_xscale("log")
finish(fig, ax, "p4_ns_per_cell.png", "P4. Time per DP cell (time / counted cells)",
       "string length n (both strings), log scale", "nanoseconds per cell",
       "Both versions evaluate exactly the same number of cells, but at n=4000 two rows needs about 1.9 ns per cell on random strings against 3.9 ns for the table.")

# P5 memory
fig, ax = newfig()
for alg, mk in (("edit_table", "o-"), ("edit_two_rows", "s-")):
    d = sel(algorithm=alg, input="random4"); d = [r for r in d if r["n"] >= 250]
    ax.plot([r["n"] for r in d], [r["mem_cells"] for r in d], mk, label=f"{alg[5:]}")
ax.set_xscale("log"); ax.set_yscale("log")
finish(fig, ax, "p5_memory.png", "P5. Memory used by the DP storage",
       "string length n (both strings), log scale", "ints allocated for DP storage, log scale",
       "At n=4000 the table holds 16 million ints (64 MB) and two rows hold 8 002 ints (32 KB): a factor of 2000.")

# P6 comparisons per char
fig, ax = newfig()
for inp in ("worst_aaab", "all_match", "dna4", "log_lines", "random26", "best_nomatch"):
    m = 22 if inp == "log_lines" else 100
    for alg, ls in (("search_naive", "-"), ("search_kmp", "--")):
        d = [r for r in sel(algorithm=alg, input=inp) if r["m"] == m]
        ax.plot([r["n"] for r in d], [r["ops"] / r["n"] for r in d], marker="o" if ls == "-" else "x", ls=ls, color=COL[inp],
                label=f"{alg[7:]}, {inp}")
ax.set_xscale("log"); ax.set_yscale("log")
ax.legend(fontsize=7, ncol=2)
finish(fig, ax, "p6_search_comparisons_per_char.png", "P6. Character comparisons per text character (m = 100; log_lines m = 22)",
       "text length n, log scale", "comparisons / n, log scale",
       "Naive is near 1 on typical inputs but reaches about 100 (= m) on worst_aaab and all_match, while KMP never goes above 2 (the two Naive worst-case lines overlap at 100).")

# P7 time
fig, ax = newfig()
for inp in ("worst_aaab", "all_match", "dna4", "random26"):
    for alg, ls in (("search_naive", "-"), ("search_kmp", "--")):
        d = [r for r in sel(algorithm=alg, input=inp) if r["m"] == 100]
        ax.plot([r["n"] for r in d], [r["time_ms"] for r in d], marker="o" if ls == "-" else "x", ls=ls, color=COL[inp],
                label=f"{alg[7:]}, {inp}")
ax.set_xscale("log"); ax.set_yscale("log")
finish(fig, ax, "p7_search_time.png", "P7. String search: measured time (m = 100)",
       "text length n, log scale", "time, ms (median of 5 runs), log scale",
       "On worst_aaab Naive is 30x slower than KMP at n = 1.6 million (123 ms vs 4.1 ms), while on random26 and dna4 the two are within noise of each other.")

# P8 pattern length sweep
fig, ax = newfig()
for alg, mk in (("search_naive", "o-"), ("search_kmp", "x--")):
    d = sorted([r for r in rows if r["algorithm"] == alg and r["input"] == "worst_aaab" and r["n"] == 400000], key=lambda r: r["m"])
    ax.plot([r["m"] for r in d], [r["ops"] / r["n"] for r in d], mk, label=alg[7:])
ms = [10, 25, 50, 100, 200, 400]
ax.plot(ms, ms, "k:", label="y = m")
ax.set_xscale("log"); ax.set_yscale("log")
finish(fig, ax, "p8_naive_vs_pattern_length.png", "P8. worst_aaab, text length n = 400 000: cost against pattern length",
       "pattern length m, log scale", "comparisons per text character, log scale",
       "Naive's cost per character equals m (10 up to 400) while KMP stays at 2 for every m, so the gap grows exactly with the pattern length.")
print("plots written to", OUT)
