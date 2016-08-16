package cn.yanweijia.beans;

import java.util.List;

public class DetailInfo {
	/**下载链接*/
	private String downloadLink;
	/**介绍文字*/
	private String content;
	/**图片*/
	private List<String> imgLsit;
	
	
	public DetailInfo(String content,List<String> imgList,String downloadLink){
		this.content = content;
		this.imgLsit = imgList;
		this.downloadLink = downloadLink;
	}
	
	public String getDownloadLink() {
		return downloadLink;
	}
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<String> getImgLsit() {
		return imgLsit;
	}
	public void setImgLsit(List<String> imgLsit) {
		this.imgLsit = imgLsit;
	}
}
