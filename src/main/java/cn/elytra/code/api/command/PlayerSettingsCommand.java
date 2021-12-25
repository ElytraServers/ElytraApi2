package cn.elytra.code.api.command;

import cn.elytra.code.api.localeV2.ELocale;
import cn.elytra.code.api.personality.Personality;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

/**
 * Settings Command (/settings).
 *
 * @see Personality
 * @since v1.1
 */
public class PlayerSettingsCommand extends BrigadierAbstractCommand {

	public PlayerSettingsCommand() {
		super("settings");

		dispatcher.register(literal("settings").executes(c -> {
			c.getSource().sendMessage(ELocale.translate(Personality.getLanguage(c.getSource()), "elytra.api.command.settings.usage"));
			return 1;
		}).then(
				literal("language").executes(c -> {
					requirePlayer(c, player -> player.sendMessage(ELocale.translate(Personality.getLanguage(player),
							"elytra.api.command.settings.language.using", Personality.get(player).getLanguage())), sender -> sender.sendMessage(ELocale.translate("elytra.api.command.general.playerOnly")));
					return 1;
				}).then(
						literal("set").then(
								argument("lang", string()).executes(c -> {
									requirePlayer(c, player -> {
										String lang = StringArgumentType.getString(c, "lang");
										Personality ps = Personality.get(player);
										ps.setLanguage(lang);
										player.sendMessage(ELocale.translate(Personality.getLanguage(player),
												"elytra.api.command.settings.language.setTo", lang));
									}, sender -> sender.sendMessage(ELocale.translate("elytra.api.command.general.playerOnly")));
									return 1;
								})
						)
				)
		));
	}

	private static void requirePlayer(CommandContext<CommandSender> ctx, Consumer<Player> isPlayer, Consumer<CommandSender> notPlayer) {
		if(ctx.getSource() instanceof Player) {
			isPlayer.accept((Player) ctx.getSource());
		} else {
			notPlayer.accept(ctx.getSource());
		}
	}
}
