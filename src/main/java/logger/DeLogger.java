package logger;

import gui.gui.ErrorBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class DeLogger {

	private static Logger logger;

	private DeLogger() throws IOException{
		logger = LoggerFactory.getLogger(DeLogger.class);
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
