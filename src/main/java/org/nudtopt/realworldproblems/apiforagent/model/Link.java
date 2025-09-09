package org.nudtopt.realworldproblems.apiforagent.model;

import org.nudtopt.api.model.NumberedObject;

import java.util.List;

public class Link extends NumberedObject {

    private List<Message> messageList;
    private Agent fromAgent;
    private Agent toAgent;

    // getter & setter
    public List<Message> getMessageList() {
        return messageList;
    }
    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public Agent getFromAgent() {
        return fromAgent;
    }
    public void setFromAgent(Agent fromAgent) {
        this.fromAgent = fromAgent;
    }

    public Agent getToAgent() {
        return toAgent;
    }
    public void setToAgent(Agent toAgent) {
        this.toAgent = toAgent;
    }

    public void reverse() {
        Agent agent = toAgent;
        this.toAgent = fromAgent;
        this.fromAgent = agent;
    }

    public Message getLastMessage() {
        if(messageList.size() > 0)  return messageList.get(messageList.size() - 1);
        else                        return null;
    }

    public long getLastMessageId() {
        return getLastMessage() == null ? 0 : getLastMessage().getId();
    }


    @Override
    public String toString() {
        return super.toString() + " (" + fromAgent + " -> " + toAgent + ")";
    }

}
