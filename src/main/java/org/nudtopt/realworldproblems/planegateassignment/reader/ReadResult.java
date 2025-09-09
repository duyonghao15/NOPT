package org.nudtopt.realworldproblems.planegateassignment.reader;

import org.nudtopt.api.model.MainLogger;
import org.nudtopt.apidata.Data;
import org.nudtopt.realworldproblems.planegateassignment.model.Plane;
import org.nudtopt.realworldproblems.planegateassignment.model.Position;
import org.nudtopt.realworldproblems.planegateassignment.model.Solution;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ReadResult extends MainLogger {


    public void readPlaneData(Solution solution) throws Exception {
        logger.info("正读取 -> 历史调度结果 ... ...");
        BufferedReader br = new BufferedReader(new InputStreamReader(Data.class.getResourceAsStream("planegateassignment/solution.txt"), "GBK")); // 相对路径
        String line;
        while ((line = br.readLine()) != null) {
            String[] str = line.split("\\s+");
            long planeId = Long.valueOf(str[1]);                            // 航班号
            String positionIndex = str[4];                                  // 机位编号
            Plane plane = (Plane) solution.getDecisionEntity(planeId);      // 找到飞机对象
            for(Position position : plane.getOptionalPositionList()) {
                if(position.getIndex().equals(positionIndex)) {
                    plane.setPosition(position);                            // 找到分配的机位
                    break;
                }
            }
        }
        logger.info("已读取 -> 历史调度结果！");
    }

}
