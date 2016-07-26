package logger;

import gui.gui.ErrorBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

public class DeLogger {

	private static Logger logger;

	private DeLogger() throws IOException{
		logger = LogManager.getLogger(DeLogger.class);
	}

	public static Logger getLogger(){
		if(logger == null){
			try{
				new DeLogger();
			}catch(IOException e){
				new ErrorBox().setMessageAndStart(e.getMessage());
			}
		}
		return logger;
	}
}
