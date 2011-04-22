/**
 * (created at 2011-3-26)
 */
package qiushuo.vote;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class LongfeiVote4DaCheng {
    private static class Info {
        AtomicInteger succ;
        AtomicInteger err;
        AtomicBoolean processing = new AtomicBoolean(false);

        public Info(AtomicInteger succ, AtomicInteger err) {
            super();
            this.succ = succ;
            this.err = err;
        }
    }

    private ConcurrentHashMap<String, Info> proxys = new ConcurrentHashMap<String, Info>();
    private AtomicInteger count = new AtomicInteger(0);
    private ExecutorService pool = Executors.newCachedThreadPool();

    private void vote() throws Exception {
        for (;;) {
            for (String ipport : proxys.keySet()) {
                Info p = proxys.get(ipport);
                if (p == null || p.err == null || p.err.get() <= 0) continue;
                if (!p.processing.compareAndSet(false, true)) continue;
                Voter v = new Voter(getIp(ipport), getPort(ipport));
                pool.execute(v);
            }
            Thread.sleep(11 * 1000);
        }
    }

    private void reloadProxys() {
        InputStream in = null;
        try {
            BufferedReader r =
                    new BufferedReader(new InputStreamReader(in =
                            getClass().getClassLoader().getResourceAsStream(getClass().getPackage()
                                                                                      .getName()
                                                                                      .replace('.', '/')
                                                                            + "/proxys.txt")));
            for (String line = null; (line = r.readLine()) != null;) {
                line = line.trim();
                if (!line.contains(":")) {
                    continue;
                }
                proxys.putIfAbsent(line, new Info(new AtomicInteger(0), new AtomicInteger(6)));
            }
            System.out.println(proxys);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e2) {
            }
        }
    }

    private static String getIp(String line) {
        if (!line.contains(":")) {
            throw new IllegalArgumentException();
        }
        String ip = line.substring(0, line.indexOf(':')).trim();
        return ip;
    }

    private static Integer getPort(String line) {
        if (!line.contains(":")) {
            throw new IllegalArgumentException();
        }
        Integer port = Integer.parseInt(line.substring(1 + line.indexOf(':')).trim());
        return port;
    }

    private void proxyErr(String ipPort) {
        Info p = proxys.get(ipPort);
        if (p == null || p.err == null) return;
        if (p.err.decrementAndGet() <= 0) {
            proxys.remove(ipPort);
            System.out.println("### ipport removed: " + ipPort);
        }
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

    private class Voter implements Runnable {
        private String ip;
        private int port;

        public Voter(String ip, int port) {
            super();
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            Socket s = null;
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                s = new Socket();
                s.connect(new InetSocketAddress(ip, port), 40000);
                s.setSoTimeout(30 * 1000);
                out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                in = new BufferedReader(new InputStreamReader(s.getInputStream(), "GBK"));
                WriteThread t = new WriteThread(s);
                t.start();
                out.print("POST http://input.vote.qq.com/survey.php HTTP/1.1\r\n");
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
                out.print("PjtID=939849&result=0&sbj_969851%5B%5D=72655\r\n");
                out.flush();
                t.shutdown();
                for (String line = null; (line = in.readLine()) != null;) {
                    if (line.contains("提交成功")) {
                        StringBuilder sb = new StringBuilder(ip).append(':').append(port);
                        Info p = proxys.get(ip + ":" + port);
                        if (p != null) {
                            Integer succ = p.succ == null ? null : p.succ.incrementAndGet();
                            Integer err = p.err == null ? null : p.err.get();
                            sb.append(", succ=").append(succ).append(", errLeft=").append(err);
                        }
                        System.out.println("succ="
                                           + count.incrementAndGet()
                                           + " "
                                           + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                           + ", proxy="
                                           + proxys.size()
                                           + ", "
                                           + sb.toString());
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
                proxyErr(ip + ":" + port);
            } finally {
                Info p = proxys.get(ip + ":" + port);
                p.processing.set(false);
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
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=============v602========");
        final ServerSocket manager = new ServerSocket(0);
        System.out.println("manage port: " + manager.getLocalPort());
        final LongfeiVote4DaCheng v = new LongfeiVote4DaCheng();
        v.reloadProxys();

        new Thread() {
            @Override
            public void run() {
                for (;;) {
                    try {
                        sleep(25 * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    v.reloadProxys();
                }
            }
        }.start();

        //manual reload
        new Thread() {
            @Override
            public void run() {
                for (;;) {
                    Socket s = null;
                    try {
                        s = manager.accept();
                        OutputStream out = s.getOutputStream();
                        out.write("start reload..\r\n".getBytes());
                        out.flush();
                        out.close();
                        v.reloadProxys();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            s.close();
                        } catch (Exception e2) {
                        }
                    }
                }
            }
        }.start();

        v.vote();
    }

}
