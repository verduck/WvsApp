package wvs.wz;

public class WzHeader {
	private String ident;
	private long fileSize;
	private int contentPos;
	private String copyright;
	
	public String getIdent() {
		return ident;
	}
	
	public void setIdent(String ident) {
		this.ident = ident;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	public int getContentPos() {
		return contentPos;
	}
	
	public void setContentPos(int contentPos) {
		this.contentPos = contentPos;
	}
	
	public String getCopyright() {
		return copyright;
	}
	
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
}
