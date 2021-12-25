package cn.elytra.code.api;

import cn.elytra.code.api.command.PlayerSettingsCommand;
import cn.elytra.code.api.locale.ILocale;
import cn.elytra.code.api.locale.LocaleService;
import cn.elytra.code.api.localeV2.ELocale;
import cn.elytra.code.api.localeV2.MergedLocaleEntries;
import cn.elytra.code.api.personality.Personality;
import cn.elytra.code.api.personality.PersonalityManager;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import static cn.elytra.code.api.personality.Personality.PS_ELYTRA_API_LANGUAGE;

public final class ElytraApi extends JavaPlugin {

	public static final Gson GSON = new Gson();
	public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

	public static final Logger LOGGER = Logger.getLogger("ElytraApi");

	public ILocale locale = ILocale.EMPTY_LOCALE;

	public final LocaleService localeService = new LocaleService(this);

	public final String version;
	public final PersonalityManager settingsManager;

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

		ELocale.initPluginLocaleYaml(this, "en", "zh");

		this.settingsManager = new PersonalityManager(this);
	}

	@Override
	public void onEnable() {

		saveDefaultConfig();
		reloadLocale();

		// Register Locale Service
		getServer().getServicesManager().register(LocaleService.class, localeService,
				this, ServicePriority.Normal);
		getLogger().info(ELocale.translate("elytra.api.loaded.localeService"));

		getServer().getServicesManager().register(PersonalityManager.class, settingsManager,
				this, ServicePriority.Normal);
		getLogger().info(ELocale.translate("elytra.api.loaded.settingsService"));

		loadSettingsDefaults();
		getLogger().info(ELocale.translate("elytra.api.loaded.playerSettingsDefaults"));

		getLogger().info(ELocale.translate("elytra.api.plugin.enabled", version));

		new PlayerSettingsCommand().register(this);
	}

	@Override
	public void onDisable() {
		getLogger().info(ELocale.translate("elytra.api.plugin.disabled", version));
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("elytra")) {
			if(args.length > 0) {
				String subcommand = args[0];
				if(subcommand.equalsIgnoreCase("reload")) {
					onReload();
					sender.sendMessage(ELocale.translate(Personality.getLanguage(sender), "elytra.api.loaded.commandMessage"));
					return true;
				} else if(subcommand.equalsIgnoreCase("translate") &&
						args.length > 1) {
					String translatingKey = args[1];
					final Map<Locale, MergedLocaleEntries> translators = ELocale.getTranslators();
					sender.sendMessage(String.format("%s translators registered.", translators.entrySet().size()));
					translators.forEach((locale, translator) -> sender.sendMessage("["+locale.getLanguage()+"] "+translator.format(translatingKey)));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
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

		getLogger().info(ELocale.translate("elytra.api.loaded.localeForPlugin"));
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
