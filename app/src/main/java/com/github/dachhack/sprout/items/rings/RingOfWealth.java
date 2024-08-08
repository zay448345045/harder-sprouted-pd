package com.github.dachhack.sprout.items.rings;

import com.github.dachhack.sprout.Messages.Messages;

/**
 * Created by debenhame on 10/09/2014.
 */
public class RingOfWealth extends Ring {
	// TODO: monitor this one as it goes, super hard to balance so you'll need
	// some feedback.
	{
//		name = "Ring of Wealth";
		name = Messages.get(this, "name");
	}

	@Override
	protected RingBuff buff() {
		return new Wealth();
	}

	@Override
	public String desc() {
//		return isKnown() ? "It's not clear what this ring does exactly, good luck may influence "
//				+ "the life an an adventurer in many subtle ways. "
//				+ "Naturally a degraded ring would give bad luck."
//				: super.desc();
		return isKnown() ? Messages.get(this, "desc")
				: super.desc();

	}

	public class Wealth extends RingBuff {
	}
}
