package com.appspot.ipaddr_img_onry;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class IPAddressServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		String ipaddr = req.getRemoteAddr(); // IP アドレスを取得 ex) "192.168.0.1"
		String[] octetArray = ipaddr.split("\\."); // 各オクテットに分割

		String ipaddrFormat = String.format("%03d", Integer.valueOf(octetArray[0])) // IP アドレスが 12 桁になるよう
				+ String.format("%03d", Integer.valueOf(octetArray[1]))             // フォーマットする
				+ String.format("%03d", Integer.valueOf(octetArray[2]))             // ex) "192168000001"
				+ String.format("%03d", Integer.valueOf(octetArray[3]));

		String indexString = req.getParameter("index"); // 表示する「桁」
		char ipaddrNum = ipaddrFormat.charAt(Integer.valueOf(indexString)); // 指定された「桁」の値を取得

		// レスポンスする画像を取得
		String path = "/num/" + ipaddrNum + ".gif"; // 予め '0' ~ '9' までの画像を用意
		String realPath = this.getServletContext().getRealPath(path);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				realPath));
		
		// 画像データをレスポンス
		byte[] data = new byte[1024];
		int len;
		resp.setContentType("image/gif"); // 画像のファイル形式に合わせてよしなに…
		ServletOutputStream out = resp.getOutputStream();
		while ((len = in.read(data, 0, 1024)) != -1) {
			out.write(data, 0, len);
		}

		in.close();
	}
}
