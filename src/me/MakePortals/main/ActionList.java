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

public class ActionList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1L;
	private int maxSize;
	private boolean maxSizeEnabled;

	public ActionList() {
		super();
		maxSize = -1;
		maxSizeEnabled = false;
	}

	public ActionList(int maxSize) {
		super();
		if (maxSize >= 1) {
			this.maxSize = maxSize;
			this.maxSizeEnabled = true;
		} else {
			maxSize = -1;
			maxSizeEnabled = false;
		}
	}

	@Override
	public boolean add(T e) {
		if (maxSizeEnabled && this.size() >= maxSize) {
			remove(0);
		}

		return super.add(e);
	}

	public void undo(Player player) {
		PortalCreatedAction e;
	
		for (int i = this.size() - 1; i >= 0; i--) {
			e = (PortalCreatedAction) get(i);
			if (e.getPlayer().equals(player) && !e.isUndone()) {
				for (int blockNum = 0; blockNum < e.size(); blockNum++) {
					e.getLocation(blockNum).getBlock().setType(e.getFromMaterial(blockNum));
				}
				e.setUndone(true);
				return;
			}
	
		}
	
	}

	public void redo(Player player) {
		PortalCreatedAction e;
	
		for (int i = 0; i < this.size() - 1; i++) {
			e = (PortalCreatedAction) get(i);
			if (e.getPlayer().equals(player) && e.isUndone()) {
				for (int blockNum = 0; blockNum < e.size(); blockNum++) {
					if (e.getToMaterial(blockNum) == Material.WALL_SIGN) {
						setSign(e.getLocation(blockNum), e.getSignDirection(), e.getSignText());
					} else if (e.getToMaterial(blockNum) == Material.COMMAND_BLOCK) {
						setCommandBlock(e.getLocation(blockNum), e.getCommandBlockCommand());
					} else {
						e.getLocation(blockNum).getBlock().setType(e.getToMaterial(blockNum));
					}
				}
				e.setUndone(false);
				return;
			}
		}
	}

	public void deleteUndosFor(Player player) {
		PortalCreatedAction e;

		for (int i = this.size() - 1; i >= 0; i--) {
			e = (PortalCreatedAction) get(i);
			if (e.isUndone() && e.getPlayer().equals(player)) {
				this.remove(i);
			}
		}
	}

	private void setCommandBlock(Location location, String commandBlockCommand) {
		location.getBlock().setType(Material.COMMAND_BLOCK);
		CommandBlock cmdBlock = (CommandBlock) location.getBlock().getState();

		cmdBlock.setCommand(commandBlockCommand);
		cmdBlock.update();
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
}
