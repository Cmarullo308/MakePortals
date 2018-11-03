package me.MakePortals.main;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class PortalCreatedAction {
	private ArrayList<Location> blockLocations;
	private ArrayList<Material> fromMaterials;
	private ArrayList<Material> toMaterials;
	private String commandBlockCommand;
	private String signtext;
	private BlockFace signDirection;
	private Player player;
	private boolean undone;
	private int size;

	public PortalCreatedAction(Player player) {
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
}
