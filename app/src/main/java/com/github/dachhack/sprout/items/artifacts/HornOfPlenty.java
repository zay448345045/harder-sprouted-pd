package com.github.dachhack.sprout.items.artifacts;

import java.util.ArrayList;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Badges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.buffs.Hunger;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.effects.SpellSprite;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.food.Blandfruit;
import com.github.dachhack.sprout.items.food.Food;
import com.github.dachhack.sprout.items.scrolls.ScrollOfRecharging;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.windows.WndBag;
import com.watabou.noosa.audio.Sample;

/**
 * Created by debenhame on 26/08/2014.
 */
public class HornOfPlenty extends Artifact {

	{
//		name = "Horn of Plenty";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.ARTIFACT_HORN1;

		level = 0;
		levelCap = 30;

		charge = 0;
		partialCharge = 0;
		chargeCap = 10;

		defaultAction = AC_EAT;
	}

	private static final float TIME_TO_EAT = 3f;

	private float energy = 36f;

//	public static final String AC_EAT = "EAT";
//	public static final String AC_STORE = "STORE";
//
//	protected String inventoryTitle = "Select a piece of food";
public static final String AC_EAT = Messages.get(HornOfPlenty.class, "ac_eat");
	public static final String AC_STORE = Messages.get(HornOfPlenty.class, "ac_store");
	protected String inventoryTitle = Messages.get(this, "prompt");

	protected WndBag.Mode mode = WndBag.Mode.FOOD;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge > 0)
			actions.add(AC_EAT);
		if (isEquipped(hero) && level < 30 && !cursed)
			actions.add(AC_STORE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_EAT)) {

			if (!isEquipped(hero))
//				GLog.i("You need to equip your horn to do that.");
				GLog.i(Messages.get(this, "equip"));
			else if (charge == 0)
//				GLog.i("Your horn has no food in it to eat!");
				GLog.i(Messages.get(this, "no_food"));
			else {
				hero.buff(Hunger.class).satisfy(energy * charge);

				// if you get at least 100 food energy from the horn
				if (charge >= 3) {
					switch (hero.heroClass) {
					case WARRIOR:
						if (hero.HP < hero.HT) {
							hero.HP = Math.min(hero.HP + 5, hero.HT);
							hero.sprite.emitter().burst(
									Speck.factory(Speck.HEALING), 1);
						}
						break;
					case MAGE:
						hero.belongings.charge(false);
						ScrollOfRecharging.charge(hero);
						break;
					case ROGUE:
					case HUNTRESS:
						break;
					}

					Statistics.foodEaten++;
				}
				charge = 0;

				hero.sprite.operate(hero.pos);
				hero.busy();
				SpellSprite.show(hero, SpellSprite.FOOD);
				Sample.INSTANCE.play(Assets.SND_EAT);
//				GLog.i("You eat from the horn.");
				GLog.i(Messages.get(this, "eat"));

				hero.spend(TIME_TO_EAT);

				Badges.validateFoodEaten();

				image = ItemSpriteSheet.ARTIFACT_HORN1;

				updateQuickslot();
			}

		} else if (action.equals(AC_STORE)) {

			GameScene.selectItem(itemSelector, mode, inventoryTitle);
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new hornRecharge();
	}

	@Override
	public String desc() {
//		String desc = "This horn can't be blown into, but instead seems to fill up with food over time.\n\n";
		String desc = Messages.get(this, "desc");

		if (charge == 0)
//			desc += "The horn is completely empty.";
			desc += Messages.get(this, "1");
		else if (charge < 3)
//			desc += "The horn is almost empty, a few small fruits and berries sit in the back.";
			desc += Messages.get(this, "2");
		else if (charge < 7)
//			desc += "The horn is partially filled, you can see several fruits & vegetables inside.";

			desc += Messages.get(this, "3");
		else if (charge < 10)
//			desc += "The horn is getting quite full, several pieces of fresh produce are poking up towards the front.";
			desc += Messages.get(this, "4");

		else
//			desc += "The horn is overflowing! A delicious array of fruit and veg is filling the horn up to its brim.";
			desc += Messages.get(this, "5");

		if (isEquipped(Dungeon.hero)) {
			if (!cursed) {
//				desc += "\n\nThe horn rests at your side and is surprisingly lightweight, even with food in it.";
				desc += Messages.get(this, "6");

				if (level < 15)
//					desc += " Perhaps there is a way to increase the horn's power by giving it food energy.";
					desc += Messages.get(this, "desc_hint");
			} else {
//				desc += "\n\nThe cursed horn has bound itself to your side, "
//						+ "it seems to be eager to take food rather than produce it.";
				desc += Messages.get(this, "desc_cursed");
			}
		}

		return desc;
	}

	public class hornRecharge extends ArtifactBuff {

		@Override
		public boolean act() {
			if (charge < chargeCap && !cursed) {

				// generates 0.25 food value every round, +0.015 value per level
				// to a max of 0.70 food value per round (0.25+0.5, at level 30)
				partialCharge += 0.25f + (0.015f * level);

				// charge is in increments of 36 food value.
				if (partialCharge >= 36) {
					charge++;
					partialCharge -= 36;

					if (charge == chargeCap)
						image = ItemSpriteSheet.ARTIFACT_HORN4;
					else if (charge >= 7)
						image = ItemSpriteSheet.ARTIFACT_HORN3;
					else if (charge >= 3)
						image = ItemSpriteSheet.ARTIFACT_HORN2;
					else
						image = ItemSpriteSheet.ARTIFACT_HORN1;

					if (charge == chargeCap) {
//						GLog.p("Your horn is full of food!");
						GLog.p(Messages.get(HornOfPlenty.class, "full"));
						partialCharge = 0;
					}

					updateQuickslot();
				}
			} else
				partialCharge = 0;

			spend(TICK);

			return true;
		}

	}

	protected static WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null && item instanceof Food) {
				if (item instanceof Blandfruit
						&& ((Blandfruit) item).potionAttrib == null) {
//					GLog.w("your horn rejects the unprepared blandfruit.");
					GLog.w(Messages.get(HornOfPlenty.class, "reject"));
				} else {
					Hero hero = Dungeon.hero;
					hero.sprite.operate(hero.pos);
					hero.busy();
					hero.spend(TIME_TO_EAT);

					curItem.upgrade(((Food) item).hornValue);
					if (curItem.level >= 30) {
						curItem.level = 30;
//						GLog.p("your horn has consumed all the food it can!");
						GLog.p(Messages.get(HornOfPlenty.class, "maxlevel"));
					} else
//						GLog.p("the horn consumes your food offering and grows in strength!");
						GLog.p(Messages.get(HornOfPlenty.class, "levelup"));
					item.detach(hero.belongings.backpack);
				}

			}
		}
	};

}
