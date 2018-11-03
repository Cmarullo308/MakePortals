package me.MakePortals.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
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
			case "signsenabled":
				toggleSigns(sender, args);
				break;
			default:
				sender.sendMessage(ChatColor.RED + "Invalid command");
				break;
			}
		}

		return true;
	}

	private void toggleSigns(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.togglesigns")) {
			noPermission(sender);
			return;
		}

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "Invalid number of arguements");
			return;
		}

		if (args[1].equalsIgnoreCase("True")) {
			getConfig().set("portal_signs_enabled", "true");
			sender.sendMessage(ChatColor.GREEN + "Portal signs enabled");
		} else if (args[1].equalsIgnoreCase("False")) {
			getConfig().set("portal_signs_enabled", "false");
			sender.sendMessage(ChatColor.GREEN + "Portal signs disabled");
		}

		saveConfig();
	}

	private void cmdTeleport(CommandSender sender, String[] args) {
		// /mp tpc world x y z warp_name

		String warp_name = args[5];
		final double distance_from_plate_block = 0.75;

		Location fromLocation = new Location(getServer().getWorld(args[1]), Double.parseDouble(args[2]),
				Double.parseDouble(args[3]), Double.parseDouble(args[4]));

		if (getConfig().getString("warp_locations." + warp_name) == null) {
			for (Player player : getServer().getOnlinePlayers()) {
				if (player.getLocation().distance(fromLocation) < distance_from_plate_block) {
					player.sendMessage("Warp \"" + warp_name + "\" does not exist");
				}
			}
			return;
		}

		World world = getServer().getWorld(getConfig().getString("warp_locations." + warp_name + ".world"));
		double x = Double.parseDouble(getConfig().getString("warp_locations." + warp_name + ".x"));
		double y = Double.parseDouble(getConfig().getString("warp_locations." + warp_name + ".y"));
		double z = Double.parseDouble(getConfig().getString("warp_locations." + warp_name + ".z"));

		Location toLocation = new Location(world, x, y, z);

		toLocation.setYaw((float) getConfig().getDouble("warp_locations." + warp_name + ".yaw"));
		toLocation.setPitch((float) getConfig().getDouble("warp_locations." + warp_name + ".pitch"));

		for (Player player : getServer().getOnlinePlayers()) {
			if (player.getLocation().distance(fromLocation) < distance_from_plate_block) {
				player.teleport(toLocation);
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
		spawnCommandBlock(moveLocation(1, "forward", location, player), args[1], player);
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
		Block block = moveLocation.getBlock();
		block.setType(Material.WALL_SIGN);

		Directional dir = (Directional) block.getBlockData();

		dir.setFacing(blockFace.getOppositeFace());
		block.setBlockData(dir);

		Sign sign = (Sign) block.getState();
		sign.setLine(1, warp_name);
		sign.update();
	}

	private void spawnCommandBlock(Location location, String warp_name, Player player) {
		location.getBlock().setType(Material.COMMAND_BLOCK);

		CommandBlock cmdBlock = (CommandBlock) location.getBlock().getState();
		double x = (int) location.getX();
		double y = (int) location.getY();
		double z = (int) location.getZ();

		x = (x < 0 ? x - 0.5 : x + 0.5);
		y += 2;
		z = (z < 0 ? z - 0.5 : z + 0.5);

		// X + 0.5 ~ Z + 0.5 ~ Y + 2 ~ Offset
		cmdBlock.setCommand("/mp tpc " + location.getWorld().getName() + " " + x + " " + y + " " + z + " " + warp_name);
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
			removeWarpLocation(sender, args[2]);
		} else if (args[1].equalsIgnoreCase("list")) {
			listWarpLocations(sender);
		}
	}

	private void listWarpLocations(CommandSender sender) {
		Object[] keys = getConfig().getConfigurationSection("warp_locations").getKeys(false).toArray();

		String listMessage = "Locations:\n";

		for (Object location : keys) {
			listMessage += ChatColor.GREEN + location.toString() + ChatColor.WHITE + ", ";
		}

		// -4 to remove space, comma, and ChatColor character
		listMessage = listMessage.substring(0, listMessage.length() - 4);
		sender.sendMessage(listMessage);
	}

	private void removeWarpLocation(CommandSender sender, String warpName) {
		// Test if exists
		if (getConfig().getString("warp_locations." + warpName) == null) {
			sender.sendMessage(ChatColor.RED + "Location\"" + warpName + "\" does not exist ");
			return;
		}

		getConfig().set("warp_locations." + warpName, null);
		saveConfig();

		sender.sendMessage(ChatColor.GREEN + "Location \"" + warpName + "\" removed");
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
