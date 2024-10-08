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
package com.github.dachhack.sprout.levels;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.Alchemy;
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.blobs.Fire;
import com.github.dachhack.sprout.actors.blobs.Foliage;
import com.github.dachhack.sprout.actors.blobs.Portal;
import com.github.dachhack.sprout.actors.blobs.WaterOfHealth;
import com.github.dachhack.sprout.actors.blobs.WaterOfTransmutation;
import com.github.dachhack.sprout.actors.blobs.WellWater;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.mobs.Bestiary;
import com.github.dachhack.sprout.actors.mobs.Mob;
import com.github.dachhack.sprout.actors.mobs.SokobanSentinel;
import com.github.dachhack.sprout.actors.mobs.npcs.Blacksmith;
import com.github.dachhack.sprout.actors.mobs.npcs.SheepSokoban;
import com.github.dachhack.sprout.actors.mobs.npcs.SheepSokobanBlack;
import com.github.dachhack.sprout.actors.mobs.npcs.SheepSokobanCorner;
import com.github.dachhack.sprout.actors.mobs.npcs.SheepSokobanSwitch;
import com.github.dachhack.sprout.effects.CellEmitter;
import com.github.dachhack.sprout.effects.Speck;
import com.github.dachhack.sprout.items.Egg;
import com.github.dachhack.sprout.items.Gold;
import com.github.dachhack.sprout.items.Heap;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.ShadowDragonEgg;
import com.github.dachhack.sprout.items.Towel;
import com.github.dachhack.sprout.items.artifacts.TimekeepersHourglass;
import com.github.dachhack.sprout.items.keys.IronKey;
import com.github.dachhack.sprout.items.misc.Spectacles;
import com.github.dachhack.sprout.items.potions.PotionOfLiquidFlame;
import com.github.dachhack.sprout.items.rings.RingOfWealth;
import com.github.dachhack.sprout.items.scrolls.ScrollOfMagicalInfusion;
import com.github.dachhack.sprout.items.scrolls.ScrollOfRegrowth;
import com.github.dachhack.sprout.items.scrolls.ScrollOfUpgrade;
import com.github.dachhack.sprout.items.wands.WandOfFlock.Sheep;
import com.github.dachhack.sprout.levels.features.Chasm;
import com.github.dachhack.sprout.levels.features.Door;
import com.github.dachhack.sprout.levels.features.HighGrass;
import com.github.dachhack.sprout.levels.traps.ActivatePortalTrap;
import com.github.dachhack.sprout.levels.traps.AlarmTrap;
import com.github.dachhack.sprout.levels.traps.ChangeSheepTrap;
import com.github.dachhack.sprout.levels.traps.FireTrap;
import com.github.dachhack.sprout.levels.traps.FleecingTrap;
import com.github.dachhack.sprout.levels.traps.GrippingTrap;
import com.github.dachhack.sprout.levels.traps.HeapGenTrap;
import com.github.dachhack.sprout.levels.traps.LightningTrap;
import com.github.dachhack.sprout.levels.traps.ParalyticTrap;
import com.github.dachhack.sprout.levels.traps.PoisonTrap;
import com.github.dachhack.sprout.levels.traps.SokobanPortalTrap;
import com.github.dachhack.sprout.levels.traps.SummoningTrap;
import com.github.dachhack.sprout.levels.traps.ToxicTrap;
import com.github.dachhack.sprout.plants.Dewcatcher;
import com.github.dachhack.sprout.plants.Flytrap;
import com.github.dachhack.sprout.plants.Phaseshift;
import com.github.dachhack.sprout.plants.Plant;
import com.github.dachhack.sprout.plants.Starflower;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

