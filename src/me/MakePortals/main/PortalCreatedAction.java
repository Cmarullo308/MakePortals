package me.MakePortals.main;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PortalCreatedAction {
	Main main;

	private ArrayList<Location> blockLocations;
	//
	private ArrayList<Material> fromMaterials;
	private ArrayList<BlockData> fromBlockDatas;
	private ArrayList<ItemStack[]> fromBlockInventoryContents;
	//
	private ArrayList<Material> toMaterials;
	private String commandBlockCommand;
	private String signtext;
	private BlockFace signDirection;
	private Player player;
	private boolean undone;
	private int size;

	public void undo() {
		main.getLogger().info(fromBlockInventoryContents.size() + "");
		for (int blockNum = 0; blockNum < size(); blockNum++) {
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
			default:
				getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
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
		//
		toMaterials = new ArrayList<Material>();
		this.player = player;
		undone = false;
		size = 0;
	}

	public void addStep(Location location, Block fromBlock, Material toMaterial) {
		blockLocations.add(location.clone());
		fromMaterials.add(fromBlock.getType());
		fromBlockDatas.add(fromBlock.getBlockData().clone());
		switch (fromBlock.getType()) {
		case CHEST:
			addStepChest(fromBlock);
			break;
		case FURNACE:
			addStepFurnace(fromBlock);
			break;
		case HOPPER:
			addStepHopper(fromBlock);
			break;
		case TRAPPED_CHEST:
			addStepChest(fromBlock);
			break;
		default:
			fromBlockInventoryContents.add(null);
			break;
		}
		toMaterials.add(toMaterial);
		size++;
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