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
	 * 这个类非常之繁杂，却不是重点 这个类主要提取tany\\work\\下的txt文件并找到指定字符串，然后输出到html文件
	 */
	File[] files;
	File file;
	static FileOutputStream fileoutputstream;//文件输出流
	static int xxxxx = 0, y = 0;//乱用static日常

	public void myListFiles(String dir) { // 生成文件列表

		File directory = new File(dir);

		if (!directory.isDirectory()) {
			System.out.println("No directory provided");
			return;
		}

		files = directory.listFiles(filefilter);
	}

	// create a FileFilter and override its accept-method
	FileFilter filefilter = new FileFilter() { // 生成文件列表

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
	//时间戳转正常时间

	public void readtxt() throws IOException { // 分析文件

		if ((files.length > 0) && files[0].renameTo(files[0])) {
			// 判断有无文件，文件是否正在写入（不能在文件写入时打开）

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(files[0])));
			StringBuffer sb = new StringBuffer("");
			String str = "";
			while ((str = br.readLine()) != null) {// br.readLine()获取每一行的字符
				sb.append(str);
			}
			sb.append("END!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");// 结束标志
			sb.deleteCharAt(sb.length() - 1);// ...

			byte[] tag_bytes;
			String pag = "<br><br>第" + MyCrawler.z + "页";
			tag_bytes = pag.getBytes();
			fileoutputstream.write(tag_bytes);// 建立文件输出流

			String vFeeds = "\"vFeeds\":[";
			int a = 0;
			if ((a = sb.toString().indexOf(vFeeds, 0)) == -1) {// 找不到vFeeds则退出
				br.close();// 关闭流，停止对文件的操作
				files[0].delete();// 删除文件
				// System.out.println("找不到vFeeds");
				y++;
				if (y > 5) {//超过5次找不到vFeeds则停止
					Test.suspend = 4;// 发出停止请求

					Test.ta.append("\n抓取完成，结果已保存");
					Test.ta.selectAll();// 自动滚动
					String overstr = "<br><br>到底啦！";
					tag_bytes = overstr.getBytes();
					fileoutputstream.write(tag_bytes);// 建立文件输出流

					fileoutputstream.close();// 关闭文件输出流
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
				while (!(e == 0 && d < c)) {// 确定一条说说的区域
					c = sb.toString().indexOf("{", b + 1);
					d = sb.toString().indexOf("}", b + 1);
					if (c == -1) {
						c = d + 1;
						over = false;// 找不到{或}则放出结束信号
					}
					if (d == -1) {
						d = c + 1;
						over = false;// 找不到{或}则放出结束信号
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

				String start = "{\"summary\":\"";// 找说说内容和时间
				String end = "\"";
				String timestr = "\"time\":";
				int f = 0, g = 0, h = 0;
				f = sb.toString().indexOf(start, a);
				g = sb.toString().indexOf(end, f + start.length());
				h = sb.toString().indexOf(timestr, a);

				String time = timeStamp2Date(
						sb.toString().substring(h + timestr.length(),
								h + timestr.length() + 10), "");
				// 时间戳转换成换时间

				if (f != -1 && g != -1 && h != -1 && f < b && g < b && h < b) {
					Test.ta.append("\n"
							+ sb.toString().substring(f + start.length(), g));
					// append()方法会将给定文本追加到文档结尾
					Test.ta.selectAll();// 自动滚动
					// 输出到窗口

					tag_bytes = ("<br>" + xxxxx + ".<br>" + "时间:" + time
							+ "<br>" + sb.toString().substring(
							f + start.length(), g)).getBytes();
					// 输出到文件
					fileoutputstream.write(tag_bytes);// 建立文件输出流
				}
			}
			br.close();// 关闭流，停止对文件的操作

			files[0].delete();// 删除文件
		}
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根

		if (Test.suspend == 1) {// 建立分析结果文件
			Date now = new Date(); // 将日期加入文件名称
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy_MM_dd_HH_mm_ss");
			int i = 0;// 将QQ号加入文件名称
			i = Test.url.getText().toString().indexOf("hostuin=");
			String namestr = Test.url.getText().toString()
					.substring(i + 8, i + 18);
			file = new File("d:\\tany\\" + "QQ_" + namestr + "_的说说_"
					+ dateFormat.format(now) + ".html");

			try {
				file.createNewFile();// 建立新文件
				fileoutputstream = new FileOutputStream(file);// 建立流
			} catch (IOException e1) {
				// TODO 自动生成的 catch 块
				e1.printStackTrace();
			}
		}

		while ((Test.suspend == 1 || Test.suspend == 3)
				|| !(Test.suspend == 4 && Test.stop1 == true)) {
			// 当下载文件进程停止后，方可停止
			Test.stop2 = false;// 停止标志

			myListFiles(Test.workpath);// 生成文件列表,存入File[] files
			try {
				readtxt();
			} catch (FileNotFoundException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}
			// 避免while占用过多CPU,过大会影响文件分析速度
		}
		Test.stop2 = true;
	}
}