public class DragonCaveLevel extends Level {


	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
		WIDTH = 48;
		HEIGHT = 48;
		LENGTH = HEIGHT*WIDTH;
		cleared=true;
	}
	
	
	public HashSet<Item> heapstogen;
	public int[] heapgenspots;
	public int[] teleportspots;
	public int[] portswitchspots;
	public int[] teleportassign;
	public int[] destinationspots;
	public int[] destinationassign;
	public int prizeNo;
	
	private static final String HEAPSTOGEN = "heapstogen";
	private static final String HEAPGENSPOTS = "heapgenspots";
	private static final String TELEPORTSPOTS = "teleportspots";
	private static final String PORTSWITCHSPOTS = "portswitchspots";
	private static final String DESTINATIONSPOTS = "destinationspots";
	private static final String TELEPORTASSIGN = "teleportassign";
	private static final String DESTINATIONASSIGN= "destinationassign";
	private static final String PRIZENO = "prizeNo";
	
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HEAPSTOGEN, heapstogen);
		bundle.put(HEAPGENSPOTS, heapgenspots);
		bundle.put(TELEPORTSPOTS, teleportspots);
		bundle.put(PORTSWITCHSPOTS, portswitchspots);
		bundle.put(DESTINATIONSPOTS, destinationspots);
		bundle.put(DESTINATIONASSIGN, destinationassign);
		bundle.put(TELEPORTASSIGN, teleportassign);
		bundle.put(PRIZENO, prizeNo);
	}


	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		
		      super.restoreFromBundle(bundle);
		      
		      heapgenspots = bundle.getIntArray(HEAPGENSPOTS);
		      teleportspots = bundle.getIntArray(TELEPORTSPOTS);
		      portswitchspots = bundle.getIntArray(PORTSWITCHSPOTS);
		      destinationspots = bundle.getIntArray(DESTINATIONSPOTS);
		      destinationassign = bundle.getIntArray(DESTINATIONASSIGN);
		      teleportassign = bundle.getIntArray(TELEPORTASSIGN);
		      prizeNo = bundle.getInt(PRIZENO);
		      
		      heapstogen = new HashSet<Item>();
		      
		      Collection <Bundlable> collectionheap = bundle.getCollection(HEAPSTOGEN);
				for (Bundlable i : collectionheap) {
					Item item = (Item) i;
					if (item != null) {
						heapstogen.add(item);
					}
				}
	}

  @Override
  public void create() {
	   heapstogen = new HashSet<Item>();
	   heapgenspots = new int[10];
	   teleportspots = new int[10];
	   portswitchspots = new int[10];
	   destinationspots = new int[10];
	   destinationassign = new int[10];
	   teleportassign = new int[10];
	   super.create();	
   }	
  
  public void addItemToGen(Item item, int arraypos, int pos) {
		if (item != null) {
			heapstogen.add(item);
			heapgenspots[arraypos]=pos;
		}
	}
  
  
	public Item genPrizeItem() {
		return genPrizeItem(null);
	}
	
	
	public Item genPrizeItem(Class<? extends Item> match) {
		
		boolean keysLeft = false;
		
		if (heapstogen.size() == 0)
			return null;

		for (Item item : heapstogen) {
			if (match.isInstance(item)) {
				heapstogen.remove(item);
				keysLeft=true;
				return item;
			}
		}
		
		if (match == null || !keysLeft) {
			Item item = Random.element(heapstogen);
			heapstogen.remove(item);
			return item;
		}

		return null;
	}
	
	@Override
	public void press(int cell, Char ch) {

		if (pit[cell] && ch == Dungeon.hero) {
			Chasm.heroFall(cell);
			return;
		}

		TimekeepersHourglass.timeFreeze timeFreeze = null;

		if (ch != null)
			timeFreeze = ch.buff(TimekeepersHourglass.timeFreeze.class);

		boolean trap = false;
		boolean interrupt = false;
		
		switch (map[cell]) {

			case Terrain.FLEECING_TRAP:			
			
			if (ch==null){	
			  interrupt=true;	
			}
			
			if (ch != null && ch==Dungeon.hero){
				trap = true;
				FleecingTrap.trigger(cell, ch);
			}
			break;
			
		case Terrain.CHANGE_SHEEP_TRAP:
			
			if (ch instanceof SheepSokoban || ch instanceof SheepSokobanSwitch || ch instanceof SheepSokobanCorner || ch instanceof Sheep){
				trap = true;
				ChangeSheepTrap.trigger(cell, ch);
			}						
			break;
			
		case Terrain.WOOL_RUG:
			
		break;
		
		case Terrain.SOKOBAN_PORT_SWITCH:
			trap=false;
			if (ch!=null){
			   ActivatePortalTrap.trigger(cell, ch);
				
					
				int arraypos = -1; //position in array of teleport switch
				int portpos = -1; //position on map of teleporter
				int portarraypos = -1; //position in array of teleporter
				int destpos = -1; //destination position assigned to switch
				
				for(int i = 0; i < portswitchspots.length; i++) {
				  if(portswitchspots[i] == cell) {
				     arraypos = i;
				     break;
				  }
				}
				
				portpos = teleportassign[arraypos];
				destpos = destinationassign[arraypos];
				
				/* Stepping on switch deactivates the portal */
				destpos = -1;
				
				for(int i = 0; i < teleportspots.length; i++) {
					  if(teleportspots[i] == portpos) {
						     portarraypos = i;
						     break;
						  }
				}
				
				if (map[portpos] == Terrain.PORT_WELL){
					destinationspots[portarraypos]=destpos;	
				}
				
				
			}				
			break;

			
        case Terrain.PORT_WELL:
			
			if (ch != null && ch==Dungeon.hero){

				int portarray=-1;
				int destinationspot=cell;
				
				for(int i = 0; i < teleportspots.length; i++) {
					  if(teleportspots[i] == cell) {
						     portarray = i;
						     break;
						  }
				}
				
				if(portarray != -1) {
					destinationspot=destinationspots[portarray];
					if (destinationspot>0){
					SokobanPortalTrap.trigger(cell, ch, destinationspot);
					}
				}				
			}						
			break;

		case Terrain.HIGH_GRASS:
			HighGrass.trample(this, cell, ch);
			break;

		case Terrain.WELL:
			WellWater.affectCell(cell);
			break;

		case Terrain.ALCHEMY:
			if (ch == null) {
				Alchemy.transmute(cell);
			}
			break;

		case Terrain.DOOR:
			Door.enter(cell);
			break;
		}

		if (trap){

			if (Dungeon.visible[cell])
				Sample.INSTANCE.play(Assets.SND_TRAP);

			if (ch == Dungeon.hero)
				Dungeon.hero.interrupt();

			set(cell, Terrain.INACTIVE_TRAP);
			GameScene.updateMap(cell);					
		} 
		
		if (interrupt){

			Dungeon.hero.interrupt();
			GameScene.updateMap(cell);					
		} 

		Plant plant = plants.get(cell);
		if (plant != null) {
			plant.activate(ch);
		}
	}

	
	
	@Override
	public void mobPress(Mob mob) {

		int cell = mob.pos;

		if (pit[cell] && !mob.flying) {
			Chasm.mobFall(mob);
			return;
		}

		boolean trap = true;
		boolean fleece = false;
		boolean sheep = false;
		switch (map[cell]) {

		case Terrain.TOXIC_TRAP:
			ToxicTrap.trigger(cell, mob);
			break;

		case Terrain.FIRE_TRAP:
			FireTrap.trigger(cell, mob);
			break;

		case Terrain.PARALYTIC_TRAP:
			ParalyticTrap.trigger(cell, mob);
			break;
			
		case Terrain.FLEECING_TRAP:
			if (mob instanceof SheepSokoban || mob instanceof SheepSokobanSwitch || mob instanceof SheepSokobanCorner || mob instanceof SheepSokobanBlack || mob instanceof Sheep){
				fleece=true;
			}
			FleecingTrap.trigger(cell, mob);
			break;
			
		case Terrain.CHANGE_SHEEP_TRAP:
			trap=false;
			if (mob instanceof SheepSokoban || mob instanceof SheepSokobanSwitch || mob instanceof SheepSokobanCorner || mob instanceof Sheep){
				trap=true;
				ChangeSheepTrap.trigger(cell, mob);
			}						
			break;
			
		case Terrain.SOKOBAN_ITEM_REVEAL:
			trap=false;
			if (mob instanceof SheepSokoban || mob instanceof SheepSokobanSwitch || mob instanceof SheepSokobanCorner || mob instanceof SheepSokobanBlack || mob instanceof Sheep){
				HeapGenTrap.trigger(cell, mob);
				drop(genPrizeItem(IronKey.class),heapgenspots[prizeNo]);
				prizeNo++;
				sheep=true;
				trap=true;
			}						
			break;
			
		case Terrain.SOKOBAN_PORT_SWITCH:
			trap=false;
			if (mob instanceof SheepSokoban || mob instanceof SheepSokobanSwitch || mob instanceof SheepSokobanCorner || mob instanceof SheepSokobanBlack || mob instanceof Sheep){
				ActivatePortalTrap.trigger(cell, mob);
				
				/*
				public int[] teleportspots; location of teleports
				public int[] portswitchspots; location of switches
				public int[] teleportassign; assignment of teleports to switches
				public int[] destinationspots; current assignment of destination spots to teleports
				public int[] destinationassign; assignemnt of destination spots to switches
				*/
				
				int arraypos = -1; //position in array of teleport switch
				int portpos = -1; //position on map of teleporter
				int portarray = -1; //position in array of teleporter
				int destpos = -1; //destination position assigned to switch
				
				for(int i = 0; i < portswitchspots.length; i++) {
				  if(portswitchspots[i] == cell) {
				     arraypos = i;
				     break;
				  }
				}
				
				portpos = teleportassign[arraypos];
				destpos = destinationassign[arraypos];
				
				for(int i = 0; i < teleportspots.length; i++) {
					  if(teleportspots[i] == portpos) {
						     portarray = i;
						     break;
						  }
				}
				
				if (map[portpos] == Terrain.PORT_WELL){
					destinationspots[portarray]=destpos;	
				}
				
				sheep=true;
			}						
			break;

		case Terrain.POISON_TRAP:
			PoisonTrap.trigger(cell, mob);
			break;

		case Terrain.ALARM_TRAP:
			AlarmTrap.trigger(cell, mob);
			break;

		case Terrain.LIGHTNING_TRAP:
			LightningTrap.trigger(cell, mob);
			break;

		case Terrain.GRIPPING_TRAP:
			GrippingTrap.trigger(cell, mob);
			break;

		case Terrain.SUMMONING_TRAP:
			SummoningTrap.trigger(cell, mob);
			break;

		case Terrain.DOOR:
			Door.enter(cell);

		default:
			trap = false;
		}

		if (trap && !fleece && !sheep) {
			if (Dungeon.visible[cell]) {
				Sample.INSTANCE.play(Assets.SND_TRAP);
			}
			set(cell, Terrain.INACTIVE_TRAP);
			GameScene.updateMap(cell);
		}
		
		if (trap && fleece) {
			if (Dungeon.visible[cell]) {
				Sample.INSTANCE.play(Assets.SND_TRAP);
			}
			set(cell, Terrain.WOOL_RUG);
			GameScene.updateMap(cell);
		} 	
		
		if (trap && sheep) {
			if (Dungeon.visible[cell]) {
				Sample.INSTANCE.play(Assets.SND_TRAP);
			}
			set(cell, Terrain.EMPTY);
			GameScene.updateMap(cell);
		}
	
		
		Plant plant = plants.get(cell);
		if (plant != null) {
			plant.activate(mob);
		}
		
		Dungeon.observe();
	}
	

	@Override
	public String tileName(int tile) {
		switch (tile) {
		case Terrain.WOOL_RUG:
		return "Wool rug";
	case Terrain.FLEECING_TRAP:
		return "Fleecing trap";
	case Terrain.CHANGE_SHEEP_TRAP:
		return "Change sheep trap";
	case Terrain.SOKOBAN_ITEM_REVEAL:
		return "Item creation switch";
	case Terrain.SOKOBAN_SHEEP:
		return "Floor";
	case Terrain.SWITCH_SOKOBAN_SHEEP:
		return "Floor";
	case Terrain.CORNER_SOKOBAN_SHEEP:
		return "Floor";
	case Terrain.BLACK_SOKOBAN_SHEEP:
		return "Floor";
	case Terrain.SOKOBAN_PORT_SWITCH:
		return "Portal switch";
	case Terrain.PORT_WELL:
		return "Portal";
		case Terrain.WATER:
			return "Dark cold water.";
		default:
			return super.tileName(tile);
		}
	}

	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case Terrain.SOKOBAN_SHEEP:
			return "";
		case Terrain.SWITCH_SOKOBAN_SHEEP:
			return "";
		case Terrain.CORNER_SOKOBAN_SHEEP:
			return "";
		case Terrain.BLACK_SOKOBAN_SHEEP:
			return "";
		case Terrain.FLEECING_TRAP:
			return "Stepping onto a fleecing trap will destroy your armor or eject you from the level.";
		case Terrain.CHANGE_SHEEP_TRAP:
			return "This trap will change the form of any sheep.";
		case Terrain.SOKOBAN_ITEM_REVEAL:
			return "This switch creates an item somewhere on the level.";
		case Terrain.SOKOBAN_PORT_SWITCH:
			return "This switch turns on and off a portal somewhere.";
		case Terrain.PORT_WELL:
			return "This is a portal to another location on this level.";
			case Terrain.WOOL_RUG:
			return "A plush wool rug. Very nice!";
		case Terrain.EMPTY_DECO:
			return "There are old blood stains on the floor.";
		case Terrain.BOOKSHELF:
			return "This is probably a vestige of a prison library. Might it burn?";
		default:
			return super.tileDesc(tile);
		}
	}


	@Override
	public String tilesTex() {
		return Assets.TILES_CAVES;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_CAVES;
	}

	@Override
	protected boolean build() {
		
		map = SokobanLayouts2.DRAGON_CAVE.clone();
		decorate();

		buildFlagMaps();
		cleanWalls();
		createSwitches();
		createSheep();
	
		entrance = 5 + WIDTH * 37;
		exit = 0;


		return true;
	}
	@Override
	protected void decorate() {
		/*
		for (int i = 0; i < getLength(); i++) {
			
		}
		*/
	}

	@Override
	protected void createMobs() {
		/*
		    SokobanSentinel mob = new SokobanSentinel();
			mob.pos = 38 + WIDTH * 21;
			mobs.add(mob);
			Actor.occupyCell(mob);
			
			SokobanSentinel mob2 = new SokobanSentinel();
			mob2.pos = 25 + WIDTH * 36;
			mobs.add(mob2);
			Actor.occupyCell(mob2);		
			*/	
	}
	
	

	protected void createSheep() {
		 for (int i = 0; i < LENGTH; i++) {				
				if (map[i]==Terrain.SOKOBAN_SHEEP){SheepSokoban npc = new SheepSokoban(); mobs.add(npc); npc.pos = i; Actor.occupyCell(npc);}
				else if (map[i]==Terrain.CORNER_SOKOBAN_SHEEP){SheepSokobanCorner npc = new SheepSokobanCorner(); mobs.add(npc); npc.pos = i; Actor.occupyCell(npc);}
				else if (map[i]==Terrain.SWITCH_SOKOBAN_SHEEP){SheepSokobanSwitch npc = new SheepSokobanSwitch(); mobs.add(npc); npc.pos = i; Actor.occupyCell(npc);}
				else if (map[i]==Terrain.BLACK_SOKOBAN_SHEEP){SheepSokobanBlack npc = new SheepSokobanBlack(); mobs.add(npc); npc.pos = i; Actor.occupyCell(npc);}
				else if (map[i]==Terrain.PORT_WELL){
				
					/*
					Portal portal = new Portal();
				portal.seed(i, 1);
				blobs.put(Portal.class, portal);
				*/
				}
				
			}
	}
	
	
	protected void createSwitches(){
		
		//spots where your portals are	
		
		
		//spots where your portal switches are	
		
			
		//assign each switch to a portal	
		
			
		//assign each switch to a destination spot	
		
			
		//set the original destination of portals	
				
	}
	

	@Override
	protected void createItems() {
		int goldmin=1; int goldmax=100;		
		if (first){
			goldmin=400; goldmax=800;
		}
		 for (int i = 0; i < LENGTH; i++) {				
				if (map[i]==Terrain.SOKOBAN_HEAP){
					if (first && Random.Int(5)==0){drop(new ScrollOfUpgrade(), i).type = Heap.Type.SKELETON;}
					else {drop(new Gold(Random.Int(goldmin, goldmax)), i).type = Heap.Type.SKELETON;}
				}
			}	
		 if (first){	
		   drop(new ShadowDragonEgg(), 43 + WIDTH * 35);
		 }
	}

	@Override
	public int randomRespawnCell() {
		return -1;
	}
	
	

}
