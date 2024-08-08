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
package com.github.dachhack.sprout.windows;

import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.actors.mobs.npcs.Wandmaker;
import com.github.dachhack.sprout.items.AdamantWand;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.wands.Wand;
import com.github.dachhack.sprout.scenes.PixelScene;
import com.github.dachhack.sprout.sprites.ItemSprite;
import com.github.dachhack.sprout.ui.RedButton;
import com.github.dachhack.sprout.ui.RenderedTextMultiline;
import com.github.dachhack.sprout.ui.Window;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.noosa.BitmapTextMultiline;

public class WndWandmaker extends Window {

//	private static final String TXT_MESSAGE = "Oh, I see you have succeeded! I do hope it hasn't troubled you too much. "
//			+ "As I promised, you can choose one of my high quality wands.";
//	private static final String TXT_BATTLE = "Battle wand";
//	private static final String TXT_NON_BATTLE = "Non-battle wand";
//
//	private static final String TXT_ADAMANT = "You might find this raw material useful later on. I'm not powerful enough to work with it.";
//	private static final String TXT_WOW = "How did you make it all this way!? I have another reward for you. ";
//	private static final String TXT_FARAWELL = "Good luck in your quest, %s!";
private static final String TXT_MESSAGE = Messages.get(WndWandmaker.class, "msg");
	private static final String TXT_BATTLE = Messages.get(WndWandmaker.class, "battle");
	private static final String TXT_NON_BATTLE = Messages.get(WndWandmaker.class, "nonbattle");

	private static final String TXT_ADAMANT = Messages.get(WndWandmaker.class, "adamant");
	private static final String TXT_WOW = Messages.get(WndWandmaker.class, "wow");
	private static final String TXT_FARAWELL = Messages.get(WndWandmaker.class, "farewell");

	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 20;
	private static final float GAP = 2;

	public WndWandmaker(final Wandmaker wandmaker, final Item item) {

		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(item.image(), null));
		titlebar.label(Utils.capitalize(item.name()));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline message = PixelScene
				.renderMultiline(TXT_MESSAGE, 6);
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add(message);

		RedButton btnBattle = new RedButton(TXT_BATTLE) {
			@Override
			protected void onClick() {
				selectReward(wandmaker, item, Wandmaker.Quest.wand1);
			}
		};
		btnBattle.setRect(0, message.top() + message.height() + GAP, WIDTH,
				BTN_HEIGHT);
		add(btnBattle);

		RedButton btnNonBattle = new RedButton(TXT_NON_BATTLE) {
			@Override
			protected void onClick() {
				selectReward(wandmaker, item, Wandmaker.Quest.wand2);
			}
		};
		btnNonBattle.setRect(0, btnBattle.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add(btnNonBattle);

		resize(WIDTH, (int) btnNonBattle.bottom());
	}

	private void selectReward(Wandmaker wandmaker, Item item, Wand reward) {

		hide();

		item.detach(Dungeon.hero.belongings.backpack);

		reward.identify();
		if (reward.doPickUp(Dungeon.hero)) {
			GLog.i(Messages.get(Hero.class,"have"), reward.name());
		} else {
			Dungeon.level.drop(reward, wandmaker.pos).sprite.drop();
		}

		
		
		wandmaker.yell(Utils.format(TXT_FARAWELL, Dungeon.hero.givenName()));
		
		if(Dungeon.hero.heroClass==HeroClass.MAGE){
		  Dungeon.level.drop(new AdamantWand(), wandmaker.pos).sprite.drop();
		  wandmaker.yell(TXT_ADAMANT);
		}
		
		if(Dungeon.hero.heroClass!=HeroClass.MAGE && Statistics.sewerKills==Statistics.enemiesSlain){
			Dungeon.level.drop(new AdamantWand(), wandmaker.pos).sprite.drop();
			  wandmaker.yell(TXT_WOW);
		}
		
		wandmaker.destroy();

		wandmaker.sprite.die();

		Wandmaker.Quest.complete();
	}
}
