package me.MakePortals.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.sun.org.apache.xpath.internal.axes.HasPositionalPredChecker;

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
	private String commandBlockCommand;
	private String signtext;
	private BlockFace signDirection;
	private Player player;
	private boolean undone;
	private int size;

	public void undo() {
		// for (int blockNum = 0; blockNum < size(); blockNum++) {
		for (int blockNum = size() - 1; blockNum >= 0; blockNum--) {
			switch (getFromMaterial(blockNum)) {
			case CHEST:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Chest chest = (Chest) getLocation(blockNum).getBlock().getState();
				chest.setBlockData(fromBlockDatas.get(blockNum));
				chest.update();// Has to update before setting contents because it erases the contents
				chest.getInventory().setContents(fromBlockInventoryContents.get(blockNum));
				break;
			case TRAPPED_CHEST:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Chest tChest = (Chest) getLocation(blockNum).getBlock().getState();
				tChest.setBlockData(fromBlockDatas.get(blockNum));
				tChest.update();// Has to update before setting contents because it erases the contents
				tChest.getInventory().setContents(fromBlockInventoryContents.get(blockNum));
				break;
			case FURNACE:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Furnace furnace = (Furnace) getLocation(blockNum).getBlock().getState();
				furnace.setBlockData(fromBlockDatas.get(blockNum));
				furnace.update();// Has to update before setting contents because it erases the contents
				furnace.getInventory().setContents(fromBlockInventoryContents.get(blockNum));
				break;
			case HOPPER:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Hopper hopper = (Hopper) getLocation(blockNum).getBlock().getState();
				hopper.setBlockData(fromBlockDatas.get(blockNum));
				hopper.update();
				hopper.getInventory().setContents(fromBlockInventoryContents.get(blockNum));
				break;
			case DISPENSER:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				Dispenser dispenser = (Dispenser) getLocation(blockNum).getBlock().getState();
				dispenser.setBlockData(fromBlockDatas.get(blockNum));
				dispenser.update();// Has to update before setting contents because it erases the contents
				dispenser.getInventory().setContents(fromBlockInventoryContents.get(blockNum));
				break;
			case BREWING_STAND:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
				BrewingStand brewingStand = (BrewingStand) getLocation(blockNum).getBlock().getState();
				brewingStand.setBlockData(fromBlockDatas.get(blockNum));
				brewingStand.update();// Has to update before setting contents because it erases the contents
				brewingStand.getInventory().setContents(fromBlockInventoryContents.get(blockNum));
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
	}

	public PortalCreatedAction(Player player, Main main) {
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
		undone = false;
		size = 0;
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
			addStepChest(fromBlock);
			hasInventory = true;
			break;
		case FURNACE:
			addStepFurnace(fromBlock);
			hasInventory = true;
			break;
		case HOPPER:
			addStepHopper(fromBlock);
			hasInventory = true;
			break;
		case TRAPPED_CHEST:
			addStepChest(fromBlock);
			hasInventory = true;
			break;
		case DISPENSER:
			addStepDispenser(fromBlock);
			hasInventory = true;
			break;
		case BREWING_STAND:
			addStepBrewingStand(fromBlock);
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
			addStepShulkerBox(fromBlock);
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

	private void addStepBeacon(Block fromBlock) {
		Beacon beacon = (Beacon) fromBlock.getState();

		fromPotionEffectTypes.add(beacon.getPrimaryEffect().getType());
	}

	private void addStepBrewingStand(Block fromBlock) {
		BrewingStand brewingStand = (BrewingStand) fromBlock.getState();
		ItemStack[] oldContents = brewingStand.getInventory().getContents();
		ItemStack[] backupContents = new ItemStack[oldContents.length];

		for (int spot = 0; spot < oldContents.length; spot++) {
			if (oldContents[spot] != null) {
				backupContents[spot] = oldContents[spot].clone();
			}
		}

		fromBlockInventoryContents.add(backupContents);

		brewingStand.getInventory().clear(); // So the chest doesn't drop its contents
	}

	private void addStepShulkerBox(Block fromBlock) {
		ShulkerBox shulkerBox = (ShulkerBox) fromBlock.getState();
		ItemStack[] oldContents = shulkerBox.getInventory().getContents();
		ItemStack[] backupContents = new ItemStack[oldContents.length];

		for (int spot = 0; spot < oldContents.length; spot++) {
			if (oldContents[spot] != null) {
				backupContents[spot] = oldContents[spot].clone();
			}
		}

		fromBlockInventoryContents.add(backupContents);

		shulkerBox.getInventory().clear(); // So the container doesn't drop its contents
	}

	private void addStepBanner(Block fromBlock) {
		Banner banner = (Banner) fromBlock.getState();

		fromPatten.add(new ArrayList<Pattern>(banner.getPatterns()));
	}

	private void addStepDispenser(Block fromBlock) {
		Dispenser dispenser = (Dispenser) fromBlock.getState();
		ItemStack[] oldContents = dispenser.getInventory().getContents();
		ItemStack[] backupContents = new ItemStack[oldContents.length];

		for (int spot = 0; spot < oldContents.length; spot++) {
			if (oldContents[spot] != null) {
				backupContents[spot] = oldContents[spot].clone();
			}
		}

		fromBlockInventoryContents.add(backupContents);

		dispenser.getInventory().clear(); // So the dispenser doesn't drop its contents
	}

	private void addStepSign(Block fromBlock) {
		Sign sign = (Sign) fromBlock.getState();
		fromSignText.add(sign.getLines().clone());
	}

	private void addStepHopper(Block fromBlock) {
		Hopper hopper = (Hopper) fromBlock.getState();
		ItemStack[] oldContents = hopper.getInventory().getContents();
		ItemStack[] backupContents = new ItemStack[oldContents.length];

		for (int spot = 0; spot < oldContents.length; spot++) {
			if (oldContents[spot] != null) {
				backupContents[spot] = oldContents[spot].clone();
			}
		}

		fromBlockInventoryContents.add(backupContents);

		hopper.getInventory().clear(); // So the chest doesn't drop its contents
	}

	private void addStepFurnace(Block fromBlock) {
		Furnace furnace = (Furnace) fromBlock.getState();
		ItemStack[] oldContents = furnace.getInventory().getContents();
		ItemStack[] backupContents = new ItemStack[oldContents.length];

		for (int spot = 0; spot < oldContents.length; spot++) {
			if (oldContents[spot] != null) {
				backupContents[spot] = oldContents[spot].clone();
			}
		}

		fromBlockInventoryContents.add(backupContents);

		furnace.getInventory().clear(); // So the chest doesn't drop its contents
	}

	private void addStepChest(Block fromBlock) {
		Chest chest = (Chest) fromBlock.getState();
		ItemStack[] oldContents = chest.getInventory().getContents();
		ItemStack[] backupContents = new ItemStack[oldContents.length];

		for (int spot = 0; spot < oldContents.length; spot++) {
			if (oldContents[spot] != null) {
				backupContents[spot] = oldContents[spot].clone();
			}
		}

		fromBlockInventoryContents.add(backupContents);

		chest.getInventory().clear(); // So the chest doesn't drop its contents
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

	public void redo() {
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