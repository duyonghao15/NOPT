package org.nudtopt.realworldproblems.apiforsatellite.resource.chance;

import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.Resource;

public class Angle extends Resource {

    private double pitchAngle;                        // 俯仰角
    private double rollAngle;                         // 侧摆角
    private double yawAngle;                          // 偏航角

    // getter & setter
    public double getPitchAngle() {
        return pitchAngle;
    }
    public void setPitchAngle(double pitchAngle) {
        this.pitchAngle = pitchAngle;
    }

    public double getRollAngle() {
        return rollAngle;
    }
    public void setRollAngle(double rollAngle) {
        this.rollAngle = rollAngle;
    }

    public double getYawAngle() {
        return yawAngle;
    }
    public void setYawAngle(double yawAngle) {
        this.yawAngle = yawAngle;
    }

    /**
     * 计算空间等效转角(欧拉角)
     * @param toAngle 目标角度
     */
    public double getRotateAngle(Angle toAngle) {
        // a. 计算旋转欧拉角
        double pitchAngle = toAngle.getPitchAngle() - this.pitchAngle;
        double rollAngle = toAngle.getRollAngle() - this.rollAngle;
        double yawAngle = toAngle.getYawAngle() - this.yawAngle;
        double y = Math.toRadians(rollAngle);
        double b = Math.toRadians(pitchAngle);
        double a = Math.toRadians(yawAngle);

        // b. 推导旋转矩阵
        double [][] R = new double [3][3];
        R[0][0] = Math.cos(a) * Math.cos(b);
        R[1][1] = Math.sin(a) * Math.sin(b) * Math.sin(y) + Math.cos(a) * Math.cos(y);
        R[2][2] = Math.cos(b) * Math.cos(y);

        // c. 计算四元组和等效旋转角
        double W = 0.5 * Math.sqrt(1 + R[0][0] + R[1][1] + R[2][2]);
        double theta = Math.toDegrees(Math.acos(W)) * 2;   // >=0
        return theta;
    }


    @Override
    public String toString() {
        return getClassName() + "-(" + Tool.round(pitchAngle, 1) + ", " + Tool.round(rollAngle, 1) + ", " + Tool.round(yawAngle, 1) + ")";
    }

/* class ends */
}
