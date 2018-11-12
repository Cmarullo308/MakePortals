package me.MakePortals.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Container;
import org.bukkit.block.Furnace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class PortalCreatedAction {
	Main main;

	private ArrayList<Location> blockLocations;
	//
	private ArrayList<Material> fromMaterials;
	private ArrayList<BlockData> fromBlockDatas;
	private ArrayList<ItemStack[]> fromBlockInventoryContents;
	private ArrayList<String[]> fromSignText;
	private ArrayList<List<Pattern>> fromPatten;
	private ArrayList<PotionEffectType> fromPotionEffectTypes;
	//
	private ArrayList<Material> toMaterials;
	private ArrayList<ItemStack> usedToMake; // Block type - Pressureplate type - sign - portalCrystal
	private String commandBlockCommand;
	private String signtext;
	private BlockFace signDirection;
	private Player player;
	private boolean madeInSurvival;
	private boolean undone;
	private int size;

	public void undo(Player player) {
		// for (int blockNum = 0; blockNum < size(); blockNum++) {
		for (int blockNum = size() - 1; blockNum >= 0; blockNum--) {
			switch (getFromMaterial(blockNum)) {
			case CHEST:
			case TRAPPED_CHEST:
			case FURNACE:
			case HOPPER:
			case DISPENSER:
			case BREWING_STAND:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Container container = (Container) getLocation(blockNum).getBlock().getState();
				container.setBlockData(fromBlockDatas.get(blockNum));
				container.update();// Has to update before setting contents because it erases the contents
				container.getInventory().setContents(fromBlockInventoryContents.get(blockNum));
				break;
			case SIGN:
			case WALL_SIGN:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Sign sign = (Sign) getLocation(blockNum).getBlock().getState();
				sign.setBlockData(fromBlockDatas.get(blockNum));
				for (int line = 0; line < fromSignText.get(blockNum).length; line++) {
					sign.setLine(line, fromSignText.get(blockNum)[line]);
				}
				sign.update();

				getLocation(blockNum).getBlock().setBlockData(fromBlockDatas.get(blockNum));
				break;
			case BEACON:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Beacon beacon = (Beacon) getLocation(blockNum).getBlock().getState();

				beacon.setPrimaryEffect(fromPotionEffectTypes.get(blockNum));
				beacon.update();
				break;
			case WHITE_SHULKER_BOX:
			case ORANGE_SHULKER_BOX:
			case MAGENTA_SHULKER_BOX:
			case LIGHT_BLUE_SHULKER_BOX:
			case YELLOW_SHULKER_BOX:
			case LIME_SHULKER_BOX:
			case PINK_SHULKER_BOX:
			case GRAY_SHULKER_BOX:
			case LIGHT_GRAY_SHULKER_BOX:
			case CYAN_SHULKER_BOX:
			case PURPLE_SHULKER_BOX:
			case BLUE_SHULKER_BOX:
			case BROWN_SHULKER_BOX:
			case GREEN_SHULKER_BOX:
			case RED_SHULKER_BOX:
			case BLACK_SHULKER_BOX:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				ShulkerBox shulkerBox = (ShulkerBox) getLocation(blockNum).getBlock().getState();
				shulkerBox.setBlockData(fromBlockDatas.get(blockNum));
				shulkerBox.update();// Has to update before setting contents because it erases the contents
				shulkerBox.getInventory().setContents(fromBlockInventoryContents.get(blockNum));
				break;
			case WHITE_BANNER:
			case ORANGE_BANNER:
			case MAGENTA_BANNER:
			case LIGHT_BLUE_BANNER:
			case YELLOW_BANNER:
			case LIME_BANNER:
			case PINK_BANNER:
			case GRAY_BANNER:
			case LIGHT_GRAY_BANNER:
			case CYAN_BANNER:
			case PURPLE_BANNER:
			case BLUE_BANNER:
			case BROWN_BANNER:
			case GREEN_BANNER:
			case RED_BANNER:
			case BLACK_BANNER:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Banner banner = (Banner) getLocation(blockNum).getBlock().getState();
				banner.setBlockData(fromBlockDatas.get(blockNum));
				banner.setPatterns(fromPatten.get(blockNum));
				banner.update();
				break;
			default:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				getLocation(blockNum).getBlock().setBlockData(fromBlockDatas.get(blockNum));
				break;
			}
		}

		String message = "";
		if (madeInSurvival) {
			for (ItemStack item : usedToMake) {
				giveItems(item, player);
				message += item.toString() + "\n";
			}
		}
		main.getLogger().info(message);

		setUndone(true);
	}

	public PortalCreatedAction(Player player, Main main, Material portalblockType, Material portalPressurePlateType,
			boolean useSign) {
		this.main = main;

		blockLocations = new ArrayList<Location>();
		//
		fromMaterials = new ArrayList<Material>();
		fromBlockDatas = new ArrayList<BlockData>();
		fromBlockInventoryContents = new ArrayList<ItemStack[]>();
		fromSignText = new ArrayList<String[]>();
		fromPatten = new ArrayList<List<Pattern>>();
		fromPotionEffectTypes = new ArrayList<PotionEffectType>();
		//
		toMaterials = new ArrayList<Material>();
		this.player = player;

		usedToMake = new ArrayList<ItemStack>();
		usedToMake.add(new ItemStack(portalblockType, 10));
		usedToMake.add(new ItemStack(portalPressurePlateType, 1));
		if (useSign) {
			usedToMake.add(new ItemStack(Material.SIGN, 1));
		} else {
			usedToMake.add(new ItemStack(Material.AIR, 1));
		}
		usedToMake.add(Main.getPortalCrystalItemStack(1));

		madeInSurvival = player.getGameMode() == GameMode.SURVIVAL;
		undone = false;
		size = 0;

		//
	}

	public void changeUsedToMake(int spot, ItemStack itemStack) {
		usedToMake.set(spot, itemStack);
	}

	public void addStep(Location location, Block fromBlock, Material toMaterial) {
		boolean hasPattern = false;
		boolean hasSignText = false;
		boolean hasInventory = false;
		boolean hadPotionEffectType = false;

		blockLocations.add(location.clone());
		fromMaterials.add(fromBlock.getType());
		fromBlockDatas.add(fromBlock.getBlockData().clone());

		switch (fromBlock.getType()) {
		case CHEST:
			addStepContainer(fromBlock);
			hasInventory = true;
			break;
		case FURNACE:
			addStepContainer(fromBlock);
			hasInventory = true;
			break;
		case HOPPER:
			addStepContainer(fromBlock);
			hasInventory = true;
			break;
		case TRAPPED_CHEST:
			addStepContainer(fromBlock);
			hasInventory = true;
			break;
		case DISPENSER:
			addStepContainer(fromBlock);
			hasInventory = true;
			break;
		case BREWING_STAND:
			addStepContainer(fromBlock);
			break;
		case SIGN:
			addStepSign(fromBlock);
			hasSignText = true;
			break;
		case WALL_SIGN:
			addStepSign(fromBlock);
			hasSignText = true;
			break;
		case BEACON:
			addStepBeacon(fromBlock);
			hadPotionEffectType = true;
			break;
		case WHITE_SHULKER_BOX:
		case ORANGE_SHULKER_BOX:
		case MAGENTA_SHULKER_BOX:
		case LIGHT_BLUE_SHULKER_BOX:
		case YELLOW_SHULKER_BOX:
		case LIME_SHULKER_BOX:
		case PINK_SHULKER_BOX:
		case GRAY_SHULKER_BOX:
		case LIGHT_GRAY_SHULKER_BOX:
		case CYAN_SHULKER_BOX:
		case PURPLE_SHULKER_BOX:
		case BLUE_SHULKER_BOX:
		case BROWN_SHULKER_BOX:
		case GREEN_SHULKER_BOX:
		case RED_SHULKER_BOX:
		case BLACK_SHULKER_BOX:
			addStepContainer(fromBlock);
			hasInventory = true;
			break;
		case WHITE_BANNER:
		case ORANGE_BANNER:
		case MAGENTA_BANNER:
		case LIGHT_BLUE_BANNER:
		case YELLOW_BANNER:
		case LIME_BANNER:
		case PINK_BANNER:
		case GRAY_BANNER:
		case LIGHT_GRAY_BANNER:
		case CYAN_BANNER:
		case PURPLE_BANNER:
		case BLUE_BANNER:
		case BROWN_BANNER:
		case GREEN_BANNER:
		case RED_BANNER:
		case BLACK_BANNER:
			addStepBanner(fromBlock);
			hasPattern = true;
			break;
		default:
			break;
		}

		if (!hasInventory) {
			fromBlockInventoryContents.add(null);
		}

		if (!hasPattern) {
			fromPatten.add(null);
		}

		if (!hasSignText) {
			fromSignText.add(null);
		}

		if (!hadPotionEffectType) {
			fromPotionEffectTypes.add(null);
		}

		toMaterials.add(toMaterial);
		size++;
	}

	private void addStepContainer(Block fromBlock) {
		Container container = (Container) fromBlock.getState();
		ItemStack[] oldContents = container.getInventory().getContents();
		ItemStack[] backupContents = new ItemStack[oldContents.length];

		for (int spot = 0; spot < oldContents.length; spot++) {
			if (oldContents[spot] != null) {
				backupContents[spot] = oldContents[spot].clone();
			}
		}

		fromBlockInventoryContents.add(backupContents);

		container.getInventory().clear(); // So the chest doesn't drop its contents
	}

	private void addStepBeacon(Block fromBlock) {
		Beacon beacon = (Beacon) fromBlock.getState();

		fromPotionEffectTypes.add(beacon.getPrimaryEffect().getType());
	}

	private void addStepBanner(Block fromBlock) {
		Banner banner = (Banner) fromBlock.getState();

		fromPatten.add(new ArrayList<Pattern>(banner.getPatterns()));
	}

	private void addStepSign(Block fromBlock) {
		Sign sign = (Sign) fromBlock.getState();
		fromSignText.add(sign.getLines().clone());
	}

	public Location getLocation(int index) {
		return blockLocations.get(index);
	}

	public Material getFromMaterial(int index) {
		return fromMaterials.get(index);
	}

	public Material getToMaterial(int index) {
		return toMaterials.get(index);
	}

	public Player getPlayer() {
		return player;
	}

	public int size() {
		return size;
	}

	public boolean isUndone() {
		return undone;
	}

	public void setSignDirection(BlockFace direction) {
		signDirection = direction;
	}

	public BlockFace getSignDirection() {
		return signDirection;
	}

	public void setUndone(boolean undone) {
		this.undone = undone;
	}

	public void setCommandBlockCommand(String command) {
		commandBlockCommand = command;
	}

	public String getCommandBlockCommand() {
		return commandBlockCommand;
	}

	public void setSignText(String text) {
		signtext = text;
	}

	public String getSignText() {
		return signtext;
	}

	private boolean checkAndRemoveBlocks(Player player, Material blockType, Material pressurePlateType) {
		// If in survival, checks that players has the portal crystal and blocks needed
		if (player.getGameMode() == GameMode.SURVIVAL) {
			Inventory inv = player.getInventory();
			ItemStack[] contents = inv.getContents();
			int portalCrystalLocation = 0;
			boolean enough_items = false;

			if (inv.contains(blockType, 10) && inv.contains(Material.SIGN, 1) && inv.contains(pressurePlateType, 1)) {
				for (int i = 0; i < contents.length; i++) {
					if (contents[i] != null && contents[i].getItemMeta().getLore() != null
							&& contents[i].getItemMeta().getLore().get(0).equals(Main.portalCrystalLore)) {
						enough_items = true;
						portalCrystalLocation = i;
					}
				}
			}

			if (!enough_items) {
				player.sendMessage(ChatColor.RED + "Not enough blocks, you need 10 " + blockType
						+ ", 1 SIGN, 1 Portal Crystal and 1 " + pressurePlateType);
				return false;
			}

			inv.removeItem(new ItemStack(blockType, 10));
			inv.removeItem(new ItemStack(Material.SIGN, 1));
			inv.removeItem(new ItemStack(pressurePlateType, 1));
			inv.getItem(portalCrystalLocation).setAmount(inv.getItem(portalCrystalLocation).getAmount() - 1);

			return true;
		} else {
			return true;
		}
	}

	public void redo() {
		if (madeInSurvival) {
			if (!checkAndRemoveBlocks(player, usedToMake.get(0).getType(), usedToMake.get(1).getType())) {
				return;
			}
		}

		for (int blockNum = 0; blockNum < size(); blockNum++) {
			// Clears inventorys before breaking
			Material materialInLocation = getLocation(blockNum).getBlock().getType();

			if (materialInLocation == Material.CHEST || materialInLocation == Material.TRAPPED_CHEST) {
				Chest chest = (Chest) getLocation(blockNum).getBlock().getState();
				chest.getInventory().clear();
			} else if (materialInLocation == Material.FURNACE) {
				Furnace furnace = (Furnace) getLocation(blockNum).getBlock().getState();
				furnace.getInventory().clear();
			}

			if (getToMaterial(blockNum) == Material.WALL_SIGN) {
				setSign(getLocation(blockNum), getSignDirection(), getSignText());
			} else if (getToMaterial(blockNum) == Material.COMMAND_BLOCK) {
				setCommandBlock(getLocation(blockNum), getCommandBlockCommand());
			} else {
				getLocation(blockNum).getBlock().setType(getToMaterial(blockNum));
			}
		}

		setUndone(false);
	}

	private void giveItems(ItemStack items, Player player) {
		HashMap<Integer, ItemStack> excessItems = player.getInventory().addItem(items);
		for (Map.Entry<Integer, ItemStack> ex : excessItems.entrySet()) {
			player.getWorld().dropItem(player.getLocation(), ex.getValue());
		}
	}

	private void setSign(Location location, BlockFace signDirection, String signText) {
		Block block = location.getBlock();
		block.setType(Material.WALL_SIGN);

		Directional direction = (Directional) block.getBlockData();

		direction.setFacing(signDirection);
		block.setBlockData(direction);

		Sign sign = (Sign) block.getState();
		sign.setLine(1, signText);
		sign.update();
	}

	private void setCommandBlock(Location location, String commandBlockCommand) {
		location.getBlock().setType(Material.COMMAND_BLOCK);
		CommandBlock cmdBlock = (CommandBlock) location.getBlock().getState();

		cmdBlock.setCommand(commandBlockCommand);
		cmdBlock.update();
	}
}