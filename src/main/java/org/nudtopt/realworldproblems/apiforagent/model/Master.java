package org.nudtopt.realworldproblems.apiforagent.model;

public class Master extends Agent {


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


    @Override
    public void receiveAndSend(Link link) {
        try {
            // 1. 接收状态
            if(link.getToAgent() == this) {
                logger.debug(toString() + "\treceiving & addressing\t" + link);
                // 处理消息
                Message message = link.getLastMessage();
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







/* class ends */
}
