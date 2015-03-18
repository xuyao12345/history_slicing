import java.text.ParseException;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class Gogo {
	static String fileName;
	static GetInfor getinfor;
	static Hashtable<String,CommitInfo> CommitInfoContainer;
public static void main(String[] args) throws Exception {
	if(args.length<2)
		throw new Exception("arguement missing");
	 fileName=args[0];
	String FileDir=args[1];
	String CommitSHA1=new String();
	if(args.length==3)
	 CommitSHA1=args[2];
	else CommitSHA1="HEAD";
	 getinfor=new GetInfor(FileDir);
	 CommitInfoContainer=new Hashtable<String,CommitInfo>();
	CommitInfo commit=getinfor.getCommitInfor(null, CommitSHA1, fileName, null);
	if(!commit.equals(null))
	{
	CommitInfoContainer.put(commit.getSHA1(), commit);
	runRecrusive(commit);
	}
	else System.out.println("this file has no history");
	PrintHistory(commit);
//	System.out.println(CommitInfoContainer);
}

public static void PrintHistory(CommitInfo lastCommit)
{
	for (Integer a:lastCommit.getLines().keySet())
	{
		System.out.println(a+"st line: ");
		System.out.printf("commit:%s ,line number:%d ,content:%s \n",lastCommit.getSHA1().substring(0, 6),
				a,lastCommit.getLines().get(a).getContent());
		Integer lineNumber =a;
		CommitInfo tempCommit=lastCommit.deepClone(lastCommit);
		//find the histroy of line 'a'
		outer: while(true)
		{
			//commit have no previous commit or commit have more lines we care
		if(tempCommit.getPreviousCommitSHA1().equals("NoMoreCommit")||tempCommit.getLines().isEmpty())
		{
			System.out.println();
			break outer;
		}
		
		else{
			TreeMap<Integer, Line> lines=CommitInfoContainer.get(tempCommit.getPreviousCommitSHA1()).getLines();
			int temp=matchLine(lines,lineNumber,tempCommit.getPreviousCommitSHA1().substring(0, 6));
		//if we found it in the previous commit
			if(temp!=0)
			{
				lineNumber=temp;
				tempCommit=CommitInfoContainer.get(tempCommit.getPreviousCommitSHA1());
			}
			//if we found it in the previous merge commit if it has one
			else if(tempCommit.isMergecommit())
				{
					TreeMap<Integer, Line> linesMerge=CommitInfoContainer.get(tempCommit.getPreviousCommitMergedSHA1()).getLines();
					 temp=matchLine(linesMerge,lineNumber,tempCommit.getPreviousCommitMergedSHA1().substring(0, 6));
						if(temp!=0)
						{
							lineNumber=temp;
							tempCommit=CommitInfoContainer.get(tempCommit.getPreviousCommitMergedSHA1());
						}
						
				}
			//other wise find process terminate.
			else{	
				System.out.println();
				break outer;
			}
			
		}
		}
	}
}
//match line from previous commit to current commit
public static int matchLine(TreeMap<Integer, Line> lines ,int lineNumber,String commit)
{
	if(lines.isEmpty())
	{
		System.out.println();
		return 0;
	}
	 for (Integer b:lines.keySet())
	{
		
		if(lines.get(b).getFutureLineNumber()==lineNumber)
		{
		System.out.printf("commit:%s ,line number:%d ,content:%s \n",commit,
				b,lines.get(b).getContent());

		return b;
		}
	
	}
	 return 0;
}

//run recursively get previous/older commit info.
public static void runRecrusive(CommitInfo commit ) throws ParseException
{
	if(commit.getLines().isEmpty()) return;
	else if(!commit.isMergecommit())
	{
		CommitInfo previousCommit=getinfor.getCommitInfor(commit, commit.getPreviousCommitSHA1(), 
		fileName,commit.getLines().keySet());
//		if(CommitInfoContainer.containsKey(previousCommit.getSHA1()))
//			System.out.println(previousCommit.getSHA1()+" has been changed 1");
		if(!CommitInfoContainer.containsKey(previousCommit.getSHA1()))
		CommitInfoContainer.put(previousCommit.getSHA1(), previousCommit);
		else
		{
			CommitInfoContainer.get(previousCommit.getSHA1()).addLines(previousCommit.getLines());
		}
		runRecrusive(previousCommit);
	}
	else
	{
		Set<Integer> lines= new TreeSet<Integer>(commit.getLines().keySet());
		CommitInfo previousCommit=getinfor.getCommitInfor(commit, commit.getPreviousCommitSHA1(), 
				fileName,lines);

				CommitInfoContainer.put(previousCommit.getSHA1(), previousCommit);
				runRecrusive(previousCommit);
				//remove the lines have been matched
		for(Integer a: previousCommit.getLines().keySet())
				lines.remove(previousCommit.getLines().get(a).getFutureLineNumber());
					
				CommitInfo previousMergeCommit=getinfor.getCommitInfor(commit, commit.getPreviousCommitMergedSHA1(), 
						fileName,lines);

						CommitInfoContainer.put(previousMergeCommit.getSHA1(), previousMergeCommit);
						runRecrusive(previousMergeCommit);
	}
}
}
