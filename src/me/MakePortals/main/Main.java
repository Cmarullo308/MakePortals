package me.MakePortals.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	static final Material[] fallingBlocks = { Material.SAND, Material.GRAVEL, Material.ANVIL, Material.DRAGON_EGG,
			Material.WHITE_CONCRETE_POWDER, Material.ORANGE_CONCRETE_POWDER, Material.MAGENTA_CONCRETE_POWDER,
			Material.LIGHT_BLUE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER,
			Material.PINK_CONCRETE_POWDER, Material.GRAY_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER,
			Material.CYAN_CONCRETE_POWDER, Material.PURPLE_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER,
			Material.BROWN_CONCRETE_POWDER, Material.GREEN_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER,
			Material.BLACK_CONCRETE_POWDER };

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
			case "setblock":
				setBlockType(sender, args);
				break;
			case "location":
				locationSubCommand(sender, args);
				break;
			case "tp":
				teleport(sender, args);
				break;
			case "test":
				testCommand(sender, args);
				break;
			case "create":
				createPortal(sender, args);
				break;
			case "tpc":
				cmdTeleport(sender, args);
				break;
			default:
				break;
			}
		}

		return true;
	}

	private void cmdTeleport(CommandSender sender, String[] args) {
		getLogger().info("AAAAAAAAAAAA");

		// /mp tpc world x y z warp_name
		Location fromLocation = new Location(getServer().getWorld(args[1]), Double.parseDouble(args[2]),
				Double.parseDouble(args[3]), Double.parseDouble(args[4])).add(0, 2, 0);

		World world = getServer().getWorld(getConfig().getString("warp_locations." + args[5] + ".world"));
		double x = Double.parseDouble(getConfig().getString("warp_locations." + args[5] + ".x"));
		double y = Double.parseDouble(getConfig().getString("warp_locations." + args[5] + ".y"));
		double z = Double.parseDouble(getConfig().getString("warp_locations." + args[5] + ".z"));

		Location toLocation = new Location(world, x, y, z);

		toLocation.setYaw((float) getConfig().getDouble("warp_locations." + args[5] + ".yaw"));
		toLocation.setPitch((float) getConfig().getDouble("warp_locations." + args[5] + ".pitch"));

		for (Player pl : getServer().getOnlinePlayers()) {
			if (pl.getLocation().distance(fromLocation) < 1) {
				pl.teleport(toLocation);
			}
		}
	}

	private void createPortal(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Must be a player to run this command");
			return;
		}

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "Invalid number of arguements");
			return;
		}

		try {
			getServer().getWorld(getConfig().getString("warp_locations." + args[1] + ".world"));
		} catch (IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + "No location named " + args[1]);
			return;
		}

		Player player = (Player) sender;
		Location location = player.getLocation().clone();
		Material portalBlockType = Material.valueOf(getConfig().getString("portal_block_type"));

		// Make Portal
		moveLocation(2, "down", location, player);
		spawnCommandBlock(moveLocation(1, "forward", location, player), args[1]);
		moveLocation(1, "up", location, player).getBlock().setType(portalBlockType);

		moveLocation(1, "left", location, player).getBlock().setType(portalBlockType);
		moveLocation(2, "right", location, player).getBlock().setType(portalBlockType);
		moveLocation(1, "forward", location, player);
		moveLocation(1, "left", location, player).getBlock().setType(portalBlockType);
		moveLocation(1, "up", location, player).getBlock().setType(portalBlockType);

		moveLocation(1, "back", location, player).getBlock().setType(Material.STONE_PRESSURE_PLATE);
		moveLocation(1, "left", location, player).getBlock().setType(portalBlockType);
		moveLocation(2, "right", location, player).getBlock().setType(portalBlockType);
		moveLocation(1, "forward", location, player);
		moveLocation(1, "left", location, player).getBlock().setType(portalBlockType);
		moveLocation(1, "up", location, player).getBlock().setType(portalBlockType);

		moveLocation(1, "back", location, player);
		moveLocation(1, "left", location, player).getBlock().setType(portalBlockType);
		moveLocation(2, "right", location, player).getBlock().setType(portalBlockType);
		moveLocation(1, "forward", location, player);
		moveLocation(1, "left", location, player).getBlock().setType(portalBlockType);

		moveLocation(1, "up", location, player).getBlock().setType(portalBlockType);
		moveLocation(1, "back", location, player).getBlock().setType(portalBlockType);
		moveLocation(1, "left", location, player).getBlock().setType(portalBlockType);
		moveLocation(2, "right", location, player).getBlock().setType(portalBlockType);
		moveLocation(1, "left", location, player);
		spawnSign(moveLocation(1, "back", location, player), args[1], player.getFacing());
	}

	private void spawnSign(Location moveLocation, String warp_name, BlockFace blockFace) {
		moveLocation.getBlock().setType(Material.WALL_SIGN);

		org.bukkit.material.Sign signData = new org.bukkit.material.Sign(Material.WALL_SIGN);

		if (blockFace == BlockFace.NORTH) {
			signData.setFacingDirection(BlockFace.SOUTH);
			getLogger().info("AAAAAAAAAAAA");
		} else if (blockFace == BlockFace.SOUTH) {
			signData.setFacingDirection(BlockFace.NORTH);
			getLogger().info("BBBBBBBBBBB");
		} else if (blockFace == BlockFace.EAST) {
			signData.setFacingDirection(BlockFace.WEST);
			getLogger().info("CCCCCCCCCCCC");
		} else if (blockFace == BlockFace.WEST) {
			getLogger().info("DDDDDDDDD");
			signData.setFacingDirection(BlockFace.EAST);
		}

		Sign s = (Sign) moveLocation.getBlock().getState();
		s.setData(signData);
		s.setLine(1, warp_name);
		s.update();
	}

	private void spawnCommandBlock(Location location, String warp_name) {
		location.getBlock().setType(Material.COMMAND_BLOCK);

		CommandBlock cmdBlock = (CommandBlock) location.getBlock().getState();

		cmdBlock.setCommand("/mp tpc " + location.getWorld().getName() + " " + (int) location.getX() + " "
				+ (int) location.getY() + " " + (int) location.getZ() + " " + warp_name);
		cmdBlock.update();
	}

	private Location moveLocation(int numOfTimes, String direction, Location location, Player player) {
		if (direction.equalsIgnoreCase("left")) {
			if (player.getFacing() == BlockFace.NORTH) {
				location.add(-numOfTimes, 0, 0);
			} else if (player.getFacing() == BlockFace.SOUTH) {
				location.add(numOfTimes, 0, 0);
			} else if (player.getFacing() == BlockFace.EAST) {
				location.add(0, 0, -numOfTimes);
			} else if (player.getFacing() == BlockFace.WEST) {
				location.add(0, 0, +numOfTimes);
			}
		} else if (direction.equalsIgnoreCase("right")) {
			if (player.getFacing() == BlockFace.NORTH) {
				location.add(numOfTimes, 0, 0);
			} else if (player.getFacing() == BlockFace.SOUTH) {
				location.add(-numOfTimes, 0, 0);
			} else if (player.getFacing() == BlockFace.EAST) {
				location.add(0, 0, numOfTimes);
			} else if (player.getFacing() == BlockFace.WEST) {
				location.add(0, 0, -numOfTimes);
			}
		} else if (direction.equalsIgnoreCase("forward")) {
			if (player.getFacing() == BlockFace.NORTH) {
				location.add(0, 0, -numOfTimes);
			} else if (player.getFacing() == BlockFace.SOUTH) {
				location.add(0, 0, numOfTimes);
			} else if (player.getFacing() == BlockFace.EAST) {
				location.add(numOfTimes, 0, 0);
			} else if (player.getFacing() == BlockFace.WEST) {
				location.add(-numOfTimes, 0, 0);
			}
		} else if (direction.equalsIgnoreCase("back")) {
			if (player.getFacing() == BlockFace.NORTH) {
				location.add(0, 0, numOfTimes);
			} else if (player.getFacing() == BlockFace.SOUTH) {
				location.add(0, 0, -numOfTimes);
			} else if (player.getFacing() == BlockFace.EAST) {
				location.add(-numOfTimes, 0, 0);
			} else if (player.getFacing() == BlockFace.WEST) {
				location.add(numOfTimes, 0, 0);
			}
		} else if (direction.equalsIgnoreCase("down")) {
			player.sendMessage("ASS");
			location.add(0, -numOfTimes, 0);
		} else if (direction.equalsIgnoreCase("up")) {
			location.add(0, numOfTimes, 0);
		}

		return location;
	}

	private void teleport(CommandSender sender, String[] args) {

		if (args.length == 2) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Must be a playr to run this command");
				return;
			}

			Location teleportLocation = getTeleportLocation(sender, args[1]);

			if (teleportLocation == null) {
				return;
			}

			((Player) sender).teleport(teleportLocation);
		} else if (args.length == 3) {
			Player player = getServer().getPlayer(args[1]);

			if (player == null) {
				sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" does not exist in this server");
				return;
			}

			Location teleportLocation = getTeleportLocation(sender, args[2]);

			player.teleport(teleportLocation);
		} else {
			sender.sendMessage(ChatColor.RED + "Invalid number of arguements");
		}
	}

	private Location getTeleportLocation(CommandSender sender, String warp_name) {
		Location tempLocation;

		World world;

		try {
			world = getServer().getWorld(getConfig().getString("warp_locations." + warp_name + ".world"));
		} catch (IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + "No location named " + warp_name);
			return null;
		}

		double x = getConfig().getDouble("warp_locations." + warp_name + ".x");
		double y = getConfig().getDouble("warp_locations." + warp_name + ".y");
		double z = getConfig().getDouble("warp_locations." + warp_name + ".z");
		float yaw = (float) getConfig().getDouble("warp_locations." + warp_name + ".yaw");
		float pitch = (float) getConfig().getDouble("warp_locations." + warp_name + ".pitch");

		tempLocation = new Location(world, x, y, z);

		tempLocation.setYaw(yaw);
		tempLocation.setPitch(pitch);

		return tempLocation;
	}

	private void locationSubCommand(CommandSender sender, String[] args) {
		if (args[1].equalsIgnoreCase("set")) {
			setWarpLocation(sender, args);
		} else if (args[1].equalsIgnoreCase("remove")) {
			removeWarpLocation(sender, args);
		}
	}

	private void removeWarpLocation(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub

	}

	private void setWarpLocation(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.setwarplocation")) {
			noPermission(sender);
			return;
		}

		Location location;
		if (!(sender instanceof Player)) {
			location = null;
		} else {
			location = ((Player) sender).getLocation();
		}

		// /MakeWarp location set WARP_NAME
		if (args.length == 3) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Must be a player to run this command");
				return;
			}

			saveLocationInConfig(sender, args[2], location);
		}
		// /Makewarp location set X Y Z WARP_NAME
		else if (args.length == 6) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Must be a player to run this command");
				return;
			}

			try {
				location = new Location(((Player) sender).getWorld(), Double.parseDouble(args[2]),
						Double.parseDouble(args[3]), Double.parseDouble(args[4]));
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "One of the numbers is invalid");
				getLogger().info(e.toString());
			}
			saveLocationInConfig(sender, args[5], location);
		}
		// /Makewarp location set WORLD X Y Z WARP_NAME
		else if (args.length == 7) {
			if (getServer().getWorld(args[2]) == null) {
				sender.sendMessage(ChatColor.RED + "Invalid world name");
				return;
			}

			if (getServer().getWorld(args[2]) == null) {
				sender.sendMessage(ChatColor.RED + "There is no world named " + args[2]);
				return;
			}

			try {
				location = new Location(getServer().getWorld(args[2]), Double.parseDouble(args[3]),
						Double.parseDouble(args[4]), Double.parseDouble(args[5]));
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "One of the numbers is invalid");
				getLogger().info(e.toString());
			}
			saveLocationInConfig(sender, args[6], location);
		}
		// /Makewarp location set X Y Z YAW PICTH WARP_NAME
		else if (args.length == 8) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Must be a player to run this command");
				return;
			}

			try {
				location = new Location(((Player) sender).getWorld(), Double.parseDouble(args[2]),
						Double.parseDouble(args[3]), Double.parseDouble(args[4]));
				location.setYaw(Float.parseFloat(args[5]));
				location.setPitch(Float.parseFloat(args[6]));
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "One of the numbers is invalid");
				getLogger().info(e.toString());
			}
			saveLocationInConfig(sender, args[7], location);
		}
		// /Makewarp location set WORLD X Y Z YAW PITCH WARP_NAME
		else if (args.length == 9) {
			if (getServer().getWorld(args[2]) == null) {
				sender.sendMessage(ChatColor.RED + "There is no world named " + args[2]);
				return;
			}

			try {
				location = new Location(getServer().getWorld(args[2]), Double.parseDouble(args[3]),
						Double.parseDouble(args[4]), Double.parseDouble(args[5]));
				location.setYaw(Float.parseFloat(args[6]));
				location.setPitch(Float.parseFloat(args[7]));
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "One of the numbers is invalid");
				getLogger().info(e.toString());
			}
			saveLocationInConfig(sender, args[8], location);
		} else {
			sender.sendMessage(ChatColor.RED + "Invalid number of arguements");
			return;
		}
	}

	private void saveLocationInConfig(CommandSender sender, String warp_name, Location location) {
		getConfig().set("warp_locations." + warp_name + ".x", location.getX());
		getConfig().set("warp_locations." + warp_name + ".y", location.getY());
		getConfig().set("warp_locations." + warp_name + ".z", location.getZ());
		getConfig().set("warp_locations." + warp_name + ".yaw", location.getYaw());
		getConfig().set("warp_locations." + warp_name + ".pitch", location.getPitch());
		getConfig().set("warp_locations." + warp_name + ".world", location.getWorld().getName());
		saveConfig();
		sender.sendMessage(ChatColor.GREEN + "Warp " + warp_name + " saved");
	}

	private void testCommand(CommandSender sender, String[] args) {
		Location location = ((Player) sender).getLocation().add(0, 1, -2);

		location.getBlock().setType(Material.WALL_SIGN);

		org.bukkit.material.Sign signData = new org.bukkit.material.Sign(Material.WALL_SIGN);
		signData.setFacingDirection(BlockFace.EAST);

		Sign s = (Sign) location.getBlock().getState();
		s.setData(signData);
		s.update();
		
	}

	private void setBlockType(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.setblocktype")) {
			noPermission(sender);
			return;
		}

		Material block;

		try {
			block = Material.valueOf(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid block type");
			return;
		}

		if (!block.isBlock()) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not a placeable block");
			return;
		}

		for (Material material : fallingBlocks) {
			if (block.equals(material)) {
				sender.sendMessage(ChatColor.RED + "Portals cannot be made out of blocks that can fall");
				return;
			}
		}

		getConfig().set("portal_block_type", args[1]);
		saveConfig();

		sender.sendMessage(ChatColor.GREEN + "Portal block type set to " + block);
	}

	/**
	 * Sends the player a message saying they don't have permission to run a command
	 * 
	 * @param sender
	 */
	private void noPermission(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
	}
}
