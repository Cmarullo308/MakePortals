package me.MakePortals.main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	static ActionList<PortalCreatedAction> actionList;

	// All blocks that are affected by gravity
	static final Material[] fallingBlocks = { Material.SAND, Material.GRAVEL, Material.ANVIL, Material.DRAGON_EGG,
			Material.WHITE_CONCRETE_POWDER, Material.ORANGE_CONCRETE_POWDER, Material.MAGENTA_CONCRETE_POWDER,
			Material.LIGHT_BLUE_CONCRETE_POWDER, Material.YELLOW_CONCRETE_POWDER, Material.LIME_CONCRETE_POWDER,
			Material.PINK_CONCRETE_POWDER, Material.GRAY_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE_POWDER,
			Material.CYAN_CONCRETE_POWDER, Material.PURPLE_CONCRETE_POWDER, Material.BLUE_CONCRETE_POWDER,
			Material.BROWN_CONCRETE_POWDER, Material.GREEN_CONCRETE_POWDER, Material.RED_CONCRETE_POWDER,
			Material.BLACK_CONCRETE_POWDER };

	// All types of pressure plates
	static final Material[] pressurePlates = { Material.STONE_PRESSURE_PLATE, Material.OAK_PRESSURE_PLATE,
			Material.SPRUCE_PRESSURE_PLATE, Material.BIRCH_PRESSURE_PLATE, Material.JUNGLE_PRESSURE_PLATE,
			Material.ACACIA_PRESSURE_PLATE, Material.DARK_OAK_PRESSURE_PLATE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
			Material.HEAVY_WEIGHTED_PRESSURE_PLATE };

	private Sound teleportSound; // Sound when the player teleports
	private Particle teleportParticle; // Particles when the player teleports
	private int teleportParticleSize; // Particle size

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		checkConfigValues();
		saveConfig();

		teleportSound = Sound.ENTITY_ENDERMAN_TELEPORT;
		teleportParticle = Particle.EXPLOSION_LARGE;
		teleportParticleSize = 5;

		actionList = new ActionList<PortalCreatedAction>(getConfig().getInt("max_undos"));
	}

	/**
	 * Called when a command is sent
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "No arguements");
		} else {
			switch (args[0].toLowerCase()) {
			case "setblocktype": // Sets the type of block that will be used on new portals
				setBlockType(sender, args);
				break;
			case "setpressureplate": // Sets what type of pressure plate will be used on new portals
				setPressurePlate(sender, args);
				break;
			case "location": // All the subcommands for the location command
				locationSubCommand(sender, args);
				break;
			case "tp": // Teleports player to a set warp
				teleport(sender, args);
				break;
			case "teleport": // Teleports player to a set warp
				teleport(sender, args);
				break;
			case "test":
				testCommand(sender, args);
				break;
			case "create": // Command to create a portal
				createPortalCommand(sender, args);
				break;
			case "tpc": // Command sent by the command block under the portals
				cmdTeleport(sender, args);
				break;
			case "signsenabled": // Set if signs spawn on new portals
				toggleSigns(sender, args);
				break;
			case "undo": // Undoes the last created portal by the player
				undo(sender, args);
				break;
			case "redo": // Recreates an undone portal
				redo(sender, args);
				break;
			case "teleportsoundenabled": // Set if a sound plays when a player is teleported
				toggleTeleportSound(sender, args);
				break;
			case "teleportparticlesenabled": // Set if particles apear around teleported players
				toggleTeleportParticles(sender, args);
				break;
			case "help": // Help menu for the player
				showHelpMenu(sender, args);
				break;
			default: // Invalid command
				sender.sendMessage(ChatColor.RED + "Invalid command");
				break;
			}
		}

		return true;
	}

	/**
	 * Checks that the values in the config file are valid
	 */
	private void checkConfigValues() {
		// Test that config values are real materials

		// Block type
		Material blockType = null;
		boolean noException = true;

		try {
			blockType = Material.valueOf(getConfig().getString("portal_block_type"));
		} catch (IllegalArgumentException e) {
			noException = false;
			getLogger().info("Invalid portal block type, resetting to default");
			getConfig().set("portal_block_type", "BLUE_CONCRETE");
		}

		if (noException) {
			boolean fallingBlock = false;
			for (Material block : fallingBlocks) {
				if (block == blockType) {
					fallingBlock = true;
					break;
				}
			}

			if (fallingBlock) {
				getLogger().info("Invalid portal block type (Cannot be a block that falls), resetting to default");
				getConfig().set("portal_block_type", "BLUE_CONCRETE");
			}
		}

		// Pressure plate type
		noException = true;

		try {
			blockType = Material.valueOf(getConfig().getString("portal_preasure_plate"));
		} catch (IllegalArgumentException e) {
			noException = false;
			getLogger().info("Invalid pressure plate type, resetting to default");
			getConfig().set("portal_preasure_plate", "STONE_PRESSURE_PLATE");
		}

		if (noException) {
			boolean validPressurePlate = false;
			for (Material plateType : pressurePlates) {
				if (plateType == blockType) {
					validPressurePlate = true;
					break;
				}
			}

			if (!validPressurePlate) {
				getLogger().info("Invalid pressure plate type, resetting to default");
				getConfig().set("portal_preasure_plate", "STONE_PRESSURE_PLATE");
			}
		}

		// Portal signs enables boolean
		if ((!getConfig().getString("portal_signs_enabled").equals("true"))
				&& (!getConfig().getString("portal_signs_enabled").equals("false"))) {
			getLogger().info("\"portal_signs_enabled\" must be true or false, resetting to default");
			getConfig().set("portal_signs_enabled", "true");
		}

		// max undos
		noException = true;

		try {
			Integer.parseInt(getConfig().getString("max_undos"));
		} catch (NumberFormatException e) {
			noException = false;
			getLogger().info("Invalid number of undos in config, resetting to default");
			getConfig().set("max_undos", 20);
		}

		if (noException) {
			if (getConfig().getInt("max_undos") < 1) {
				getLogger().info("Max undos must be 1 or higher, resetting to default");
			}
		}

		// teleport particles enabled
		if ((!getConfig().getString("teleport_particles_enabled").equals("true"))
				&& (!getConfig().getString("teleport_particles_enabled").equals("false"))) {
			getLogger().info("\"teleport_particles_enabled\" must be true or false, resetting to default");
			getConfig().set("teleport_particles_enabled", "true");
		}

		// teleport sound enabled
		if ((!getConfig().getString("teleport_sound_enabled").equals("true"))
				&& (!getConfig().getString("teleport_sound_enabled").equals("false"))) {
			getLogger().info("\"teleport_sound_enabled\" must be true or false, resetting to default");
			getConfig().set("teleport_sound_enabled", "true");
		}

	}

	/**
	 * Command sent by the command block under the portals
	 * 
	 * @param sender
	 * @param args
	 */
	private void cmdTeleport(CommandSender sender, String[] args) {
		// /mp tpc world x y z warp_name
		if (sender instanceof Player) {
			sender.sendMessage(ChatColor.RED + "This command is only for command blocks");
			return;
		}

		String warp_name = args[5];
		final double distance_from_plate_block = 0.75;

		Location fromLocation = new Location(getServer().getWorld(args[1]), Double.parseDouble(args[2]),
				Double.parseDouble(args[3]), Double.parseDouble(args[4]));

		if (getConfig().getString("warp_locations." + warp_name) == null) {
			for (Player player : getServer().getOnlinePlayers()) {
				if (player.getLocation().distance(fromLocation) < distance_from_plate_block) {
					player.sendMessage("Warp \"" + warp_name + "\" does not exist");
				}
				break;
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
				if (getConfig().getString("teleport_sound_enabled").equals("true")) {
					player.playSound(toLocation, teleportSound, 1f, 1f);
				}

				if (getConfig().getString("teleport_particles_enabled").equals("true")) {
					player.spawnParticle(teleportParticle, toLocation, teleportParticleSize);
				}

				break;
			}
		}
	}

	/**
	 * Command to create a portal
	 * 
	 * @param sender
	 * @param args
	 */
	private void createPortalCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Must be a player to run this command");
			return;
		}

		if (!sender.hasPermission("makeportals.create.makeportal")) {
			noPermission(sender);
			return;
		}

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED + "Invalid number of arguements");
			return;
		}

		String locationName = args[1];//

		try {
			getServer().getWorld(getConfig().getString("warp_locations." + locationName + ".world"));
		} catch (IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + "No location named " + locationName);
			return;
		}

		// Checks done

		Player player = (Player) sender;
		Location location = player.getLocation().clone();

		if (location.getY() >= 254) {
			player.sendMessage(ChatColor.RED + "Too high up, blocks cannot be placed above 256");
			return;
		}

		actionList.deleteUndosFor(player);

		makePortal(player, location, locationName);
	}

	/**
	 * Gets the location of a saved warp
	 * 
	 * @param sender
	 * @param warp_name
	 * @return
	 */
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

	/**
	 * Lists all saved warps to the sender
	 * 
	 * @param sender
	 */
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

	/**
	 * All the subcommands for the location command
	 * 
	 * @param sender
	 * @param args
	 */
	private void locationSubCommand(CommandSender sender, String[] args) {
		if (args[1].equalsIgnoreCase("set")) {
			setWarpLocation(sender, args);
		} else if (args[1].equalsIgnoreCase("remove")) {
			removeWarpLocation(sender, args[2]);
		} else if (args[1].equalsIgnoreCase("list")) {
			listWarpLocations(sender);
		}
	}

	/**
	 * Creates a portal
	 * 
	 * @param player
	 * @param location
	 * @param warp_name
	 */
	private void makePortal(Player player, Location location, String warp_name) {
		PortalCreatedAction action = new PortalCreatedAction(player, this); // For undos

		Material portalBlockType = Material.valueOf(getConfig().getString("portal_block_type"));
		Material portalPressurePlateType = Material.valueOf(getConfig().getString("portal_preasure_plate"));

		moveLocation(2, "up", location, player);
		// Block 1 -- row 3 middle
		moveLocation(1, "forward", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// Block 2 -- Sign
		moveLocation(1, "back", location, player);
		if (getConfig().getString("portal_signs_enabled").equalsIgnoreCase("true")) {
			spawnSign(location, warp_name, player.getFacing(), action);
		}

		// --
		moveLocation(1, "forward", location, player);
		// --
		// Block 3 -- row 3 right
		moveLocation(1, "right", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// Block 4 -- row 3 left
		moveLocation(2, "left", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// --
		moveLocation(2, "right", location, player);
		// --
		// Block 5 -- row 2 right
		moveLocation(1, "down", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// Block 6 -- row 2 middle
		moveLocation(1, "left", location, player);
		action.addStep(location, location.getBlock(), Material.AIR);
		location.getBlock().setType(Material.AIR);
		// Block 7 -- row 2 left
		moveLocation(1, "left", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// --
		moveLocation(1, "forward", location, player);
		// --
		// Block 8 -- row 2 back
		moveLocation(1, "right", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// Block 9 -- row 1 back
		moveLocation(1, "down", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// Block 10 -- preasure plate
		moveLocation(1, "back", location, player);
		action.addStep(location, location.getBlock(), portalPressurePlateType);
		location.getBlock().setType(portalPressurePlateType);
		// Block 11 -- row 1 right
		moveLocation(1, "right", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// Block 12 -- row 1 left
		moveLocation(2, "left", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// --
		moveLocation(1, "right", location, player);
		// --
		// Block 13 -- floor middle
		moveLocation(1, "down", location, player);
		action.addStep(location, location.getBlock(), portalBlockType);
		location.getBlock().setType(portalBlockType);
		// Block 14 -- command block
		moveLocation(1, "down", location, player);
		spawnCommandBlock(location, warp_name, player, action);

		actionList.add(action);
		player.sendMessage(ChatColor.GREEN + "Portal to " + warp_name + " created");
	}

	/**
	 * Moves the location relative to the blockface direction
	 * 
	 * @param numOfTimes
	 * @param direction
	 * @param location
	 * @param player
	 * @return
	 */
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

	/**
	 * Sends the player a message saying they don't have permission to run a command
	 * 
	 * @param sender
	 */
	private void noPermission(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");

	}

	/**
	 * Recreates an undone portal
	 * 
	 * @param sender
	 * @param args
	 */
	private void redo(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Must be a player to run this command");
			return;
		}

		if (!sender.hasPermission("makeportals.create.redo")) {
			noPermission(sender);
			return;
		}

		actionList.redo((Player) sender);
	}

	/**
	 * Removes a saved warp location
	 * 
	 * @param sender
	 * @param warpName
	 */
	private void removeWarpLocation(CommandSender sender, String warpName) {
		if (!sender.hasPermission("makeportals.locations.remove")) {
			noPermission(sender);
			return;
		}

		// Test if exists
		if (getConfig().getString("warp_locations." + warpName) == null) {
			sender.sendMessage(ChatColor.RED + "Location\"" + warpName + "\" does not exist ");
			return;
		}

		getConfig().set("warp_locations." + warpName, null);
		saveConfig();

		sender.sendMessage(ChatColor.GREEN + "Location \"" + warpName + "\" removed");
	}

	/**
	 * Sets the type of block that will be used on new portals
	 * 
	 * @param sender
	 * @param args
	 */
	private void setBlockType(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.preferences.setblocktype")) {
			noPermission(sender);
			return;
		}

		Material blockType;

		try {
			blockType = Material.valueOf(args[1].toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid block type");
			return;
		}

		if (!blockType.isBlock()) {
			sender.sendMessage(ChatColor.RED + args[1] + " is not a placeable block");
			return;
		}

		for (Material material : fallingBlocks) {
			if (blockType.equals(material)) {
				sender.sendMessage(ChatColor.RED + "Portals cannot be made out of blocks that can fall");
				return;
			}
		}

		getConfig().set("portal_block_type", args[1].toUpperCase());
		saveConfig();

		sender.sendMessage(ChatColor.GREEN + "Portal block type set to " + blockType);
	}

	/**
	 * Sets what type of pressure plate will be used on new portals
	 * 
	 * @param sender
	 * @param args
	 */
	private void setPressurePlate(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.preferences.setpressureplatetype")) {
			noPermission(sender);
			return;
		}

		Material blockType;

		try {
			blockType = Material.valueOf(args[1].toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid block type");
			return;
		}

		boolean isPressurePlate = false;
		for (Material pressurePlateInList : pressurePlates) {
			if (pressurePlateInList == blockType) {
				isPressurePlate = true;
			}
		}

		if (!isPressurePlate) {
			sender.sendMessage(ChatColor.RED + args[1].toUpperCase() + " is not a type of pressure plate");
			return;
		}

		getConfig().set("portal_preasure_plate", args[1].toUpperCase());
		saveConfig();

		sender.sendMessage(ChatColor.GREEN + "Portal pressure plate set to " + blockType);
	}

	/**
	 * Sets a new warp location
	 * 
	 * @param sender
	 * @param args
	 */
	private void setWarpLocation(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.locations.set")) {
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

	/**
	 * Saves new warps to the config file
	 * 
	 * @param sender
	 * @param warp_name
	 * @param location
	 */
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

	/**
	 * Help menu for the player
	 * 
	 * @param sender
	 * @param args
	 */
	private void showHelpMenu(CommandSender sender, String[] args) {
		String helpMenu = "";

		if (args.length == 1 || args[1].equals("1")) {
			helpMenu += ChatColor.GREEN + "Commands\n";
			helpMenu += ChatColor.GREEN + "Create <Location Name>: " + ChatColor.WHITE
					+ "Creates a portal to that location\n";
			helpMenu += ChatColor.GREEN + "Location <set/remove/list>: " + ChatColor.WHITE
					+ "Set/remove a location or list all set locations\n";
			helpMenu += ChatColor.GREEN + "Setblocktype <blockType>: " + ChatColor.WHITE
					+ "Set the type of blocks portals will be made of\n";
			helpMenu += ChatColor.GREEN + "SetPressurePlate <Type Of Pressure Plate>: " + ChatColor.WHITE
					+ "Sets the type of pressure plates used in the portals\n";
			helpMenu += ChatColor.GREEN + "Teleport <Location name>: " + ChatColor.WHITE
					+ "Teleports you to the location\n";
			helpMenu += ChatColor.GREEN + "Signsenabled <true/false>: " + ChatColor.WHITE
					+ "Sets if signs will spawn on the portals\n";
			helpMenu += ChatColor.GREEN + "Undo: " + ChatColor.WHITE + "Undoes portal creation\n";
			helpMenu += ChatColor.GREEN + "Redo: " + ChatColor.WHITE + "redoes portal creation\n";
			helpMenu += ChatColor.GREEN + "TeleportSoundEnabled <true/false>: " + ChatColor.WHITE
					+ "Toggles teleport sound\n";
			helpMenu += ChatColor.GREEN + "TeleportParticlesEnabled <true/false>: " + ChatColor.WHITE
					+ "Toggles teleport particles";
		}

		sender.sendMessage(helpMenu);
	}

	/**
	 * Spawns the command block under the portal
	 * 
	 * @param location
	 * @param warp_name
	 * @param player
	 * @param action
	 */
	private void spawnCommandBlock(Location location, String warp_name, Player player, PortalCreatedAction action) {
		action.addStep(location, location.getBlock(), Material.COMMAND_BLOCK);

		location.getBlock().setType(Material.COMMAND_BLOCK);

		CommandBlock cmdBlock = (CommandBlock) location.getBlock().getState();
		double x = (int) location.getX();
		double y = (int) location.getY();
		double z = (int) location.getZ();

		x = (x < 0 ? x - 0.5 : x + 0.5);
		y += 2;
		z = (z < 0 ? z - 0.5 : z + 0.5);

		// X + 0.5 ~ Z + 0.5 ~ Y + 2 ~ Offset

		String commandBlockCommand = "/mp tpc " + location.getWorld().getName() + " " + x + " " + y + " " + z + " "
				+ warp_name;

		cmdBlock.setCommand(commandBlockCommand);
		cmdBlock.update();

		action.setCommandBlockCommand(commandBlockCommand);
	}

	/**
	 * Spawns a sign on the portal while being created
	 * 
	 * @param moveLocation
	 * @param warp_name
	 * @param blockFace
	 * @param action
	 */
	private void spawnSign(Location moveLocation, String warp_name, BlockFace blockFace, PortalCreatedAction action) {
		action.addStep(moveLocation, moveLocation.getBlock(), Material.WALL_SIGN);

		moveLocation.getBlock().setType(Material.WALL_SIGN);
		Block block = moveLocation.getBlock();
		block.setType(Material.AIR); // Incase theres already a sign there it doesnt just add text
		block.setType(Material.WALL_SIGN);

		Directional dir = (Directional) block.getBlockData();

		dir.setFacing(blockFace.getOppositeFace());
		block.setBlockData(dir);

		Sign sign = (Sign) block.getState();
		sign.setLine(1, warp_name);
		sign.update();

		action.setSignText(warp_name);
		action.setSignDirection(blockFace.getOppositeFace());
	}

	/**
	 * Teleports player to a set warp
	 * 
	 * @param sender
	 * @param args
	 */
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
			// Sound
			if (getConfig().getString("teleport_sound_enabled").equals("true")) {
				((Player) sender).playSound(teleportLocation, teleportSound, 1f, 1f);
			}
			// Particles
			if (getConfig().getString("teleport_particles_enabled").equals("true")) {
				((Player) sender).spawnParticle(teleportParticle, teleportLocation, teleportParticleSize);
			}
		} else if (args.length == 3) {
			Player player = getServer().getPlayer(args[1]);

			if (player == null) {
				sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" does not exist in this server");
				return;
			}

			Location teleportLocation = getTeleportLocation(sender, args[2]);

			player.teleport(teleportLocation);
			// Sound
			if (getConfig().getString("teleport_sound_enabled").equals("true")) {
				player.playSound(teleportLocation, teleportSound, 1f, 1f);
			}
			// Particles
			if (getConfig().getString("teleport_particles_enabled").equals("true")) {
				player.spawnParticle(teleportParticle, teleportLocation, teleportParticleSize);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Invalid number of arguements");
		}
	}

	private void testCommand(CommandSender sender, String[] args) {
//		Location location = new Location(getServer().getWorld("world"), -139, 71, 89);
//
//		Block block = location.getBlock();

//		Container chest = (Container) block.getState();
//
//		ItemStack[] contents = chest.getInventory().getContents().clone();
//		ItemStack[] backupContents = new ItemStack[contents.length];
//
//		String test = "";
//
//		for (int i = 0; i < contents.length; i++) {
//			if (contents[i] != null) {
//				backupContents[i] = contents[i].clone();
//			}
//		}
//
//		chest.getInventory().clear();
//		block.setType(Material.DIAMOND_BLOCK);
//
//		for (int i = 0; i < contents.length; i++) {
//			if (backupContents[i] != null) {
//				test += backupContents[i].toString() + "[" + i + "], ";
//			}
//		}
//
//		sender.sendMessage(test);
	}

	/**
	 * Set if signs spawn on new portals
	 * 
	 * @param sender
	 * @param args
	 */
	private void toggleSigns(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.preferences.togglesigns")) {
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

	/**
	 * Set if particles apear around teleported players
	 * 
	 * @param sender
	 * @param args
	 */
	private void toggleTeleportParticles(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.preferences.setteleportparticlesenabled")) {
			noPermission(sender);
			return;
		}

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED
					+ "Invalid number of arguements\nUsage: /MakePortals teleportparticlesenabled <true/false>");
			return;
		}

		if (!args[1].equalsIgnoreCase("true") && !args[1].toLowerCase().equalsIgnoreCase("false")) {
			sender.sendMessage(
					ChatColor.RED + "Invalid arguements\nUsage: /MakePortals teleportparticlesenabled <true/false>");
			return;
		}

		getConfig().set("teleport_particles_enabled", args[1].toLowerCase());
		saveConfig();
		if (args[1].equalsIgnoreCase("true")) {
			sender.sendMessage(ChatColor.GREEN + "Teleport particles enabled");
		} else {
			sender.sendMessage(ChatColor.GREEN + "Teleport particles disabled");
		}
	}

	/**
	 * Set if a sound plays when a player is teleported
	 * 
	 * @param sender
	 * @param args
	 */
	private void toggleTeleportSound(CommandSender sender, String[] args) {
		if (!sender.hasPermission("makeportals.preferences.setteleportsoundenabled")) {
			noPermission(sender);
			return;
		}

		if (args.length != 2) {
			sender.sendMessage(ChatColor.RED
					+ "Invalid number of arguements\nUsage: /MakePortals teleportSoundEnabled <true/false>");
			return;
		}

		if (!args[1].equalsIgnoreCase("true") && !args[1].toLowerCase().equalsIgnoreCase("false")) {
			sender.sendMessage(
					ChatColor.RED + "Invalid arguements\nUsage: /MakePortals teleportSoundEnabled <true/false>");
			return;
		}

		getConfig().set("teleport_sound_enabled", args[1].toLowerCase());
		saveConfig();
		if (args[1].equalsIgnoreCase("true")) {
			sender.sendMessage(ChatColor.GREEN + "Teleport sound enabled");
		} else {
			sender.sendMessage(ChatColor.GREEN + "Teleport sound disabled");
		}
	}

	/**
	 * Undoes the last created portal by the player
	 * 
	 * @param sender
	 * @param args
	 */
	private void undo(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Must be a player to run this command");
			return;
		}

		if (!sender.hasPermission("makeportals.create.undo")) {
			noPermission(sender);
			return;
		}

		actionList.undo((Player) sender);
	}
}