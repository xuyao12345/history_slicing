package program;

import java.util.*;

public class FindMatchedBlock {
	public static Set<Block> FindMatchedBlock(TreeMap oldcommit, TreeMap newcommit){
		Integer[] oldcommitK = (Integer[]) oldcommit.keySet().toArray(new Integer[oldcommit.size()]);
		Integer[] newcommitK = (Integer[]) newcommit.keySet().toArray(new Integer[newcommit.size()]);
		int oldtag = 1;
		int countx = 0;
		int newtag = 1;
		int county = 0;
		boolean temp = false;
		Set<Block> result = new HashSet<Block>();
		
		while(true){
			if(oldtag>=oldcommitK.length && newtag>=newcommitK.length){
				break;
			}
			TreeMap<Integer,Line> oldresult = new TreeMap<Integer,Line>();
			TreeMap<Integer,Line> newresult = new TreeMap<Integer,Line>();
			
			for(int x=countx;x<oldcommitK.length;x++){
				if(oldcommitK[x]==oldtag){
					oldtag++;
					oldresult.put(oldcommitK[x],(Line) oldcommit.get(oldcommitK[x]));
					temp = true;
					countx++;
				}else{
					oldtag++;
					break;
				}
			}
			for(int y=county;y<newcommitK.length;y++){
				if(newcommitK[y]==newtag){
					newtag++;
					newresult.put(newcommitK[y],(Line) newcommit.get(newcommitK[y]));
					temp = true;
					county++;
				}else{
					newtag++;
					break;
				}
			}
			if(temp == true){
				Block resultB = new Block(oldresult,newresult);
				result.add(resultB);
				temp = false;
			}
		}
		return result;
	}
	
	public static void main(String args[]){
		TreeMap<Integer,Line> oldC = new TreeMap<Integer,Line>();
		TreeMap<Integer,Line> newC = new TreeMap<Integer,Line>();
		oldC.put(1,new Line("abcdefgh",1));
		oldC.put(2,new Line("abc",2));
		oldC.put(4,new Line("lianjiayi",3));
		
		newC.put(1,new Line("ab",1));
		newC.put(3,new Line("abcde",2));
		newC.put(4,new Line("weianqi",3));
		newC.put(5,new Line("abc",4));
		
		
		Set<Block> result = FindMatchedBlock(oldC,newC);
		System.out.println(result);
	}
}
