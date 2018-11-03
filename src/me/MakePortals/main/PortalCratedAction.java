package me.MakePortals.main;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PortalCratedAction {
	ArrayList<Location> blockLocations;
	ArrayList<Block> fromBlocks;
	ArrayList<Block> toBlocks;
	Player player;
	boolean undone;
	int size;

	public PortalCratedAction(Player player) {
		blockLocations = new ArrayList<Location>();
		fromBlocks = new ArrayList<Block>();
		toBlocks = new ArrayList<Block>();
		this.player = player;
		undone = false;
		size = 0;
	}

	public void addStep(Location location, Block fromBlock, Block toblock) {
		blockLocations.add(location);
		fromBlocks.add(fromBlock);
		toBlocks.add(toblock);
	}

	public Location getLocation(int index) {
		return blockLocations.get(index);
	}

	public Block getFromBlock(int index) {
		return fromBlocks.get(index);
	}

	public Block getToBlock(int index) {
		return toBlocks.get(index);
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

	public void setUndone(boolean undone) {
		this.undone = undone;
	}
}
