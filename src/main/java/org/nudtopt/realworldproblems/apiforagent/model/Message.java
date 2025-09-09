package org.nudtopt.realworldproblems.apiforagent.model;

import org.nudtopt.api.model.NumberedObject;

public class Message extends NumberedObject {

    private String text;
    private Agent fromAgent;
    private Agent toAgent;

    // getter & setter
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
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

    @Override
    public String toString() {
        return super.toString() + " (" + fromAgent + " -> " + toAgent + ")";
    }

}
