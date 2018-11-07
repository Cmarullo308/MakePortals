package me.MakePortals.main;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;

public class PortalCreatedAction {
	Main main;

	private ArrayList<Location> blockLocations;
	private ArrayList<Material> fromMaterials;
	private ArrayList<Material> toMaterials;
	private String commandBlockCommand;
	private String signtext;
	private BlockFace signDirection;
	private Player player;
	private boolean undone;
	private int size;

	public void undo() {
		for (int blockNum = 0; blockNum < size(); blockNum++) {
			getLocation(blockNum).getBlock().setType(getFromMaterial(blockNum));
		}
	}

	public PortalCreatedAction(Player player, Main main) {
		this.main = main;

		blockLocations = new ArrayList<Location>();
		fromMaterials = new ArrayList<Material>();
		toMaterials = new ArrayList<Material>();
		this.player = player;
		undone = false;
		size = 0;
	}

	public void addStep(Location location, Material fromMaterial, Material toMaterial) {
		blockLocations.add(location.clone());
		fromMaterials.add(fromMaterial);
		toMaterials.add(toMaterial);
		size++;
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