package me.MakePortals.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "No arguements");
		} else {
			switch (args[0].toLowerCase()) {
			default:
				break;
			}
		}

		return true;
	}
}
