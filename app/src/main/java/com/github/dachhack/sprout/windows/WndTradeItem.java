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
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.Mob;
import com.github.dachhack.sprout.actors.mobs.npcs.Shopkeeper;
import com.github.dachhack.sprout.items.EquipableItem;
import com.github.dachhack.sprout.items.Gold;
import com.github.dachhack.sprout.items.Heap;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.artifacts.MasterThievesArmband;
import com.github.dachhack.sprout.scenes.PixelScene;
import com.github.dachhack.sprout.sprites.ItemSprite;
import com.github.dachhack.sprout.ui.ItemSlot;
import com.github.dachhack.sprout.ui.RedButton;
import com.github.dachhack.sprout.ui.RenderedTextMultiline;
import com.github.dachhack.sprout.ui.Window;
import com.github.dachhack.sprout.utils.GLog;
import com.github.dachhack.sprout.utils.Utils;
import com.watabou.noosa.BitmapTextMultiline;

public class WndTradeItem extends Window {

	private static final float GAP = 2;
	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 16;

//	private static final String TXT_SALE = "FOR SALE: %s - %dg";
//	private static final String TXT_BUY = "Buy for %dg";
//	private static final String TXT_STEAL = "Steal with %d%% chance";
//	private static final String TXT_SELL = "Sell for %dg";
//	private static final String TXT_SELL_1 = "Sell 1 for %dg";
//	private static final String TXT_SELL_ALL = "Sell all for %dg";
//	private static final String TXT_CANCEL = "Never mind";
//
//	private static final String TXT_SOLD = "You've sold your %s for %dg";
//	private static final String TXT_BOUGHT = "You've bought %s for %dg";
//	private static final String TXT_STOLE = "You've stolen the %s";
private static final String TXT_SALE = Messages.get(WndTradeItem.class, "sale");
	private static final String TXT_BUY = Messages.get(WndTradeItem.class, "buy");
	private static final String TXT_STEAL = Messages.get(WndTradeItem.class, "steal");
	private static final String TXT_SELL = Messages.get(WndTradeItem.class, "sell");
	private static final String TXT_SELL_1 = Messages.get(WndTradeItem.class, "sell1");
	private static final String TXT_SELL_ALL = Messages.get(WndTradeItem.class, "sellall");
	private static final String TXT_CANCEL = Messages.get(WndTradeItem.class, "cancel");

	private static final String TXT_SOLD = Messages.get(WndTradeItem.class, "sold");
	private static final String TXT_BOUGHT = Messages.get(WndTradeItem.class, "bought");
	private static final String TXT_STOLE = Messages.get(WndTradeItem.class, "stole");

	private WndBag owner;

	public WndTradeItem(final Item item, WndBag owner) {

		super();

		this.owner = owner;

		float pos = createDescription(item, false);

		if (item.quantity() == 1) {

			RedButton btnSell = new RedButton(Utils.format(TXT_SELL,
					item.price())) {
				@Override
				protected void onClick() {
					sell(item);
					hide();
				}
			};
			btnSell.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
			add(btnSell);

			pos = btnSell.bottom();

		} else {

			int priceAll = item.price();
			RedButton btnSell1 = new RedButton(Utils.format(TXT_SELL_1,
					priceAll / item.quantity())) {
				@Override
				protected void onClick() {
					sellOne(item);
					hide();
				}
			};
			btnSell1.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
			add(btnSell1);
			RedButton btnSellAll = new RedButton(Utils.format(TXT_SELL_ALL,
					priceAll)) {
				@Override
				protected void onClick() {
					sell(item);
					hide();
				}
			};
			btnSellAll.setRect(0, btnSell1.bottom() + GAP, WIDTH, BTN_HEIGHT);
			add(btnSellAll);

			pos = btnSellAll.bottom();

		}

		RedButton btnCancel = new RedButton(TXT_CANCEL) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
		add(btnCancel);

		resize(WIDTH, (int) btnCancel.bottom());
	}

