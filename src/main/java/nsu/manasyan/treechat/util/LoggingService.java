package nsu.manasyan.treechat.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class LoggingService {
    private static final Logger logger = LogManager.getLogger(LoggingService.class);

    public static void info(String message){
        logger.info(message);
    }

    public static  void error(String message){
        logger.error(message);
    }
}
