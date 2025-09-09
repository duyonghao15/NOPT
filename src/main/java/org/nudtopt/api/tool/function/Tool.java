package org.nudtopt.api.tool.function;

import org.nudtopt.api.algorithm.Algorithm;
import org.nudtopt.api.constraint.Score;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tool {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static ThreadLocal<SimpleDateFormat> threadDateFormat = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {

        System.out.println(Boolean.valueOf("TRUE"));

    }


    //  1. ************ 深克隆对象函数 **************
    public static Object deepClone(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            // 将bos作为收集字节的数组中介
            oos = new ObjectOutputStream(bos);
            // 将传入参数object类写入bos中
            oos.writeObject(object);
            // 将读取到数据传入ObjectInpuStream
            ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // JDK1.7后引入 可以同时用|优化代码
            e.printStackTrace();
            return null;
        } finally {
            try {
                bos.close();
                oos.close();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//  1.2. ************ 二维数组深度克隆 **************
    public static double[][] clone(double[][] matrix) {
        if(matrix == null) {
            return null;
        }
        double[][] newMatrix = new double[matrix.length][matrix[0].length];
        for(int i = 0 ; i < matrix.length ; i ++) {
            newMatrix[i] = matrix[i].clone();
        }
        return newMatrix;
    }


//  2.1 ********** 创建txt文件 ************
    public static void writeFile(String path, String name, StringBuilder str) throws IOException {
        File file = new File(path + "/" + name);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("" + str);
        bw.close();
        System.out.println("txt文件创建成功：\n" + path + "\n");
    }


//  2.2 ********** 读取目录下所有文件 ************
    public static List<File> readAllFiles(String path) {
        List<File> fileList = new ArrayList<>();
        File[] files = new File(path).listFiles();
        if(files == null) {
            System.out.println("error: 路径不存在!");
        } else if (files.length == 0) {
            System.out.println("error: 路径中无文件!");
        } else {
            for(File file : files) {
                if(file.isFile()) {
                    // System.out.println("文件：" + tempList[i]);
                    fileList.add(file);
                }
                if(file.isDirectory()) {
                    // System.out.println("文件夹：" + tempList[i]);
                }
            }
        }
        return fileList;
    }


//  2.3 ********** 合并文件夹中所有csv **********
    public static void combineCsv(String path) throws IOException {
        List<File> fileList  = readAllFiles(path);
        StringBuilder str = new StringBuilder();
        for(File file : fileList) {
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line).append("\n");
            }
        }
        Exporter.writeFile(str, path + "/A", ".csv");
    }


//  3. ********** 首字母大写 ************
    public static String firstUpperCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }


//  4. ********** 首字母小写 ************
    public static String firstLowerCase(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }


