import java.util.*;
public class DeletLargeBlock {
	public static TreeMap<Integer,Line> deletelargeBlock(TreeMap file)
	{
		///first part : record the position to delete
		Integer[] oldcommitK = (Integer[]) file.keySet().toArray(new Integer[file.size()]);
		int threshold = 2;  // calculate threshold
		int count = 0;
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
					if(count >= threshold);
					end = start+count;
					DeleteContent.put(start, end);
				}
			}
			else
			{
				if(count >= threshold)
				{
					end = start+count;
					DeleteContent.put(start, end);
				}
					start = oldcommitK[i];
					temp = oldcommitK[i];
					count = 0;
			}
		}
		
		//second part : return file changed
		Integer[] tempdelete = (Integer[]) DeleteContent.keySet().toArray(new Integer[DeleteContent.size()]);
		Integer first, last;
		
		for (int i=0; i<DeleteContent.size(); i++)
		{
			first =  i;
			last = DeleteContent.get(i);
			for(int number=first; number<=last; number++)
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
		oldC.put(3,new Line("lianjiayi",3));
		
		System.out.println(deletelargeBlock(oldC));
	}

}
