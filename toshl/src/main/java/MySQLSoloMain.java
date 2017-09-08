import org.apache.commons.lang.StringUtils;

public class MySQLSoloMain {
    /** every bill can has maximunly {@value #MAX_TAG} tags */
    public static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/toshl?characterEncoding=utf8";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    //    INSERT INTO `ipayment{db}`.`ipayment_sequence{tb}` (`name`, `value`, `min_value`, `max_value`, `step`, `memo`, `gmt_modified`, `gmt_create`) VALUES ('seq_gfas_msg_record', 0, 0, 99999999, 100, NULL, now(),now()),('seq_msg_record', 0, 0, 99999999999, 100, NULL, now(),now()),('seq_unique', 0, 0,  99999999999, 100, NULL, now(),now());
    //delete `ipayment{db}`.`ipayment_sequence{tb}` where name in ('seq_gfas_msg_record','seq_msg_record','seq_unique');

    //INSERT INTO `ipaycore_sequence{tb}` (`name`, `value`, `min_value`, `max_value`, `step`, `memo`, `gmt_create`, `gmt_modified`) VALUES('seq_msg_record_gfas', 0, 0, 99999999, 100, NULL, now(),now());
// delete from     `ipaycore_sequence{tb}` where name='seq_msg_record_gfas'

    
    public static void main(String[] args) throws Throwable {
//        final String template = "INSERT INTO `ipaycore_sequence{tb}` (`name`, `value`, `min_value`, `max_value`, `step`, `memo`, `gmt_create`, `gmt_modified`) VALUES('seq_msg_record_gfas', 0, 0, 99999999, 100, NULL, now(),now());";
                final String template = "delete from `ipaycore_sequence{tb}` where name='seq_msg_record_gfas'";
        for (int i = 0; i < 100; ++i) {
            String db = getString(i / 10);
            String tb = getString(i);
            String sql = StringUtils.replace(template, "{db}", db + "_0");
            sql = StringUtils.replace(sql, "{tb}", "_" + tb);
            System.out.println(sql);
        }
        for (int i = 0; i < 100; ++i) {
            String db = getString(i / 10);
            String tb = getString(i);
            String sql = StringUtils.replace(template, "{db}", db + "_0");
            sql = StringUtils.replace(sql, "{tb}", "_" + tb + "_t");
            System.out.println(sql);
        }
    }

    private static String getString(int i) {
        String dbStr = String.valueOf(i);
        if (dbStr.length() < 2) {
            dbStr = "0" + dbStr;
        }
        return dbStr;
    }

}
