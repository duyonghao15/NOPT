package org.nudtopt.realworldproblems.planegateassignment.reader;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.apidata.Data;
import org.nudtopt.realworldproblems.planegateassignment.model.Plane;
import org.nudtopt.realworldproblems.planegateassignment.model.Position;
import org.nudtopt.realworldproblems.planegateassignment.model.Solution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Reader extends MainLogger {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");            // 时间格式

    public static void main(String[] args) throws Exception {
        new Reader().read();
    }


    /**
     * 读取数据, 生成solution全部对象
     */
    public Solution read() throws Exception {
        Solution solution = new Solution();
        solution.setId(0L);
        solution.setPositionList(readPositionData());
        solution.setPlaneList(readPlaneData(solution.getPositionList()));
        return solution;
    }


    /**
     * 读取飞机数据
     * 并根据机位及可停飞机约束, 为每架飞机筛选出可停的机位
     * @author             杜永浩
     * @param positionList 全部停机位
     * @return             飞机清单 (含可停的机位)
     */
    public List<Plane> readPlaneData(List<Position> positionList) throws Exception {
        logger.info("正读取 -> 停机位分配问题 飞机数据 ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("planegateassignment/flight.txt"), "GBK")); // 相对路径
        List<Plane> planeList = new ArrayList<>();
        String line;
        long minTime = 9999999999L;                                                         // 记录最早时间
        long maxTime = 0L;                                                                  // 记录最晚时间
        int optionalPositionSize = 0;                                                       // 可用的停机位数 (后取平均)
        int nullPositionPlaneNum = 0;                                                       // 无可用机位的飞机数 (根本无法安排)
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\\s+");
            long id = Long.valueOf(str[1]);                                                 // 航班号
            String company = str[0];                                                        // 航空公司代号
            String inTime  = str[2] + " " + str[3];                                         // 进场时间
            String outTime = str[4] + " " + str[5];                                         // 出场时间
            String task = str[7];                                                           // 飞行任务
            String type = str[8];                                                           // 机型
            boolean international = str[6].equals("国际");                                  // 是否为国际航班
            // 新建飞机
            Plane plane = new Plane();                                                      // 新建飞机
            plane.setId(id);
            plane.setCompany(company);
            plane.setInDate (sdf.parse(inTime));
            plane.setOutDate(sdf.parse(outTime));
            plane.setInTime (plane.getInDate().getTime()  / 1000);                          // 时间单位: 秒
            plane.setOutTime(plane.getOutDate().getTime() / 1000);                          // 时间单位: 秒
            plane.setTask(task);
            plane.setType(type);
            plane.setInternational(international);
            planeList.add(plane);
            minTime = Math.min(minTime, plane.getInTime());
            maxTime = Math.max(maxTime, plane.getOutTime());
            // 过滤出可停的停机位
            List<Position> optionalPositionList = positionList.stream().filter(position -> position.allow(plane)).collect(Collectors.toList());
            if(optionalPositionList.size() > 0) {
                plane.setOptionalPositionList(optionalPositionList);                    // 决策变量取值范围赋值
                // plane.setPosition(Tool.randomFromList(plane.getOptionalPositionList()));// 决策变量(机位)随机赋初值
                optionalPositionSize += optionalPositionList.size();
            } else {
                nullPositionPlaneNum ++;
            }
        }
        logger.info("已读取 -> 飞机数据 " + planeList.size() + " 架次 (平均每架可停 " + optionalPositionSize / planeList.size() + " 机位, 其中 " + nullPositionPlaneNum + " 架无机位可停), 时间范围: " + sdf.format(new Date(minTime * 1000)) + " 至 " + sdf.format(new Date(maxTime * 1000)) + ".\n");
        logger.info("正分析 -> 飞机时段两两潜在冲突 ... ...");
        AtomicInteger conflictNum = new AtomicInteger(0);
        planeList.parallelStream().forEach(plane -> {
            for(int i = 0 ; i < planeList.size() ; i ++) {
                if(plane.possibleConflict(planeList.get(i))) {
                    plane.getPossibleConflictPlaneList().add(planeList.get(i));
                    conflictNum.getAndAdd(1);
                }
            }
        });
        logger.info("已分析 -> 飞机时段潜在冲突 (平均每架冲突数 " + conflictNum.get() / planeList.size() + " ).\n");
        return planeList;
    }


    /**
     * 读取停机位数据
     */
    public List<Position> readPositionData() throws Exception {
        logger.info("正读取 -> 停机位分配问题 停机位数据 ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("planegateassignment/pos.txt"))); // 相对路径
        List<Position> positionList = new ArrayList<>();
        String line;
        String[] typeData;          // 飞行任务
        boolean close;              // 是否为近机位
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\\s+");
            Position position = new Position();
            positionList.add(position);
            position.setId((long) positionList.size());
            position.setIndex(str[0]);
            if(isInDetail(position.getId())) {
                // 1. 读取允许的国际/国内
                String[] internationalData = str[1].split(",");
                for(String international : internationalData) {
                    position.getInternationalSet().add(international.equals("国际"));
                }
                // 2. 读取允许的飞行任务
                String[] taskData = str[5].split(",");
                for(String task : taskData) {
                    position.getTaskSet().add(task);
                }
                // 3. 读取可停的航空公司代码
                String[] companyData = str[6].split(",");
                for(String company : companyData) {
                    position.getCompanySet().add(company);
                }
                // 4. 读取可停的机型
                typeData = str[7].split(",");
                close    = str[2].equals("近机位");
            } else {
                typeData = str[4].split(",");
                close    = str[1].equals("近机位");
            }
            for(String type : typeData) {
                position.getTypeSet().add(type);
            }
            position.setClose(close);
        }
        logger.info("已读取 -> 停机位 " + positionList.size() + " 个！\n");
        return positionList;
    }


    /**
     * 停机位数据有两种格式
     * 一种比较详细, 有国际/国内, 飞行任务, 航司, 机型等
     * 另一种比较简单, 只有机型数据
     * 两种字段数据有所不同
     * @author  杜永浩
     * @param i 数据中的第几行
     * @return  第一种还是第二种
     */
    private boolean isInDetail(long i) {
        return i<188||i==220||i==224||i==228||i==232||i==236||i==240||i==244||i==248||i==252||i==256||i==260||i==264||i==268||i==272||i==274;
    }


/* class ends */
}
