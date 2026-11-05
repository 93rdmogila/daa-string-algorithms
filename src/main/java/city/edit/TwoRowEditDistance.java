package city.edit;
public final class TwoRowEditDistance implements EditDistance{
    private long cells;
    private long mem;

    @Override
    public int distance(String a, String b){
        int n = a.length(), m = b.length();
        cells = 0;
        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];
        mem = 2L * (m + 1);
        for(int j = 0; j <= m; j++) prev[j] = j;
        for(int i = 1; i <= n; i++){
            cur[0] = i;
            for(int j = 1; j <= m; j++){
                cells++;
                if(a.charAt(i - 1) == b.charAt(j - 1)){
                    cur[j] = prev[j - 1];
                }else{
                    cur[j] = 1 + Math.min(prev[j], Math.min(cur[j - 1], prev[j - 1]));
                }
            }
            int[] tmp = prev;
			prev = cur;
			cur = tmp;
        }
        return prev[m];
    }

    @Override
	public String name(){
		return "edit_two_rows";
	}
    @Override
	public long operations(){
		return cells;
	}
    @Override
	public long memoryCells(){
		return mem;
	}
}
