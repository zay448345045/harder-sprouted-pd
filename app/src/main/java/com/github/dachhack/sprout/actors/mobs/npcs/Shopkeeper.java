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
package com.github.dachhack.sprout.actors.mobs.npcs;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.particles.ElmoParticle;
import com.github.dachhack.sprout.items.Heap;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ShopkeeperSprite;
import com.github.dachhack.sprout.windows.WndBag;
import com.github.dachhack.sprout.windows.WndTradeItem;

public class Shopkeeper extends NPC {

	public static final String TXT_THIEF = "Thief, Thief!";

	{
		name = Messages.get(this, "name");
		spriteClass = ShopkeeperSprite.class;
	}

	@Override
	protected boolean act() {

		throwItem();

		sprite.turnTo(pos, Dungeon.hero.pos);
		spend(TICK);
		return true;
	}

	@Override
	public void damage(int dmg, Object src) {
		flee();
	}

	@Override
	public void add(Buff buff) {
		flee();
	}

	public void flee() {
		for (Heap heap : Dungeon.level.heaps.values()) {
			if (heap.type == Heap.Type.FOR_SALE) {
				CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
				heap.destroy();
			}
		}

		destroy();

		sprite.killAndErase();
		CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public String description() {
		return "This stout guy looks more appropriate for a trade district in some large city "
				+ "than for a dungeon. His prices explain why he prefers to do business here.";
	}

	public static WndBag sell() {
		return GameScene.selectItem(itemSelector, WndBag.Mode.FOR_SALE,
				"Select an item to sell");
	}

	private static WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {
				WndBag parentWnd = sell();
				GameScene.show(new WndTradeItem(item, parentWnd));
			}
		}
	};

	@Override
	public void interact() {
		sell();
	}
}
