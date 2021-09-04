package cn.elytra.code.api.utils;

import cn.elytra.code.api.ElytraApi;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static cn.elytra.code.api.psettings.PlayerSettings.PS_ELYTRA_API_LANGUAGE;

public class Senders {

	public static void sendMessage(CommandSender sender, String key, Object...args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			String lang = ElytraApi.instance().settingsManager.getPlayerSettings(player).getString(PS_ELYTRA_API_LANGUAGE);
			sender.sendMessage(ElytraApi.instance().localeManager.getLocaleOrDefault(lang).format(key, args));
		} else {
			sender.sendMessage(ElytraApi.instance().locale.format(key, args));
		}
	}

}
