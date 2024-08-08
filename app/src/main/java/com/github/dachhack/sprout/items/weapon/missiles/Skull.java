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
package com.github.dachhack.sprout.items.weapon.missiles;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.effects.particles.ShadowParticle;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.weapon.enchantments.Death;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Skull extends MissileWeapon {

	{
//		name = "skull";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.SKULLWEP;

		MIN = 1;
		MAX = 4;

		scaling = 1;

		bones = false; // Finding them in bones would be semi-frequent and
						// disappointing.
	}

	public Skull() {
		this(1);
	}

	public Skull(int number) {
		super();
		quantity = number;
	}

	@Override
//	public String desc() {
//		return "A evil skull thingy";
//	}
	public String desc() {
		return Messages.get(this, "desc");
	}


	@Override
	public Item random() {
		quantity = Random.Int(5, 15);
		return this;
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		super.proc(attacker, defender, damage);
		int level = Math.max(0, this.level);

		if (Random.Int(level + 100) >= 92) {

			defender.damage(defender.HP, new Death());
			defender.sprite.emitter().burst(ShadowParticle.UP, 5);

			if (!defender.isAlive() && attacker instanceof Hero) {
				Badges.validateGrimWeapon();
			}

		}
	}

	@Override
	public int price() {
		return quantity * 2;
	}
}
