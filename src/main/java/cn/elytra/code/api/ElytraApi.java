package cn.elytra.code.api;

import cn.elytra.code.api.command.BrigadierTestCommand;
import cn.elytra.code.api.command.PlayerSettingsCommand;
import cn.elytra.code.api.locale.ILocale;
import cn.elytra.code.api.locale.LocaleService;
import cn.elytra.code.api.localeV1.PluginLocaleManagerV1;
import cn.elytra.code.api.psettings.PlayerSettingsManager;
import cn.elytra.code.api.utils.Loggers;
import cn.elytra.code.api.utils.Senders;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static cn.elytra.code.api.psettings.PlayerSettings.PS_ELYTRA_API_LANGUAGE;

public final class ElytraApi extends JavaPlugin {

	public static final Gson GSON = new Gson();
	public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

	public ILocale locale = ILocale.EMPTY_LOCALE;

	public final LocaleService localeService = new LocaleService(this);

	public final String version;
	public final PluginLocaleManagerV1 localeManager;
	public final PlayerSettingsManager settingsManager;

	@Nullable
	private static ElytraApi instance;

	@NotNull
	public static ElytraApi instance() {
		if(instance != null) {
			return instance;
		} else {
			throw new IllegalStateException("ElytraApi2 has not implemented yet!");
		}
	}

	public ElytraApi() {
		instance = this;

		this.version = getDescription().getVersion();

		this.localeManager = new PluginLocaleManagerV1(this, "en", "zh");
		this.settingsManager = new PlayerSettingsManager(this);
	}

	@Override
	public void onEnable() {

		saveDefaultConfig();
		reloadLocale();

		// Register Locale Service
		getServer().getServicesManager().register(LocaleService.class, localeService,
				this, ServicePriority.Normal);
		Loggers.i18n("elytra.api.loaded.localeService");

		getServer().getServicesManager().register(PlayerSettingsManager.class, settingsManager,
				this, ServicePriority.Normal);
		Loggers.i18n("elytra.api.loaded.settingsService");

		loadSettingsDefaults();
		Loggers.i18n("elytra.api.loaded.playerSettingsDefaults");

		Loggers.i18n("elytra.api.plugin.enabled", version);

		new BrigadierTestCommand().register(this);
		new PlayerSettingsCommand().register(this);
	}

	@Override
	public void onDisable() {
		Loggers.i18n("elytra.api.plugin.disabled", version);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("elytra")) {
			if(args.length > 0) {
				String subcommand = args[0];
				if(subcommand.equalsIgnoreCase("reload")) {
					onReload();
					Senders.sendMessage(sender, "elytra.api.loaded.commandMessage");
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias, String[] args) {
		if(alias.equalsIgnoreCase("elytra")) {
			return Lists.newArrayList("reload", "settings");
		}
		return null;
	}

	private void onReload() {
		reloadConfig();
		reloadLocale();
	}

	private void reloadLocale() {
		localeService.loadConfig();
		// this.locale = localeManager.loadLocaleYaml();

		localeManager.unregisterAll();
		localeManager.loadAndRegisterLocaleYaml("en");
		localeManager.loadAndRegisterLocaleYaml("zh");

		this.locale = localeManager.getLocaleOrDefault(localeService.getSuggestedLanguage());

		Loggers.i18n("elytra.api.loaded.localeV1.loadedCount",
				localeManager.getCacheSize(), localeManager.getAvailableLanguages());

		getLogger().info(locale.format("elytra.api.loaded.localeForPlugin"));
	}

	/**
	 * Put default values for PlayerSettings.
	 */
	private void loadSettingsDefaults() {
		final Configuration defaults = new YamlConfiguration();
		defaults.set(PS_ELYTRA_API_LANGUAGE, "en");
		settingsManager.addDefaults(defaults);
	}
}
