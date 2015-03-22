package program;

import static org.junit.Assert.*;

import java.util.TreeMap;

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
}
