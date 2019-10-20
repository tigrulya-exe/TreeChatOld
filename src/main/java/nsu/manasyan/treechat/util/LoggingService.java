package nsu.manasyan.treechat.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LoggingService {
    static final Logger logger = LogManager.getLogger(LoggingService.class);

    static public void info(String message){
        logger.info(message);
    }

    static public void error(String message){
        logger.error(message);
    }
}
