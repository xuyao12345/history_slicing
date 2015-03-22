package program;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;

public class GetInfor {
public String path;

GetInfor(String path)
{
	this.path=path;
}

public String showCommit(String commit,String fileName)
{
	String Showcommand="git show "+commit+" "+fileName;
	String output=new String();
	 output=executeCommand(Showcommand);
	if(!(output==null))
	{
	 if(output.startsWith("fatal"))
			return "NoMoreCommit";
	 else return output;
	}
	else return "UnchangedCommit";
		
}
public String showPreviousCommit(String commit)
{
	String Showcommand="git show "+commit+"^";
	String output=new String();
	 output=executeCommand(Showcommand);
	 if(output.startsWith("fatal"))
			return "NoMoreCommit";
	 else return output;
		
}

//public String showCommitRecurisive(String commit,String fileName)
//{
//	String Showcommand="git show "+commit+" "+fileName;
//	String output=new String();
//	 output=executeCommand(Showcommand);
//	if(!(output==null))
//	{
//	 if(output.startsWith("fatal"))
//			return "NoMoreCommit";
//	 else return output;
//	}
//	else return showCommitRecurisive( commit+"^",fileName);
//		
//}

 public String FindSHA1(String outputFromShow)
 {
	 int SHA1Start=outputFromShow.indexOf("commit");
		int SHA1Finish=outputFromShow.indexOf("\n",SHA1Start+1);
		return outputFromShow.substring(SHA1Start+7, SHA1Finish);
 }
 public Date FindDate(String outputFromShow) throws ParseException
 {
		int dateStart=outputFromShow.indexOf("Date: ");
		int dateFinish=outputFromShow.indexOf("+",dateStart);
		String date=outputFromShow.substring(dateStart+8, dateFinish);
		//Date:   Tue Mar 3 20:14:11 2015 +0000
		DateFormat format = new SimpleDateFormat("EEE MMMM d H:m:s yyyy", Locale.ENGLISH);
			Date dateCovered = format.parse(date);
			return dateCovered;
 }
 
