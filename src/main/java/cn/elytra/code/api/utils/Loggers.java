package cn.elytra.code.api.utils;

import cn.elytra.code.api.ElytraApi;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.text.MessageFormat;
import java.util.logging.Logger;

public class Loggers {

	private static final ElytraApi elytra = ElytraApi.instance();
	private static final Logger logger = ElytraApi.instance().getLogger();

	public static void info(String message, Object...args) {
		logger.info(MessageFormat.format(message, args));
	}

	public static void warn(String message, Object...args) {
		logger.warning(MessageFormat.format(message, args));
	}

	public static void error(String message, Object...args) {
		logger.severe(MessageFormat.format(message, args));
	}

	public static void fatal(String message, Object...args) {
		logger.severe(MessageFormat.format(message, args));
	}

	public static void error(String message, Throwable throwable, Object...args) {
		logger.severe(MessageFormat.format(message, args));
		logger.severe(ExceptionUtils.getStackTrace(throwable));
	}

	public static String format(String message, Object...args) {
		return MessageFormat.format(message, args);
	}

	public static void i18n(String key, Object...args) {
		info(ElytraApi.instance().locale.format(key, args));
	}

}
