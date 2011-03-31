/**
 * (created at 2011-3-30)
 */
package qiushuo.treasurebox.disksync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Decoder;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class VCFConvert {

    private static class Contact {

    }

    public static void main(String[] args) {
        try {
            File fromDir = new File("D:\\cell phone\\contact\\123\\123");
            File toDir = new File("D:\\cell phone\\contact\\123\\to");
            File[] fs = fromDir.listFiles();
            for (File f : fs) {
                if (f.isFile() && f.getName().endsWith(".vcf")) {
                    BufferedReader in = null;
                    PrintWriter out = null;
                    try {
                        in = new BufferedReader(new FileReader(f));
                        File to = new File(toDir, f.getName());
                        out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(to), "GBK"));
                        for (String line = null; (line = in.readLine()) != null;) {
                            if (line.trim().length() == 0) continue;

                            if (line.contains(";CHARSET=UTF-8")) {
                                line = StringUtils.replace(line, ";CHARSET=UTF-8", "");
                            }
                            if (line.contains(";ENCODING=8BIT")) {
                                line = StringUtils.replace(line, ";ENCODING=8BIT", "");
                            }
                            if(line.contains(";PREF")){
                                line = StringUtils.replace(line, ";PREF", "");
                            }
                            if (line.contains(";ENCODING=BASE64")) {
                                final int left = line.indexOf(";ENCODING=BASE64");
                                StringBuilder sb = new StringBuilder();
                                sb.append(line.substring(0, left));
                                String msg = line.substring(line.lastIndexOf(':') + 1);
                                BASE64Decoder decoder = new BASE64Decoder();
                                msg = new String(decoder.decodeBuffer(msg));
                                sb.append(line.substring(left + ";ENCODING=BASE64".length(), line.lastIndexOf(':') + 1));
                                sb.append(msg);
                                line=sb.toString();
                            }
                            if(line.startsWith("N;")||line.startsWith("N:")){
                                line+=";;;";
                            }out.println(line);
                        }
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            out.close();
                        } catch (Exception e2) {
                        }
                        try {
                            in.close();
                        } catch (Exception e2) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
