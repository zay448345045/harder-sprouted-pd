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
package com.github.dachhack.sprout.items.armor.glyphs;

import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.ResultDescriptions;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.items.armor.Armor;
import com.github.dachhack.sprout.items.armor.Armor.Glyph;
import com.github.dachhack.sprout.sprites.CharSprite;
import com.github.dachhack.sprout.sprites.ItemSprite;
import com.github.dachhack.sprout.sprites.ItemSprite.Glowing;
import com.github.dachhack.sprout.ui.BuffIndicator;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Viscosity extends Glyph {

//	private static final String TXT_VISCOSITY = "%s of viscosity";
private static final String TXT_VISCOSITY = Messages.get(Viscosity.class, "name");

	private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing(0x8844CC);

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		if (damage == 0) {
			return 0;
		}

		int level = Math.max(0, armor.level);

		if (Random.Int(level + 7) >= 6) {

			DeferedDamage debuff = defender.buff(DeferedDamage.class);
			if (debuff == null) {
				debuff = new DeferedDamage();
				debuff.attachTo(defender);
			}
			debuff.prolong(damage);

			defender.sprite.showStatus(CharSprite.WARNING, Messages.get(this, "deferred"),
					damage);

			return 0;

		} else {
			return damage;
		}
	}

	@Override
	public String name(String weaponName) {
		return String.format(TXT_VISCOSITY, weaponName);
	}

	@Override
	public Glowing glowing() {
		return PURPLE;
	}

	public static class DeferedDamage extends Buff {

		protected int damage = 0;

		private static final String DAMAGE = "damage";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(DAMAGE, damage);

		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			damage = bundle.getInt(DAMAGE);
		}

		@Override
		public boolean attachTo(Char target) {
			if (super.attachTo(target)) {
				postpone(TICK);
				return true;
			} else {
				return false;
			}
		}

		public void prolong(int damage) {
			this.damage += damage;
		};

		@Override
		public int icon() {
			return BuffIndicator.DEFERRED;
		}

		@Override
		public String toString() {
			return Utils.format(Messages.get(Viscosity.class, "bname"), damage);
		}

		@Override
		public boolean act() {
			if (target.isAlive()) {

				target.damage(1, this);
				if (target == Dungeon.hero && !target.isAlive()) {

					Glyph glyph = new Viscosity();
					Dungeon.fail(Utils.format(ResultDescriptions.GLYPH,
							glyph.name()));
//					GLog.n("%s killed you...", glyph.name());
					GLog.n(Messages.get(Viscosity.class, "bkill"), glyph.name());

					Badges.validateDeathFromGlyph();
				}
				spend(TICK);

				if (--damage <= 0) {
					detach();
				}

			} else {

				detach();

			}

			return true;
		}
	}
}
