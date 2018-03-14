package com.autmatika.testing.api.util;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class LogManager {
    private static final ConcurrentHashMap<Long, Logger> loggers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, FileAppender> appenders = new ConcurrentHashMap<>();

    public static Logger getLogger(){
        if (loggers.containsKey(Thread.currentThread().getId())) {
            return loggers.get(Thread.currentThread().getId());
        }
        else {
            Logger logger = Logger.getLogger(Thread.currentThread().getName());
            loggers.put(Thread.currentThread().getId(), logger);
            return logger;
        }
    }

    public static void addFileAppender(FileAppender appender){
        getLogger().removeAppender(appenders.get(Thread.currentThread().getId()));
        appenders.put(Thread.currentThread().getId(), appender);
        getLogger().addAppender(appender);
    }
}
