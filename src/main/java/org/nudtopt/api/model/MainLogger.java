package org.nudtopt.api.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public class MainLogger {

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    protected void log(String str) {
        logger.info(str);
    }


    protected SimpleDateFormat format;



    /**
     * 将时间(单位秒)转化为指定格式的字符串
     * @param time 时间(单位秒)
     * @return     指定格式的字符串
     */
    public String format(long time) {
        return format.format(time * 1000);
    }


}
