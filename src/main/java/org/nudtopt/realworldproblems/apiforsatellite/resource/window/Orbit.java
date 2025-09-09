package org.nudtopt.realworldproblems.apiforsatellite.resource.window;

import org.nudtopt.api.tool.comparator.IdComparator;
import org.nudtopt.realworldproblems.apiforsatellite.resource.platform.Satellite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Orbit extends Window {

    protected Satellite satellite;    // 轨道所属卫星
    protected Day day;                // 轨道所属天数
    protected boolean rise;           // 升轨 or 降轨
    protected Orbit lastOrbit;        // 紧邻的上一条轨道
    protected Orbit nextOrbit;        // 紧邻的下一条轨道

    // getter & setter
    public Satellite getSatellite() {
        return satellite;
    }
    public void setSatellite(Satellite satellite) {
        this.satellite = satellite;
    }

    public Day getDay() {
        return day;
    }
    public void setDay(Day day) {
        this.day = day;
    }

    public boolean isRise() {
        return rise;
    }
    public void setRise(boolean rise) {
        this.rise = rise;
    }

    public Orbit getLastOrbit() {
        return lastOrbit;
    }
    public void setLastOrbit(Orbit lastOrbit) {
        this.lastOrbit = lastOrbit;
    }

    public Orbit getNextOrbit() {
        return nextOrbit;
    }
    public void setNextOrbit(Orbit nextOrbit) {
        this.nextOrbit = nextOrbit;
    }


    // 为每轨设置前后相邻轨道
    public static void setClosingOrbits(List<Orbit> orbitList) {
        // a. 区分不同卫星的轨道
        Map<Satellite, List<Orbit>> satelliteOrbitMap = new HashMap<>();
        for(Orbit orbit : orbitList) {
            Satellite satellite = orbit.getSatellite();
            if(!satelliteOrbitMap.containsKey(satellite)) {
                satelliteOrbitMap.put(satellite, new ArrayList<>());
                satelliteOrbitMap.get(satellite).add(orbit);
            } else {
                if(!satelliteOrbitMap.get(satellite).contains(orbit)) {
                    satelliteOrbitMap.get(satellite).add(orbit);
                }
            }
        }
        // b. 逐颗卫星设置轨道
        for(Satellite satellite : satelliteOrbitMap.keySet()) {
            List<Orbit> orbits = satelliteOrbitMap.get(satellite);
            orbits.sort(new IdComparator());
            for(int i = 0 ; i < orbits.size() - 1 ; i ++) {
                Orbit orbit = orbits.get(i);
                Orbit nextOrbit = orbits.get(i + 1);
                orbit.setNextOrbit(nextOrbit);
                nextOrbit.setLastOrbit(orbit);
            }
        }
    }


    @Override
    public String toString() {
        return index != null ? index : super.toString();
    }


/* class ends */
}
