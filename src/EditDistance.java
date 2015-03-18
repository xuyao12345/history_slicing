
public class EditDistance {
	public static int EditDistance(String a, String b){
		int distance = 0;
		int row = a.length()+1;
		int column = b.length()+1;
		int[][] Dmatrix = new int[row][column];
		Dmatrix[0][0] = 0;
		for(int x=1;x<row;x++){
			Dmatrix[x][0] = x;
		}
		for(int y=1;y<column;y++){
			Dmatrix[0][y] = y;
		}
		
		for(int x=1;x<row;x++){
			for(int y=1;y<column;y++){
				if(a.charAt(x-1)==b.charAt(y-1)){
					Dmatrix[x][y] = Dmatrix[x-1][y-1];
				}else{
					Dmatrix[x][y] = Math.min(Dmatrix[x][y-1], Dmatrix[x-1][y]) + 1;
				}
			}
		}
		distance = Dmatrix[row-1][column-1];
		return distance;
	}
	
	public static void main(String arg[]){
		System.out.println(EditDistance("panama","banana"));
		System.out.println(EditDistance("amapan","panama"));
	}
}
