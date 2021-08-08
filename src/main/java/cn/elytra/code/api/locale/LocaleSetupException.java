package cn.elytra.code.api.locale;

public class LocaleSetupException extends RuntimeException {

	public static final int TYPE_UNDEFINED = 0;
	public static final int TYPE_FILE_MISSING = 1;
	public static final int TYPE_JSON_ROOT_NOT_FIT = 2;

	public int type;

	public LocaleSetupException(String message) {
		this(message, TYPE_UNDEFINED);
	}

	public LocaleSetupException(String message, int exceptionType) {
		super(message);
		this.type = exceptionType;
	}

	public int getExceptionType() {
		return type;
	}

}
