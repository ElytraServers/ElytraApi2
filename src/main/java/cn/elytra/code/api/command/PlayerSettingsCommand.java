package cn.elytra.code.api.command;

import cn.elytra.code.api.localeV1.PluginLocaleManagerV1;
import cn.elytra.code.api.psettings.PlayerSettings;
import cn.elytra.code.api.utils.Senders;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

/**
 * Settings Command (/settings).
 *
 * @since v1.1
 * @see PlayerSettings
 */
public class PlayerSettingsCommand extends BrigadierAbstractCommand {

	public PlayerSettingsCommand() {
		super("settings");

		dispatcher.register(literal("settings").executes(c -> {
			Senders.sendMessage(c.getSource(), "elytra.api.command.settings.usage");
			return 1;
		}).then(
				literal("language").executes(c -> {
					requirePlayer(c, player -> Senders.sendMessage(player, "elytra.api.command.settings.language.using",
							PlayerSettings.get(player).getLanguage()), sender -> Senders.sendMessage(sender, "elytra.api.command.general.playerOnly"));
					return 1;
				}).then(
						literal("set").then(
								argument("lang", string()).executes(c -> {
									requirePlayer(c, player -> {
										String lang = StringArgumentType.getString(c, "lang");
										PlayerSettings ps = PlayerSettings.get(player);
										ps.setLanguage(lang);
										Senders.sendMessage(player, "elytra.api.command.settings.language.setTo", lang);
									}, sender -> Senders.sendMessage(sender, "elytra.api.command.general.playerOnly"));
									return 1;
								})
						)
				)
		));
	}

	private static void requirePlayer(CommandContext<CommandSender> ctx, Consumer<Player> isPlayer, Consumer<CommandSender> notPlayer) {
		if (ctx.getSource() instanceof Player) {
			isPlayer.accept((Player) ctx.getSource());
		} else {
			notPlayer.accept(ctx.getSource());
		}
	}
}
