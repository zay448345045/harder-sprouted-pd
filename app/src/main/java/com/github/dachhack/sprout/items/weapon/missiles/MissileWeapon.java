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

import java.util.ArrayList;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.PinCushion;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.rings.RingOfSharpshooting;
import com.github.dachhack.sprout.items.weapon.Weapon;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.windows.WndOptions;
import com.watabou.utils.Random;

public class MissileWeapon extends Weapon {

//	private static final String TXT_MISSILES = "Missile weapon";
//	private static final String TXT_YES = "Yes, I know what I'm doing";
//	private static final String TXT_NO = "No, I changed my mind";
//	private static final String TXT_R_U_SURE = "Do you really want to equip it as a melee weapon?";
private static final String TXT_MISSILES = Messages.get(MissileWeapon.class, "missiles");
	private static final String TXT_YES = Messages.get(MissileWeapon.class, "yes");
	private static final String TXT_NO = Messages.get(MissileWeapon.class, "no");
	private static final String TXT_R_U_SURE = Messages.get(MissileWeapon.class, "sure");

	{
		stackable = true;
		levelKnown = true;
		defaultAction = AC_THROW;
	}

	public boolean upgradeable = false;

	public int min(){
		return scaleFactor() + scalesWith();
	}

	public int max(){
		return scaleFactor() * 2 + scaleFactor() * scalesWith();
	}

	public int scalesWith() {
		if (upgradeable) {
			return level;
		} else {
			return Dungeon.hero.lvl;
		}
	}

	int scaling;

	public int scaleFactor() {
		int tier = scaling;
		if (Dungeon.hero.heroClass == HeroClass.HUNTRESS) {
			tier = (int) (tier*1.25f);
		}
		if (Dungeon.sanchikarahdeath) {
			tier *= 2;
		}
		return tier;
	}

	@Override
	public int damageRoll(Hero hero) {
		MIN = min();
		MAX = max();
		return Random.NormalIntRange(min(), max());
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (hero.heroClass != HeroClass.HUNTRESS
				&& hero.heroClass != HeroClass.ROGUE) {
			actions.remove(AC_EQUIP);
			actions.remove(AC_UNEQUIP);
		}
		return actions;
	}

	@Override
	protected void onThrow(int cell) {
		Char enemy = Actor.findChar(cell);
		if (enemy == null || enemy == curUser) {
			miss(cell);
		} else {
			if (!curUser.shoot(enemy, this)) {
				miss(cell);
			} else if (!(this instanceof Boomerang || this instanceof JupitersWrath)) {
				/*int bonus = 0;

				for (Buff buff : curUser.buffs(RingOfSharpshooting.Aim.class))
					bonus += ((RingOfSharpshooting.Aim) buff).level;

				if (curUser.heroClass == HeroClass.HUNTRESS
						&& enemy.buff(PinCushion.class) == null)
					bonus += 3;*/

				//if (enemy.buff(PinCushion.class) == null || Random.Float() > Math.pow(0.9, bonus))
			}
		}
	}

	protected void miss(int cell) {
		int bonus = 0;
		for (Buff buff : curUser.buffs(RingOfSharpshooting.Aim.class)) {
			bonus += ((RingOfSharpshooting.Aim) buff).level;
		}

		// degraded ring of sharpshooting will even make missed shots break.
		if (Random.Float() < Math.pow(0.6, -bonus))
			super.onThrow(cell);
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {

		super.proc(attacker, defender, damage);

		Hero hero = (Hero) attacker;
		if (hero.rangedWeapon == null && stackable) {
			if (quantity == 1) {
				doUnequip(hero, false, false);
			} else {
				detach(null);
			}
		}
		if ((defender.buff(PinCushion.class) == null || Random.Int(Dungeon.hero.heroClass == HeroClass.HUNTRESS ? 2 : 5) == 0) && !(this instanceof Boomerang | this instanceof JupitersWrath)) {
			Buff.affect(defender, PinCushion.class).stick(this);
		}
	}

	@Override
	public boolean doEquip(final Hero hero) {
		GameScene.show(new WndOptions(TXT_MISSILES, TXT_R_U_SURE, TXT_YES,
				TXT_NO) {
			@Override
			protected void onSelect(int index) {
				if (index == 0) {
					MissileWeapon.super.doEquip(hero);
				}
			}
		});

		return false;
	}

	@Override
	public Item random() {
		return this;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public String info() {

		StringBuilder info = new StringBuilder(desc());

//		info.append("It will deal " + min() + "-" + max() + " damage per hit and require " + STR + " strength to use. ");
		info.append(Messages.get(this, "avgdmg", MIN, MAX));
		if (STR > Dungeon.hero.STR()) {
//			info.append("This weapon is too heavy for you. ");
			info.append(Messages.get(this, "decreased", name));
		}

		if (Dungeon.hero.belongings.backpack.items.contains(this)) {
			if (STR < Dungeon.hero.STR()
					&& Dungeon.hero.heroClass == HeroClass.HUNTRESS) {
//				info.append("\n\nBecause of your excess strength the damage "
//						+ "of your attack with this " + name + " is increased.");
				info.append(Messages.get(this, "increased", name));
			}
		}

//		info.append("\n\nAs this weapon is designed to be used at a distance, it is much less accurate if used at melee range.");
		info.append(Messages.get(this, "distance"));

		if (isEquipped(Dungeon.hero)) {
//			info.append("\n\nYou hold the " + name + " at the ready.");
			info.append(Messages.get(this, "ready", name));
		}

		return info.toString();
	}
}
