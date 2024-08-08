package com.github.dachhack.sprout.items.potions;

import com.github.dachhack.sprout.Messages.Messages;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class MegaExperiencePotion extends Item {
    {
//        name = "Mega Potion of Experience";
        name = Messages.get(this, "name");
        bones = false;

        image = ItemSpriteSheet.POTION_AQUA;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public static final String AC_DRINK = Messages.get(Potion.class, "ac_drink");

    public void apply(Hero hero) {
        for (int i = 0; i < 52; i++) {
            hero.earnExp(hero.maxExp() - hero.exp);
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_DRINK);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {
        if (action.equals(AC_DRINK)) {
            apply(hero);
        } else {
            super.execute(hero,action);
        }
    }
}
