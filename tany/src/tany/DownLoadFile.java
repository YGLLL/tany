package tany;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Random;

//import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpStatus;

public class DownLoadFile {
	/*
	 * DownLoadFile类是谭雅的核心 主要用于发送请求，并接收服务器的响应文件（json）
	 * 并保存将文件在tany\\html\\和tany\\work\\下（一式两份）
	 * html下是原文件，work下是临时txt文件，用于分析，分析完将删除
	 */
	public String getFileName(String url, int mo) {//获得文件名
		int i = 0;
		i = url.indexOf("hostuin=");

		Date now = new Date(); // 将日期加入文件名称
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"_yyyy_MM_dd_HH_mm_ss_");

		int a;//加入随机尾
		Random ra = new Random();
		a = ra.nextInt(10000);

		if (mo == 0) {
			return url = "QQ_" + url.substring(i + 8, i + 18)
					+ dateFormat.format(now) + a + ".txt";
		}
		if (mo == 1) {
			return url = "QQ_" + url.substring(i + 8, i + 18)
					+ dateFormat.format(now) + a + ".json";
		}
		return url;
	}

	/**
	 * 保存文件字节数组到本地文件 filePath 为要保存的文件的相对地址
	 */
	private void saveToLocal(byte[] data, String filePath) {//复制的，我看不懂
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					new File(filePath)));
			for (int i = 0; i < data.length; i++)
				out.write(data[i]);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 下载 url 指向的网页 */
	public void downloadFile(String url) {
		

		/* 1.生成 HttpClinet 对象并设置参数 */
		HttpClient httpClient = new HttpClient();
		// 设置 Http 连接超时 5s
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		// 设置通用请求属性(模拟浏览器)
		List<Header> headers = new ArrayList<Header>();
		// headers.add(new Header("Accept",
		// " text/html, application/xhtml+xml, image/jxr, */*"));
		// headers.add(new Header("Referer",
		// "http://user.qzone.qq.com/2369015621"));
		// headers.add(new Header("Accept-Language", "zh-CN"));
		// headers.add(new Header("Content-Type",
		// "application/x-www-form-urlencoded"));
		// headers.add(new Header("Accept-Encoding", "gzip, deflate"));
		// headers.add(new Header("Content-Length", "123"));
		// headers.add(new Header("If-Modified-Since",
		// "Sat, 04 Jun 2016 23:55:20 GMT"));
		// headers.add(new Header("Host", "w.qzone.qq.com"));
		// headers.add(new Header("Connection", "Keep-Alive"));
		// headers.add(new Header("Pragma", "no-cache"));
		headers.add(new Header(
				// 模拟安卓设备
				"User-Agent",
				"Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"));
		headers.add(new Header("Cookie", Test.Cookie.getText()));// Cookie

		httpClient.getHostConfiguration().getParams()
				.setParameter("http.default-headers", headers);

		/* 2.生成 GetMethod 对象并设置参数 */
		GetMethod getMethod = new GetMethod(url);
		// 设置 get 请求超时 5s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		// 设置请求重试处理
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		/* 3.执行 HTTP GET 请求 */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// 判断访问的状态码
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			}

			/* 4.处理 HTTP 响应内容 */
			String filePath = null;// 文件保存位置
			// 将文件保存为txt
			String pureText = getMethod.getResponseBodyAsString();// 读取json为String
			byte[] responseBody = pureText.getBytes();
			filePath = Test.workpath + getFileName(url, 0);
			saveToLocal(responseBody, filePath);

			// 将文件保存为json
			responseBody = getMethod.getResponseBody();
			filePath = "d:\\tany\\json\\" + getFileName(url, 1);
			saveToLocal(responseBody, filePath);

			// } catch (HttpException e){
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			// System.out.println("Please check your provided http address!");
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			// 发生网络异常
		} finally {
			getMethod.releaseConnection();
			// 释放连接
		}
	}
}
