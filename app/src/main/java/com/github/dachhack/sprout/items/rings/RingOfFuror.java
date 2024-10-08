package com.github.dachhack.sprout.items.rings;

import com.github.dachhack.sprout.Messages.Messages;

/**
 * Created by debenhame on 10/09/2014.
 */
public class RingOfFuror extends Ring {

	{
//		name = "Ring of Furor";
		name = Messages.get(this, "name");
	}

	@Override
	protected RingBuff buff() {
		return new Furor();
	}

	@Override
//	public String desc() {
//		return isKnown() ? "This ring grants the wearer an inner fury, allowing them to attack more rapidly. "
//				+ "This fury works best in large bursts, so slow weapons benefit far more than fast ones. "
//				+ "A degraded ring will instead slow the wearer's speed of attack."
//				: super.desc();
//	}
	public String desc() {
		return isKnown() ? Messages.get(this, "desc") : super.desc();
	}

	public class Furor extends RingBuff {
	}
}