 public String findAuthor(String outputFromShow)
 {
	 int authorStart=outputFromShow.indexOf("Author:");
		int authorFinish=outputFromShow.indexOf("\n",authorStart);
		String author=outputFromShow.substring(authorStart+8, authorFinish);
		return author;
 }
//lineNumbers are the number of lines that we care in the next(newer) commit,we need find the correlation to the current commit. 
public final CommitInfo getCommitInfor(CommitInfo NextCommit,String commit, String fileName,Set<Integer> lineNumbers) throws ParseException, MissingObjectException, IncorrectObjectTypeException, NullPointerException, IOException
{
	 CommitInfo info=new CommitInfo();
	String output=showCommit(commit,"");
	if(output.equals("NoMoreCommit"))
		return null;
	boolean unchanged=false;
	
	if(NextCommit!=null&&showCommit(NextCommit.getSHA1(),fileName).equals("UnchangedCommit"))
	{
		//get the unchanged commit informaiton;
		// output=showCommit(commit,"");
		 unchanged=true;

	}
	String SHA1=FindSHA1(output);
//	System.out.println(SHA1);
	info.setSHA1(SHA1);
	boolean isMerge;
	
	if (output.contains("Merge branch"))	{
		isMerge=true;
		int previousCommitStart=output.indexOf("Merge:");
		int previousCommitFinish=output.indexOf(" ",previousCommitStart+7);
		String previousCommit =output.substring(previousCommitStart+7, previousCommitFinish);
	
		String Showcommand="git show "+previousCommit+" "+fileName;
		previousCommit=executeCommand(Showcommand);
		previousCommit=FindSHA1(previousCommit);
		info.setPreviousCommitSHA1(previousCommit);
		
		info.setPreviousCommitSHA1(previousCommit);
		//System.out.println("previousCommit: "+previousCommit);
		info.setMergecommit(isMerge);
		int previousCommitMergedFinish=output.indexOf("\n",previousCommitFinish+1);
		String priviouseCommitMerged=output.substring(previousCommitFinish+1, previousCommitMergedFinish);
		Showcommand="git show "+priviouseCommitMerged+" "+fileName;
		 priviouseCommitMerged=executeCommand(Showcommand);
		 priviouseCommitMerged=FindSHA1(priviouseCommitMerged);
		info.setPreviousCommitMergedSHA1(priviouseCommitMerged);
		//System.out.println("priviouseCommitMerged: "+priviouseCommitMerged);
		}
	else{
		isMerge=false;
		info.setMergecommit(isMerge);
		//add the previousCommitSHA1
		String previouscommit=showPreviousCommit(commit);
		if(previouscommit.equals("NoMoreCommit"))
			info.setPreviousCommitSHA1(previouscommit);
		else
		{
		String preSHA1=FindSHA1(previouscommit);
		info.setPreviousCommitSHA1(preSHA1);
		}
		}

	
	String author=findAuthor(output);
	info.setAuthor(author);
		Date dateCovered = FindDate(output);
		info.setDate(dateCovered);
		
		if(unchanged==true)
		{
			for(Integer x : lineNumbers)
			{
				info.getLines().put(x, new Line(NextCommit.getLine(x).getContent(),x));
			}
			System.out.println(info);

			return info;
		}
		//System.out.println(dateCovered);	
		//System.out.println(isMerge+" "+author);
		
		if(lineNumbers==null)
		{
			executeCommand("git checkout "+commit);
			String file=executeCommand("cat "+fileName);
			String lines[] = file.split("\n");
			TreeMap<Integer,Line> Setlines= new TreeMap<Integer,Line>();
			int lineCode=1;
				for (String a : lines)
				{
					Setlines.put(lineCode,new Line(a, 0));
					lineCode++;
				}
			info.setLines(Setlines);
		//	System.out.println(Setlines.toString());
			System.out.println(info);
			return info;
		}
		else
		{
			String diffoutput=executeCommand("git diff "+NextCommit.getSHA1()+" "+commit);
			Vector<TreeMap<Integer,Line>> result=matchUnchanged(diffoutput,lineNumbers,NextCommit);
			info.addLines(result.get(0));	
			Set<Integer> tempLineNumber= new TreeSet<Integer>(lineNumbers);
			tempLineNumber.removeAll(result.get(0).keySet());
			int oldFilesize=GItApi.getSize(path,fileName, commit);
			int newFilesize=GItApi.getSize(path,fileName, NextCommit.getSHA1());
			System.out.println("old:" +oldFilesize+"new"+ newFilesize);
			TreeMap<Integer,Line> temp=BlockAlgorithm.BlockAlgorithm(
					result.get(1),result.get(2),0.9);
			for(Integer a: temp.keySet()){
				if(lineNumbers.contains(a))
				info.addline(a, temp.get(a));
			}
			System.out.println(info);
			return info;
			
		}



	 
}

// the lines not in diff report that means have not be changed including the changes of lineNumber.
public final Vector<TreeMap<Integer,Line>>  matchUnchanged(String diffOutPut,Set<Integer> lineNumbers,CommitInfo NextCommit)
{
	TreeMap<Integer,Line>   Unchanged= new TreeMap<Integer,Line>();
	TreeMap<Integer,Line> InOlderCommit= new TreeMap<Integer,Line>();
	TreeMap<Integer,Line> InNewerCommit= new TreeMap<Integer,Line>();
	Vector<TreeMap<Integer,Line>> resultSet=new Vector<TreeMap<Integer,Line>>();
	resultSet.add(Unchanged);
	resultSet.add(InOlderCommit);
	resultSet.add(InNewerCommit);
	Set<Integer> lines=new TreeSet<Integer>(lineNumbers);
	DiffRange spans=FindDiffSpan(diffOutPut);
	if(diffOutPut.indexOf("@@")==-1)
		return resultSet ;

	diffOutPut=diffOutPut.substring(diffOutPut.indexOf("@@"));
	String[] SplitedOutPut =diffOutPut.split("\n");
	// minus represents current commit
	int minusCounter=0;
	//plus represents next commit.
	int plusCounter=0;
	Iterator<Integer> minusIter=spans.minusRange.iterator();
	Iterator<Integer> plusIter=spans.plusRange.iterator();
	
	for(int c=0;c<SplitedOutPut.length;c++)
	{
		String a=SplitedOutPut[c];
		if (a.matches("@@ -[0-9]*,[0-9]* \\+[0-9]*,[0-9]* @@.*"))
		{
			Integer plusTemp= plusIter.next();
			Integer minusTemp=minusIter.next();
			if(plusTemp>plusCounter)
			{
				Set<Integer> offSets= findNumberInRangeOffSet(lineNumbers,minusTemp,minusCounter);
				for(Integer offSet: offSets)
				{
					Line LineTemp=new Line(NextCommit.getLine(offSet+minusCounter).getContent(),offSet+minusCounter);
					Unchanged.put(offSet+plusCounter, LineTemp);
					
				}
			}
			plusCounter=plusTemp-1;
			minusCounter=minusTemp-1;
		}
		//lines belong to future commit
		else if(a.charAt(0)=='-')
		{
			minusCounter++;
			Line LineTemp=new Line(a.substring(1),minusCounter);
			InNewerCommit.put(minusCounter, LineTemp);
			//collect the changed informaiton
		}
		//lines belong to current commit
		else if(a.charAt(0)=='+')
		{
			plusCounter++;
			//collect the changed informaiton
			Line LineTemp=new Line(a.substring(1),plusCounter);
			InOlderCommit.put(plusCounter, LineTemp);
		}
		else if(a.charAt(0)==' ')
		{
			if(lineNumbers.contains((minusCounter+1)))
			{
			Line LineTemp=new Line(NextCommit.getLine(minusCounter+1).getContent(),minusCounter+1);
			Unchanged.put(plusCounter+1, LineTemp);
			for(int x=1;x<=minusCounter+1;x++)
				lines.remove(x);
			}
			plusCounter++;
			minusCounter++;
		}
		if(c==SplitedOutPut.length-1)
		{
			for(Integer x: lines)
			{
				Line LineTemp=new Line(NextCommit.getLine(x).getContent(),x);
				Unchanged.put(++plusCounter, LineTemp);	
			}
		}
		
		
	}

	//System.out.println("InOlderCommit: "+InOlderCommit);
	//System.out.println("InNewerCommit: "+InNewerCommit);

	return resultSet;
	
}


public Set<Integer> findNumberInRangeOffSet(Set<Integer> lineNumbers,int upperBond, int lowerbond)
{
	 Set<Integer> ToReturn =new TreeSet<Integer>();
	 for(Integer temp: lineNumbers)
	 {
		 if(temp<upperBond&&temp>lowerbond)
			 ToReturn.add(temp-lowerbond);
		 
	 }
	 return ToReturn;
}

public DiffRange FindDiffSpan(String diffOutPut)
{
	DiffRange range=new DiffRange();
 String[] lines=diffOutPut.split("\n");	
 for(String line : lines)
 {
	if(line.matches("@@ -[0-9]*,[0-9]* \\+[0-9]*,[0-9]* @@.*"))
	{
		 int minusStart=line.indexOf("-")+1;
		 int minusEnd=line.indexOf(",",minusStart);
		int minus=Integer.parseInt(line.substring(minusStart,minusEnd));
		range.minusRange.add(minus);
		
		 int plusStart=line.indexOf("+",minusEnd)+1;
		 int plusEnd=line.indexOf(",",plusStart);
		int plus=Integer.parseInt(line.substring(plusStart,plusEnd));
		range.plusRange.add(plus);
	}
 }
 return range;
}


public  String executeCommand(String command) {

	StringBuffer output = new StringBuffer();
File dir=new File(path);
	Process p;
	try {
		p = Runtime.getRuntime().exec(command,null,dir);
		p.waitFor();
		BufferedReader reader = 
                        new BufferedReader(new InputStreamReader(p.getInputStream()));

                    String line = "";			
		while ((line = reader.readLine())!= null) {
			output.append(line + "\n");
		}
	if(output.toString().isEmpty())
	{
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String result=new String();
				result=stdError.readLine();
		stdError.close();
		return result;
	}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return output.toString();
	
	
}
//public static void main(String[] args) throws ParseException
//{
//	GetInfor getinfor= new GetInfor("/Users/sea/Downloads/bash2");
//	CommitInfo newCommit=	getinfor.getCommitInfor(null, "HEAD", "original", null);
////	System.out.println(lastCommit);
//	TreeSet<Integer> number=new TreeSet<Integer>(); 
//	for( int a=0;a<28;a++)
//	{
//		number.add(a+1);
//	}
//	CommitInfo olderCommit=	getinfor.getCommitInfor(newCommit, "HEAD^^", "original", number);
//
//	System.out.println(olderCommit);
//}
}
