package cn.elytra.code.api.locale;

import cn.elytra.code.api.ElytraApi;
import cn.elytra.code.api.annotation.ApiFeature;
import cn.elytra.code.api.annotation.ApiVersion;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * 使用本控制器之前需要确认安装本插件！
 *
 * <pre>
 * if(getServer().getPluginManager().getPlugin("ElytraApi") != null) {
 *     new PluginLocaleManager(this);
 * }
 * </pre>
 */
@ApiFeature(since = ApiVersion.V1_RC3)
public class PluginLocaleManager {

	private final Plugin plugin;
	private final String defaultLang;
	private final String[] allowedLang;

	private Function<LocaleSetupException, ILocale> exceptionHandler = (ex) -> {
		ex.printStackTrace();
		return null;
	};

	public PluginLocaleManager(@NotNull Plugin plugin, @NotNull String defaultLang, @Nullable String... allowedLang) {
		this.plugin = plugin;
		this.defaultLang = defaultLang;
		this.allowedLang = (String[]) ArrayUtils.add(allowedLang, defaultLang);
	}

	@NotNull
	public LocaleService getLocaleService() {
		return Objects.requireNonNull(JavaPlugin.getPlugin(ElytraApi.class).localeService,
				"LocaleService is NULL!");
	}

	/**
	 * 设置错误捕捉后的操作
	 */
	public void setExceptionHandler(Function<LocaleSetupException, ILocale> exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * 获取最合适的语言。若支持建议语言则返回建议语言，若没有则返回默认语言。
	 */
	@NotNull
	public String getLanguageAvailable() {
		String suggest = getLocaleService().getSuggestedLanguage();
		if(ArrayUtils.contains(allowedLang, suggest)) {
			return suggest;
		} else {
			return defaultLang;
		}
	}

	/**
	 * @see LocaleService#loadLocaleYaml(Plugin, String)
	 */
	public ILocale loadLocaleYaml() {
		final String language = getLanguageAvailable();
		final LocaleService service = getLocaleService();

		try {
			return service.loadLocaleYaml(plugin, "locale/"+language+".yml");
		} catch(LocaleSetupException ex) {
			return exceptionHandler.apply(ex);
		}
	}

	/**
	 * @see LocaleService#loadLocaleJson(Plugin, String)
	 */
	public ILocale loadLocaleJson() {
		final String language = getLanguageAvailable();
		final LocaleService service = getLocaleService();

		try {
			return service.loadLocaleJson(plugin, "locale/"+language+".json");
		} catch(LocaleSetupException ex) {
			return exceptionHandler.apply(ex);
		}
	}

}
