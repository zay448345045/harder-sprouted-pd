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
package com.github.dachhack.sprout.items.weapon.melee.relic;

import java.util.ArrayList;
import java.util.HashSet;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.ToxicGas;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.Burning;
import com.github.dachhack.sprout.actors.buffs.GasesImmunity;
import com.github.dachhack.sprout.actors.buffs.MagicImmunity;
import com.github.dachhack.sprout.actors.buffs.Poison;
import com.github.dachhack.sprout.actors.buffs.Slow;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.Eye;
import com.github.dachhack.sprout.actors.mobs.Warlock;
import com.github.dachhack.sprout.actors.mobs.Yog;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.artifacts.Artifact.ArtifactBuff;
import com.github.dachhack.sprout.items.artifacts.CloakOfShadows.cloakStealth;
import com.github.dachhack.sprout.items.rings.Ring.RingBuff;
import com.github.dachhack.sprout.items.weapon.Weapon;
import com.github.dachhack.sprout.levels.Level;
import com.github.dachhack.sprout.levels.Terrain;
import com.github.dachhack.sprout.levels.traps.LightningTrap;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.ui.BuffIndicator;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class CromCruachAxe extends RelicMeleeWeapon {

	public CromCruachAxe() {
		super(6, 1f, 1f);
		// TODO Auto-generated constructor stub
	}

	
	{
//		name = "Crom Cruach Axe";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.CROMAXE;

		level = 0;
		exp = 0;
		levelCap = 15;

		charge = 0;
		chargeCap = 1000;

		cooldown = 0;
		bones = false;

		defaultAction = AC_DISPEL;
		
  }
		
//	public static final String AC_DISPEL = "DISPEL";
public static final String AC_DISPEL = Messages.get(CromCruachAxe.class, "ac_dispel");


	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge >= chargeCap)
			actions.add(AC_DISPEL);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_DISPEL)) {
//			GLog.w("Negating energy surrounds you!");
			GLog.p(Messages.get(this, "ready"));
			charge = 0;
			Buff.prolong(hero, MagicImmunity.class, 10f + (level/5f));
		} else
			super.execute(hero, action);
	}

	
	public class DispelCounter extends WeaponBuff {

		@Override
		public boolean act() {
			if (charge < chargeCap) {
				charge+=1;
				if (charge >= chargeCap) {
//					GLog.w("Your axe pulsates with negating power.");
					GLog.p(Messages.get(CromCruachAxe.class, "buffdesc"));
				}
				updateQuickslot();
			}
			spend(TICK);
			return true;
		}
		
		@Override
//		public String toString() {
//			return "Dispel";
//		}
		public String toString() {
			return Messages.get(CromCruachAxe.class, "buffname");
		}

		@Override
		public int icon() {
			if (cooldown == 0)
				return BuffIndicator.NONE;
			else
				return BuffIndicator.NONE;
		}

		@Override
		public void detach() {
			cooldown = 0;
			charge = 0;
			super.detach();
		}

	}
	
	
	
	
	@Override
	protected WeaponBuff passiveBuff() {
		return new DispelCounter();
	}
	
}


