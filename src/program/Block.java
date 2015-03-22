package program;

import java.util.TreeMap;

public class Block {
	TreeMap<Integer,Line> oldC = new TreeMap<Integer,Line>();
	TreeMap<Integer,Line> newC = new TreeMap<Integer,Line>();
	
	public Block(TreeMap<Integer, Line> oldC, TreeMap<Integer, Line> newC) {
		super();
		this.oldC = oldC;
		this.newC = newC;
	}
	
	public TreeMap<Integer, Line> getOldC() {
		return oldC;
	}
	
	public void setOldC(TreeMap<Integer, Line> oldC) {
		this.oldC = oldC;
	}
	
	public TreeMap<Integer, Line> getNewC() {
		return newC;
	}
	
	public void setNewC(TreeMap<Integer, Line> newC) {
		this.newC = newC;
	}

	@Override
	public String toString() {
		return "Block [oldC=" + oldC + ", newC=" + newC + "]";
	}

}
