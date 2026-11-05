package city.edit;
public final class RecursiveEditDistance implements EditDistance{
    private long calls;

    @Override
    public int distance(String a, String b){
        calls = 0;
        return rec(a, b, a.length(), b.length());
    }

    private int rec(String a, String b, int i, int j){
        calls++;
        if(i == 0) return j;
        if(j == 0) return i;
        if(a.charAt(i - 1) == b.charAt(j - 1)){
            return rec(a, b, i - 1, j - 1);
        }
        int del = rec(a, b, i - 1, j);
        int ins = rec(a, b, i, j - 1);
        int rep = rec(a, b, i - 1, j - 1);
        return 1 + Math.min(del, Math.min(ins, rep));
    }

    @Override
	public String name(){
		return "edit_recursive";
	}
    @Override
	public long operations(){
		return calls;
	}
    @Override
	public long memoryCells(){
		return 0;
	}
}
