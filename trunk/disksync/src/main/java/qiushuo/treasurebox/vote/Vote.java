/**
 * (created at 2011-3-26)
 */
package qiushuo.treasurebox.vote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class Vote {

    public Vote() {
    }

    private static class WriteThread extends Thread {
        private Socket so;
        private CountDownLatch count = new CountDownLatch(1);

        public WriteThread(Socket so) {
            super();
            this.so = so;
        }

        public void shutdown() {
            count.countDown();
        }

        @Override
        public void run() {
            try {
                if (!count.await(10, TimeUnit.SECONDS)) {
                    so.close();
                    System.out.println("wirte for too long time");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void vote() throws Exception {
        Socket s = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            s = new Socket();
            s.connect(new InetSocketAddress("input.vote.qq.com", 80), 20000);
            s.setSoTimeout(30 * 1000);
            out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(s.getInputStream(), "GBK"));
            WriteThread t = new WriteThread(s);
            t.start();
            out.print("POST /survey.php HTTP/1.1\r\n");
            out.print("Host: input.vote.qq.com\r\n");
            out.print("User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16\r\n");
            out.print("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n");
            out.print("Accept-Language: en-us,en;q=0.5\r\n");
            out.print("Accept-Encoding: deflate\r\n");
            out.print("Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n");
            out.print("Referer: http://page.vote.qq.com/?id=939849&result=yes\r\n");
            out.print("Content-Type: application/x-www-form-urlencoded\r\n");
            out.print("Connection: close\r\n");
            out.print("Content-Length: 44\r\n\r\n");
            out.print("PjtID=939849&result=0&sbj_969851%5B%5D=72655");
            out.flush();
            t.shutdown();
            for (String line = null; (line = in.readLine()) != null;) {
                System.out.println(line);
                if (line.trim().length() < 0) break;
            }
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            }
            try {
                in.close();
            } catch (Exception e) {
            }
            try {
                s.close();
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==============");
        Vote v = new Vote();
        for (int i = 1; i < Integer.MAX_VALUE; ++i) {
            try {
                v.vote();
                System.out.println("i=" + i + ", " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                Thread.sleep(10001);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(2000);
            }
        }
    }

}
