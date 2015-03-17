import java.util.TreeSet;


public class DiffRange {
public TreeSet<Integer> minusRange;
public TreeSet<Integer> plusRange;
public DiffRange() {
	this.minusRange = new TreeSet<Integer>();
	this.plusRange =new TreeSet<Integer>();
}
@Override
public String toString() {
	return "DiffRange [minusRange=" + minusRange.toString() + ", plusRange=" + plusRange.toString()
			+ "]";
}

}
