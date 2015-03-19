import java.util.*;
public class DeletLargeBlock {
	public static TreeMap<Integer,Line> deletelargeBlock(TreeMap file)
	{
		///first part : record the position to delete
		Integer[] oldcommitK = (Integer[]) file.keySet().toArray(new Integer[file.size()]);
		int threshold = 2;  // calculate threshold
		int count = 1;
		Integer start, end;
		start = oldcommitK[0];
		Integer temp = oldcommitK[0];
		TreeMap<Integer,Integer> DeleteContent = new TreeMap<Integer,Integer>();
		for(int i=1;i<file.size();i++)
		{
			if((++temp) == oldcommitK[i])
			{
				count++;
				if((i+1) == file.size())
				{
					if(count >= threshold)
					{
						end = start+count-1;
						DeleteContent.put(start, end);
						//System.out.println("lllll");
						break;
					}
				}
				continue;
			}
			else
			{
				if(count >= threshold)
				{
					end = start+count-1;
					DeleteContent.put(start, end);
				}
					start = oldcommitK[i];
					temp = oldcommitK[i];
					count = 1;
			}
		}
		
		//second part : return file changed
		Integer[] tempdelete = (Integer[]) DeleteContent.keySet().toArray(new Integer[DeleteContent.size()]);
		Integer first, last;
		
		for (int i=0; i<DeleteContent.size(); i++)
		{
			first =  tempdelete[i];
			last = DeleteContent.get(first);
			for(Integer number=first; number<=last; number++)
			{
				file.remove(number);
			}
		}
		return file;
	}
	/*======================testcase=====================*/
	public static void main(String args[])
	{
		TreeMap<Integer,Line> oldC = new TreeMap<Integer,Line>();
		TreeMap<Integer,Line> newC = new TreeMap<Integer,Line>();
		oldC.put(1,new Line("abcdefgh",1));
		oldC.put(2,new Line("abc",2));
		oldC.put(4,new Line("lianjiayi",4));
		oldC.put(6,new Line("lianjiayi",6));
		oldC.put(7,new Line("lianjiayi",7));
		
		System.out.println(deletelargeBlock(oldC));
	}

}
