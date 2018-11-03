package me.MakePortals.main;

import java.util.ArrayList;

public class ActionList<T> extends ArrayList<Object> {
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
	public boolean add(Object e) {
		if (maxSizeEnabled && this.size() >= maxSize) {
			remove(0);
		}

		return super.add(e);
	}
}
