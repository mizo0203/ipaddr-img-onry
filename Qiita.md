# img タグだけで訪問者の IP アドレスを表示する
URL: http://qiita.com/mizo0203/items/2c556891168bfafec30a
Tag: AdventCalendar,GAE

#はじめに
Web ページで訪問者の IP アドレスを表示させたい場合、PHP や SSI を用いる方法が一般的です。しかし、レンタルしたホームページサービスによってはこれらが使用できない場合があります。
今回は、画像表示に用いる img タグだけで IP アドレスを表示させ、前述の問題を回避したいと思います。


#サンプルプログラム
## HTML コード例
html ファイルは下記のようになります。
※サンプルそのままのご利用は ipaddr-img-onry.appspot.com へサーバ負荷が集中するため、ご遠慮頂けますようお願いいたします。

```html:index.html
<html>
  <body>
    <p>
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=0" />
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=1" />
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=2" />
    .
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=3" />
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=4" />
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=5" />
    .
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=6" />
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=7" />
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=8" />
    .
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=9" />
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=10" />
    <img src="http://ipaddr-img-onry.appspot.com/ipaddress?index=11" />
    </p>
  </body>
</html>
```

## 表示例
下記のように表示されます。IP アドレスの表示には画像データを使用します。
Qiita 上でも機能します。

### 訪問者の IP アドレス
![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=0)![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=1)![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=2) **.** ![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=3)![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=4)![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=5) **.** ![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=6)![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=7)![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=8) **.** ![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=9)![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=10)![ipaddr](http://ipaddr-img-onry.appspot.com/ipaddress?index=11)

今回、画像データは[もず倉](http://members.jcom.home.ne.jp/mozunatto/)様からお借りしました。
![banner](http://ipaddr-img-onry.appspot.com/banner/banner_mozu03b.gif)

## サーバ側プログラム
このサンプルはサーバ側で画像リクエストを解析し、適当な画像データをレスポンスとして返却することで実現しています。
今回のサーバプログラムは、無料でサーブレットアプリケーションを実行できる[Google App Engine](https://cloud.google.com/appengine)にて稼働させています。
![banner](http://ipaddr-img-onry.appspot.com/banner/appengine-noborder-120x30.gif)


```java:IPAddressServlet.java
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
```

#さいごに
いかがだったでしょうか。
私としては、序章で述べた問題に直面した場合、このようなまどろっこしいことをせず、
素直に PHP や SSI が利用できるサービスに切り替えることをおすすめいたします。
