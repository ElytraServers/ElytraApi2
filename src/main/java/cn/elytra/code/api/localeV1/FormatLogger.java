package cn.elytra.code.api.localeV1;

import cn.elytra.code.api.locale.ILocale;

import java.util.logging.Logger;

public class FormatLogger {

	private final Logger logger;
	private final ILocale locale;

	public FormatLogger(Logger logger, ILocale locale) {
		this.logger = logger;
		this.locale = locale;
	}

	public void severe(String key, Object...args) {
		logger.severe(locale.format(key, args));
	}

	public void warning(String key, Object...args) {
		logger.warning(locale.format(key, args));
	}

	public void info(String key, Object...args) {
		logger.info(locale.format(key, args));
	}

	public Logger getLogger() {
		return logger;
	}
}
