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
package com.github.dachhack.sprout.items.armor;

import java.util.HashMap;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.actors.mobs.Mob;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.weapon.missiles.Shuriken;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.sprites.MissileSprite;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.utils.Callback;

public class HuntressArmor extends ClassArmor {

//	private static final String TXT_NO_ENEMIES = "No enemies in sight";
//	private static final String TXT_NOT_HUNTRESS = "Only huntresses can use this armor!";
//
//	private static final String AC_SPECIAL = "SPECTRAL BLADES";
private static final String TXT_NO_ENEMIES = Messages.get(HuntressArmor.class, "no_enemies");
	private static final String TXT_NOT_HUNTRESS = Messages.get(HuntressArmor.class, "not_huntress");

	private static final String AC_SPECIAL = Messages.get(HuntressArmor.class, "ac_special");

	{
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.ARMOR_HUNTRESS;
	}

	private HashMap<Callback, Mob> targets = new HashMap<Callback, Mob>();

	@Override
	public String special() {
		return AC_SPECIAL;
	}

	@Override
	public void doSpecial() {

		Item proto = new Shuriken();

		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Level.fieldOfView[mob.pos]) {

				Callback callback = new Callback() {
					@Override
					public void call() {
						curUser.attack(targets.get(this));
						targets.remove(this);
						if (targets.isEmpty()) {
							curUser.spendAndNext(curUser.attackDelay());
						}
					}
				};

				((MissileSprite) curUser.sprite.parent
						.recycle(MissileSprite.class)).reset(curUser.pos,
						mob.pos, proto, callback);

				targets.put(callback, mob);
			}
		}

		if (targets.size() == 0) {
			GLog.w(TXT_NO_ENEMIES);
			return;
		}

		curUser.HP -= (curUser.HP / 3);

		curUser.sprite.zap(curUser.pos);
		curUser.busy();
	}

	@Override
	public boolean doEquip(Hero hero) {
		if (hero.heroClass == HeroClass.HUNTRESS) {
			return super.doEquip(hero);
		} else {
			GLog.w(TXT_NOT_HUNTRESS);
			return false;
		}
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
}