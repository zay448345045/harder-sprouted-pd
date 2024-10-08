/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.github.dachhack.sprout.ui;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.DungeonTilemap;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.wands.Wand;
import com.github.dachhack.sprout.items.weapon.missiles.Boomerang;
import com.github.dachhack.sprout.items.weapon.missiles.JupitersWrath;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.scenes.PixelScene;
import com.github.dachhack.sprout.windows.WndBag;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Button;

public class QuickSlotButton extends Button implements WndBag.Listener {

	private static final String TXT_SELECT_ITEM = "Select an item for the quickslot";

	private static QuickSlotButton[] instance = new QuickSlotButton[4];
	private int slotNum;

	private ItemSlot slot;

	private static Image crossB;
	private static Image crossM;

	private static boolean targeting = false;
	public static Char lastTarget = null;

	public QuickSlotButton(int slotNum) {
		super();
		this.slotNum = slotNum;
		item(select(slotNum));

		instance[slotNum] = this;
	}

	@Override
	public void destroy() {
		super.destroy();

		reset();
	}

	public static void reset() {
		instance = new QuickSlotButton[4];

		lastTarget = null;
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		slot = new ItemSlot() {
			@Override
			protected void onClick() {
				if (targeting) {
					GameScene.handleCell(lastTarget.pos);
				} else {
					Item item = select(slotNum);
					if (item.stackable || item instanceof Wand
							|| item instanceof Boomerang
							|| item instanceof JupitersWrath)
						useTargeting();
					item.execute(Dungeon.hero);
				}
			}

			@Override
			protected boolean onLongClick() {
				return QuickSlotButton.this.onLongClick();
			}

			@Override
			protected void onTouchDown() {
				icon.lightness(0.7f);
			}

			@Override
			protected void onTouchUp() {
				icon.resetColor();
			}
		};
		add(slot);

		crossB = Icons.TARGET.get();
		crossB.visible = false;
		add(crossB);

		crossM = new Image();
		crossM.copy(crossB);
	}

	@Override
	protected void layout() {
		super.layout();

		slot.fill(this);

		crossB.x = PixelScene.align(x + (width - crossB.width) / 2);
		crossB.y = PixelScene.align(y + (height - crossB.height) / 2);
	}

	@Override
	protected void onClick() {
		GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, Messages.get(QuickSlotButton.class,"quicktips"));
	}

	@Override
	protected boolean onLongClick() {
		GameScene.selectItem(this, WndBag.Mode.QUICKSLOT, Messages.get(QuickSlotButton.class,"quicktips"));
		return true;
	}

	private static Item select(int slotNum) {
		return Dungeon.quickslot.getItem(slotNum);
	}

	@Override
	public void onSelect(Item item) {
		if (item != null) {
			Dungeon.quickslot.setSlot(slotNum, item);
			refresh();
		}
	}

	public void item(Item item) {
		slot.item(item);
		enableSlot();
	}

	public void enable(boolean value) {
		active = value;
		if (value) {
			enableSlot();
		} else {
			slot.enable(false);
		}
	}

	private void enableSlot() {
		slot.enable(Dungeon.quickslot.isNonePlaceholder(slotNum));
	}

	private void useTargeting() {

		targeting = lastTarget != null && lastTarget.isAlive()
				&& Dungeon.visible[lastTarget.pos];

		if (targeting) {
			if (Actor.all().contains(lastTarget)) {
				lastTarget.sprite.parent.add(crossM);
				crossM.point(DungeonTilemap.tileToWorld(lastTarget.pos));
				crossB.x = PixelScene.align(x + (width - crossB.width) / 2);
				crossB.y = PixelScene.align(y + (height - crossB.height) / 2);
				crossB.visible = true;
			} else {
				lastTarget = null;
			}
		}
	}



	public static void refresh() {
		for (int i = 0; i < instance.length; i++) {
			if (instance[i] != null) {
				instance[i].item(select(i));
			}
		}
	}

	public static void target(Char target) {
		if (target != Dungeon.hero) {
			lastTarget = target;

			HealthIndicator.instance.target(target);
		}
	}

	public static void cancel() {
		if (targeting) {
			crossB.visible = false;
			crossM.remove();
			targeting = false;
		}
	}
}
