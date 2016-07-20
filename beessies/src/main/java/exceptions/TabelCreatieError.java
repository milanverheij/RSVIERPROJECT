package exceptions;

public class TabelCreatieError extends Error{

	private static final long serialVersionUID = 4141345040536042669L;

	public TabelCreatieError(String message){
		super(message);
	}

	public TabelCreatieError(String message, StackTraceElement[] stackTrace){
		super(message);
		super.setStackTrace(stackTrace);
	}

}
