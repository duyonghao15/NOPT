package org.nudtopt.realworldproblems.apiforagent.model;

import org.nudtopt.api.model.NumberedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Agent extends NumberedObject implements Runnable {

    protected List<Link> linkList = new ArrayList<>();
    protected String state = "start";                                   // 状态(start, wait, stop)
    protected final static Logger logger = LoggerFactory.getLogger(Agent.class);

    // getter & setter
    public List<Link> getLinkList() {
        return linkList;
    }
    public void setLinkList(List<Link> linkList) {
        this.linkList = linkList;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }


    @Override
    public void run() {
        while(true) {
            for(Link link : linkList) {
                synchronized (link) {
                    receiveAndSend(link);
                }
            }
            if(state.equals("wait"))    wait(true);
            if(state.equals("stop"))    break;
        }
    }


    public void sendMessage(Message message, Link link) {
        message.setFromAgent(link.getFromAgent());
        message.setToAgent(link.getToAgent());
        link.getMessageList().add(message);
    }


    // 接受/处理/发送消息
    public void receiveAndSend(Link link) {
        try {
            // 1. 接收状态
            if(link.getToAgent() == this) {
                logger.debug(toString() + "\treceiving & addressing\t" + link);
                // 处理消息
                Thread.sleep(1000);
                // 处理完毕
                link.reverse();        // 反置链接, 进入发送状态
            }
            // 2. 发送状态
            if(link.getFromAgent() == this) {
                Message message = new Message();
                message.setId(link.getLastMessageId() + 1);
                sendMessage(message, link);
                logger.debug(toString() + "\tresponded & waiting\t");
                link.notify();         // 唤醒link另一头
                link.wait();           // 自己进入等待
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Agent所在线程暂停和继续
    public void wait(boolean wait) {
        synchronized (state) {
            if(wait) {
                try {
                    state.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else    state.notify();
        }
    }


    // 获取Agent当前线程
    public static Thread getCurrentThread() {
        return Thread.currentThread();
    }


/* class ends */
}
