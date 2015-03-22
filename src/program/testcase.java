package program;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

public class testcase {

	@Test
	public void TestEditDistance()
	{
		EditDistance edit = new EditDistance();
		String string1 = "abc";
		String string2 = "abcd";
		assertEquals(edit.editDistance(string1,string2),1);
	}
	@Test
	public void TestDeletLargeBlock()
	{
			TreeMap<Integer,Line> oldC = new TreeMap<Integer,Line>();
			TreeMap<Integer,Line> testcase = new TreeMap<Integer,Line>();
			DeletLargeBlock deletetreemap = new DeletLargeBlock();
			oldC.put(1,new Line("abcdefgh",1));
			oldC.put(2,new Line("abc",2));
			oldC.put(4,new Line("lianjiayi",4));
			oldC.put(6,new Line("lianjiayi",6));
			oldC.put(7,new Line("lianjiayi",7));
			testcase.put(4,new Line("lianjiayi",4));
			String a=deletetreemap.deletelargeBlock(oldC).toString();
			String b=testcase.toString();
		//	assertEquals(deletetreemap.deletelargeBlock(oldC),testcase);
			assertTrue(a.equals(b));
	}
	@Test
	public void TestBlockAlgorithm() {
		TreeMap<Integer,Line> oldC = new TreeMap<Integer,Line>();
		TreeMap<Integer,Line> newC = new TreeMap<Integer,Line>();
		TreeMap<Integer,Line> testcase = new TreeMap<Integer,Line>();
		TreeMap<Integer,Line> empty = new TreeMap<Integer,Line>();
		TreeMap<Integer,Line> emptyD = new TreeMap<Integer,Line>();
		BlockAlgorithm block = new BlockAlgorithm();
		oldC.put(1,new Line("abcdefgh",1));
		oldC.put(2,new Line("abc",2));
		oldC.put(3,new Line("lianjiayi",3));
		
		newC.put(1,new Line("ab",1));
		newC.put(2,new Line("abcde",2));
		newC.put(3,new Line("abc",3));
		newC.put(4,new Line("abc",4));
		
		testcase.put(2, new Line("abc",3));
		TreeMap<Integer,Line> result = block.BlockAlgorithm(oldC,newC,0.497);
		String a = result.toString();
		String b = testcase.toString();
		System.out.println(a);
		assertTrue(a.equals(b));
		
		String Empty = empty.toString();
		String EmptyD = block.BlockAlgorithm(oldC, emptyD, 0.497).toString();
		assertTrue(EmptyD.equals(Empty));
		
	}
	@Test
	public void TestShowCommit(){
		GetInfor a=new GetInfor("/Users/peer_free/Desktop/TestCase3");
		String result=a.showCommit("992680", "test.txt");
		String result1=a.showCommit("1", "test.txt");
		String result2=a.showCommit("07ea43", "test.txt");
	//	System.out.println(result);
		assertTrue(result.startsWith("commit 9926800baec7071c9fe4f5930262c95eacc79764"));
		assertTrue(result1.equals("NoMoreCommit"));
		assertTrue(result2.equals("UnchangedCommit"));	
	}
	
	@Test
	public void TestShowPreviousCommit()
	{
		GetInfor a=new GetInfor("/Users/peer_free/Desktop/TestCase3");
		String result = a.showPreviousCommit("1");
		String result1 = a.showPreviousCommit("e0fda9");
		assertTrue(result.equals("NoMoreCommit"));
		//System.out.println(result1);
		assertTrue(result1.startsWith("commit 1f70d3ba817218141013ef06e4ec1eaca365596e"));
	}
	
	@Test
	public void TestFindSHA1()
	{
		GetInfor a=new GetInfor("/Users/peer_free/Desktop/TestCase3");
		String result=a.showCommit("992680", "test.txt");
	  
		String SHA = a.FindSHA1(result);
		//System.out.println(SHA);
		assertTrue(SHA.equals("9926800baec7071c9fe4f5930262c95eacc79764"));
	}
	@Test
	public void TestFindAuthor()
	{
		GetInfor a=new GetInfor("/Users/peer_free/Desktop/TestCase3");
		String result=a.showCommit("992680", "test.txt");
	  
		String author = a.findAuthor(result);
		//System.out.println(author);
		assertTrue(author.equals("ztaxx <ztaxx1412@gmail.com>"));
	}
	@Test
	public void TestFindDate() throws ParseException
	{
		GetInfor a=new GetInfor("/Users/peer_free/Desktop/TestCase3");
		String result=a.showCommit("992680", "test.txt");
	  
		String date = a.FindDate(result).toString();
		//System.out.println(date);
		assertTrue(date.equals("Sun Mar 22 11:32:59 GMT 2015"));
	}
	@Test
	public void TestgetCommitInfor() throws ParseException
	{
		GetInfor a=new GetInfor("/Users/peer_free/Desktop/TestCase3");
		String result = a.getCommitInfor(null,"9926800", "test.txt",null).toString();
		System.out.println(result);
		System.out.println("{2=Line [content=abc, FutureNumber=3]\n}");
		//String result = a.getCommitInfor(null,"9926800", "test.txt",null).toString();

		assertTrue(result.contains("2=Line[content=abc, FutureNumber=3]"));
	}
}