//  *.0 ********** license ************
    public static int licence = (int)(Math.random() * 10);
    public static void checkLicence() {
        try {
            Date beginDate = new SimpleDateFormat("yyyy/MM/dd").parse("2019/6/1");
            Date endDate   = new SimpleDateFormat("yyyy/MM/dd").parse("2025/6/1");
            Date today     = new Date();
            if(today.after(beginDate) && today.before(endDate)) licence = 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


//  5.0 ********** 随机数 ************
    private static Random random = new Random();                            // 随机数(全局变量)
    private static ThreadLocal<Random> threadRandom = new ThreadLocal<>();  // 随机数(线程变量)
    public static ThreadLocal<Random> getRandom() {
        return threadRandom;
    }
    public static void setRandom(Random random) {
        Tool.random = random;
        Tool.threadRandom.set(random);
    }
    public static double random() {
        if(threadRandom.get() == null)    threadRandom.set(random);
        return threadRandom.get().nextDouble();
    }


//  5.1 ********** 从list中随机取值 ************
    public static <E> E randomFromList(List<E> list) {
        if(list.size() == 1)                        return list.get(0);
        if(list.get(0) instanceof Number && list.size() == 2) {
            double min = Math.min(((Number)list.get(0)).doubleValue(), ((Number)list.get(1)).doubleValue());
            double max = Math.max(((Number)list.get(0)).doubleValue(), ((Number)list.get(1)).doubleValue());
            double n = Tool.random() * (max - min) + min;
            if(list.get(0) instanceof Double)       return (E) Double.valueOf(n);
            if(list.get(0) instanceof Float)        return (E) Float.valueOf((float) n);
            if(list.get(0) instanceof Long)         return (E) Long.valueOf(Math.round(n));
            if(list.get(0) instanceof Integer)      return (E) Integer.valueOf((int) Math.round(n));
            else                                    return (E) (Number) n;
        }
        return list.get((int) (Tool.random() * list.size()));
    }


//  5.2 ********** 从nullable list中随机取值 ************
    public static <E> E randomFromNullableList(List<E> list, double probability) {
        int nullSize = (int) Math.max(1, list.size() * probability / (1 - probability));    // 在原list后面加至少1个null
        int n =  (int) (Tool.random() *  (list.size() + nullSize));
        if(n < list.size()) {
            return list.get(n);
        } else {
            return null;
        }
    }


//  5.3 ********** 从list中随机取n个值 ************
    public static <E> List<E> randomFromList(List<E> list, int num, boolean different) {
        if(num > list.size() && different)              return new ArrayList<>(list);    // 若要求取值超过列表长度的不同值, 只能全部返回
        List<E> elementList = new ArrayList<>();
        for(int i = 0 ; i < num ; i ++) {
            E element = randomFromList(list);
            if(different) {  // 若要求取值各不相同
                while (elementList.contains(element))   element = randomFromList(list);
            }
            elementList.add(element);
        }
        return elementList;
    }


//  6.1 ********** list实现FIFO ************
    public static void listFIFO(List list, int maxSize) {
        int size = list.size();
        if(maxSize >= 0 && size > maxSize) {
            for(int i = 0 ; i < size - maxSize ; i ++) {
                list.remove(0);               // 依次删除首位元素
            }
        }
    }
//  6.2 ********** list实现FILO ************
    public static void listFILO(List list, int maxSize) {
        int size = list.size();
        if(maxSize >= 0 && size > maxSize) {
            for(int i = 0 ; i < size - maxSize ; i ++) {
                list.remove(list.size() - 1); // 依次删除末位元素
            }
        }
    }


//  7. ********** 将list中嵌套的list展开 **********
    public static <E> List<E> openList(List<E> list) {
        List<E> newList = new ArrayList<>();
        for(E object : list) {
            if(object instanceof List) {
                newList.addAll((List) object);
            } else {
                newList.add(object);
            }
        }
        return newList;
    }


//  8.1 ********** a是否等于或包含b **********
    public static boolean equalOrContain(Object a, Object b) {
        if(a == b)                                              return true;
        else if(a instanceof List && ((List)a).contains(b))     return true;
        else                                                    return false;
    }


//  8.2 ********** a是否等于或包含b (元胞数组情况) **********
    public static boolean equalOrTupleContain(Object a, Object b) {
        if(equalOrContain(a, b)) {
            return true;
        } else if(a instanceof Object[]) {
            for(Object object : (Object[])a) {
                if(equalOrContain(object, b)) return true;
            }
            return false;
        } else {
            return false;
        }
    }


//  8.3 ********** a是否等于或包含b (元胞数组情况) **********
    public static <E> boolean equalOrTupleContain(List<E> aList, List<E> bList) {
        for(E a : aList) {
            for(E b : bList) {
                if(equalOrTupleContain(a, b)) {
                    return true;
                }
            }
        }
        return false;
    }


//  8.4 ********** 判断list是否为空 **********
    public static boolean isEmpty(List list) {
        if(list == null)            return true;
        else if(list.size() == 0)   return true;
        else                        return false;
    }


//  9. ********** 清除list中的null **********
    public static void removeNull(List list) {
        while (list.remove(null));
    }


//  10. ********** 对Map按value值降序排列 **********
    public static List<Map.Entry> sortMap(Map map, boolean increasing){
        List<Map.Entry> list = new ArrayList<Map.Entry>(map.entrySet());    // 1. 将map.entrySet()转换成list
        Collections.sort(list, (o1, o2) -> {                        // 2. 通过比较器来实现排序
            double o1d = Double.parseDouble(o1.getValue().toString());
            double o2d = Double.parseDouble(o2.getValue().toString());
            if(o1d > o2d)       return increasing ? 1 : -1;
            else if(o1d < o2d)  return increasing ? -1 : 1;
            else                return 0;
        });
        return list;
    }

//  11.1 ********** 输出一个数组的均值和方差 **********
    public static double[] getMeanAndVariance(double[] array) {
        double sum = 0;
        for(double a : array) {
            sum += a;
        }
        double mean = sum / array.length;
        double variance = 0;
        for(double a : array) {
            variance += Math.pow(a - mean, 2);
        }
        variance = variance / (array.length  -1);
        return new double[]{mean, variance};
    }

//  11.1 ********** 将list数组转化为array **********
    public static double[] listToArray(List list) {
        double[] array = new double[list.size()];
        for(int i = 0 ; i < list.size() ; i ++) {
            Object object = list.get(i);
            double number = 0;
            if(object instanceof Integer)   number = (Integer) object;
            if(object instanceof Long)      number = (Long)    object;
            if(object instanceof Double)    number = (Double)  object;
            if(object instanceof Float)     number = (Float)   object;
            array[i] = number;
        }
        return array;
    }


//  11.2 ********** 输出一个数组的均值和标准差 **********
    public static double[] getMeanAndStandard(double[] array) {
        double[] meanAndVariance = getMeanAndVariance(array);
        double mean = meanAndVariance[0];
        double variance = meanAndVariance[1];
        return new double[]{mean, Math.sqrt(variance)};
    }

//  12.1 ********** 根据名称获取成员变量值 **********
    public static Object getValue(Object object, String name) {
        try {
            Method method = object.getClass().getDeclaredMethod("get" + Tool.firstUpperCase(name));
            if(method.invoke(object) != null)   return method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }   return null;
    }


//  12.2 ********** 根据名称设置成员变量值 **********
    public static void setValue(Object object, String name, Object value) {
        try {
            Class variableClass = object.getClass().getDeclaredField(name).getType();
            Method method = object.getClass().getDeclaredMethod("set" + Tool.firstUpperCase(name), variableClass);
            method.invoke(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//  13.1 ********** 输出历史评分 **********
    public static void printHistoryScore(Algorithm algorithm, String path) {
        List<Score> scoreList = algorithm.getHistoryScoreList(0, false);
        List<Score> printList = new ArrayList<>();
        int pointNum = 100;  // 只画100个点
        double gap = scoreList.size() / pointNum;
        for(int i = 0 ; i < scoreList.size() ; i += gap) {
            Score score = scoreList.get(i);
            printList.add(score);
        }
        Collections.reverse(printList);
        StringBuilder text = new StringBuilder();
        for(Score score : printList) {
            text.append(score.getMeanScore()).append("\n");
        }
        try {
            Exporter.writeFile(text, path + algorithm + "-" + scoreList.get(0).getMeanScore(), ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//  14.0 ********** 获得时间 **********
    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }


//  14.1 ********** 获得日期的年月日 **********
    public static List<Long> getTime(Date date) {
        List<Long> timeList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-EEE");
        String[] time = format.format(date).toString().split("-");
        timeList.add(Long.parseLong(time[0]));  // 年
        timeList.add(Long.parseLong(time[1]));  // 月
        timeList.add(Long.parseLong(time[2]));  // 日
        timeList.add(Long.parseLong(time[3]));  // 时
        timeList.add(Long.parseLong(time[4]));  // 分
        timeList.add(Long.parseLong(time[5]));  // 秒
        switch (time[6]) {                      // 星期几
            case "星期一": timeList.add(1L); break;
            case "星期二": timeList.add(2L); break;
            case "星期三": timeList.add(3L); break;
            case "星期四": timeList.add(4L); break;
            case "星期五": timeList.add(5L); break;
            case "星期六": timeList.add(6L); break;
            case "星期日": timeList.add(7L); break;
            default:      System.out.println("error: 星期几匹配错误!"); break;
        }
        return timeList;
    }


//  14.2 ********** 获得日期当日0点 **********
    public static synchronized Date getZeroTime(Date date) {
        if(threadDateFormat.get() == null)  threadDateFormat.set(dateFormat);
        SimpleDateFormat format = threadDateFormat.get();
        try {
            if(date == null)    return format.parse(format.format(System.currentTimeMillis()));
            else                return format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }   return null;
    }


//  14.3 ********** 根据大窗口裁剪地影区, 返回阳照区窗口列表 **********
    public static List<long[]> splitSunWindow(long firstTime, long lastTime) {
        Date firstDate = new Date(firstTime);
        Date zeroDate  = getZeroTime(firstDate);
        long zeroTime  = zeroDate.getTime();
        List<long[]> windowList = new ArrayList<>();
        while (zeroTime < lastTime) {
            long sunBeginTime = zeroTime + 6  * 3600 * 1000; // 早上六点
            long sunEndTime   = zeroTime + 18 * 3600 * 1000; // 下午六点
            long beginTime = Math.max(firstTime, sunBeginTime);
            long endTime   = Math.min(lastTime, sunEndTime);
            if(beginTime < endTime)   windowList.add(new long[]{beginTime, endTime});
            zeroTime += 24 * 3600 * 1000;
        }
        return windowList;
    }


//  15 ********** 轮盘赌函数 **********
    public static int roulette(List<Double> weightList) {
        return roulette(weightList, Tool.random());
    }
    public static int roulette(List<Double> weightList, double random) {
        // 1. 统计权重总和
        double weightSum = 0 ;
        for(double weight : weightList) weightSum += weight;
        // 2. 全是0, 随机输出
        if(weightSum == 0)              return (int)(random * weightList.size());
        // 3. 轮盘赌
        double probability = weightList.get(0) / weightSum; // 首个概率
        for(int i = 0 ; i < weightList.size() ; i ++) {
            double weight = weightList.get(i) / weightSum;  // 标准化的权重
            if(random < probability || i == weightList.size() - 1)   return i;
            probability += weight;
        }
        return weightList.size() - 1;
    }


    /** 16
     * 引射线法: 判断一个多边形(顺时针或逆时针坐标)是否覆盖一个点
     * @param x  多边形顶点x坐标
     * @param y  多边形顶点y坐标
     * @param x0 目标点x坐标
     * @param y0 目标点y坐标
     * @return   是否包含
     */
    public static boolean cover(double[] x, double[] y, double x0, double y0) {
        int crossings = 0;
        for(int i = 0 ; i < x.length ; i ++) {
            double x1 = x[i];                                           // 边的顶点坐标
            double y1 = y[i];
            double x2 = i != x.length - 1 ? x[i + 1] : x[0];
            double y2 = i != x.length - 1 ? y[i + 1] : y[0];
            if(x1 == x0 && y1 == y0)    return true;                    // 点在顶点上, 视为包含
            double slope = x2 == x1 ? 999999999 : (y2 - y1) / (x2 - x1);// i和i+1两点的斜率
            double y_cross = slope * (x0 - x1) + y1;
            if(x0 >= Math.min(x1, x2) && x0 <= Math.max(x1, x2)) {      // x0位于x1和x2之间
                if(y0 == y_cross)   return true;                        // 点在边上, 视为包含
                if(y0 <  y_cross)   crossings ++;                       // 向上引射线: 交叉, 顶点数+1
            }
        }
        if(crossings % 2 == 0)      return false;                       // 偶数, 不包含
        else                        return true;                        // 奇数, 包含
    }


//  17 判断两个list所含元素是否相同 (长度相同且相互包含)
    public static <E> boolean isListSame(List<E> list_1, List<E> list_2) {
        return list_1.size() == list_2.size() && list_1.containsAll(list_2) && list_2.containsAll(list_1);
    }

//  18 获取桌面绝对路径
    public static String getDesktopPath() {
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        return desktopDir.getAbsolutePath();
    }

//  19 计算直线距离
    public static double getDistance(double x1, double y1, double x2, double y2) {
        double distance = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        return Math.sqrt(distance);
    }
    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double distance = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2);
        return Math.sqrt(distance);
    }

//  20.1 取两个数字集合的交集
    public static double[] getCrossWindow(double beginTime1, double endTime1, double beginTime2, double endTime2) {
        double beginTime = Math.max(beginTime1, beginTime2);
        double endTime   = Math.min(endTime1, endTime2);
        if(beginTime <= endTime)    return new double[]{beginTime, endTime};
        else                        return null;
    }

//  20.2 取两个数字集合的并集
    public static double[] getCombineWindow(double beginTime1, double endTime1, double beginTime2, double endTime2) {
        double beginTime = Math.min(beginTime1, beginTime2);
        double endTime   = Math.max(endTime1, endTime2);
        return new double[]{beginTime, endTime};
    }

//  20.3 取两个list的交集
    public static <E> List<E> getCrossList(List<E> list1, List<E> list2) {
        List<E> crossList = new ArrayList<>();
        for(int i = 0 ; i < list1.size() ; i ++) {
            for(int j = 0 ; j < list2.size() ; j ++) {
                if(list1.get(i) == list2.get(j))    crossList.add(list1.get(i));
            }
        }
        return crossList;
    }

//  21 sigmod函数
    public static double sigmoid(double num) {
        return sigmoid(num, "tan-sigmoid");
    }
    public static double sigmoid(double num, String type) {
        switch (type) {
            case "log-sigmoid": return 1 / (1 + Math.exp(- num));
            case "tan-sigmoid": return 2 / (1 + Math.exp(- num * 2)) - 1;
            case "relu":        return Math.max(0, num);
            default:            return num;
        }
    }


    /**
     * 保留n位小数 (四舍五入)
     * @param n 小数点后位数
     */
    public static double round(double num, int n) {
        double scale = Math.pow(10, n);
        return Math.round(num * scale) / scale;
    }


    /** 23
     * 经纬高转换为xyz
     * @param L 经度
     * @param B 纬度
     * @param H 高度
     */
    public static double[] getEarthXYZ(double L, double B, double H) {
        double R = 6378137.0;               // 地球半径(m)
        double e = 0.00669438002290;	    // 转换为弧度
        L = L * Math.PI / 180;
        B = B * Math.PI / 180;
        double fac1 = 1 - e * Math.sin(B) * Math.sin(B);
        R = R / Math.sqrt(fac1);            // 卯酉圈曲率半径

        double x = (R + H) * Math.cos(B) * Math.cos(L);
        double y = (R + H) * Math.cos(B) * Math.sin(L);
        double z = (R * (1 - e) + H) * Math.sin(B);
        return new double[]{x, y, z};
    }


//  24 计算两个窗口间隔或重叠时间
    public static double getIntervalTime(double beginTime1, double endTime1, double beginTime2, double endTime2) {
        if(beginTime2 >= endTime1)  return beginTime2 - endTime1;     // 1-2的间隔时间 (>0)
        if(beginTime1 >= endTime2)  return beginTime1 - endTime2;     // 2-1的间隔时间 (>0)
        if(beginTime2 < endTime1)   return beginTime2 - endTime1;     // 1-2的重叠时间 (<0)
        else                        return beginTime1 - endTime2;     // 2-1的重叠时间 (<0)
    }
    public static double getIntervalTime(Window window1, Window window2) {
        return getIntervalTime(window1.getBeginTime(), window1.getEndTime(), window2.getBeginTime(), window2.getEndTime());
    }


    /** 25
     * 求过中心点的直线, 与某凸多边形的交点坐标
     * @param x0 中心点经度
     * @param y0 中心点纬度
     * @param k0 斜率/轨道倾角
     * @param x  多边形经度集合
     * @param y  多边形纬度集合
     * @return   交点坐标[x1, y1, x2, y2]
     */
    public static double[] getCrossPoint(double x0, double y0, double k0, double[] x, double[] y) {
        List<double[]> crossPointList = new ArrayList<>();
        for(int i = 0 ; i < x.length ; i ++) {
            double x1 = x[i];                                           // 边的顶点坐标
            double y1 = y[i];
            double x2 = i != x.length - 1 ? x[i + 1] : x[0];
            double y2 = i != x.length - 1 ? y[i + 1] : y[0];
            double[] crossPoint = getCrossPoint(x0, y0, k0, x1, y1, x2, y2);
            if(crossPoint != null)      crossPointList.add(crossPoint);
        }
        // 有2个以上交点时（一般为2个，多个也是2个重叠的点）
        if(crossPointList.size() >= 2) {
            double[] point_1 = crossPointList.get(0);
            double[] point_2 = crossPointList.get(1);
            return new double[]{point_1[0], point_1[1], point_2[0], point_2[1]};    // 返回 x1, y1, x2, y2
        } else {
            return null;
        }
    }


    /** 26
     * 求过中心点的直线，与某线段的交点
     * @param x0 中心点经度
     * @param y0 中心点纬度
     * @param k0 斜率/轨道倾角 //
     * @param x1 线段端点1经度
     * @param y1 线段端点1纬度 //
     * @param x2 线段端点2经度
     * @param y2 线段端点2纬度
     * @return   交点坐标[x, y]
     */
    public static double[] getCrossPoint(double x0, double y0, double k0, double x1, double y1, double x2, double y2) {
        double b0 = y0 - k0 * x0;               // 过中心点直线 y = k0 * x + b0
        double x;
        double y;
        if(x1 != x2) {
            double k1 = (y2 - y1) / (x2 - x1);  // 线段        y = k1 * x + b1
            double b1 = (x2 * y1 - x1 * y2) / (x2 - x1);
            if(k0 == k1)        return null;    // 两条线平行, 无交点
            x = (b1 - b0) / (k0 - k1);          // 交点 x
        } else {
            x = x1;
        }
        y = k0 * x + b0;
        x = round(x, 3);                     // 保留3位小数(四舍五入), 防止精度误差
        y = round(y, 3);
        // 判断交点是否在直线范围以内
        if(x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
           y >= Math.min(y1, y2) && y <= Math.max(y1, y2)) {
            return new double[]{x, y};          // 返回 x, y
        } else {
            return null;
        }
    }


    /** 26.2
     * 求过中心点的直线，与某线段的垂线交点
     * @param x0 中心点经度
     * @param y0 中心点纬度
     * @param x1 线段端点1经度
     * @param y1 线段端点1纬度 //
     * @param x2 线段端点2经度
     * @param y2 线段端点2纬度
     * @return   交点坐标[x, y]
     */
    public static double[] getVerticalCrossPoint(double x0, double y0, double x1, double y1, double x2, double y2) {
        double k0;
        if(x1 == x2) {
            k0 = 0;
        } else {
            double k = (y2 - y1) / (x2 - x1);
            k0 = -1 / k;
        }
        double[] crossPoint = getCrossPoint(x0, y0, k0, x1, y1, x2, y2);
        return crossPoint;
    }


    /** 27
     * 根据给定条带及多边形区域, 对条带进行裁剪、延长, 直到条带与区域刚好交点
     * @param x1 条带左上经度
     * @param y1 条带左上纬度 //
     * @param x2 条带右上经度
     * @param y2 条带右上纬度 //
     * @param x3 条带右下经度
     * @param y3 条带右下纬度 //
     * @param x4 条带左下经度
     * @param y4 条带左下纬度
     * @param startTime 条带开始时间
     * @param endTime   条带结束时间
     * @param x  区域经度集合(顺时针或逆时针)
     * @param y  区域纬度集合
     * @return   修正后的条带坐标[x1, y1, x2, y2, x3, y3, x4, y4, startTime, endTime]
     */
    public static double[] getCrossStripe(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double startTime, double endTime, double[] x, double[] y) {
        // 1. 根据条带两边与区域取交点, 计算修正比例
        double[] ratios_14 = getCrossExtendRatio(x1, y1, x4, y4, x, y);     // 1->4边与多边形相交的延长比例
        double[] ratios_23 = getCrossExtendRatio(x2, y2, x3, y3, x, y);     // 2->3边与多边形相交的延长比例
        if(ratios_14 == null && ratios_23 == null)  return null;            // 无交点, 直接返回
        double ratio_1 = Math.max(ratios_14 != null ? ratios_14[0] : -999, ratios_23 != null ? ratios_23[0] : -999);    // 上方延长比例 (开始时间方向)
        double ratio_2 = Math.max(ratios_14 != null ? ratios_14[1] : -999, ratios_23 != null ? ratios_23[1] : -999);    // 下方延长比例 (结束时间方向)
        // 2. 修正坐标
        double new_x1 = (x1 + x4) / 2 + (x1 - x4) / 2 * ratio_1;    // x1
        double new_y1 = (y1 + y4) / 2 + (y1 - y4) / 2 * ratio_1;
        double new_x2 = (x2 + x3) / 2 + (x2 - x3) / 2 * ratio_1;    // x2
        double new_y2 = (y2 + y3) / 2 + (y2 - y3) / 2 * ratio_1;
        double new_x3 = (x2 + x3) / 2 - (x2 - x3) / 2 * ratio_2;    // x3
        double new_y3 = (y2 + y3) / 2 - (y2 - y3) / 2 * ratio_2;
        double new_x4 = (x1 + x4) / 2 - (x1 - x4) / 2 * ratio_2;    // x4
        double new_y4 = (y1 + y4) / 2 - (y1 - y4) / 2 * ratio_2;
        // 3. 修正时间
        double newStartTime = (startTime + endTime) / 2 + (startTime - endTime) / 2 * ratio_1;
        double newEndTime   = (startTime + endTime) / 2 - (startTime - endTime) / 2 * ratio_2;
        return new double[]{new_x1, new_y1, new_x2, new_y2, new_x3, new_y3, new_x4, new_y4, newStartTime, newEndTime};
    }


    /** 27.2
     * 根据给定线段及多边形区域, 对直线进行裁剪、延长, 直到线段与区域刚好交点
     * @param x1 线段端点1经度
     * @param y1 线段端点1纬度
     * @param x2 线段端点2经度
     * @param y2 线段端点2纬度
     * @param x  区域经度集合(顺时针或逆时针)
     * @param y  区域纬度集合
     * @return   线段端点1和2的延长比例[ratio1, ratio2]
     */
    public static double[] getCrossExtendRatio(double x1, double y1, double x2, double y2, double[] x, double[] y) {
        double k;   // 条带斜率
        if(x1 != x2)    k = (y1 - y2) / (x1 - x2);
        else            k = 9999999999D;
        double x0 = 0.5 * (x1 + x2);
        double y0 = 0.5 * (y1 + y2);
        double[] crossPoints = getCrossPoint(x0, y0, k, x, y);  // k斜率直线与多边形交点
        if(crossPoints == null)     return null;                // 无交点, 直接返回
        double cross_1_x;   // 交点1, 约定靠上方向 (开始时间方向)
        double cross_1_y;
        double cross_2_x;   // 交点2, 约定靠下方向 (结束时间方向)
        double cross_2_y;
        if((x1 - x2) * (crossPoints[0] - crossPoints[2]) > 0) { // 同号 (同方向)
            cross_1_x = crossPoints[0];  cross_1_y = crossPoints[1];
            cross_2_x = crossPoints[2];  cross_2_y = crossPoints[3];
        } else {
            cross_1_x = crossPoints[2];  cross_1_y = crossPoints[3];
            cross_2_x = crossPoints[0];  cross_2_y = crossPoints[1];
        }
        // 2. 计算延长(修正)比例, 用y(纬度)方向修正, 精度更高
        double ratio_1;
        double ratio_2;
        if(Math.abs(k) > 0.01) {  // 一般升/降轨卫星条带斜率都较大, 用y方向计算误差小
            if(y1 - y0 != 0)    ratio_1 = (cross_1_y - y0) / (y1 - y0);  // 上方延长比例 (开始时间方向)
            else                ratio_1 = 9999999999D;
            if(y2 - y0 != 0)    ratio_2 = (cross_2_y - y0) / (y2 - y0);  // 下方延长比例 (结束时间方向)
            else                ratio_2 = 9999999999D;
        } else {        // todo 但如果k斜率趋于0, 容易产生精度误差, 故用x方向计算
            if(x1 - x0 != 0)    ratio_1 = (cross_1_x - x0) / (x1 - x0);  // 上方延长比例 (开始时间方向)
            else                ratio_1 = 9999999999D;
            if(x2 - x0 != 0)    ratio_2 = (cross_2_x - x0) / (x2 - x0);  // 下方延长比例 (结束时间方向)
            else                ratio_2 = 9999999999D;
        }
        return new double[]{ratio_1, ratio_2};
    }



    /** 28
     * 对矩形(顺时针或逆时针坐标)进行缩放
     * @param ratio     缩放比例
     * @param direction 缩放方向
     * @return [x1, y1, x2, y2, x3, y3, x4, y4]
     */
    public static double[] scale(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double ratio, String direction) {
        if(ratio == 0)  return new double[]{x1, y1, x2, y2, x3, y3, x4, y4};
        double new_x1;  double new_y1;
        double new_x2;  double new_y2;
        double new_x3;  double new_y3;
        double new_x4;  double new_y4;
        if(direction.contains("1->4")) {                        // 方向1:
            new_x1 = (x1 + x4) / 2 + (x1 - x4) / 2 * ratio;     // 1
            new_y1 = (y1 + y4) / 2 + (y1 - y4) / 2 * ratio;     // ↓
            new_x4 = (x1 + x4) / 2 - (x1 - x4) / 2 * ratio;     // 4
            new_y4 = (y1 + y4) / 2 - (y1 - y4) / 2 * ratio;
            new_x2 = (x2 + x3) / 2 + (x2 - x3) / 2 * ratio;     // 2
            new_y2 = (y2 + y3) / 2 + (y2 - y3) / 2 * ratio;     // ↓
            new_x3 = (x2 + x3) / 2 - (x2 - x3) / 2 * ratio;     // 3
            new_y3 = (y2 + y3) / 2 - (y2 - y3) / 2 * ratio;
        } else if (direction.contains("1->2")) {                // 方向2:
            new_x1 = (x1 + x2) / 2 + (x1 - x2) / 2 * ratio;     // 1
            new_y1 = (y1 + y2) / 2 + (y1 - y2) / 2 * ratio;     // ↓
            new_x2 = (x1 + x2) / 2 - (x1 - x2) / 2 * ratio;     // 2
            new_y2 = (y1 + y2) / 2 - (y1 - y2) / 2 * ratio;
            new_x4 = (x4 + x3) / 2 + (x4 - x3) / 2 * ratio;     // 4
            new_y4 = (y4 + y3) / 2 + (y4 - y3) / 2 * ratio;     // ↓
            new_x3 = (x4 + x3) / 2 - (x4 - x3) / 2 * ratio;     // 3
            new_y3 = (y4 + y3) / 2 - (y4 - y3) / 2 * ratio;
        } else {                                                // 中心方向:
            new_x1 = (x1 + x3) / 2 + (x1 - x3) / 2 * ratio;     // 1
            new_y1 = (y1 + y3) / 2 + (y1 - y3) / 2 * ratio;     // ↓
            new_x3 = (x1 + x3) / 2 - (x1 - x3) / 2 * ratio;     // 3
            new_y3 = (y1 + y3) / 2 - (y1 - y3) / 2 * ratio;
            new_x4 = (x4 + x2) / 2 + (x4 - x2) / 2 * ratio;     // 4
            new_y4 = (y4 + y2) / 2 + (y4 - y2) / 2 * ratio;     // ↓
            new_x2 = (x4 + x2) / 2 - (x4 - x2) / 2 * ratio;     // 2
            new_y2 = (y4 + y2) / 2 - (y4 - y2) / 2 * ratio;
        }
        return new double[]{new_x1, new_y1, new_x2, new_y2, new_x3, new_y3, new_x4, new_y4};
    }


    /** 29
     * 插值函数(线性)
     * @param input_1   输入值1
     * @param output_1  输出值1
     * @param input_2   输入值2
     * @param output_2  输出值2
     * @param input     插值
     * @return          输出值
     */
    public static double insert(double input_1, double output_1, double input_2, double output_2, double input) {
        double k;
        if(input_2 != input_1)  k = (output_2 - output_1) / (input_2 - input_1);
        else                    k = 999999999D;
        double output = output_1 + k * (input - input_1);
        return output;
    }


    /** 30
     * 将条带(四点坐标)平移至某中心位置
     * @param xTarget 目标中心经度
     * @param yTarget 目标中心纬度
     * @return        平移后的四点坐标
     */
    public static double[] move(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double xTarget, double yTarget) {
        double x0 = (x1 + x2 + x3 + x4) / 4;    // 中心点经度
        double y0 = (y1 + y2 + y3 + y4) / 4;    // 中心点纬度
        double xDelta = xTarget - x0;           // 与目标点的经度差值
        double yDelta = yTarget - y0;           // 与目标点的纬度差值
        return new double[]{x1 + xDelta, y1 + yDelta, x2 + xDelta, y2 + yDelta, x3 + xDelta, y3 + yDelta, x4 + xDelta, y4 + yDelta};
    }


    /** 31
     * 将条带(四点坐标)平移至某中心位置 (仅方位向, 即x1 -> x4方向)
     * @param xTarget 目标中心经度
     * @param yTarget 目标中心纬度
     * @return        平移后的四点坐标
     */
    public static double[] moveLongitude(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double xTarget, double yTarget) {
        double k;
        if(x1 != x4)    k = (y1 - y4) / (x1 - x4);
        else            k = 999999999D;
        double k2 = -1/k;       // 两线垂直
        // 线1 y = k * x + b1    (与条带方向平行, 过目标点的直线)
        double b1 = yTarget - k * xTarget;
        // 线2 y = k2 * x + b2   (与条带方向垂直, 过条带中心的直线)
        double b2 = (y1 + y4) / 2 - k2 * (x1 + x4) / 2;
        // 两线交点坐标
        double x0 = (b2 - b1) / (k - k2);       // 交点经度
        double y0 = k * x0 + b1;                // 交点纬度
        double xDelta = xTarget - x0;           // 与交点的经度差值
        double yDelta = yTarget - y0;           // 与交点的纬度差值
        return new double[]{x1 + xDelta, y1 + yDelta, x2 + xDelta, y2 + yDelta, x3 + xDelta, y3 + yDelta, x4 + xDelta, y4 + yDelta};
    }


    /** 32
     * 按经度方向再多边形区域内等距离拾取点, 返回n个点
     * @param xList 多边形经度集
     * @param yList 多边形纬度集
     * @param num   需要返回的等距离中间点数量(不包括左右两端, 建议取5-10)
     * @return
     */
    public static List<double[]> pickupMidPoints(double[] xList, double[] yList, int num) {
        double xMin = 999;                      // 记录最小经度
        double xMax = -999;                     // 记录最大经度
        double yMin = 999;                      // 记录最小纬度
        double yMax = -999;                     // 记录最大纬度
        for(int i = 0 ; i < xList.length ; i ++) {
            double x = xList[i];
            double y = yList[i];
            xMin = Math.min(x, xMin);
            xMax = Math.max(x, xMax);
            yMin = Math.min(y, yMin);
            yMax = Math.max(y, yMax);
        }
        double y = 0.5 * (yMin + yMax);         // 纬度取中点纬度(平均值)
        double step = (xMax - xMin) / (num + 1);// 计算间距
        step = Math.max(step, 1);               // 间距最小为1°, 防止区域过小, 取点过多的情况
        // 从最小经度 -> 最大经度, 等间距地生成num个点
        List<double[]> pointList = new ArrayList<>();
        for(double x = xMin + step ; x <= xMax ; x += step) {
            double[] point = new double[]{x, y};
            pointList.add(point);
        }
        return pointList;
    }


    // ############################# 2022.08新增 ###########################################

    /** 33
     * @param meanMotion 平均运动速率n（圈/每天）
     * @return           半长轴
     */
    public static double meanMotion2SemiAxis2(double meanMotion) {
        double mu = 3.986004415 * Math.pow(10, 5);
        double n = meanMotion * 2 * Math.PI / (24 * 3600);
        double semiAxis = Math.pow(mu / n / n, 1.0/3);
        return semiAxis;
    }


    /** 34
     * 平均运动速率与半长轴转换
     * @param meanMotion 平均运动速率 (圈/每天)
     * @return           半长轴
     */
    public static double meanMotion2SemiAxis(double meanMotion) {
        double M = 5.965 * Math.pow(10, 24);        // 地球质量 (kg)
        double G = 6.6740831 * Math.pow(10, -11);   // 万有引力常数G (N·m²/kg²)
        double period = 3600 * 24 / meanMotion;     // 轨道周期 (s)
        double semiAxis = Math.pow((M * G / (4 * Math.PI * Math.PI) * period * period), 1.0/3);  // 半长轴 (m)
        return semiAxis  / 1000;                    // 单位由 m 转为 km
    }
    public static double semiAxis2MeanMotion(double semiAxis) {
        double M = 5.965 * Math.pow(10, 24);        // 地球质量 (kg)
        double G = 6.6740831 * Math.pow(10, -11);   // 万有引力常数G (N·m²/kg²)
        semiAxis = semiAxis * 1000;                 // 单位由 km 转为 m
        double period = Math.sqrt(Math.pow(semiAxis, 3) / M / G * (4 * Math.PI * Math.PI));     // 轨道周期 (s)
        double meanMotion = 3600 * 24 / period;     // 平均运动速率 (圈/每天)
        return meanMotion;
    }


    /** 35
     * 将日期转换成JuLian
     * @param date 日期
     * @return     儒略日
     */
    public static double getJulianDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        int a = (14 - month) / 12;
        int y = year + 4800 - a;
        int m = month + 12 * a - 3;
        int jdn = day + (153 * m + 2)/5 + 365*y + y/4 - y/100 + y/400 - 32045;
        double delta = (hour * 3600 + minute * 60 + second + millisecond / 1000.0) / (3600 * 24);
        return jdn + delta;
    }


    /** 36
     * 最短路径算法
     * @param edgeMatrix i->j的边的权重矩阵
     */
    public static double[][] floydWashall(Double[][] edgeMatrix){
        int size = edgeMatrix.length;
        double[][] D = new double[size][size];  // 距离矩阵
        for (int i = 0; i < D.length; i++) {
            for (int j = 0; j < D.length; j++) {
                if (edgeMatrix[i][j] != null) {
                    D[i][j] = edgeMatrix[i][j];
                }else {
                    if (i == j) {
                        D[i][j] = 0;
                    }else {
                        D[i][j] = 100000000L;   // 代表不可达
                    }
                }
            }
        }
        for (int k = 0; k < D.length; k++) {
            for (int i = 0; i < D.length; i++) {
                for (int j = 0; j < D.length; j++) {
                    D[i][j] = Math.min(D[i][j], D[i][k] + D[k][j]);
                }
            }
        }
        for (int i = 0; i < edgeMatrix.length; i++) {
            for (int j = 0; j < edgeMatrix.length; j++) {
                System.out.print(D[i][j] + "\t");
            }
            System.out.println();
        }
        return D;
    }


    /** 38
     * 经纬高转地固坐标系XYZ
     * @param lon 经度(°)
     * @param lat 纬度(°)
     * @param alt 高度(m)
     * @return x, y, z(m)
     */
    public static double[] LLAToXYZ(double lon, double lat, double alt){
        double d2r = Math.PI / 180;
        double a = 6378137.0;               // 椭球长半轴
        double f = 298.257223563;			// 扁率倒数
        double b = a - a / f;
        double e = Math.sqrt(a * a - b * b) / a;
        double L = lon * d2r;
        double B = lat * d2r;
        double H = alt;
        double N = a / Math.sqrt(1 - e * e * Math.sin(B) * Math.sin(B));
        double x = (N + H) * Math.cos(B) * Math.cos(L);
        double y = (N + H) * Math.cos(B) * Math.sin(L);
        double z = (N * (1 - e * e) + H) * Math.sin(B);
        return new double[]{x, y, z};
    }


    /** 39
     * 地固坐标系XYZ转经纬高
     * @param x (m)
     * @param y (m)
     * @param z (m)
     * @return 经度(°), 纬度(°), 高度(m)
     */
    public static double[] XYZToLLA(double x, double y, double z) {
        double epsilon = 0.000000000000001;
        double r2d = 180 / Math.PI;
        double a = 6378137.0;		        //椭球长半轴
        double f = 298.257223563;			//扁率倒数
        double b = a - a / f;
        double e = Math.sqrt(a * a - b * b) / a;
        double curB = 0;
        double N = 0;
        double calB = Math.atan2(z, Math.sqrt(x * x + y * y));
        int counter = 0;
        while (Math.abs(curB - calB) * r2d > epsilon  && counter < 25) {
            curB = calB;
            N = a / Math.sqrt(1 - e * e * Math.sin(curB) * Math.sin(curB));
            calB = Math.atan2(z + N * e * e * Math.sin(curB), Math.sqrt(x * x + y * y));
            counter++;
        }
        double lon = Math.atan2(y, x) * r2d;
        double lat = curB * r2d;
        double alt = z / Math.sin(curB) - N * (1 - e * e);
        return new double[]{lon, lat, alt};
    }


    /** 40
     * 矢量化向量
     * @return 长度为1的向量矢量
     */
    public static double[] vector(double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        double x0 = x / length;
        double y0 = y / length;
        double z0 = z / length;
        return new double[]{x0, y0, z0};
    }


    /** 41
     * 计算两个向量之间的夹角
     * @return 夹角(°)
     */
    public static double calIncludedAngle(double x1, double y1, double z1, double x2, double y2, double z2) {
        double value = (x1 * x2 + y1 * y2 + z1 * z2) / (Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1) * Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2));   // 余弦值
        double angle = Math.toDegrees(Math.acos(value));    // 角度
        return angle;
    }


    /** 42
     * 获得欧拉旋转矩阵
     * @param angle 旋转角度(°)
     * @param axis  旋转轴(x, y, z)
     * @return      绕不同轴旋转的旋转矩阵
     */
    public static double[][] eulerRotationMatrix(double angle, String axis) {
        double[][] matrix;
        angle = Math.toRadians(angle);  // 转换为弧度
        switch (axis) {
            default:
            case "x":
                matrix = new double[][] { {1,  0,  0},
                        {0,  Math.cos(angle),  -Math.sin(angle)},
                        {0,  Math.sin(angle),   Math.cos(angle)}};
                break;
            case "y":
                matrix = new double[][] { {Math.cos(angle),  0,  Math.sin(angle)},
                        {0,  1,  0},
                        {-Math.sin(angle),  0,  Math.cos(angle)}};
                break;
            case "z":
                matrix = new double[][] { {Math.cos(angle),  -Math.sin(angle),  0},
                        {Math.sin(angle),  Math.cos(angle),  0},
                        {0,  0,  1}};
                break;
        }
        return matrix;
    }


    /** 43
     * 矩阵乘法
     */
    public double[][] multiMatrix(int A[][], int B[][]) {
        if(A[0].length != B.length) {
            System.out.println("矩阵不像容");
            return null;
        }
        double[][] C = new double[A.length][B[0].length];
        for(int i = 0 ; i < A.length ; i ++) {
            for(int j = 0 ; j < B[0].length ; j ++) {
                for(int t = 0 ; t < A[0].length ; t ++) {
                    C[i][j] += A[i][t] * B[t][j];
                }
            }
        }
        return C;
    }




    /**
     * 存储结构为<key, list>的map, 往map中key对应的list增加成员 (若map无key, 则先自动添加)
     * @param map     待修改的map
     * @param key     键值
     * @param element 待增加的list中的元素
     * @param <K>     键值的类型
     * @param <E>     待增加的元素的类型
     */
    public static <K, E> void mapListAdd(Map<K, List<E>> map, K key, E element) {
        if(!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(element);
    }


    /**
     * 存储结构为<key, list>的map, 往map中key对应的set增加成员 (若map无key, 则先自动添加)
     * @param map     待修改的map
     * @param key     键值
     * @param element 待增加的list中的元素
     * @param <K>     键值的类型
     * @param <E>     待增加的元素的类型
     */
    public static <K, E> void mapSetAdd(Map<K, Set<E>> map, K key, E element) {
        if(!map.containsKey(key)) {
            map.put(key, new HashSet<>());
        }
        map.get(key).add(element);
    }


    /**
     * 读取xml元素, 有则返回值, 无则返回默认值
     * @author        杜永浩
     * @param node    xml节点
     * @param index   xml字符索引
     * @param Default 若无时的默认值
     * @param <D>     泛型
     * @return        默认值
     */
    public static <D> D readXml(Node node, String index, D Default) {
        Node indexNode = null;
        if(node instanceof Element)             indexNode = ((Element)  node).getElementsByTagName(index).item(0);
        else if(node instanceof Document)       indexNode = ((Document) node).getElementsByTagName(index).item(0);
        // 1. 若无该字段, 返回默认值
        if(indexNode == null || indexNode.getTextContent().length() == 0)     return Default;
        // 2. 若有字段
        String value = indexNode.getTextContent();
        if(Default instanceof String)           return (D) value;
        if(Default instanceof Integer)          return (D) Integer.valueOf(value);
        if(Default instanceof Long)             return (D) Long.valueOf(value);
        if(Default instanceof Double)           return (D) Double.valueOf(value);
        if(Default instanceof Boolean)          return (D) Boolean.valueOf(value);
        return Default;
    }


/* class ends */
}
