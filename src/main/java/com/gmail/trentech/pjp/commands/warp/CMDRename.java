package com.gmail.trentech.pjp.commands.warp;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjp.portal.Portal;
import com.gmail.trentech.pjp.portal.Portal.PortalType;
import com.gmail.trentech.pjp.utils.Help;

public class CMDRename implements CommandExecutor {

	public CMDRename() {
		new Help("warp rename", "rename", "Rename warp", false)
			.setPermission("pjp.cmd.warp.rename")
			.setUsage("/warp rename <oldName> <newName>\n /w rn <oldName> <newName>")
			.setExample("/warp rename Spawn Lobby")
			.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Portal portal = args.<Portal>getOne("oldName").get();

		String newName = args.<String>getOne("newName").get().toLowerCase();

		if (Portal.get(newName, PortalType.WARP).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newName, " already exists"), false);
		}

		portal.remove();
		portal.create(newName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Warp renamed to ", newName));

		return CommandResult.success();
	}

}
