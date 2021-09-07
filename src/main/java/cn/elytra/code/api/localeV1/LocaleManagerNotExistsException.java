package cn.elytra.code.api.localeV1;

public class LocaleManagerNotExistsException extends RuntimeException {

	public LocaleManagerNotExistsException() {
	}

	public LocaleManagerNotExistsException(String message) {
		super(message);
	}

	public LocaleManagerNotExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocaleManagerNotExistsException(Throwable cause) {
		super(cause);
	}

	public LocaleManagerNotExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
