package tany;

import java.util.HashSet;
import java.util.Set;

public class MyCrawler implements Runnable {
	/*
	 * ����ÿ��ֻ���������Ҫ10��˵˵������ֻ���ظ�Ҫ����Ҫ����url �������Ҫ����url������DownLoadFile��
	 */
	static int z = 0;// ����static�ճ�

	public void crawling(String seeds) {

		while (Test.suspend == 1 || Test.suspend == 3) {

			Test.stop1 = false;

			int i = 0;
			i = seeds.indexOf("Finfo%253Doffset%25253D");
			int a = 0;
			a = seeds.indexOf("%252526total%25253D");
			seeds = seeds.substring(0, i + 23) + (z * 10)
					+ seeds.substring(a, seeds.length());
			// ����URL

			DownLoadFile downLoader = new DownLoadFile();
			downLoader.downloadFile(seeds);
			// ������ҳ
			z++;
		}
		Test.stop1 = true;
	}

	@Override
	public void run() {
		// TODO �Զ����ɵķ������
		crawling(Test.url.getText());
	}

}