import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class PhotoMngMain {

    private final static Random rnd = new Random();
    private final static DateFormat df_y_m_d = new SimpleDateFormat("yyyy-MM-dd");
    private final static DateFormat df_y = new SimpleDateFormat("yyyy");
    private final static DateFormat df_m = new SimpleDateFormat("MM");
    private final static DateFormat df_d = new SimpleDateFormat("dd");
    static {
        df_y_m_d.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Chongqing")));
        df_y.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Chongqing")));
        df_m.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Chongqing")));
        df_d.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Asia/Chongqing")));
    }

    private static final Set<String> excludeSuffix = new HashSet<>(Arrays.asList(".aae", ".ithmb", ".txt"));

//    public static void main(String[] args) throws Throwable{
//        File dir=new File("/Users/shuo.qius/Pictures/qiuxm/2020_三亚/untitled folder/0");
//        int i=0;
//        
//        for(File f:dir.listFiles()){
////            System.out.println(f.getAbsolutePath());
//            String name=f.getName();
//            System.out.println(name);
//            f.renameTo(new File(dir,(System.currentTimeMillis())+"_"+(++i)+".JPG"));
//        }
//    }
    
    public static void main(String[] args) throws Throwable {

//        final String fromDir = "/Users/shuo.qius/temp/temp_qs";
//        final String to__Dir = "/Users/shuo.qius/Pictures/input_iphone_qs";
        final String fromDir = "/Users/shuo.qius/temp/temp_qs";
        final String to__Dir = "/Users/shuo.qius/Pictures/input_iphone_qs";

        final String startFrom = "2020-04-21";

        organize(fromDir, to__Dir, startFrom);
    }

    /**
     * @param fromDir
     * @param toDir
     * @param fromDate e.g. "2019-08-02" and later include
     * @throws Throwable
     */
    private static void organize(final String fromDir, final String toDir, final String fromDate) throws Throwable {
        final File toRoot = new File(toDir);
        if (!toRoot.exists()) {
            toRoot.mkdirs();
        }
        if (!toRoot.isDirectory()) {
            throw new IllegalArgumentException("not dir: " + toDir);
        }

        final File fromFile = new File(fromDir);

        // dir
        if (fromFile.isDirectory()) {
            File[] children = fromFile.listFiles();
            for (File child : children) {
                organize(child.getAbsolutePath(), toDir, fromDate);
            }
            return;
        }

        // file

        if (isExclude(fromFile)) {
            return;
        }

        DateTag tag = parseLastModifyTime(fromFile);
        if (tag.format().compareTo(fromDate) < 0) {
            System.out.println("skiped: " + tag.format() + ": " + fromFile.getAbsolutePath());
            return;
        }
        System.out.println("dealing: " + tag.format() + ": " + fromFile.getAbsolutePath());

        File toDirDate = prepareTargetDir(toRoot, tag);

        File aaeFile = getMyLr(fromFile, ".aae");
        FilePair toFiles = toAsFile(fromFile, aaeFile, toDirDate);

        fromFile.renameTo(toFiles.img);
        if (aaeFile != null) {
            aaeFile.renameTo(toFiles.aae);
        }
        System.out.println("  -> " + toFiles.img.getAbsolutePath());
    }

    private static FilePair toAsFile(File source, File sourceAAE, File toDir) {
        File newF = new File(toDir, source.getName());
        if (newF.exists()) {
            String newName = insertRandom(source.getName());
            return new FilePair(
                    new File(toDir, newName),
                    sourceAAE == null ? null : new File(toDir, getFileNameNoExt(newName) + ".AAE"));
        }
        return new FilePair(
                newF,
                sourceAAE == null ? null : new File(toDir, getFileNameNoExt(newF.getName()) + ".AAE"));
    }

    private static boolean isExclude(File f) {
        if (f.getName().startsWith(".") || f.getName().toLowerCase().startsWith("Thumbs.db".toLowerCase())) {
            return true;
        }
        for (String suf : excludeSuffix) {
            if (f.getName().toLowerCase().endsWith(suf.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param pic
     * @return
     * @throws Throwable
     */
    private static File getMyLr(File pic, String lrSuffix) throws Throwable {
        int idxLast = pic.getName().lastIndexOf('.');
        if (idxLast <= 0) {
            return null;
        }
        final String prefix = pic.getName().substring(0, idxLast);
        File[] fs = pic.getParentFile().listFiles();
        for (File f : fs) {
            if (!f.isFile()) {
                continue;
            }
            if (f.getName().equals(pic.getName())) {
                continue;
            }
            if (f.getName().toLowerCase().startsWith(prefix.toLowerCase() + ".")) {
                if (f.getName().lastIndexOf('.') == prefix.length()) {
                    if (f.getName().toLowerCase().endsWith(lrSuffix.toLowerCase())) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    private static DateTag parseLastModifyTime(File f) {
        try {
            long minTime = 0;
            Metadata meta = ImageMetadataReader.readMetadata(f);
            for (Directory d : meta.getDirectories()) {
                if (!d.getName().toLowerCase().startsWith("exif")) {
                    continue;
                }
                for (Tag t : d.getTags()) {
                    if ("Date/Time".equalsIgnoreCase(t.getTagName())
                            || "Date/Time Original".equalsIgnoreCase(t.getTagName())
                            || "Date/Time Digitized".equalsIgnoreCase(t.getTagName())) {
                        long time = d.getDate(t.getTagType(), TimeZone.getTimeZone(ZoneId.of("Asia/Chongqing")))
                                     .getTime();
                        if (minTime <= 0 || minTime > time) {
                            minTime = time;
                        }
                    }
                }
            }
            if (minTime <= 0) {
                return new DateTag(f.lastModified());
            }
            return new DateTag(minTime);
        } catch (Exception e) {
            System.err.println("fail to parse meta: " + f.getAbsolutePath() + ", use OS default");
            return new DateTag(f.lastModified());
        }
    }

    private static File prepareTargetDir(File toRoot, DateTag tag) {
        File toDirYear = new File(toRoot, tag.year);
        if (!toDirYear.exists()) {
            toDirYear.mkdirs();
        }
        if (!toDirYear.isDirectory()) {
            throw new IllegalArgumentException("exist not dir: " + toDirYear.getAbsolutePath());
        }
        File toDirDate = new File(toDirYear, tag.format());
        if (!toDirDate.exists()) {
            toDirDate.mkdirs();
        }
        if (!toDirDate.isDirectory()) {
            throw new IllegalArgumentException("exist not dir: " + toDirDate.getAbsolutePath());
        }
        return toDirDate;
    }

    private static String insertRandom(String fileName) {
        int idxlast = fileName.lastIndexOf('.');
        if (idxlast <= 0) {
            return fileName + "_" + genRandomString();
        }
        return fileName.substring(0, idxlast) + "_" + genRandomString() + fileName.substring(idxlast);
    }

    private static String getFileNameNoExt(String fileName) {
        int idxlast = fileName.lastIndexOf('.');
        if (idxlast <= 0) {
            return fileName;
        }
        return fileName.substring(0, idxlast);
    }

    private static String genRandomString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; ++i) {
            int d = rnd.nextInt(36);
            if (d < 10) {
                char c = (char) ('0' + d);
                sb.append(c);
            } else {
                char c = (char) ('A' + d - 10);
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static class FilePair {
        final File img;
        final File aae;

        public FilePair(File img, File aae) {
            super();
            this.img = img;
            this.aae = aae;
        }

    }

    private static class DateTag {
        /** yyyy */
        final String year;
        /** MM */
        final String month;
        /** dd */
        final String day;

        public DateTag(long time) {
            Date date = new Date(time);
            this.year = df_y.format(date);
            this.month = df_m.format(date);
            this.day = df_d.format(date);
        }

        /**
         * @return MUST BE yyyy-MM-dd
         */
        public String format() {
            return year + "-" + month + "-" + day;
        }

        @Override
        public String toString() {
            return "DateTag [year=" + year + ", month=" + month + ", day=" + day + "]";
        }

    }

}
