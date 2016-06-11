package tany;

import java.util.HashSet;
import java.util.Set;

public class MyCrawler implements Runnable {
	/*
	 * 由于每次只能向服务器要10条说说，所以只能重复要，需要更新url 这个类主要更改url并传给DownLoadFile类
	 */
	static int z = 0;// 乱用static日常

	public void crawling(String seeds) {

		while (Test.suspend == 1 || Test.suspend == 3) {

			Test.stop1 = false;

			int i = 0;
			i = seeds.indexOf("Finfo%253Doffset%25253D");
			int a = 0;
			a = seeds.indexOf("%252526total%25253D");
			seeds = seeds.substring(0, i + 23) + (z * 10)
					+ seeds.substring(a, seeds.length());
			// 更新URL

			DownLoadFile downLoader = new DownLoadFile();
			downLoader.downloadFile(seeds);
			// 下载网页
			z++;
		}
		Test.stop1 = true;
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		crawling(Test.url.getText());
	}

}