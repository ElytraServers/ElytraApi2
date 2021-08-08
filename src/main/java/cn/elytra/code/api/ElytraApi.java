package cn.elytra.code.api;

import cn.elytra.code.api.locale.ILocale;
import cn.elytra.code.api.locale.LocaleService;
import cn.elytra.code.api.locale.LocaleSetupException;
import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ElytraApi extends JavaPlugin {

	public ILocale locale = ILocale.EMPTY_LOCALE;

	public final LocaleService localeService = new LocaleService(this);

	@Override
	public void onEnable() {

		saveDefaultConfig();
		reloadLocale();

		// Register Locale Service
		getServer().getServicesManager().register(LocaleService.class, localeService,
				this, ServicePriority.Normal);
		getLogger().info(locale.format("elytra.api.loaded.localeService"));

		getLogger().info(locale.format("elytra.api.plugin.enabled"));
	}

	@Override
	public void onDisable() {
		getLogger().info(locale.format("elytra.api.plugin.disabled"));
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("elytra")) {
			if(args.length > 0) {
				String subcommand = args[0];
				if(subcommand.equalsIgnoreCase("reload")) {
					onReload();
					sender.sendMessage(locale.format("elytra.api.loaded.commandMessage"));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, String alias, String[] args) {
		if(alias.equalsIgnoreCase("elytra")) {
			return Lists.newArrayList("reload");
		}
		return null;
	}

	protected void onReload() {
		reloadConfig();
		reloadLocale();
	}

	protected void reloadLocale() {
		String lang = getConfig().getString("language");

		try {
			this.locale = localeService.loadLocaleYaml(this, "locale/"+lang+".yml");
		} catch (LocaleSetupException setup) {
			if(LocaleSetupException.TYPE_FILE_MISSING == setup.getExceptionType()) {
				getLogger().warning("Locale file is missing! Report this to the Issues in Github repo.");
			}
			throw setup;
		}

		getLogger().info(locale.format("elytra.api.loaded.localeForPlugin"));
	}
}
