package org.nudtopt.realworldproblems.satelliterangescheduling.tool.readerTZB.constraintchecker.tool;

/**
 * @author Xu Shilong
 * @version 1.0
 */
public class CalScore {

    public static int calTTCScore(double ttcPercent) {
        ttcPercent = ttcPercent * 100;

        if (ttcPercent < 94.0) {
            return 0;
        }

        if (ttcPercent >= 94.0 && ttcPercent < 99.0) {
            return 1 + (int) (ttcPercent - 94);  // 每高1个百分点加1分, 99以下向下取整
        }

        // 如果成功率高于99%
        if (ttcPercent >= 99.0 && ttcPercent < 99.9) {
            double extraRate = ttcPercent - 99.0;
            return 1 + 5 + (int) Math.round(extraRate * 10) * 2;  // 每高0.1个百分点加2分, 99以上四舍五入
        }

        // 如果成功率高于99.9%
        if (ttcPercent >= 99.9) {
            double extraRate = ttcPercent - 99.9;
            return 1 + 5 + 18 + (int) Math.round(extraRate * 100) * 4; // 每高0.01个百分点加4分,99以上四舍五入
        }

        return 0;
    }

    public static int calDDTScore(double ddtPercent) {
        ddtPercent = ddtPercent * 100;

        if (ddtPercent < 90.0) {
            return 0;
        }

        if (90.0 <= ddtPercent && ddtPercent < 94) {
            return 1 + (int) (ddtPercent - 90);      // 每高1个百分点加1分, 99以下向下取整
        }

        // 如果成功率高于94%
        if (ddtPercent >= 94.0 && ddtPercent < 99.0) {
            double extraRate = ddtPercent - 94.0;
            return 1 + 4 + (int) Math.round(extraRate * 10) * 4;   // 每高0.1个百分点加4分, 99以下向下取整
        }

        // 如果成功率高于99%
        if (ddtPercent >= 99.0) {
            double extraRate = ddtPercent - 99.0;
            return 1 + 4 + 200 + (int) Math.round(extraRate * 100) * 8; // 每高0.01个百分点加8分, 99.0后以下向下取整
        }

        return 0;
    }


}
