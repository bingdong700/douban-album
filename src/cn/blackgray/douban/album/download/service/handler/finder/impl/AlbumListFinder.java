package cn.blackgray.douban.album.download.service.handler.finder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.URLUtils;
import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;

/**
 * ���URL��ȡ��ʵ�� - ��������б���ȡ���
 */
public class AlbumListFinder implements IAlbumURLFinder{

	private static final int PAGE_SIZE_ALBUM = 16;			//����ҳ��С(һҳ16�����)
	

	@Override
	public List<String> findAlbumURL(String albumListURL) {
		if (albumListURL.endsWith("/")) {
			albumListURL = albumListURL.substring(0,albumListURL.length() - 1);
		}
		Console.print("ɨ������б���ҳ��" + albumListURL);
		//		http://www.douban.com/people/BlackGray/photos
		//		http://www.douban.com/photos/album/xxxxx
		//���õ���������ҳ��ÿҳ16����ᣩ��
		List<String> pageURLList = new ArrayList<String>();
		String source = URLUtils.readSource(albumListURL);
		String regex = albumListURL + "\\?\\w+=\\d+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		//���ҳ�ҳ�������з�ҳ����
		int maxStartNum = 0;
		while (m.find()) {
			String url = m.group();
			int num = Integer.parseInt(url.substring(url.lastIndexOf("=") + 1));
			maxStartNum = num > maxStartNum ? num : maxStartNum;
		}
		//���������ʼ���֣�����ҳ���ַ
		for (int i = 0; i <= maxStartNum; i += PAGE_SIZE_ALBUM) {
			String u = albumListURL + "?start=" + i;
			pageURLList.add(u);
			Console.print("��ȡ����ҳ��ַ��" + u);
		}

		//���õ�������᡿
		Set<String> albumURLSet = new TreeSet<String>();
		for (int i = 0; i < pageURLList.size(); i++) {
			source = URLUtils.readSource(pageURLList.get(i));
			String albumRegex = "(http|https)://www.douban.com/photos/album/\\d+";
			Pattern pattern = Pattern.compile(albumRegex);
			Matcher matcher = pattern.matcher(source);
			while (matcher.find()) {
				String url = matcher.group();
				if (!url.endsWith("/")) {
					url += "/";
				}
				albumURLSet.add(url);
			}
		}
		return new ArrayList<String>(albumURLSet);
	}

	@Override
	public String getURLRegex() {
		return "(http|https)://www.douban.com/people/\\w+/photos/";
	}

}