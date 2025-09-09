package org.nudtopt.realworldproblems.apiforagent.app;

import org.nudtopt.api.tool.function.Tool;
import org.nudtopt.realworldproblems.apiforagent.gui.MultiAgentGUI;
import org.nudtopt.realworldproblems.apiforagent.model.Agent;
import org.nudtopt.realworldproblems.apiforagent.model.Link;
import org.nudtopt.realworldproblems.apiforagent.model.Master;
import org.nudtopt.realworldproblems.apiforagent.model.Slave;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GUI {


    public static void main(String[] args) {

        List<Agent> agentList = new ArrayList<>();
        List<Link> linkList = new ArrayList<>();
        Tool.setRandom(new Random(30));
        int slave_Num = 2;

        // 1. 新建agent
        for(long i = 0 ; i <= slave_Num; i ++) {
            Agent agent;
            if(i == 0) {
                Master master = new Master();
                master.setLinkList(linkList);
                agent = master;
            } else {
                Slave slave = new Slave();
                Link link = new Link();
                link.setId(i);
                link.setFromAgent(agentList.get(0));
                link.setToAgent(slave);
                link.setMessageList(new ArrayList<>());
                linkList.add(link);
                slave.setLink(link);
                agent = slave;
            }
            agent.setId(i);
            agentList.add(agent);
        }

        // 2. 启动gui
        MultiAgentGUI gui = new MultiAgentGUI();
        gui.setAgentList(agentList);
        gui.openGUI();



    }


/* class ends */
}
