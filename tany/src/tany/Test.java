package tany;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Test {
	/*
	 * 这个类是谭雅运行的起点，主要是一些界面的代码
	 */
	JFrame j;
	static String workpath = "d:\\tany\\work\\";//乱用static日常
	static JTextArea ta;// 创建组件
	static JTextField Cookie = new JTextField(// 创建组件
			"pgv_pvid=5063962969; ptui_loginuin=2369015621; pt2gguin=o2369015621; ptcz=eaafeaf9f4a7199d2c55c554e5f9fa223e41d7010d34ae29aa65f4e6e9d78cc3; o_cookie=2369015621; eas_sid=8134D6X3X7v2Z5k5q7T5Y4Y7s6; v6uin=; pgv_pvi=7658449920; RK=kOFH1AETxw; ptisp=ctc; uin=o2369015621; skey=@jMjc0coa6; QZ_FE_WEBP_SUPPORT=0; cpu_performance_v8=47; __Q_w_s__QZN_TodoMsgCnt=1; __Q_w_s_hat_seed=1; __Q_w_s__appDataSeed=1; p_uin=o2369015621; p_skey=FOZA88d83fqG9uSwbmd5jbuSvaSNw1vyHww4SOt0oSI_; pt4_token=ah0g4J4R4SSodLysUEYGK9uvXILVKfYPK0fzsy9vAR0_",
			30);
	static JTextField url = new JTextField(// 创建组件
			"https://mobile.qzone.qq.com/get_feeds?g_tk=1505101327&hostuin=511458939&res_type=2&res_attach=att%3Dback%255Fserver%255Finfo%253Doffset%25253D6%252526total%25253D89%252526basetime%25253D1464188751%252526feedsource%25253D0%2526lastrefreshtime%253D1465628067%2526lastseparatortime%253D0%2526loadcount%253D0%26tl%3D1464188751&refresh_type=2&format=json",
			30);
	// https://mobile.qzone.qq.com/get_feeds?g_tk=1824898759&hostuin=846016368&res_type=2&res_attach=att%3Dback%255Fserver%255Finfo%253Doffset%25253D0%252526total%25253D62%252526basetime%25253D1464448910%252526feedsource%25253D0%2526lastrefreshtime%253D1465446461%2526lastseparatortime%253D0%2526loadcount%253D0%26tl%3D1464448910&refresh_type=2&format=json
	
	static int suspend = 0;
	static boolean stop1 = false;
	static boolean stop2 = false;
	//控制暂停继续停止的变量

	public Test() {
		j = new JFrame("test");// 建立窗口
		j.setSize(800, 430);
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.getContentPane().setBackground(Color.gray);

		JButton bt = new JButton("Run or Pause");
		JButton stopbt = new JButton("stop");
		JLabel wz2 = new JLabel("Cookie");
		JLabel wz = new JLabel("URL");

		Font zt = new Font("微软雅黑", 0, 20);
		ta = new JTextArea(10, 40);// 设置组件
		ta.setFont(zt);
		ta.setBackground(Color.black);// 設置背景
		ta.setForeground(Color.gray);// 設置字體
		ta.setLineWrap(true);// 激活自动换行功能
		ta.setWrapStyleWord(true);// 激活断行不断字功能
		bt.addActionListener(new ActionListener() {//设定按键触发事件
			@Override
			public void actionPerformed(ActionEvent e) {
				runnn();
			}
		});
		stopbt.addActionListener(new ActionListener() {//设定按键触发事件
			@Override
			public void actionPerformed(ActionEvent e) {
				stoprun();
			}
		});

		j.add(new JScrollPane(ta));// 加入下拉栏
		j.add(wz2);
		j.add(Cookie);
		j.add(wz);
		j.add(url);
		j.add(bt);
		j.add(stopbt);

		j.setLayout(new FlowLayout()); // FlowLayout（流式布局)
		j.validate();// 确保组件具有有效的布局

		File file = new File("d:\\tany\\work\\");// 建立工作目录
		if (!file.exists()) {// 不存在此路径则建立文件夹
			file.mkdirs();
		}
		file = new File("d:\\tany\\json\\");
		if (!file.exists()) {// 不存在此路径则建立文件夹
			file.mkdirs();
		}
	}

	public void runnn() {//按键触发事件
		if (suspend > 2) {//这里会比较烧脑
			suspend--;
		} else {
			suspend++;
		}
		if (suspend == 1 || suspend == 3) {
			Test.ta.append("\n进行中");
			Test.ta.selectAll();// 自动滚动
		}
		if (suspend == 2) {
			Test.ta.append("\n暂停");
			Test.ta.selectAll();// 自动滚动
			return;
		}

		int a;// 随机产生声音
		Random ra = new Random();
		a = ra.nextInt(40) + 1;
		String soundsurl = "src\\sounds\\tany (" + a + ").wav";
		PlaySounds sounds = new PlaySounds(soundsurl);
		sounds.start();

		MyCrawler crawler = new MyCrawler();// 下载文件线程启动
		(new Thread(crawler)).start();

		LookForTxt lft = new LookForTxt();// 分析文件线程启动
		(new Thread(lft)).start();
	}

	public void stoprun() {//按键触发事件
		PlaySounds sounds = new PlaySounds("src\\sounds\\tany (10).wav");// 声音
		sounds.start();

		suspend = 4;// 发出停止请求
		while (true) {// 等待停止
			if (stop1 == true && stop2 == true) {
				try {
					LookForTxt.fileoutputstream.close();// 关闭流
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				break;
			}
			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}// 避免while占用过多CPU
		}
		suspend = 0;
		Test.ta.append("\n已停止,抓取结果已保存");
		Test.ta.selectAll();// 自动滚动
	}

	// main 方法入口
	public static void main(String[] args) {
		new Test();
	}
}
