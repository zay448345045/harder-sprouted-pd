package com.github.dachhack.sprout.items.artifacts;

import java.util.ArrayList;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.ResultDescriptions;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.effects.particles.ShadowParticle;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.plants.Earthroot;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.github.dachhack.sprout.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

/**
 * Created by debenhame on 27/08/2014.
 */
public class ChaliceOfBlood extends Artifact {

//	private static final String TXT_CHALICE = "Chalice of Blood";
//	private static final String TXT_YES = "Yes, I know what I'm doing";
//	private static final String TXT_NO = "No, I changed my mind";
//	private static final String TXT_PRICK = "Each time you use the chalice it will drain more life energy, "
//			+ "if you are not careful this draining effect can easily kill you.\n\n"
//			+ "Are you sure you want to offer it more life energy?";
private static final String TXT_CHALICE = Messages.get(ChaliceOfBlood.class, "name");
	private static final String TXT_YES = Messages.get(ChaliceOfBlood.class, "yes");
	private static final String TXT_NO = Messages.get(ChaliceOfBlood.class, "no");
	private static final String TXT_PRICK = Messages.get(ChaliceOfBlood.class, "prick_warn");

	{
//		name = "Chalice of Blood";
		name = Messages.get(this, "name");
		image = ItemSpriteSheet.ARTIFACT_CHALICE1;

		level = 0;
		levelCap = 10;
	}

//	public static final String AC_PRICK = "PRICK";
public static final String AC_PRICK = Messages.get(ChaliceOfBlood.class, "ac_prick");

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && level < levelCap && !cursed)
			actions.add(AC_PRICK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_PRICK)) {

			int damage = 3 * (level * level);

			if (damage > hero.HP * 0.75) {

				GameScene.show(new WndOptions(TXT_CHALICE, TXT_PRICK, TXT_YES,
						TXT_NO) {
					@Override
					protected void onSelect(int index) {
						if (index == 0)
							prick(Dungeon.hero);
					};
				});

			} else {
				prick(hero);
			}
		}
	}

	private void prick(Hero hero) {
		int damage = 3 * (level * level);

		Earthroot.Armor armor = hero.buff(Earthroot.Armor.class);
		if (armor != null) {
			damage = armor.absorb(damage);
		}

		damage -= Random.IntRange(0, hero.dr());

		hero.sprite.operate(hero.pos);
		hero.busy();
		hero.spend(3f);
//		if (damage <= 0) {
//			GLog.i("You prick yourself, and your blood drips into the chalice.");
//		} else if (damage < 25) {
//			GLog.w("You prick yourself and the chalice feeds on you.");
//			Sample.INSTANCE.play(Assets.SND_CURSED);
//			hero.sprite.emitter().burst(ShadowParticle.CURSE, 6);
//		} else if (damage < 100) {
//			GLog.w("Your life essence drains into the chalice.");
//			Sample.INSTANCE.play(Assets.SND_CURSED);
//			hero.sprite.emitter().burst(ShadowParticle.CURSE, 12);
//		} else {
//			GLog.w("The chalice devours your life energy.");
//			Sample.INSTANCE.play(Assets.SND_CURSED);
//			hero.sprite.emitter().burst(ShadowParticle.CURSE, 18);
//		}
		if (damage <= 0) {
			GLog.i(Messages.get(this, "onprick2"));
		} else if (damage < 25) {
			GLog.w(Messages.get(this, "onprick3"));
			Sample.INSTANCE.play(Assets.SND_CURSED);
			hero.sprite.emitter().burst(ShadowParticle.CURSE, 6);
		} else if (damage < 100) {
			GLog.w(Messages.get(this, "onprick"));
			Sample.INSTANCE.play(Assets.SND_CURSED);
			hero.sprite.emitter().burst(ShadowParticle.CURSE, 12);
		} else {
			GLog.w(Messages.get(this, "onprick4"));
			Sample.INSTANCE.play(Assets.SND_CURSED);
			hero.sprite.emitter().burst(ShadowParticle.CURSE, 18);
		}

		if (damage > 0)
			hero.damage(damage, this);

//		if (!hero.isAlive()) {
//			Dungeon.fail(Utils.format(ResultDescriptions.ITEM, name));
//			GLog.n("The Chalice sucks your life essence dry...");
//		} else {
//			upgrade();
//		}
		if (!hero.isAlive()) {
			Dungeon.fail(Utils.format(ResultDescriptions.ITEM, name));
			GLog.n(Messages.get(this, "ondeath"));
		} else {
			upgrade();
		}
	}

	@Override
	public Item upgrade() {
		if (level >= 6)
			image = ItemSpriteSheet.ARTIFACT_CHALICE3;
		else if (level >= 2)
			image = ItemSpriteSheet.ARTIFACT_CHALICE2;
		return super.upgrade();
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new chaliceRegen();
	}

	@Override
	public String desc() {
//		String desc = "This shining silver chalice is oddly adorned with sharp gems at the rim. ";
//		if (level < levelCap)
//			desc += "The chalice is pulling your attention strangely, you feel like it wants something from you.";
//		else
//			desc += "The chalice is full and radiating energy.";
//
//		if (isEquipped(Dungeon.hero)) {
//			desc += "\n\n";
//			if (cursed)
//				desc += "The cursed chalice has bound itself to your hand, and is slowly tugging at your life energy.";
//			else if (level == 0)
//				desc += "As you hold the chalice, you feel oddly compelled to prick yourself on the sharp gems.";
//			else if (level < 3)
//				desc += "Some of your blood is pooled into the chalice, you can subtly feel the chalice feeding life "
//						+ "energy into you. You still want to cut yourself on the chalice, even though you know it will hurt.";
//			else if (level < 7)
//				desc += "The chalice is about half full of your blood and you can feel it feeding life energy "
//						+ "into you. you still want to hurt yourself, the chalice needs your energy, it's your friend.";
//			else if (level < levelCap)
//				desc += "The chalice is getting pretty full, and the life force it's feeding you is stronger than "
//						+ "ever. You should give it more energy, you need too, your friend needs your energy, it needs "
//						+ "your help. Your friend knows you have limits though, it doesn't want you to die, just bleed.";
//			else
//				desc += "The chalice is filled to the brim with your life essence. You can feel the chalice pouring "
//						+ "life energy back into you. It's your best friend. It's happy with you. So happy. "
//						+ "You've done well. So well. You're being rewarded. You don't need to touch the sharp gems anymore.";
//		}
		String desc = Messages.get(this, "desc");
		if (level < levelCap)
			desc += Messages.get(this, "desc_5");
		else
			desc += Messages.get(this, "desc_4");

		if (isEquipped(Dungeon.hero)) {
			desc += "\n\n";
			if (cursed)
				desc += Messages.get(this, "desc_cursed");
			else if (level == 0)
				desc += Messages.get(this, "desc_1");
			else if (level < levelCap)
				desc += Messages.get(this, "desc_2");
			else
				desc += Messages.get(this, "desc_3");
		}
		return desc;
	}

	public class chaliceRegen extends ArtifactBuff {

	}

}
