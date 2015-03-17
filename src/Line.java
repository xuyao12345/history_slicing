public class Line
{
	private String content;
	private int FutureLineNumber;

	public Line(String content, int previousLineNumber) {
		super();
		this.content = content;
		FutureLineNumber = previousLineNumber;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getFutureLineNumber() {
		return FutureLineNumber;
	}
	public void setFutureLineNumber(int previousLineNumber) {
		FutureLineNumber = previousLineNumber;
	}

	@Override
	public String toString() {
		return "Line [content=" + content + ", FutureNumber="
				+ FutureLineNumber + "]\n";
	}
	
}