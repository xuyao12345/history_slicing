package program;

import java.util.*;

public class BlockDeletion {
	public static Set<Block> BlockDeletion(Set<Block> block, double percentage, int oldlength, int newlength){
		Set<Block> result = new HashSet<Block>();
		Iterator iterator = block.iterator();
		
		while(iterator.hasNext()){
			Block tempB = (Block) iterator.next();
			double oldperC = (double) tempB.oldC.size() / (double) oldlength;
			double newperC = (double) tempB.newC.size() / (double) newlength;
			if(oldperC < percentage && newperC < percentage){
				result.add(tempB);
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
		
		
		Set<Block> result = FindMatchedBlock.FindMatchedBlock(oldC,newC);
		System.out.println(BlockDeletion(result,0.70,3,4));
		String a="asdf";
		String b=String.format("%10s",a);
		System.out.printf(b);

	}
}
