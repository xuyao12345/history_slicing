package program;
import java.io.IOException;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;

import com.rits.cloning.Cloner;


public class Gogo {
	static String fileName;
	static GetInfor getinfor;
	static Hashtable<String,CommitInfo> CommitInfoContainer;
public static void main(String[] args) throws Exception {
	if(args.length<1)
		throw new Exception("arguement missing");
	int namestart=args[0].lastIndexOf("/");
	if(namestart==-1)
	{
		namestart=args[0].lastIndexOf("\\");	
	}
	 fileName=args[0].substring(namestart+1);
	String FileDir=args[0].substring(0, namestart);
	
	String CommitSHA1=new String();
	if(args.length==3)
	 CommitSHA1=args[1];
	else CommitSHA1="MASTER";
	 getinfor=new GetInfor(FileDir);
	 CommitInfoContainer=new Hashtable<String,CommitInfo>();
//	 String VaildShowCommit=getinfor.showCommitRecurisive(CommitSHA1,fileName);
//	 VaildShowCommit=getinfor.FindSHA1(VaildShowCommit);
	CommitInfo commit=getinfor.getCommitInfor(null, CommitSHA1, fileName, null);
	//go back to very beginning 
	getinfor.executeCommand("git checkout "+CommitSHA1);
	//CommitInfo lastCommit=addUnchangedCommit(commit,CommitSHA1,true);
	if(!commit.equals(null))
	{
	CommitInfoContainer.put(commit.getSHA1(), commit);
	runRecrusive(commit);
	}
	else System.out.println("this file has no history");
//	if(lastCommit.getSHA1()!=null)
//	{
//		for (Integer a:commit.getLines().keySet())
//		 {
//			commit.getLine(a).setFutureLineNumber(a);
//		 }
//	PrintHistory(lastCommit);
//	}
//	else 
		PrintHistory(commit);
	//System.out.println(CommitInfoContainer);
}
// add the commits that didnt changed the target file
public static CommitInfo addUnchangedCommit(CommitInfo olderCommit,String newerCommit,boolean firstCommit)
{
	CommitInfo toBeReturn= new CommitInfo();
	TreeMap<String,CommitInfo> result=new TreeMap<String,CommitInfo>();
	while (true){
	String Showresult=getinfor.executeCommand("git show "+newerCommit);
	 String SHA1=getinfor.FindSHA1(Showresult);
	 if(SHA1.contains(olderCommit.getSHA1()))
	 {
		 for(String a: result.keySet())
		 {
			 String  temp=result.get(a).getSHA1();
			 String next=getinfor.executeCommand("git show "+temp+"^");
			 result.get(a).setPreviousCommitSHA1(getinfor.FindSHA1(next));
		 }
		 CommitInfoContainer.putAll(result);
		 return toBeReturn;
	 }
	 else 
	 {
		 CommitInfo temp=new CommitInfo();
		 Cloner clone=new Cloner();
		 temp.setSHA1(SHA1);
		 temp.setMergecommit(false);
		 temp.addLines(clone.deepClone(olderCommit.getLines()));
		 
	
		 
		 // add lines and date
		 result.put(temp.getSHA1(),temp);
		 newerCommit=newerCommit+"^";
		 if(firstCommit)
		 {
			 firstCommit=false;
			 toBeReturn=temp; 
		 }
		 else
		 {
			 for (Integer a:temp.getLines().keySet())
			 {
				 temp.getLine(a).setFutureLineNumber(a);
			 }
		 }
	 }
	 
	 }
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
public static void runRecrusive(CommitInfo commit ) throws ParseException, MissingObjectException, IncorrectObjectTypeException, NullPointerException, IOException
{
	if(commit.getLines().isEmpty()||commit.getPreviousCommitSHA1().contains("NoMoreCommit")) return;
	else if(!commit.isMergecommit())
	{
		CommitInfo previousCommit=getinfor.getCommitInfor(commit, commit.getPreviousCommitSHA1(), 
		fileName,commit.getLines().keySet());
		//	CommitInfo Gogo.addUnchangedCommit(CommitInfo olderCommit, String newerCommit, boolean firstCommit)
			//addUnchangedCommit(previousCommit,);

		
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
