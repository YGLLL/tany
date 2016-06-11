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
	 * DownLoadFile����̷�ŵĺ��� ��Ҫ���ڷ������󣬲����շ���������Ӧ�ļ���json��
	 * �����潫�ļ���tany\\html\\��tany\\work\\�£�һʽ���ݣ�
	 * html����ԭ�ļ���work������ʱtxt�ļ������ڷ����������꽫ɾ��
	 */
	public String getFileName(String url, int mo) {//����ļ���
		int i = 0;
		i = url.indexOf("hostuin=");

		Date now = new Date(); // �����ڼ����ļ�����
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"_yyyy_MM_dd_HH_mm_ss_");

		int a;//�������β
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
	 * �����ļ��ֽ����鵽�����ļ� filePath ΪҪ������ļ�����Ե�ַ
	 */
	private void saveToLocal(byte[] data, String filePath) {//���Ƶģ��ҿ�����
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

	/* ���� url ָ�����ҳ */
	public void downloadFile(String url) {
		

		/* 1.���� HttpClinet �������ò��� */
		HttpClient httpClient = new HttpClient();
		// ���� Http ���ӳ�ʱ 5s
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		// ����ͨ����������(ģ�������)
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
				// ģ�ⰲ׿�豸
				"User-Agent",
				"Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"));
		headers.add(new Header("Cookie", Test.Cookie.getText()));// Cookie

		httpClient.getHostConfiguration().getParams()
				.setParameter("http.default-headers", headers);

		/* 2.���� GetMethod �������ò��� */
		GetMethod getMethod = new GetMethod(url);
		// ���� get ����ʱ 5s
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		// �����������Դ���
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());

		/* 3.ִ�� HTTP GET ���� */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// �жϷ��ʵ�״̬��
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "
						+ getMethod.getStatusLine());
			}

			/* 4.���� HTTP ��Ӧ���� */
			String filePath = null;// �ļ�����λ��
			// ���ļ�����Ϊtxt
			String pureText = getMethod.getResponseBodyAsString();// ��ȡjsonΪString
			byte[] responseBody = pureText.getBytes();
			filePath = Test.workpath + getFileName(url, 0);
			saveToLocal(responseBody, filePath);

			// ���ļ�����Ϊjson
			responseBody = getMethod.getResponseBody();
			filePath = "d:\\tany\\json\\" + getFileName(url, 1);
			saveToLocal(responseBody, filePath);

			// } catch (HttpException e){
			// �����������쳣��������Э�鲻�Ի��߷��ص�����������
			// System.out.println("Please check your provided http address!");
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			// ���������쳣
		} finally {
			getMethod.releaseConnection();
			// �ͷ�����
		}
	}
}
