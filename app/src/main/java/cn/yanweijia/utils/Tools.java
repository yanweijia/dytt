package cn.yanweijia.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.io.UnsupportedEncodingException;  
/**
 * 工具类
 * @author 严唯嘉
 * @date 2016/07/22
 * @version 1.0
 */
public class Tools {
	private static final String TAG = "Tools";
	
	public static void main(String[] args){
		System.out.println(Tools.URLEncode("奇幻森林"));
		System.out.println("你好呀AndroidStudio");
	}

	/**
	 * 关闭软键盘
	 */
	public static void hintKbTwo(Activity activity) {
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()&&activity.getCurrentFocus()!=null){
			if (activity.getCurrentFocus().getWindowToken()!=null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
	/**
	 * 检测网络是否连接
	 *
	 * @return
	 */
	public static boolean isNetworkAvailable(Activity activity) {
		// 得到网络连接信息
		ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			return manager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}
	
	/**
	 * 网址编码
	 * @param text 待编码字符串
	 * @return 编码后字符串
	 */
	public static String URLEncode(String text){
		String encodeText = null;
		try {
			encodeText = java.net.URLEncoder.encode(text, "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeText;
	}
	
	/**
	 * 将普通下载链接转换为迅雷下载链接
	 * @param url
	 * @return
	 */
	public static String convertToThunderLink(String url){
		url = "AA" + url + "ZZ";
		String result = "thunder://" + Tools.Base64Encoding(url);
		result = result.replaceAll("\r\n", "");
		return result;
	}
	
	/**
	 * 获取Base64编码
	 * @param str 待编码的字符串
	 * @return 编码后的字符串
	 */
	public static String Base64Encoding(String str){
		byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("UTF-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        if (b != null) {
			s = Base64.encodeToString(b, Base64.DEFAULT);
        }  
        return s;  
	}

	
	/**
	 * 截取指定字符串中间的字符串
	 * @param str 字符串
	 * @param front 前面字符串
	 * @param behind 后面字符串
	 * @return 截取后的字符串
	 */
	public static String getMidString(String str,String front,String behind){
		if(str == null)
			return "";
		int frontIndex = str.indexOf(front) + front.length();
		int behindIndex = str.indexOf(behind);
		if (frontIndex >= behindIndex || frontIndex < 0)
			return "";
		String result = str.substring(frontIndex,behindIndex);
		return result;
	}

	
	
	/**
	 * 获取当前日期时间,精确到秒
	 * @author 严唯嘉
	 * @return 格式为 <strong>yyyy-MM-dd HH:mm:ss</strong>
	 */
	public static String getFormatDatetime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime = simpleDateFormat.format(new java.util.Date());
		return dateTime;
	}
	/**
	 * 获取当前日期
	 * @author 严唯嘉
	 * @return 格式为<strong>yyyy-MM-dd</strong>
	 */
	public static String getFormatDate(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new java.util.Date());
		return date;
	}

	/**
	 * 获取网络图片
	 * @param url 图片的网址
	 * @return 图片的BItmap表示
     */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			Log.d(TAG, url);
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setConnectTimeout(0);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
}
