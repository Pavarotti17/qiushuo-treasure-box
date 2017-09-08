package qiushuo.treasurebox.toshl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import qiushuo.treasurebox.toshl.csv.IOReader;
import qiushuo.treasurebox.toshl.mysql.DBOutput;
import qiushuo.treasurebox.toshl.util.CommonUtil;

public class Toshl2 {
    public static void main(String[] args) throws Throwable {
        System.out.println("enter toshl report cvs path: /Users/shuo.qius/Downloads/toshl/toshl2_export_all.csv ");
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        final String path = sin.readLine().trim();
        System.out.println(path);

        final List<List<String>> rows = IOReader.readCsvFile(path, false, 1);

        rows.forEach(line -> {
            if (!"CNY".equals(line.get(8))) {
                throw new IllegalArgumentException("main currency must be CNY " + line);
            }
        });
        rows.forEach(line -> System.out.println(line));

        final Connection conn = DBOutput.createConn();

        Statement stmt = conn.createStatement();
        stmt.execute("delete from b");

        final PreparedStatement ps = conn.prepareStatement(
                "insert into b (id,gmt,acc,amt,cat,tags,sub_type,info) values (?,?,?,?,?,?,?,?)");

        rows.forEach(line -> {
            try {
                ps.clearParameters();
                int i = 0;
                String date = CommonUtil.convertDate(line.get(i++));
                String acc = line.get(i++);
                String cat = line.get(i++);
                String tags = line.get(i++);
                String extAmt = line.get(i++);
                String InAmt = line.get(i++);
                String cur = line.get(i++);
                String amt = line.get(i++);
                String mainCur = line.get(i++);
                String desc = line.get(i++);
                i = 1;
                {
                    ps.setString(i++, Bill.genBillId(date.replace("-", "").trim()));
                    ps.setString(i++, date);
                    ps.setString(i++, acc);
                    {
                        boolean exp = InAmt == null || InAmt.isEmpty() || CommonUtil.convertAmount(InAmt) == 0;
                        long amount = exp ? -CommonUtil.convertAmount(amt) : CommonUtil.convertAmount(amt);
                        ps.setLong(i++, amount);
                    }
                    ps.setString(i++, cat);
                    ps.setString(i++, tags);
                    ps.setString(i++, CommonUtil.extractOnlyPlaceHolder(desc));
                    ps.setString(i++, desc);
                }

                ps.execute();

                System.out.println(new StringBuilder().append(date)
                                                      .append(", ")
                                                      .append(acc)
                                                      .append(", ")
                                                      .append(cat)
                                                      .append(", ")
                                                      .append(tags)
                                                      .append(", ")
                                                      .append(amt));

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        conn.close();
    }
}
