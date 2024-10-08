package com.github.dachhack.sprout.plants;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroSubClass;
import com.github.dachhack.sprout.items.food.Blandfruit;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/**
 * Created by Evan on 13/08/2014.
 */
public class BlandfruitBush extends Plant {

	private static final String TXT_DESC = "Distant cousin of the Rotberry, the pear-shaped produce of the Blandfruit bush tastes like caked dust. "
			+ "The fruit is gross and unsubstantial but isn't poisonous. perhaps it could be cooked.";

	{
		image = 8;
		plantName = "Blandfruit";
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);

		if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN && Random.Int(2) == 0) {
			Dewcatcher.explodeDew(ch.pos);
		}

		Dungeon.level.drop(new Blandfruit(), pos).sprite.drop();
	}

	@Override
	public String desc() {
		return TXT_DESC;
	}

	public static class Seed extends Plant.Seed {
		{
			plantName = "Blandfruit";

			name = "seed of " + plantName;
			image = ItemSpriteSheet.SEED_BLANDFRUIT;

			plantClass = BlandfruitBush.class;
			alchemyClass = null;
		}

		@Override
		public String desc() {
			return TXT_DESC;
		}
	}
}
