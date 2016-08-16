package cn.yanweijia.beans;

/**
 * 超链接的封装
 * @author 严唯嘉
 * @createDate 2016/08/14
 * @lastModify 
 */
public class Link {
	/**链接名称*/
	private String name;
	/**网址*/
	private String url;
	/**更新时间*/
	private String date;
	public Link(String name,String url,String date){
		this.name = name;
		this.url = url;
		this.date = date;
	}
	public Link(String name,String url){
		this.name = name;
		this.url = url;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
