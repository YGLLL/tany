package tany;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LookForTxt implements Runnable {
	/*
	 * �����ǳ�֮���ӣ�ȴ�����ص� �������Ҫ��ȡtany\\work\\�µ�txt�ļ����ҵ�ָ���ַ�����Ȼ�������html�ļ�
	 */
	File[] files;
	File file;
	static FileOutputStream fileoutputstream;//�ļ������
	static int xxxxx = 0, y = 0;//����static�ճ�

	public void myListFiles(String dir) { // �����ļ��б�

		File directory = new File(dir);

		if (!directory.isDirectory()) {
			System.out.println("No directory provided");
			return;
		}

		files = directory.listFiles(filefilter);
	}

	// create a FileFilter and override its accept-method
	FileFilter filefilter = new FileFilter() { // �����ļ��б�

		public boolean accept(File file) {
			// if the file extension is .txt return true, else false
			if (file.getName().endsWith(".txt")) {
				return true;
			}
			return false;
		}
	};

	public String timeStamp2Date(String seconds, String format) {
		if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
			return "";
		}
		if (format == null || format.isEmpty())
			format = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(seconds + "000")));
	}
	//ʱ���ת����ʱ��

	public void readtxt() throws IOException { // �����ļ�

		if ((files.length > 0) && files[0].renameTo(files[0])) {
			// �ж������ļ����ļ��Ƿ�����д�루�������ļ�д��ʱ�򿪣�

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(files[0])));
			StringBuffer sb = new StringBuffer("");
			String str = "";
			while ((str = br.readLine()) != null) {// br.readLine()��ȡÿһ�е��ַ�
				sb.append(str);
			}
			sb.append("END!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");// ������־
			sb.deleteCharAt(sb.length() - 1);// ...

			byte[] tag_bytes;
			String pag = "<br><br>��" + MyCrawler.z + "ҳ";
			tag_bytes = pag.getBytes();
			fileoutputstream.write(tag_bytes);// �����ļ������

			String vFeeds = "\"vFeeds\":[";
			int a = 0;
			if ((a = sb.toString().indexOf(vFeeds, 0)) == -1) {// �Ҳ���vFeeds���˳�
				br.close();// �ر�����ֹͣ���ļ��Ĳ���
				files[0].delete();// ɾ���ļ�
				// System.out.println("�Ҳ���vFeeds");
				y++;
				if (y > 5) {//����5���Ҳ���vFeeds��ֹͣ
					Test.suspend = 4;// ����ֹͣ����

					Test.ta.append("\nץȡ��ɣ�����ѱ���");
					Test.ta.selectAll();// �Զ�����
					String overstr = "<br><br>��������";
					tag_bytes = overstr.getBytes();
					fileoutputstream.write(tag_bytes);// �����ļ������

					fileoutputstream.close();// �ر��ļ������
					y = 0;
				}
				return;
			}

			int b = a;
			int c = 0, d = 0, e = 0;
			boolean over = true;
			while (over) {
				xxxxx++;
				a = b;
				e = c = d = 0;
				while (!(e == 0 && d < c)) {// ȷ��һ��˵˵������
					c = sb.toString().indexOf("{", b + 1);
					d = sb.toString().indexOf("}", b + 1);
					if (c == -1) {
						c = d + 1;
						over = false;// �Ҳ���{��}��ų������ź�
					}
					if (d == -1) {
						d = c + 1;
						over = false;// �Ҳ���{��}��ų������ź�
					}
					if (c < d) {
						e++;
						b = c;
					} else {
						e--;
						b = d;
					}
				}
				b = d;
				// System.out.printf(sb.toString().substring(a, d + 1));

				String start = "{\"summary\":\"";// ��˵˵���ݺ�ʱ��
				String end = "\"";
				String timestr = "\"time\":";
				int f = 0, g = 0, h = 0;
				f = sb.toString().indexOf(start, a);
				g = sb.toString().indexOf(end, f + start.length());
				h = sb.toString().indexOf(timestr, a);

				String time = timeStamp2Date(
						sb.toString().substring(h + timestr.length(),
								h + timestr.length() + 10), "");
				// ʱ���ת���ɻ�ʱ��

				if (f != -1 && g != -1 && h != -1 && f < b && g < b && h < b) {
					Test.ta.append("\n"
							+ sb.toString().substring(f + start.length(), g));
					// append()�����Ὣ�����ı�׷�ӵ��ĵ���β
					Test.ta.selectAll();// �Զ�����
					// ���������

					tag_bytes = ("<br>" + xxxxx + ".<br>" + "ʱ��:" + time
							+ "<br>" + sb.toString().substring(
							f + start.length(), g)).getBytes();
					// ������ļ�
					fileoutputstream.write(tag_bytes);// �����ļ������
				}
			}
			br.close();// �ر�����ֹͣ���ļ��Ĳ���

			files[0].delete();// ɾ���ļ�
		}
	}

	@Override
	public void run() {
		// TODO �Զ����ɵķ������

		if (Test.suspend == 1) {// ������������ļ�
			Date now = new Date(); // �����ڼ����ļ�����
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy_MM_dd_HH_mm_ss");
			int i = 0;// ��QQ�ż����ļ�����
			i = Test.url.getText().toString().indexOf("hostuin=");
			String namestr = Test.url.getText().toString()
					.substring(i + 8, i + 18);
			file = new File("d:\\tany\\" + "QQ_" + namestr + "_��˵˵_"
					+ dateFormat.format(now) + ".html");

			try {
				file.createNewFile();// �������ļ�
				fileoutputstream = new FileOutputStream(file);// ������
			} catch (IOException e1) {
				// TODO �Զ����ɵ� catch ��
				e1.printStackTrace();
			}
		}

		while ((Test.suspend == 1 || Test.suspend == 3)
				|| !(Test.suspend == 4 && Test.stop1 == true)) {
			// �������ļ�����ֹͣ�󣬷���ֹͣ
			Test.stop2 = false;// ֹͣ��־

			myListFiles(Test.workpath);// �����ļ��б�,����File[] files
			try {
				readtxt();
			} catch (FileNotFoundException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}
			// ����whileռ�ù���CPU,�����Ӱ���ļ������ٶ�
		}
		Test.stop2 = true;
	}
}
