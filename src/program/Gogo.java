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
	static boolean collapes=false;
	static boolean FullcommitInfo=false;
	static char pathInterver=' ';
	static String CommitSHA1=new String();
public static void main(String[] args) throws Exception {
	String fileDir=new String();
	double alfa=0.2;
	int beta=5;
	double lt=0.5;

	for(String arg:args)
	{
		if(arg.startsWith("-filePath="))
		{
			String filePath=arg.substring(10);
			int namestart=filePath.lastIndexOf("/");
			if(namestart==-1)
			{
				namestart=filePath.lastIndexOf("\\");
				pathInterver='\\';
			}
			else pathInterver='/';
			 fileName=filePath.substring(namestart+1);
			 fileDir=filePath.substring(0, namestart);
		}
		if(arg.startsWith("-BThreshold="))
		{	
			try
		{
		
			int comma=arg.indexOf(',');
			String a=arg.substring(12,comma);
			String b=arg.substring(comma+1);
			 alfa=Double.parseDouble(a);
			 beta=Integer.parseInt(b);
		}
		catch(Exception e)
		{
			 System.out.println("invaild Block Threshold Value in format");
			 System.exit(-1);
		}
			 if(alfa<0||beta<0)
			 {
				 System.out.println("invaild Block Threshold Value");
				 System.exit(-1);
			 }
		}
		if(arg.startsWith("-LThreshold="))
		{
			String temp=arg.substring(12);
			 lt=Double.parseDouble(temp);
			 if(alfa<0)
			 {
				 System.out.println("invaild line Threshold Value");
				 System.exit(-1);

			 }
		}
		if(arg.startsWith("-commit="))
		{
			CommitSHA1=arg.substring(8);
		}
		if(arg.startsWith("-fullDetails="))
		{
			String temp=arg.substring(13);
			if(temp.charAt(0)=='y'||temp.charAt(0)=='t')
				FullcommitInfo=true;
			else if(temp.charAt(0)=='f'||temp.charAt(0)=='n')
				FullcommitInfo=false;
			else {
				System.out.println("invaild details indicator");
			}

					
		}
		if(arg.startsWith("-collapse="))
		{
			String temp=arg.substring(10);
			if(temp.charAt(0)=='y'||temp.charAt(0)=='t')
				collapes=true;
			else if(temp.charAt(0)=='f'||temp.charAt(0)=='n')
				collapes=false;
			else {
				System.out.println("invaild copllapes indicator");
			}
		}
		
		
		
	}
	if(CommitSHA1==null||CommitSHA1.isEmpty())
	{
		CommitSHA1="Master";
	}
	
	GItApi api=new GItApi(fileDir,fileName,pathInterver);
	 getinfor=new GetInfor(fileDir,alfa,beta,lt,pathInterver,api);
	 CommitInfoContainer=new Hashtable<String,CommitInfo>();
//	 String VaildShowCommit=getinfor.showCommitRecurisive(CommitSHA1,fileName);
//	 VaildShowCommit=getinfor.FindSHA1(VaildShowCommit);
	CommitInfo commit=getinfor.getCommitInfor(null, CommitSHA1, fileName, null);
	CommitSHA1=commit.getSHA1();
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
		if(FullcommitInfo==false)
		System.out.printf("commit:%s ,line number:%d ,content:%s \n",lastCommit.getSHA1().substring(0, 6),
				a,lastCommit.getLines().get(a).getContent());
		else 
			System.out.printf("commit:%s, line number:%d, author:%s, date:%s content:%s \n",lastCommit.getSHA1().substring(0, 6),
					a,lastCommit.getAuthor(),lastCommit.getDate().toString(),lastCommit.getLines().get(a).getContent());
		Integer lineNumber =a;
		CommitInfo tempCommit=lastCommit.deepClone(lastCommit);
		
		//find the histroy of line 'a'
		outer: while(true)
		{
			//commit have no previous commit or commit have more lines we care
		if(tempCommit.getPreviousCommitSHA1().equals("NoMoreCommit")||tempCommit.getLines().isEmpty()&&!tempCommit.getSHA1().equals(CommitSHA1))
		{
			if(collapes==true&&tempCommit.getLines().keySet().contains(lineNumber))
			{
				if(FullcommitInfo==false)
				{
					System.out.printf("commit:%s ,line number:%d ,content:%s \n",tempCommit.getSHA1().substring(0, 6),
							lineNumber,tempCommit.getLines().get(lineNumber).getContent());		
				System.out.println();

				}
				else 
				{
					System.out.printf("commit:%s, line number:%d, author:%s, date:%s content:%s \n",tempCommit.getSHA1().substring(0, 6),
							lineNumber,tempCommit.getAuthor(),tempCommit.getDate().toString(),tempCommit.getLines().get(lineNumber).getContent());
					System.out.println();

				}
			}
				
			break outer;
		}
		
		else{
			TreeMap<Integer, Line> lines=CommitInfoContainer.get(tempCommit.getPreviousCommitSHA1()).getLines();
			int temp=matchLine(lines,lineNumber,tempCommit.getPreviousCommitSHA1(),tempCommit);
		//if we found it in the previous commit
			if(temp!=0)
			{
				lineNumber=temp;
				tempCommit=CommitInfoContainer.get(tempCommit.getPreviousCommitSHA1());
			}
			//if we found it in the previous merge commit if there is  one
			else if(tempCommit.isMergecommit())
				{
					TreeMap<Integer, Line> linesMerge=CommitInfoContainer.get(tempCommit.getPreviousCommitMergedSHA1()).getLines();
					 temp=matchLine(linesMerge,lineNumber,tempCommit.getPreviousCommitMergedSHA1(),tempCommit);
						if(temp!=0)
						{
							lineNumber=temp;
							tempCommit=CommitInfoContainer.get(tempCommit.getPreviousCommitMergedSHA1());
						}
						
				}
			//otherwise find process terminate.
			else{	
				if(collapes==true&&tempCommit.getLines().keySet().contains(lineNumber)&&!tempCommit.getSHA1().equals(CommitSHA1))
				{
					if(FullcommitInfo==false)
					{
						System.out.printf("commit:%s ,line number:%d ,content:%s \n",tempCommit.getSHA1().substring(0, 6),
								lineNumber,tempCommit.getLines().get(lineNumber).getContent());		
					System.out.println();

					}
					else 
					{
						System.out.printf("commit:%s, line number:%d, author:%s, date:%s content:%s \n",tempCommit.getSHA1().substring(0, 6),
								lineNumber,tempCommit.getAuthor(),tempCommit.getDate().toString(),tempCommit.getLines().get(lineNumber).getContent());
						System.out.println();

					}
				}
				break outer;
			}
			
		}
		}
	}
}
//match line from previous commit to current commit
public static int matchLine(TreeMap<Integer, Line> lines ,int lineNumber,String commit,CommitInfo futureCommit)
{
	if(lines.isEmpty())
	{
		System.out.println();
		return 0;
	}
	CommitInfo temp=CommitInfoContainer.get(commit);

	if(collapes==false)
	{
	for (Integer b:lines.keySet())
	{
		 
		
		if(lines.get(b).getFutureLineNumber()==lineNumber)
		{
			//dont print the lines has then same conent when collapes indicated.
			if(collapes==true&&futureCommit.getLine(lineNumber).getContent().equals(lines.get(b).getContent()));
			
			else
			{
			if(FullcommitInfo==false)
		System.out.printf("commit:%s ,line number:%d ,content:%s \n",commit.substring(0, 6),
				b,lines.get(b).getContent());
			else
			{
				System.out.printf("commit:%s, line number:%d, author:%10s, date:%s content:%s \n",commit.substring(0, 6),b,temp.getAuthor(),
						temp.getDate(),lines.get(b).getContent());
			}
			}

		return b;
		}
	
	}
}
	 else{
	 for (Integer b:lines.keySet())
		{
			 
			
			if(lines.get(b).getFutureLineNumber()==lineNumber)
			{
			//	System.out.println(lines.get(b));
				//dont print the lines has then same conent when collapes indicated.
				if(!futureCommit.getLine(lineNumber).getContent().equals(lines.get(b).getContent())&&!futureCommit.getSHA1().equals(CommitSHA1))
				{
				if(FullcommitInfo==false)
			System.out.printf("commit:%s ,line number:%d ,content:%s \n",futureCommit.getSHA1().substring(0, 6),
					lineNumber,futureCommit.getLine(lineNumber).getContent());
				else
				{
					System.out.printf("commit:%s, line number:%d, author:%10s, date:%s content:%s \n",futureCommit.getSHA1().substring(0, 6)
							,lineNumber,futureCommit.getAuthor(),
							futureCommit.getDate(),futureCommit.getLine(lineNumber).getContent());
				}
				}

			return b;
			}
		
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
