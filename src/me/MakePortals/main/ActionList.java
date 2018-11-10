package me.MakePortals.main;

import java.util.ArrayList;

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
				e.undo();
				e.setUndone(true);
				return;
			}
		}
	}

	public void redo(Player player) {
		PortalCreatedAction e;

		for (int i = 0; i < this.size(); i++) {
			e = (PortalCreatedAction) get(i);
			if (e.getPlayer().equals(player) && e.isUndone()) {
				e.redo();
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
}