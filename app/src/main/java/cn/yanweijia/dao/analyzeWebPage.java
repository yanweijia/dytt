package cn.yanweijia.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.yanweijia.beans.DetailInfo;
import cn.yanweijia.beans.Link;
import cn.yanweijia.utils.GetHTML;
import cn.yanweijia.utils.Tools;

/**
 * 分析获取网页中的电影信息
 * @author 严唯嘉
 * @createDate 2016/08/13
 * @lastModify 2016/08/15
 */
public class analyzeWebPage {
	//网站域名
	private static final String DOMAIN = "http://www.ygdy8.net";
	
	public static void main(String[] args) {
	    searchMovie("关键词");
	}
	
	/**
	 * 搜索指定电影
	 * @param key 关键词
	 * @return 查找结果,如果没有返回空list
	 */
	public static List<Link> searchMovie(String key){
		if(key == null || key.equals(""))
			return null;
		List<Link> list = new ArrayList<Link>();
		String url = "s.dydytt.net/plus/search.php?kwtype=0&searchtype=title&keyword=" + Tools.URLEncode(key);
		String sourceStr = GetHTML.getHtmlContent(url, "gb2312");
		if(sourceStr.indexOf("0条记录") > 0){
//			System.out.println("找不到");
			return list;
		}
		
		//结果的div
		sourceStr = Tools.getMidString(sourceStr, "<div class=\"co_content8\">", "</table> </td>");
		//System.out.println(sourceStr);

		//开始匹配文本,匹配的结果格式为 全部文本,网址,标题
		String regex = "href='(.*?)'>(.*?)</a>";
		String regex_date = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
		Pattern pattern = Pattern.compile(regex);
		Pattern pattern_date = Pattern.compile(regex_date);
		Matcher matcher = pattern.matcher(sourceStr);
		Matcher matcher_date = pattern_date.matcher(sourceStr);
		while(matcher.find() && matcher_date.find()){
			String movieUrl = DOMAIN + matcher.group(1);
			String movieTitle = matcher.group(2);
			movieTitle = movieTitle.replaceAll("</font>", "").replaceAll("<font color='red'>", "");
			String date = matcher_date.group();
			//System.out.println(movieUrl + movieTitle + date);
			Link link = new Link(movieTitle, movieUrl,date);
			list.add(link);
		}
		return list;
	}
	/**
	 * 获取最新电影列表
	 * @return 最新电影
	 */
	public static List<Link> getNewestMovie(){
		List<Link> list = new ArrayList<Link>();
		String url = "http://www.ygdy8.net/html/gndy/dyzz/index.html";
		String sourceStr = GetHTML.getHtmlContent(url, "gb2312");
		
		sourceStr = Tools.getMidString(sourceStr, "<div class=\"co_content8\">", "</ul>\n<div class=\"x\">");
		
		//之后就可以用 正则表达式 来匹配 网址/名称/发布时间了
		//匹配日期
		String regex_date = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
		//匹配网址和名称 匹配结果格式为: 全文本,子文本1(网址,不含域名),子文本2(电影名称标题)
		String regex_url = "<a href=\"(.*?)\" class=\"ulink\">(.*?)</a>";
		Pattern pattern_date = Pattern.compile(regex_date);
		Pattern pattern_url = Pattern.compile(regex_url);
		Matcher matcher_date = pattern_date.matcher(sourceStr);
		Matcher matcher_url = pattern_url.matcher(sourceStr);
		
		while(matcher_date.find() && matcher_url.find()){
			String date = matcher_date.group();
			String movieUrl = DOMAIN + matcher_url.group(1);
			String movieTitle = matcher_url.group(2);
			Link link = new Link(movieTitle, movieUrl, date);
			list.add(link);
		}
		return list;
	}
	
	public static DetailInfo getDownloadInfo(String url){
		String sourceStr = GetHTML.getHtmlContent(url, "gb2312");
		//获取源码中文章介绍的部分
		sourceStr = Tools.getMidString(sourceStr, "<span style=\"FONT-SIZE: 12px\">", "<center></center>");
//		System.out.println(sourceStr);
		
		
		//匹配源码中出现的影片图片
		String regex_img = "http.*?jpg";
		Pattern pattern_img = Pattern.compile(regex_img);
		Matcher matcher_img = pattern_img.matcher(sourceStr);
		List<String> imgList = new ArrayList<String>();
		while(matcher_img.find()){
			String str = matcher_img.group();
//			System.out.println(str);
			imgList.add(str);
		}
		//获取电影介绍
		String content = Tools.getMidString(sourceStr, "<br /><br />", "<br /><br /><img");
		content = content.replaceAll("<br />","\n");
//		System.out.println(content);

		String downloadLink = "ftp://" + Tools.getMidString(sourceStr, "ftp://", "\">ftp://");
//		System.out.println(downloadLink);
		
		
		DetailInfo detailInfo = new DetailInfo(content,imgList,downloadLink);
		return detailInfo;
	}
	
}
