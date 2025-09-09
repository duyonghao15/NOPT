package org.nudtopt.realworldproblems.apiforsatellite.resource.chance;

import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.DownlinkWindow;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.ImageWindow;
import org.nudtopt.realworldproblems.apiforsatellite.resource.window.Window;
import org.nudtopt.realworldproblems.apiforsatellite.target.Stripe;

import java.util.ArrayList;
import java.util.List;

public class ImageChance extends Window {

    private List<Angle> angleList;                                      // 角度列表
    private Stripe stripe;                                              // 条带
    private ImageWindow imageWindow;                                    // 时刻所属成像窗口
    private List<ImageChance> betterChanceList = new ArrayList<>();     // 倾向性机会集合(与当前机会当比, 更好的机会, 供规划后调整)
    private List<DownlinkChance> realDownlinkChanceList;                // 可以实拍实传的数传机会

    // getter & setter
    public List<Angle> getAngleList() {
        return angleList;
    }
    public void setAngleList(List<Angle> angleList) {
        this.angleList = angleList;
    }

    public Stripe getStripe() {
        return stripe;
    }
    public void setStripe(Stripe stripe) {
        this.stripe = stripe;
    }

    public ImageWindow getImageWindow() {
        return imageWindow;
    }
    public void setImageWindow(ImageWindow imageWindow) {
        this.imageWindow = imageWindow;
    }

    public List<ImageChance> getBetterChanceList() {
        return betterChanceList;
    }
    public void setBetterChanceList(List<ImageChance> betterChanceList) {
        this.betterChanceList = betterChanceList;
    }

    public List<DownlinkChance> getRealDownlinkChanceList() {
        return realDownlinkChanceList;
    }
    public void setRealDownlinkChanceList(List<DownlinkChance> realDownlinkChanceList) {
        this.realDownlinkChanceList = realDownlinkChanceList;
    }


    @Override
    public double getSpeed() {
        /* Currently not available online, please contact duyonghao15@163.com for more details */
        return 1;
    }


    @Override // 浅克隆
    public ImageChance clone() {
        ImageChance imageChance = (ImageChance) super.clone();
        /*for(Field field : this.getClass().getDeclaredFields()) {
            if(field.getName().contains("List")) {                      // list变量全部重新new ArrayList, 否则地址不变, 一改全改
                try {
                    Class variableClass = this.getClass().getDeclaredField(name).getType();
                    Method method = this.getClass().getDeclaredMethod("set" + Tool.firstUpperCase(name), variableClass);
                    method.invoke(this, new ArrayList<>());         // list变量全部重新new ArrayList, 否则地址不变, 一改全改
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/
        imageChance.setTaskList(new ArrayList<>(imageChance.getTaskList()));
        return imageChance;
    }

}
