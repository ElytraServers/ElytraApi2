package cn.elytra.code.api.command;

import cn.elytra.code.api.utils.Senders;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

/**
 * Brigadier Test Command Class.
 *
 * @since v1.1
 */
public class BrigadierTestCommand extends BrigadierAbstractCommand {

	public BrigadierTestCommand() {
		super("brigadier");
		dispatcher.register(
				literal("brigadier").executes(ctx -> {
					Senders.sendMessage(ctx.getSource(), "elytra.api.brigadier.working");
					return Command.SINGLE_SUCCESS;
				})
						.then(
								literal("test").executes(ctx -> {
									ctx.getSource().sendMessage("Brigadier!!");
									return Command.SINGLE_SUCCESS;
								})
						)
						.then(
								literal("parseInt").then(
										argument("integer", integer()).executes(ctx -> {
											int i = IntegerArgumentType.getInteger(ctx, "integer");
											Senders.sendMessage(ctx.getSource(), "elytra.api.brigadier.testParseInt", i);
											return Command.SINGLE_SUCCESS;
										})
								)
						)
		);
	}
}