	public WndTradeItem(final Heap heap, boolean canBuy) {

		super();

		Item item = heap.peek();

		float pos = createDescription(item, true);

		final int price = price(item);

		if (canBuy) {

			RedButton btnBuy = new RedButton(Utils.format(TXT_BUY, price)) {
				@Override
				protected void onClick() {
					hide();
					buy(heap);
				}
			};
			btnBuy.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
			btnBuy.enable(price <= Dungeon.gold);
			add(btnBuy);

			RedButton btnCancel = new RedButton(TXT_CANCEL) {
				@Override
				protected void onClick() {
					hide();
				}
			};

			final MasterThievesArmband.Thievery thievery = Dungeon.hero
					.buff(MasterThievesArmband.Thievery.class);
			if (thievery != null) {
				final float chance = thievery.stealChance(price);
				RedButton btnSteal = new RedButton(Utils.format(TXT_STEAL,
						Math.min(100, (int) (chance * 100)))) {
					@Override
					protected void onClick() {
						if (thievery.steal(price)) {
							Hero hero = Dungeon.hero;
							Item item = heap.pickUp();
							GLog.i(TXT_STOLE, item.name());
							hide();

							if (!item.doPickUp(hero)) {
								Dungeon.level.drop(item, heap.pos).sprite
										.drop();
							}
						} else {
							for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
								if (mob instanceof Shopkeeper) {
									mob.yell(Shopkeeper.TXT_THIEF);
									((Shopkeeper) mob).flee();
									break;
								}
							}
							hide();
						}
					}
				};
				btnSteal.setRect(0, btnBuy.bottom() + GAP, WIDTH, BTN_HEIGHT);
				add(btnSteal);

				btnCancel
						.setRect(0, btnSteal.bottom() + GAP, WIDTH, BTN_HEIGHT);
			} else
				btnCancel.setRect(0, btnBuy.bottom() + GAP, WIDTH, BTN_HEIGHT);

			add(btnCancel);

			resize(WIDTH, (int) btnCancel.bottom());

		} else {

			resize(WIDTH, (int) pos);

		}
	}

	@Override
	public void hide() {

		super.hide();

		if (owner != null) {
			owner.hide();
			Shopkeeper.sell();
		}
	}

	private float createDescription(Item item, boolean forSale) {

		// Title
		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(item.image(), item.glowing()));
		titlebar.label(forSale ? Utils.format(TXT_SALE, item.toString(),
				price(item)) : Utils.capitalize(item.toString()));
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		// Upgraded / degraded
		if (item.levelKnown && item.level > 0) {
			titlebar.color(ItemSlot.UPGRADED);
		} else if (item.levelKnown && item.level < 0) {
			titlebar.color(ItemSlot.DEGRADED);
		}

		// Description
		RenderedTextMultiline info = PixelScene.renderMultiline(item.info(), 6);
		info.maxWidth(WIDTH);
		info.setPos(titlebar.left(), titlebar.bottom() + GAP);
		add(info);

		return info.bottom();
	}

	private void sell(Item item) {

		Hero hero = Dungeon.hero;

		if (item.isEquipped(hero)
				&& !((EquipableItem) item).doUnequip(hero, false)) {
			return;
		}
		item.detachAll(hero.belongings.backpack);

		int price = item.price();

		new Gold(price).doPickUp(hero);
		GLog.i(TXT_SOLD, item.name(), price);
	}

	private void sellOne(Item item) {

		if (item.quantity() <= 1) {
			sell(item);
		} else {

			Hero hero = Dungeon.hero;

			item = item.detach(hero.belongings.backpack);
			int price = item.price();

			new Gold(price).doPickUp(hero);
			GLog.i(TXT_SOLD, item.name(), price);
		}
	}

	private int price(Item item) {
		int price = item.price() * 5 * (Dungeon.depth / 5 + 1);
		return price;
	}

	private void buy(Heap heap) {

		Hero hero = Dungeon.hero;
		Item item = heap.pickUp();

		int price = price(item);
		Dungeon.gold -= price;

		GLog.i(TXT_BOUGHT, item.name(), price);

		if (!item.doPickUp(hero)) {
			Dungeon.level.drop(item, heap.pos).sprite.drop();
		}
	}
}
