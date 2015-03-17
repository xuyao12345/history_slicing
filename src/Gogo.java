import java.text.ParseException;
import java.util.Hashtable;
import java.util.Set;


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
	CommitInfoContainer.put(commit.getSHA1(), commit);
	runRecrusive(commit);

}
//run recursively get previous/older commit info.
public static void runRecrusive(CommitInfo commit ) throws ParseException
{
	if(commit.getLines().isEmpty());
	else if(!commit.isMergecommit())
	{
		CommitInfo previousCommit=getinfor.getCommitInfor(commit, commit.getPreviousCommitSHA1(), 
		fileName,commit.getLines().keySet());
		CommitInfoContainer.put(previousCommit.getSHA1(), previousCommit);
		runRecrusive(previousCommit);
	}
	else
	{
		Set<Integer> lines=commit.getLines().keySet();
		CommitInfo previousCommit=getinfor.getCommitInfor(commit, commit.getPreviousCommitSHA1(), 
				fileName,lines);
				CommitInfoContainer.put(previousCommit.getSHA1(), previousCommit);
				runRecrusive(previousCommit);
			
				lines.removeAll(previousCommit.getLines().keySet());
						
				CommitInfo previousMergeCommit=getinfor.getCommitInfor(commit, commit.getPreviousCommitMergedSHA1(), 
						fileName,lines);
						CommitInfoContainer.put(previousMergeCommit.getSHA1(), previousMergeCommit);
						runRecrusive(previousMergeCommit);
	}
}
}
