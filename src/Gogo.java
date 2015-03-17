import java.text.ParseException;
import java.util.Hashtable;
import java.util.Set;
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

	System.out.println(CommitInfoContainer);
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
		CommitInfoContainer.put(previousCommit.getSHA1(), previousCommit);
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
