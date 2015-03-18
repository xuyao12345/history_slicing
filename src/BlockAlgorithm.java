import java.util.TreeMap;


public class BlockAlgorithm {
	public static TreeMap<Integer,Line> BlockAlgorithm(TreeMap oldcommit, TreeMap newcommit,int editdistance){
		Integer[] oldcommitK = (Integer[]) oldcommit.keySet().toArray(new Integer[oldcommit.size()]);
		Integer[] newcommitK = (Integer[]) newcommit.keySet().toArray(new Integer[newcommit.size()]);
		
		int row = oldcommit.size();
		int column = newcommit.size();
		int[][] Dmatrix = new int[row][column];
		for(int x=0;x<row;x++){
			for(int y=0;y<column;y++){
				Line oldcommitL = (Line) oldcommit.get(oldcommitK[x]);
				Line newcommitL = (Line) newcommit.get(newcommitK[y]);
				Dmatrix[x][y] = EditDistance.EditDistance(oldcommitL.getContent(), newcommitL.getContent());
			}
		}
		int[][] sortedED = sortDmatrix(Dmatrix,editdistance);
		TreeMap<Integer,Line> Changed = new TreeMap<Integer,Line>();
		for(int a=0;a<sortedED.length;a++){
			Line oldcommitLtemp = (Line) oldcommit.get(oldcommitK[sortedED[a][0]]);
			Line temp = new Line(oldcommitLtemp.getContent(),newcommitK[sortedED[a][1]]);
			Changed.put(oldcommitK[sortedED[a][0]], temp);
		}
		
		return Changed;
	}
	
	public static int[][] sortDmatrix(int[][] Dmatrix, int editdistance){
		int[][] result = new int[Math.min(Dmatrix.length, Dmatrix[0].length)][3];
		boolean temp = true;
		int countX = 0;
		
		for(int distance=0; distance<=editdistance;distance++){
			for(int x=0;x<Dmatrix.length;x++){
				for(int y=0;y<Dmatrix[0].length;y++){
					if(Dmatrix[x][y] == distance){
						for(int x2=0;x2<countX;x2++){
							if(x==result[x2][0]||y==result[x2][1]){
								temp = false;
								break;
							}
						}
						if(temp==true){
							result[countX][0] = x;
							result[countX][1] = y;
							result[countX][2] = distance;
							countX++;
						}
						temp = true;
					}
				}
			}
		}
		
		int[][] returnresult = new int[countX][3];
		for(int x=0;x<countX;x++){
			for(int y=0;y<3;y++){
				returnresult[x][y] = result[x][y];
			}
		}
		
		return returnresult;
	}
	
	public static void main(String args[]){
		TreeMap<Integer,Line> oldC = new TreeMap<Integer,Line>();
		TreeMap<Integer,Line> newC = new TreeMap<Integer,Line>();
		oldC.put(1,new Line("abcdefgh",1));
		oldC.put(2,new Line("abc",2));
		oldC.put(3,new Line("lianjiayi",3));
		
		newC.put(1,new Line("ab",1));
		newC.put(2,new Line("abcde",2));
		newC.put(3,new Line("weianqi",3));
		newC.put(4,new Line("abc",4));
		
		
		TreeMap<Integer,Line> result = BlockAlgorithm(oldC,newC,4);
		System.out.println(result);
	}
}
