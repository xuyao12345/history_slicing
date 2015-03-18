import java.util.Date;
import java.util.TreeMap;
import java.util.Map;

import com.rits.cloning.Cloner;



public class CommitInfo {
private String SHA1;
private boolean isMergecommit;
private String previousCommitSHA1;
private String previousCommitMergedSHA1;
private TreeMap<Integer,Line> Lines;// integer is current lineNumber
private String author;
private Date date;


public CommitInfo() {

	this.Lines=new TreeMap<Integer,Line>();
	
}

public CommitInfo deepClone(CommitInfo info)
{
	 
	Cloner cloner= new Cloner();
	CommitInfo clone=cloner.deepClone(info);
	return clone;
}

public String getAuthor() {
	return author;
}



public void setAuthor(String author) {
	this.author = author;
}






public Date getDate() {
	return date;
}



public void setDate(Date date) {
	this.date = date;
}



public String getSHA1() {
	return SHA1;
}



public void setSHA1(String sHA1) {
	SHA1 = sHA1;
}



public boolean isMergecommit() {
	return isMergecommit;
}



public void setMergecommit(boolean isMergecommit) {
	this.isMergecommit = isMergecommit;
}



public String getPreviousCommitSHA1() {
	return previousCommitSHA1;
}



public void setPreviousCommitSHA1(String previousCommitSHA1) {
	this.previousCommitSHA1 = previousCommitSHA1;
}



public CommitInfo(String sHA1, boolean isMergecommit,
		String previousCommitSHA1, String previousCommitMergedSHA1,
		TreeMap<Integer, Line> Lines,String author, Date date) {
if (isMergecommit==true)
{
SHA1 = sHA1;
	this.isMergecommit = isMergecommit;
	this.previousCommitSHA1 = previousCommitSHA1;
	this.previousCommitMergedSHA1 = previousCommitMergedSHA1;
	this.Lines = Lines;
	this.author = author;
	this.date = date;
}
else throw new RuntimeException("it is not a merged commit");
}

public CommitInfo(String sHA1, boolean isMergecommit,
		String previousCommitSHA1,
		TreeMap<Integer, Line> Lines,String author, Date date) {
if (isMergecommit!=true)
{
SHA1 = sHA1;
	this.isMergecommit = isMergecommit;
	this.previousCommitSHA1 = previousCommitSHA1;
	this.Lines = Lines;
	this.author = author;
	this.date = date;
}
else throw new RuntimeException("it is a merged commit");
}


public String getPreviousCommitMergedSHA1()  {
	if(this.isMergecommit()==true)
	return previousCommitMergedSHA1;
	else throw new RuntimeException("it is not a merged commit");
}



public void setPreviousCommitMergedSHA1(String previousCommitMergedSHA1) {
	if(this.isMergecommit()==true)
		this.previousCommitMergedSHA1 = previousCommitMergedSHA1;
	else throw new RuntimeException("it is not a merged commit");
}



public TreeMap<Integer, Line> getLines() {
	return Lines;
}



public void setLines(TreeMap<Integer, Line> lines) {
	this.Lines = lines;
}

public void addLines(Map<? extends Integer, ? extends Line> coll) {
this.Lines.putAll(coll);
}

public Line getLine(int lineNumber)
{
	return this.Lines.get(lineNumber);
}


public void addline(int lineNumber,Line line)
{
	this.Lines.put(lineNumber, line);
}



@Override
public String toString() {
	return "CommitInfo [SHA1=" + SHA1 + ",\n isMergecommit=" + isMergecommit
			+ "\n, previousCommitSHA1=" + previousCommitSHA1
			+ "\n, previousCommitMergedSHA1=" + previousCommitMergedSHA1
			+ ",\n Lines=\n " + Lines + ", author=" + author + ", date=" + date
			+ "]\n";
}
}


