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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.github.dachhack.sprout.Assets;
import com.github.dachhack.sprout.Challenges;
import com.github.dachhack.sprout.Dungeon;
import com.github.dachhack.sprout.Statistics;
import com.github.dachhack.sprout.actors.Actor;
import com.github.dachhack.sprout.actors.Char;
import com.github.dachhack.sprout.actors.blobs.Alchemy;
import com.github.dachhack.sprout.actors.blobs.Blob;
import com.github.dachhack.sprout.actors.blobs.WellWater;
import com.github.dachhack.sprout.actors.buffs.Awareness;
import com.github.dachhack.sprout.actors.buffs.Blindness;
import com.github.dachhack.sprout.actors.buffs.Buff;
import com.github.dachhack.sprout.actors.buffs.MindVision;
import com.github.dachhack.sprout.actors.buffs.Shadows;
import com.github.dachhack.sprout.actors.hero.Hero;
import com.github.dachhack.sprout.actors.hero.HeroClass;
import com.github.dachhack.sprout.actors.mobs.Bestiary;
import com.github.dachhack.sprout.actors.mobs.Mob;
import com.github.dachhack.sprout.actors.mobs.npcs.SheepSokoban;
import com.github.dachhack.sprout.actors.mobs.npcs.SheepSokobanCorner;
import com.github.dachhack.sprout.actors.mobs.npcs.SheepSokobanSwitch;
import com.github.dachhack.sprout.actors.mobs.pets.BlueDragon;
import com.github.dachhack.sprout.actors.mobs.pets.Bunny;
import com.github.dachhack.sprout.actors.mobs.pets.Fairy;
import com.github.dachhack.sprout.actors.mobs.pets.GreenDragon;
import com.github.dachhack.sprout.actors.mobs.pets.PET;
import com.github.dachhack.sprout.actors.mobs.pets.RedDragon;
import com.github.dachhack.sprout.actors.mobs.pets.Scorpion;
import com.github.dachhack.sprout.actors.mobs.pets.ShadowDragon;
import com.github.dachhack.sprout.actors.mobs.pets.Spider;
import com.github.dachhack.sprout.actors.mobs.pets.SugarplumFairy;
import com.github.dachhack.sprout.actors.mobs.pets.Velocirooster;
import com.github.dachhack.sprout.actors.mobs.pets.VioletDragon;
import com.github.dachhack.sprout.actors.mobs.pets.bee;
import com.github.dachhack.sprout.effects.Pushing;
import com.github.dachhack.sprout.effects.particles.FlowParticle;
import com.github.dachhack.sprout.effects.particles.WindParticle;
import com.github.dachhack.sprout.items.Dewdrop;
import com.github.dachhack.sprout.items.Generator;
import com.github.dachhack.sprout.items.Heap;
import com.github.dachhack.sprout.items.Item;
import com.github.dachhack.sprout.items.Stylus;
import com.github.dachhack.sprout.items.Torch;
import com.github.dachhack.sprout.items.StoneOre;
import com.github.dachhack.sprout.items.armor.Armor;
import com.github.dachhack.sprout.items.artifacts.AlchemistsToolkit;
import com.github.dachhack.sprout.items.artifacts.DriedRose;
import com.github.dachhack.sprout.items.artifacts.TimekeepersHourglass;
import com.github.dachhack.sprout.items.bags.ScrollHolder;
import com.github.dachhack.sprout.items.bags.SeedPouch;
import com.github.dachhack.sprout.items.food.Blandfruit;
import com.github.dachhack.sprout.items.food.Food;
import com.github.dachhack.sprout.items.potions.PotionOfHealing;
import com.github.dachhack.sprout.items.potions.PotionOfMight;
import com.github.dachhack.sprout.items.potions.PotionOfStrength;
import com.github.dachhack.sprout.items.rings.RingOfWealth;
import com.github.dachhack.sprout.items.scrolls.Scroll;
import com.github.dachhack.sprout.items.scrolls.ScrollOfMagicalInfusion;
import com.github.dachhack.sprout.items.scrolls.ScrollOfUpgrade;
import com.github.dachhack.sprout.levels.features.Chasm;
import com.github.dachhack.sprout.levels.features.Door;
import com.github.dachhack.sprout.levels.features.HighGrass;
import com.github.dachhack.sprout.levels.painters.Painter;
import com.github.dachhack.sprout.levels.traps.AlarmTrap;
import com.github.dachhack.sprout.levels.traps.ChangeSheepTrap;
import com.github.dachhack.sprout.levels.traps.FireTrap;
import com.github.dachhack.sprout.levels.traps.FleecingTrap;
import com.github.dachhack.sprout.levels.traps.GrippingTrap;
import com.github.dachhack.sprout.levels.traps.HeapGenTrap;
import com.github.dachhack.sprout.levels.traps.LightningTrap;
import com.github.dachhack.sprout.levels.traps.ParalyticTrap;
import com.github.dachhack.sprout.levels.traps.PoisonTrap;
import com.github.dachhack.sprout.levels.traps.SummoningTrap;
import com.github.dachhack.sprout.levels.traps.ToxicTrap;
import com.github.dachhack.sprout.mechanics.ShadowCaster;
import com.github.dachhack.sprout.plants.BlandfruitBush;
import com.github.dachhack.sprout.plants.Plant;
import com.github.dachhack.sprout.scenes.GameScene;
import com.github.dachhack.sprout.utils.GLog;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

public abstract class Level implements Bundlable {

	public static enum Feeling {
		NONE, CHASM, WATER, GRASS, DARK
	};

	
	
	/*  -W-1 -W  -W+1
	 *  -1    P  +1
	 *  W-1   W  W+1
	 * 
	 */
	
	public static int WIDTH = 48;
	public static int HEIGHT = 48;
	public static int LENGTH = WIDTH * HEIGHT;

	public static final int[] NEIGHBOURS4 = { -getWidth(), +1, +getWidth(), -1 };
	public static final int[] NEIGHBOURS8 = { +1, -1, +getWidth(), -getWidth(),
			+1 + getWidth(), +1 - getWidth(), -1 + getWidth(), -1 - getWidth() };
	public static final int[] NEIGHBOURS9 = { 0, +1, -1, +getWidth(), -getWidth(),
			+1 + getWidth(), +1 - getWidth(), -1 + getWidth(), -1 - getWidth() };

	// Note that use of these without checking values is unsafe, mobs can be
	// within 2 tiles of the
	// edge of the map, unsafe use in that case will cause an array out of
	// bounds exception.
	public static final int[] NEIGHBOURS8DIST2 = { +2 + 2 * getWidth(),
			+1 + 2 * getWidth(), 2 * getWidth(), -1 + 2 * getWidth(), -2 + 2 * getWidth(),
			+2 + getWidth(), +1 + getWidth(), +getWidth(), -1 + getWidth(), -2 + getWidth(), +2, +1, -1,
			-2, +2 - getWidth(), +1 - getWidth(), -getWidth(), -1 - getWidth(), -2 - getWidth(),
			+2 - 2 * getWidth(), +1 - 2 * getWidth(), -2 * getWidth(), -1 - 2 * getWidth(),
			-2 - 2 * getWidth() };
	public static final int[] NEIGHBOURS9DIST2 = { +2 + 2 * getWidth(),
			+1 + 2 * getWidth(), 2 * getWidth(), -1 + 2 * getWidth(), -2 + 2 * getWidth(),
			+2 + getWidth(), +1 + getWidth(), +getWidth(), -1 + getWidth(), -2 + getWidth(), +2, +1, 0,
			-1, -2, +2 - getWidth(), +1 - getWidth(), -getWidth(), -1 - getWidth(), -2 - getWidth(),
			+2 - 2 * getWidth(), +1 - 2 * getWidth(), -2 * getWidth(), -1 - 2 * getWidth(),
			-2 - 2 * getWidth() };

	protected static final float TIME_TO_RESPAWN = 50;
	protected static final int REGROW_TIMER = 10;
	protected static final int DROP_TIMER = 10;
	protected static final int PET_TICK = 1;
		
	private static final String TXT_HIDDEN_PLATE_CLICKS = "A hidden pressure plate clicks!";

	public static boolean resizingNeeded;
	public static boolean first;
	public static int loadedMapSize;
	
	public int[] map;
	public boolean[] visited;
	public boolean[] mapped;
	
	public int movepar=0;
	public int currentmoves=0;
	public boolean genpetnext = false;

	public int viewDistance = Dungeon.isChallenged(Challenges.DARKNESS) ? 3 : 8;

	public static boolean[] fieldOfView = new boolean[getLength()];

	public static boolean[] passable = new boolean[getLength()];
	public static boolean[] losBlocking = new boolean[getLength()];
	public static boolean[] flamable = new boolean[getLength()];
	public static boolean[] secret = new boolean[getLength()];
	public static boolean[] solid = new boolean[getLength()];
	public static boolean[] avoid = new boolean[getLength()];
	public static boolean[] water = new boolean[getLength()];
	public static boolean[] pit = new boolean[getLength()];

	public static boolean[] discoverable = new boolean[getLength()];
	
	public Feeling feeling = Feeling.NONE;


	public int entrance;
	public int exit;
	public int pitSign;

	// when a boss level has become locked.
	public boolean locked = false;
	public boolean special = false;
	public boolean cleared = false;
	public boolean forcedone = false;
	public boolean sealedlevel = false;

	public HashSet<Mob> mobs;
	public SparseArray<Heap> heaps;
	public HashMap<Class<? extends Blob>, Blob> blobs;
	public SparseArray<Plant> plants;

	protected ArrayList<Item> itemsToSpawn = new ArrayList<Item>();

	public int color1 = 0x004400;
	public int color2 = 0x88CC44;

	protected static boolean pitRoomNeeded = false;
	protected static boolean weakFloorCreated = false;
	public boolean reset = false;

	private static final String MAP = "map";
	private static final String VISITED = "visited";
	private static final String MAPPED = "mapped";
	private static final String ENTRANCE = "entrance";
	private static final String EXIT = "exit";
	private static final String LOCKED = "locked";
	private static final String HEAPS = "heaps";
	private static final String PLANTS = "plants";
	private static final String MOBS = "mobs";
	private static final String BLOBS = "blobs";
	private static final String FEELING = "feeling";
	private static final String PITSIGN = "pitSign";
	private static final String MOVES = "currentmoves";
	private static final String CLEARED = "cleared";
	private static final String RESET = "reset";
	private static final String FORCEDONE = "forcedone";
	private static final String GENPETNEXT = "genpetnext";
	private static final String SEALEDLEVEL = "sealedlevel";

	
	public void create() {

		resizingNeeded = false;		
		
		map = new int[getLength()];
		visited = new boolean[getLength()];
		Arrays.fill(visited, false);
		mapped = new boolean[getLength()];
		Arrays.fill(mapped, false);

		mobs = new HashSet<Mob>();
		heaps = new SparseArray<Heap>();
		blobs = new HashMap<Class<? extends Blob>, Blob>();
		plants = new SparseArray<Plant>();

		if (!Dungeon.bossLevel()) {
			addItemToSpawn(Generator.random(Generator.Category.FOOD));
			if (Dungeon.posNeeded()) {
				addItemToSpawn(new PotionOfStrength());
				Dungeon.limitedDrops.strengthPotions.count++;
			}
			if (Dungeon.souNeeded()) {
				addItemToSpawn(new ScrollOfUpgrade());
				Dungeon.limitedDrops.upgradeScrolls.count++;
			}
			if (Dungeon.asNeeded()) {
				addItemToSpawn(new Stylus());
				Dungeon.limitedDrops.arcaneStyli.count++;
			}

			int bonus = 0;
			for (Buff buff : Dungeon.hero.buffs(RingOfWealth.Wealth.class)) {
				bonus += ((RingOfWealth.Wealth) buff).level;
			}
			if (Random.Float() > Math.pow(0.95, bonus)) {
				if (Random.Int(2) == 0)
					addItemToSpawn(new ScrollOfMagicalInfusion());
				else
					addItemToSpawn(new PotionOfMight());
			}

			DriedRose rose = Dungeon.hero.belongings.getItem(DriedRose.class);
			if (rose != null && !rose.cursed) {
				// this way if a rose is dropped later in the game, player still
				// has a chance to max it out.
				int petalsNeeded = (int) Math
						.ceil((float) ((Dungeon.depth / 2) - rose.droppedPetals) / 3);

				for (int i = 1; i <= petalsNeeded; i++) {
					// the player may miss a single petal and still max their
					// rose.
					if (rose.droppedPetals < 11) {
						addItemToSpawn(new DriedRose.Petal() {
						});
						rose.droppedPetals++;
					}
				}
			}

			if (Dungeon.depth > 1 && Dungeon.depth < 6) {
				if (Dungeon.depth == 4 && !Dungeon.earlygrass) {
					feeling = Feeling.GRASS;
				} else {
				  switch (Random.Int(10)) {
				  case 0:
				  	if (!Dungeon.bossLevel(Dungeon.depth + 1)) {
				 		feeling = Feeling.CHASM;
					}
					break;
				  case 1:
					feeling = Feeling.WATER;
					break;
				  case 2: case 3: case 4: 
					feeling = Feeling.GRASS;
					Dungeon.earlygrass = true;
					break;
				 }
				}
			} else if (Dungeon.depth > 5 && Dungeon.depth < 22) {
				switch (Random.Int(10)) {
				case 0:
					if (!Dungeon.bossLevel(Dungeon.depth + 1)) {
						feeling = Feeling.CHASM;
					}
					break;
				case 1:
					feeling = Feeling.WATER;
					break;
				case 2: 
					feeling = Feeling.GRASS;
					break;
				case 3:
					feeling = Feeling.DARK;
					addItemToSpawn(new Torch());
					addItemToSpawn(new Torch());
					addItemToSpawn(new Torch());
					viewDistance = (int) Math.ceil(viewDistance / 3f);
					break;
				}
			} else if (Dungeon.depth > 21 && Dungeon.depth < 27) {
				switch (Random.Int(10)) {
				case 1:
					feeling = Feeling.WATER;
					break;
				case 2: 
					feeling = Feeling.GRASS;
					break;
				case 3:
				case 0:
					feeling = Feeling.DARK;
					addItemToSpawn(new Torch());
					addItemToSpawn(new Torch());
					addItemToSpawn(new Torch());
					viewDistance = (int) Math.ceil(viewDistance / 3f);
					break;
				}
			} else if (Dungeon.depth==29) {
				feeling = Feeling.WATER;
			} else if (Dungeon.depth==31) {
				feeling = Feeling.DARK;
				addItemToSpawn(new Torch());
				addItemToSpawn(new Torch());
				addItemToSpawn(new Torch());
				viewDistance = (int) Math.ceil(viewDistance / 3f);
			} else if (Dungeon.depth>55) {			
				addItemToSpawn(new StoneOre());
				addItemToSpawn(new StoneOre());
				addItemToSpawn(new StoneOre());
				addItemToSpawn(new StoneOre());
				addItemToSpawn(new StoneOre());
				addItemToSpawn(new StoneOre());
				addItemToSpawn(new StoneOre());
				addItemToSpawn(new StoneOre());
				addItemToSpawn(new StoneOre());
			}else if (Dungeon.depth==32) {
				feeling = Feeling.WATER;
			} else if (Dungeon.depth==33) {
				feeling = Feeling.CHASM;			
			}
			
		}

		boolean pitNeeded = Dungeon.depth > 1 && weakFloorCreated;

		do {
			Arrays.fill(map, feeling == Feeling.CHASM ? Terrain.CHASM
					: Terrain.WALL);

			pitRoomNeeded = pitNeeded;
			weakFloorCreated = false;

		} while (!build());
		decorate();

		buildFlagMaps();
		cleanWalls();

		createMobs();
		createItems();
	}

	public void reset() {

		for (Mob mob : mobs.toArray(new Mob[0])) {
			if (!mob.reset()) {
				mobs.remove(mob);
			}
		}
		createMobs();
		reset=true;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {

		mobs = new HashSet<Mob>();
		heaps = new SparseArray<Heap>();
		blobs = new HashMap<Class<? extends Blob>, Blob>();
		plants = new SparseArray<Plant>();

		map = bundle.getIntArray(MAP);
		visited = bundle.getBooleanArray(VISITED);
		mapped = bundle.getBooleanArray(MAPPED);

		entrance = bundle.getInt(ENTRANCE);
		exit = bundle.getInt(EXIT);
		pitSign = bundle.getInt(PITSIGN);
		currentmoves = bundle.getInt(MOVES);

		locked = bundle.getBoolean(LOCKED);
		
		cleared = bundle.getBoolean(CLEARED);
		reset = bundle.getBoolean(RESET);
		forcedone = bundle.getBoolean(FORCEDONE);
		genpetnext = bundle.getBoolean(GENPETNEXT);
		sealedlevel = bundle.getBoolean(SEALEDLEVEL);

		weakFloorCreated = false;

		adjustMapSize();

		Collection<Bundlable> collection = bundle.getCollection(HEAPS);
		for (Bundlable h : collection) {
			Heap heap = (Heap) h;
			if (resizingNeeded) {
				heap.pos = adjustPos(heap.pos);
			}
			if (!heap.isEmpty())
				heaps.put(heap.pos, heap);
		}

		collection = bundle.getCollection(PLANTS);
		for (Bundlable p : collection) {
			Plant plant = (Plant) p;
			if (resizingNeeded) {
				plant.pos = adjustPos(plant.pos);
			}
			plants.put(plant.pos, plant);
		}

		collection = bundle.getCollection(MOBS);
		for (Bundlable m : collection) {
			Mob mob = (Mob) m;
			if (mob != null) {
				if (resizingNeeded) {
					mob.pos = adjustPos(mob.pos);
				}
				mobs.add(mob);
			}
		}

		collection = bundle.getCollection(BLOBS);
		for (Bundlable b : collection) {
			Blob blob = (Blob) b;
			blobs.put(blob.getClass(), blob);
		}

		feeling = bundle.getEnum(FEELING, Feeling.class);
		if (feeling == Feeling.DARK)
			viewDistance = (int) Math.ceil(viewDistance / 3f);

		buildFlagMaps();
		cleanWalls();
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(MAP, map);
		bundle.put(VISITED, visited);
		bundle.put(MAPPED, mapped);
		bundle.put(ENTRANCE, entrance);
		bundle.put(EXIT, exit);
		bundle.put(LOCKED, locked);
		bundle.put(HEAPS, heaps.values());
		bundle.put(PLANTS, plants.values());
		bundle.put(MOBS, mobs);
		bundle.put(BLOBS, blobs.values());
		bundle.put(FEELING, feeling);
		bundle.put(PITSIGN, pitSign);
		bundle.put(MOVES, currentmoves);
		bundle.put(CLEARED, cleared);
		bundle.put(RESET, reset);
		bundle.put(FORCEDONE, forcedone);
		bundle.put(GENPETNEXT, genpetnext);
		bundle.put(SEALEDLEVEL, sealedlevel);
	}

	public int tunnelTile() {
		return feeling == Feeling.CHASM ? Terrain.EMPTY_SP : Terrain.EMPTY;
	}

	private void adjustMapSize() {
		// For levels saved before 1.6.3
		// Seeing as shattered started on 1.7.1 this is never used, but the code
		// may be resused in future.
		if (map.length < getLength()) {

			resizingNeeded = true;
			loadedMapSize = (int) Math.sqrt(map.length);

			int[] map = new int[getLength()];
			Arrays.fill(map, Terrain.WALL);

			boolean[] visited = new boolean[getLength()];
			Arrays.fill(visited, false);

			boolean[] mapped = new boolean[getLength()];
			Arrays.fill(mapped, false);

			for (int i = 0; i < loadedMapSize; i++) {
				System.arraycopy(this.map, i * loadedMapSize, map, i * getWidth(),
						loadedMapSize);
				System.arraycopy(this.visited, i * loadedMapSize, visited, i
						* getWidth(), loadedMapSize);
				System.arraycopy(this.mapped, i * loadedMapSize, mapped, i
						* getWidth(), loadedMapSize);
			}

			this.map = map;
			this.visited = visited;
			this.mapped = mapped;

			entrance = adjustPos(entrance);
			exit = adjustPos(exit);
		} else {
			resizingNeeded = false;
		}
	}

	public int adjustPos(int pos) {
		return (pos / loadedMapSize) * getWidth() + (pos % loadedMapSize);
	}

	public String tilesTex() {
		return null;
	}

	public String waterTex() {
		return null;
	}

	abstract protected boolean build();

	abstract protected void decorate();

	abstract protected void createMobs();

	abstract protected void createItems();

	public void addVisuals(Scene scene) {
		for (int i = 0; i < getLength(); i++) {
			if (pit[i]) {
				scene.add(new WindParticle.Wind(i));
				if (i >= getWidth() && water[i - getWidth()]) {
					scene.add(new FlowParticle.Flow(i - getWidth()));
				}
			}
		}
	}

	public int nMobs() {
		return 0;
	}
	

	public int movepar(){
		return movepar+Statistics.prevfloormoves;
	}


	public Actor respawner() {
		return new Actor() {
			@Override
			protected boolean act() {
				if (
						   (Dungeon.dewDraw && Dungeon.level.cleared && mobs.size() < nMobs()) 
						|| (!Dungeon.dewDraw && mobs.size() < nMobs())
					) {

					Mob mob = Bestiary.mutable(Dungeon.depth);
					mob.state = mob.WANDERING;
					mob.pos = randomRespawnCell();
					if (Dungeon.hero.isAlive() && mob.pos != -1) {
						GameScene.add(mob);
					}
				}
				spend(Dungeon.level.feeling == Feeling.DARK ? TIME_TO_RESPAWN / 2 : TIME_TO_RESPAWN);
				return true;
			}
		};
	}
	
	public Actor respawnerPet() {
		return new Actor() {
			@Override
			protected boolean act() {
				//GLog.i("Check Pet");
				int petpos = -1;
				int heropos = Dungeon.hero.pos;
				if (Actor.findChar(heropos) != null && Dungeon.hero.petfollow) {
					//GLog.i("Check Pet 2");
					ArrayList<Integer> candidates = new ArrayList<Integer>();
					boolean[] passable = Level.passable;

					for (int n : Level.NEIGHBOURS8) {
						int c = heropos + n;
						if (passable[c] && Actor.findChar(c) == null) {
							candidates.add(c);
						}
					}

					petpos = candidates.size() > 0 ? Random.element(candidates) : -1;
				}

				if (petpos != -1 && Dungeon.hero.haspet && Dungeon.hero.petfollow) {
					
					  PET petCheck = checkpet();
					  if(petCheck!=null){petCheck.destroy();petCheck.sprite.killAndErase();}
					  
					 if (Dungeon.hero.petType==1){
						 Spider pet = new Spider();
						  spawnPet(pet,petpos,heropos);					 
						}
				   if (Dungeon.hero.petType==2){
					  bee pet = new bee();
					  spawnPet(pet,petpos,heropos);					 
					}
				   if (Dungeon.hero.petType==3){
					      Velocirooster pet = new Velocirooster();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==4){
						  RedDragon pet = new RedDragon();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==5){
					   GreenDragon pet = new GreenDragon();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==6){
						  VioletDragon pet = new VioletDragon();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==7){
					   BlueDragon pet = new BlueDragon();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==8){
					   Scorpion pet = new Scorpion();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==9){
					   Bunny pet = new Bunny();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==10){
					   Fairy pet = new Fairy();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==11){
					   SugarplumFairy pet = new SugarplumFairy();
						  spawnPet(pet,petpos,heropos);					 
				   }
				   if (Dungeon.hero.petType==12){
					   ShadowDragon pet = new ShadowDragon();
						  spawnPet(pet,petpos,heropos);					 
				   }
					
				}
				
				spend(PET_TICK);
				return true;
			}
		};
	}

	private PET checkpet(){
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if(mob instanceof PET) {
				return (PET) mob;
			}
		}	
		return null;
	}
	
	public void spawnPet(PET pet, Integer petpos, Integer heropos){
		  pet.spawn(Dungeon.hero.petLevel);
		  pet.HP = Dungeon.hero.petHP;
		  pet.pos = petpos;
		  pet.state = pet.HUNTING;
		  pet.kills = Dungeon.hero.petKills;
		  pet.experience = Dungeon.hero.petExperience;
		  pet.cooldown = Dungeon.hero.petCooldown;

			GameScene.add(pet);
			Actor.addDelayed(new Pushing(pet, heropos, petpos), -1f);
			Dungeon.hero.petfollow = false;
	}
	
	public boolean checkOriginalGenMobs (){
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob.originalgen){return true;}
		 }	
		return false;
	}
	
	public Actor regrower() {
		return new Actor() {
			@Override
			protected boolean act() {
				
				int growPos = -1;
				for (int i = 0; i < 20; i++) {
					growPos = randomRegrowthCell();
					
				if (growPos != -1) {
					break;
				}
			} 
			if (growPos != -1) {
				  if (map[growPos] == Terrain.GRASS){
				  Level.set(growPos, Terrain.HIGH_GRASS);
				  } 
				  GameScene.updateMap();
				  Dungeon.observe();
			}		
				
			
				spend(REGROW_TIMER);
				return true;
			}
		};
	}
	
	public int randomRegrowthCell() {
		int cell;
		int count = 1;
		do {
			cell = Random.Int(getLength());
			count++;
		} while (map[cell] != Terrain.GRASS && count < 100);
		     return cell;
	}
	
	public Actor floordropper() {
		return new Actor() {
			@Override
			protected boolean act() {
				
							
				int dropPos = -1;
				for (int i = 0; i < 20; i++) {
					dropPos = randomChasmCell();
					
				if (dropPos != -1) {
					//GLog.i("one ");
					break;
				}
			}
			if (dropPos != -1) {
				
				//GLog.i("two %s",dropPos);
				  if (map[dropPos] == Terrain.EMPTY && Actor.findChar(dropPos) == null){
					  
					  //GLog.i("three ");
					  //if the tile above is not chasm then set to chasm floor. If is chasm then set to chasm
					 
					  if  (map[dropPos-getWidth()]==Terrain.WALL ||
							  map[dropPos-getWidth()]==Terrain.WALL_DECO){
						  
							  set(dropPos, Terrain.CHASM_WALL); 
							  //GLog.i("four ");
						   }
					  else if (map[dropPos-getWidth()]!=Terrain.CHASM && 
						  map[dropPos-getWidth()]!=Terrain.CHASM_FLOOR &&
						  map[dropPos-getWidth()]!=Terrain.CHASM_WALL)
					        {
								  set(dropPos, Terrain.CHASM_FLOOR); 
					         }  
						 else {
						   set(dropPos, Terrain.CHASM);
						  // GLog.i("five ");
					   }
					  	
					  if (map[dropPos+getWidth()]==Terrain.CHASM_FLOOR){
						  set(dropPos+getWidth(), Terrain.CHASM);
						 // GLog.i("six ");
					  }
					  
				
				    } 
				  GameScene.updateMap(dropPos);
				  GameScene.updateMap(dropPos-getWidth());
				  GameScene.updateMap(dropPos+getWidth());
				  Dungeon.observe();				  
			}				
			
				spend(DROP_TIMER);
				return true;
			}
		};
	}
	
	public int randomChasmCell() {
		int cell;
		int count = 1;
		do {
			cell = Random.Int(getWidth()+1, getLength()-(getWidth()+1));
			count++;
		} while (map[cell] != Terrain.EMPTY && count < 100);
		     return cell;
	}
	
	public int randomRespawnCell() {
		int cell;
		do {
			cell = Random.Int(getLength());
		} while (!passable[cell] || Dungeon.visible[cell]
				|| Actor.findChar(cell) != null);
		return cell;
	}

	public int randomRespawnCellMob() {
		int cell;
		do {
			cell = Random.Int(getLength());
		} while (!passable[cell] || Dungeon.visible[cell]
				|| Actor.findChar(cell) != null);
		return cell;
	}
	
	public int randomRespawnCellSheep(int start, int dist) {
		int cell;
		do {
			cell = Random.Int(getLength());
		} while (!avoid[cell] || Actor.findChar(cell) != null || map[cell]!=Terrain.FLEECING_TRAP
				  || distance(start, cell) > dist);
		return cell;
	}
	
	public int countFleeceTraps(int start, int dist) {
		int count=0;
		for (int cell = 0; cell < getLength(); cell++) {
		  if(avoid[cell] && Actor.findChar(cell) == null && map[cell]==Terrain.FLEECING_TRAP && distance(start, cell) < dist){
			  count++;
		  }
		}
		return count;		
	}
	
	
	public int randomRespawnCellFish() {
		int cell;
		do {
			cell = Random.Int(getLength());
		} while (!passable[cell] || Actor.findChar(cell) != null || map[cell]!=Terrain.EMPTY);
		return cell;
	}

	public int randomDestination() {
		int cell;
		do {
			cell = Random.Int(getLength());
		} while (!passable[cell]);
		return cell;
	}

	public void addItemToSpawn(Item item) {
		if (item != null) {
			itemsToSpawn.add(item);
		}
	}

	public Item findPrizeItem() {
		return findPrizeItem(null);
	}

	public Item findPrizeItem(Class<? extends Item> match) {
		if (itemsToSpawn.size() == 0)
			return null;

		if (match == null) {
			Item item = Random.element(itemsToSpawn);
			itemsToSpawn.remove(item);
			return item;
		}

		for (Item item : itemsToSpawn) {
			if (match.isInstance(item)) {
				itemsToSpawn.remove(item);
				return item;
			}
		}

		return null;
	}

	protected void buildFlagMaps() {

		for (int i = 0; i < getLength(); i++) {
			int flags = Terrain.flags[map[i]];
			passable[i] = (flags & Terrain.PASSABLE) != 0;
			losBlocking[i] = (flags & Terrain.LOS_BLOCKING) != 0;
			flamable[i] = (flags & Terrain.FLAMABLE) != 0;
			secret[i] = (flags & Terrain.SECRET) != 0;
			solid[i] = (flags & Terrain.SOLID) != 0;
			avoid[i] = (flags & Terrain.AVOID) != 0;
			water[i] = (flags & Terrain.LIQUID) != 0;
			pit[i] = (flags & Terrain.PIT) != 0;
		}

		int lastRow = getLength() - getWidth();
		for (int i = 0; i < getWidth(); i++) {
			passable[i] = avoid[i] = false;
			passable[lastRow + i] = avoid[lastRow + i] = false;
		}
		for (int i = getWidth(); i < lastRow; i += getWidth()) {
			passable[i] = avoid[i] = false;
			passable[i + getWidth() - 1] = avoid[i + getWidth() - 1] = false;
		}

		for (int i = getWidth(); i < getLength() - getWidth(); i++) {

			if (water[i]) {
				int t = Terrain.WATER_TILES;
				for (int j = 0; j < NEIGHBOURS4.length; j++) {
					if ((Terrain.flags[map[i + NEIGHBOURS4[j]]] & Terrain.UNSTITCHABLE) != 0) {
						t += 1 << j;
					}
				}
				map[i] = t;
			}

			if (pit[i]) {
				if (!pit[i - getWidth()]) {
					int c = map[i - getWidth()];
					if (c == Terrain.EMPTY_SP || c == Terrain.STATUE_SP) {
						map[i] = Terrain.CHASM_FLOOR_SP;
					} else if (water[i - getWidth()]) {
						map[i] = Terrain.CHASM_WATER;
					} else if ((Terrain.flags[c] & Terrain.UNSTITCHABLE) != 0) {
						map[i] = Terrain.CHASM_WALL;
					} else {
						map[i] = Terrain.CHASM_FLOOR;
					}
				}
			}
		}
	}

	protected void cleanWalls() {
		for (int i = 0; i < getLength(); i++) {

			boolean d = false;

			for (int j = 0; j < NEIGHBOURS9.length; j++) {
				int n = i + NEIGHBOURS9[j];
				if (n >= 0 && n < getLength() && map[n] != Terrain.WALL
						&& map[n] != Terrain.WALL_DECO) {
					d = true;
					break;
				}
			}

			if (d) {
				d = false;

				for (int j = 0; j < NEIGHBOURS9.length; j++) {
					int n = i + NEIGHBOURS9[j];
					if (n >= 0 && n < getLength() && !pit[n]) {
						d = true;
						break;
					}
				}
			}

			discoverable[i] = d;
		}
	}

	public static void set(int cell, int terrain) {
		Painter.set(Dungeon.level, cell, terrain);

		int flags = Terrain.flags[terrain];
		passable[cell] = (flags & Terrain.PASSABLE) != 0;
		losBlocking[cell] = (flags & Terrain.LOS_BLOCKING) != 0;
		flamable[cell] = (flags & Terrain.FLAMABLE) != 0;
		secret[cell] = (flags & Terrain.SECRET) != 0;
		solid[cell] = (flags & Terrain.SOLID) != 0;
		avoid[cell] = (flags & Terrain.AVOID) != 0;
		pit[cell] = (flags & Terrain.PIT) != 0;
		water[cell] = terrain == Terrain.WATER	|| (terrain >= Terrain.WATER_TILES && terrain<Terrain.WOOL_RUG);
	}

	public int checkdew(){
		int dewdrops=0;
		for (int i = 0; i < LENGTH; i++) {
			Heap heap = heaps.get(i);
			if (heap != null)
				dewdrops += heap.dewdrops();
		 }
		return dewdrops;
	}
	
	
	public Heap drop(Item item, int cell) {

		// This messy if statement deals will items which should not drop in
		// challenges primarily.
		if ((Dungeon.isChallenged(Challenges.NO_FOOD) && (item instanceof Food || item instanceof BlandfruitBush.Seed))
				|| (Dungeon.isChallenged(Challenges.NO_ARMOR) && item instanceof Armor)
				|| (Dungeon.isChallenged(Challenges.NO_HEALING) && item instanceof PotionOfHealing)
				|| (Dungeon.isChallenged(Challenges.NO_HERBALISM) && (item instanceof Plant.Seed
						|| item instanceof Dewdrop || item instanceof SeedPouch))
				|| (Dungeon.isChallenged(Challenges.NO_SCROLLS) && ((item instanceof Scroll && !(item instanceof ScrollOfUpgrade)) || item instanceof ScrollHolder))
				|| item == null) {

			Heap heap = new Heap();
			GameScene.add(heap);
			return heap;

		}

		if ((map[cell] == Terrain.ALCHEMY)
				&& (!(item instanceof Plant.Seed || item instanceof Blandfruit)
						|| item instanceof BlandfruitBush.Seed
						|| (item instanceof Blandfruit && (((Blandfruit) item).potionAttrib != null || heaps
								.get(cell) != null)) || Dungeon.hero
						.buff(AlchemistsToolkit.alchemy.class) != null
						&& Dungeon.hero.buff(AlchemistsToolkit.alchemy.class)
								.isCursed())) {
			int n;
			do {
				n = cell + NEIGHBOURS8[Random.Int(8)];
			} while (map[n] != Terrain.EMPTY_SP);
			cell = n;
		}

		Heap heap = heaps.get(cell);
		if (heap == null) {

			heap = new Heap();
			heap.seen = Dungeon.visible[cell];
			heap.pos = cell;
			if (map[cell] == Terrain.CHASM
					|| (Dungeon.level != null && pit[cell])) {
				Dungeon.dropToChasm(item);
				GameScene.discard(heap);
			} else {
				heaps.put(cell, heap);
				GameScene.add(heap);
			}

		} else if (heap.type == Heap.Type.LOCKED_CHEST
				|| heap.type == Heap.Type.CRYSTAL_CHEST
				//|| heap.type == Heap.Type.MONSTERBOX
				) {

			int n;
			do {
				n = cell + Level.NEIGHBOURS8[Random.Int(8)];
			} while (!Level.passable[n] && !Level.avoid[n]);
			return drop(item, n);

		}
		heap.drop(item);

		if (Dungeon.level != null) {
			press(cell, null);
		}

		return heap;
	}

	public Plant plant(Plant.Seed seed, int pos) {

		Plant plant = plants.get(pos);
		if (plant != null) {
			plant.wither();
		}

		if (map[pos] == Terrain.HIGH_GRASS || map[pos] == Terrain.EMPTY
				|| map[pos] == Terrain.EMBERS || map[pos] == Terrain.EMPTY_DECO) {
			map[pos] = Terrain.GRASS;
			GameScene.updateMap(pos);
		}

		plant = seed.couch(pos);
		plants.put(pos, plant);

		GameScene.add(plant);

		return plant;
	}

	public void uproot(int pos) {
		plants.delete(pos);
	}

	public int pitCell() {
		return randomRespawnCell();
	}

	public void press(int cell, Char ch) {

		if (pit[cell] && ch == Dungeon.hero) {
			Chasm.heroFall(cell);
			return;
		}

		TimekeepersHourglass.timeFreeze timeFreeze = null;

		if (ch != null)
			timeFreeze = ch.buff(TimekeepersHourglass.timeFreeze.class);

		boolean frozen = timeFreeze != null;

		boolean trap = false;
		boolean fleece = false;
		boolean sheep = false;

		switch (map[cell]) {

		case Terrain.SECRET_TOXIC_TRAP:
			GLog.i(TXT_HIDDEN_PLATE_CLICKS);
		case Terrain.TOXIC_TRAP:
			trap = true;
			if (!frozen)
				ToxicTrap.trigger(cell, ch);
			break;

		case Terrain.SECRET_FIRE_TRAP:
			GLog.i(TXT_HIDDEN_PLATE_CLICKS);
		case Terrain.FIRE_TRAP:
			trap = true;
			if (!frozen)
				FireTrap.trigger(cell, ch);
			break;

		case Terrain.SECRET_PARALYTIC_TRAP:
			GLog.i(TXT_HIDDEN_PLATE_CLICKS);
		case Terrain.PARALYTIC_TRAP:
			trap = true;
			if (!frozen)
				ParalyticTrap.trigger(cell, ch);
			break;

		case Terrain.SECRET_POISON_TRAP:
			GLog.i(TXT_HIDDEN_PLATE_CLICKS);
		case Terrain.POISON_TRAP:
			trap = true;
			if (!frozen)
				PoisonTrap.trigger(cell, ch);
			break;

		case Terrain.SECRET_ALARM_TRAP:
			GLog.i(TXT_HIDDEN_PLATE_CLICKS);
		case Terrain.ALARM_TRAP:
			trap = true;
			if (!frozen)
				AlarmTrap.trigger(cell, ch);
			break;

		case Terrain.SECRET_LIGHTNING_TRAP:
			GLog.i(TXT_HIDDEN_PLATE_CLICKS);
		case Terrain.LIGHTNING_TRAP:
			trap = true;
			if (!frozen)
				LightningTrap.trigger(cell, ch);
			break;

		case Terrain.SECRET_GRIPPING_TRAP:
			GLog.i(TXT_HIDDEN_PLATE_CLICKS);
		case Terrain.GRIPPING_TRAP:
			trap = true;
			if (!frozen)
				GrippingTrap.trigger(cell, ch);
			break;

		case Terrain.SECRET_SUMMONING_TRAP:
			GLog.i(TXT_HIDDEN_PLATE_CLICKS);
		case Terrain.SUMMONING_TRAP:
			trap = true;
			if (!frozen)
				SummoningTrap.trigger(cell, ch);
			break;
			
		case Terrain.FLEECING_TRAP:
			trap = true;
			if (ch instanceof SheepSokoban || ch instanceof SheepSokobanSwitch || ch instanceof SheepSokobanCorner){
				fleece=true;
			}			
			if (ch != null)
				FleecingTrap.trigger(cell, ch);
			break;
			
		case Terrain.CHANGE_SHEEP_TRAP:
			trap = true;
			if (ch instanceof SheepSokoban || ch instanceof SheepSokobanSwitch || ch instanceof SheepSokobanCorner){
				sheep=true;
				ChangeSheepTrap.trigger(cell, ch);
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

		if (trap && !frozen && !fleece) {

			if (Dungeon.visible[cell])
				Sample.INSTANCE.play(Assets.SND_TRAP);

			if (ch == Dungeon.hero)
				Dungeon.hero.interrupt();

			set(cell, Terrain.INACTIVE_TRAP);
			GameScene.updateMap(cell);

		} else if (trap && frozen && !fleece) {

			Sample.INSTANCE.play(Assets.SND_TRAP);

			Level.set(cell, Terrain.discover(map[cell]));
			GameScene.updateMap(cell);

			timeFreeze.setDelayedPress(cell);

		} else if (trap && frozen && fleece) {

			Sample.INSTANCE.play(Assets.SND_TRAP);

			Level.set(cell, Terrain.discover(map[cell]));
			GameScene.updateMap(cell);

			timeFreeze.setDelayedPress(cell);
			
		} else if (trap && !frozen && fleece) {

			if (Dungeon.visible[cell])
				Sample.INSTANCE.play(Assets.SND_TRAP);

			if (ch == Dungeon.hero)
				Dungeon.hero.interrupt();

			set(cell, Terrain.WOOL_RUG);
			GameScene.updateMap(cell);

		} else if (trap && sheep) {

			if (Dungeon.visible[cell])
				Sample.INSTANCE.play(Assets.SND_TRAP);

			set(cell, Terrain.INACTIVE_TRAP);
			GameScene.updateMap(cell);

		}

		Plant plant = plants.get(cell);
		if (plant != null) {
			plant.activate(ch);
		}
	}

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
			if (mob instanceof SheepSokoban || mob instanceof SheepSokobanSwitch || mob instanceof SheepSokobanCorner){
				fleece=true;
			}
			FleecingTrap.trigger(cell, mob);
			break;
			
		case Terrain.CHANGE_SHEEP_TRAP:
			if (mob instanceof SheepSokoban || mob instanceof SheepSokobanSwitch || mob instanceof SheepSokobanCorner){
				sheep=true;
				ChangeSheepTrap.trigger(cell, mob);
			}						
			break;
			
		case Terrain.SOKOBAN_ITEM_REVEAL:
			trap=false;
			if (mob instanceof SheepSokoban || mob instanceof SheepSokobanSwitch || mob instanceof SheepSokobanCorner){
				HeapGenTrap.trigger(cell, mob);
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

		if (trap && !fleece) {
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
			set(cell, Terrain.INACTIVE_TRAP);
			GameScene.updateMap(cell);
		}
		
		if (!trap && sheep) {
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
	}

	public boolean[] updateFieldOfView(Char c) {

		int cx = c.pos % getWidth();
		int cy = c.pos / getWidth();

		boolean sighted = c.buff(Blindness.class) == null
				&& c.buff(Shadows.class) == null
				&& c.buff(TimekeepersHourglass.timeStasis.class) == null
				&& c.isAlive();
		if (sighted) {
			ShadowCaster.castShadow(cx, cy, fieldOfView, c.viewDistance);
		} else {
			Arrays.fill(fieldOfView, false);
		}

		int sense = 1;
		if (c.isAlive()) {
			for (Buff b : c.buffs(MindVision.class)) {
				sense = Math.max(((MindVision) b).distance, sense);
			}
		}

		if ((sighted && sense > 1) || !sighted) {

			int ax = Math.max(0, cx - sense);
			int bx = Math.min(cx + sense, getWidth() - 1);
			int ay = Math.max(0, cy - sense);
			int by = Math.min(cy + sense, HEIGHT - 1);

			int len = bx - ax + 1;
			int pos = ax + ay * getWidth();
			for (int y = ay; y <= by; y++, pos += getWidth()) {
				Arrays.fill(fieldOfView, pos, pos + len, true);
			}

			for (int i = 0; i < getLength(); i++) {
				fieldOfView[i] &= discoverable[i];
			}
		}

		if (c.isAlive()) {
			if (c.buff(MindVision.class) != null) {
				for (Mob mob : mobs) {
					int p = mob.pos;
					fieldOfView[p] = true;
					fieldOfView[p + 1] = true;
					fieldOfView[p - 1] = true;
					fieldOfView[p + getWidth() + 1] = true;
					fieldOfView[p + getWidth() - 1] = true;
					fieldOfView[p - getWidth() + 1] = true;
					fieldOfView[p - getWidth() - 1] = true;
					fieldOfView[p + getWidth()] = true;
					fieldOfView[p - getWidth()] = true;
				}
			} else if (c == Dungeon.hero
					&& ((Hero) c).heroClass == HeroClass.HUNTRESS) {
				for (Mob mob : mobs) {
					int p = mob.pos;
					if (distance(c.pos, p) == 2) {
						fieldOfView[p] = true;
						fieldOfView[p + 1] = true;
						fieldOfView[p - 1] = true;
						fieldOfView[p + getWidth() + 1] = true;
						fieldOfView[p + getWidth() - 1] = true;
						fieldOfView[p - getWidth() + 1] = true;
						fieldOfView[p - getWidth() - 1] = true;
						fieldOfView[p + getWidth()] = true;
						fieldOfView[p - getWidth()] = true;
					}
				}
		} 
			if (c.buff(Awareness.class) != null) {
				for (Heap heap : heaps.values()) {
					int p = heap.pos;
					fieldOfView[p] = true;
					fieldOfView[p + 1] = true;
					fieldOfView[p - 1] = true;
					fieldOfView[p + getWidth() + 1] = true;
					fieldOfView[p + getWidth() - 1] = true;
					fieldOfView[p - getWidth() + 1] = true;
					fieldOfView[p - getWidth() - 1] = true;
					fieldOfView[p + getWidth()] = true;
					fieldOfView[p - getWidth()] = true;
				}
			}
		}

		for (Heap heap : heaps.values())
						if (!heap.seen && fieldOfView[heap.pos])
							heap.seen = true;
		
		return fieldOfView;
	}

	public static int distance(int a, int b) {
		int ax = a % getWidth();
		int ay = a / getWidth();
		int bx = b % getWidth();
		int by = b / getWidth();
		return Math.max(Math.abs(ax - bx), Math.abs(ay - by));
	}

	public static boolean adjacent(int a, int b) {
		int diff = Math.abs(a - b);
		return diff == 1 || diff == getWidth() || diff == getWidth() + 1
				|| diff == getWidth() - 1;
	}

	public String tileName(int tile) {

		if (tile >= Terrain.WATER_TILES) {
			return tileName(Terrain.WATER);
		}
		// && tile<Terrain.WOOL_RUG

		if (tile != Terrain.CHASM && (Terrain.flags[tile] & Terrain.PIT) != 0) {
			return tileName(Terrain.CHASM);
		}

		switch (tile) {
		case Terrain.CHASM:
			return "Chasm";
		case Terrain.EMPTY:
		case Terrain.EMPTY_SP:
		case Terrain.EMPTY_DECO:
		case Terrain.SECRET_TOXIC_TRAP:
		case Terrain.SECRET_FIRE_TRAP:
		case Terrain.SECRET_PARALYTIC_TRAP:
		case Terrain.SECRET_POISON_TRAP:
		case Terrain.SECRET_ALARM_TRAP:
		case Terrain.SECRET_LIGHTNING_TRAP:
			return "Floor";
		case Terrain.GRASS:
			return "Grass";
		case Terrain.WATER:
			return "Water";
		case Terrain.WALL:
		case Terrain.WALL_DECO:
		case Terrain.SECRET_DOOR:
			return "Wall";
		case Terrain.DOOR:
			return "Closed door";
		case Terrain.OPEN_DOOR:
			return "Open door";
		case Terrain.ENTRANCE:
			return "Depth entrance";
		case Terrain.EXIT:
			return "Depth exit";
		case Terrain.EMBERS:
			return "Embers";
		case Terrain.LOCKED_DOOR:
			return "Locked door";
		case Terrain.PEDESTAL:
			return "Pedestal";
		case Terrain.BARRICADE:
			return "Barricade";
		case Terrain.HIGH_GRASS:
			return "High grass";
		case Terrain.LOCKED_EXIT:
			return "Locked depth exit";
		case Terrain.UNLOCKED_EXIT:
			return "Unlocked depth exit";
		case Terrain.SIGN:
			return "Sign";
		case Terrain.WELL:
			return "Well";
		case Terrain.EMPTY_WELL:
			return "Empty well";
		case Terrain.STATUE:
		case Terrain.STATUE_SP:
			return "Statue";
		case Terrain.TOXIC_TRAP:
			return "Toxic gas trap";
		case Terrain.FIRE_TRAP:
			return "Fire trap";
		case Terrain.PARALYTIC_TRAP:
			return "Paralytic gas trap";
		//case Terrain.WOOL_RUG:
			//return "Wool rug";
		//case Terrain.FLEECING_TRAP:
			//return "Fleecing trap";
		//case Terrain.CHANGE_SHEEP_TRAP:
			//return "Change sheep trap";
		//case Terrain.SOKOBAN_ITEM_REVEAL:
			//return "Item creation switch";
		//case Terrain.SOKOBAN_PORT_SWITCH:
			//return "Portal switch";
		//case Terrain.PORT_WELL:
			//return "Portal";
		case Terrain.POISON_TRAP:
			return "Poison dart trap";
		case Terrain.ALARM_TRAP:
			return "Alarm trap";
		case Terrain.LIGHTNING_TRAP:
			return "Lightning trap";
		case Terrain.GRIPPING_TRAP:
			return "Gripping trap";
		case Terrain.SUMMONING_TRAP:
			return "Summoning trap";
		case Terrain.INACTIVE_TRAP:
			return "Triggered trap";
		case Terrain.BOOKSHELF:
			return "Bookshelf";
		case Terrain.ALCHEMY:
			return "Alchemy pot";
		case Terrain.SHRUB:
			return "Overgrown shrub";
		default:
			return "???";
		}
	}

	public String tileDesc(int tile) {

		switch (tile) {
		case Terrain.CHASM:
			return "You can't see the bottom.";
		case Terrain.WATER:
			return "In case of burning step into the water to extinguish the fire.";
		case Terrain.ENTRANCE:
			return "Stairs lead up to the upper depth.";
		case Terrain.EXIT:
		case Terrain.UNLOCKED_EXIT:
			return "Stairs lead down to the lower depth.";
		case Terrain.EMBERS:
			return "Embers cover the floor.";
		case Terrain.HIGH_GRASS:
			return "Dense vegetation blocks the view.";
		case Terrain.SHRUB:
			return "Dense srubs block the view.";
		case Terrain.LOCKED_DOOR:
			return "This door is locked, you need a matching key to unlock it.";
		case Terrain.LOCKED_EXIT:
			return "Heavy bars block the stairs leading down.";
		case Terrain.BARRICADE:
			return "The wooden barricade is firmly set but has dried over the years. Might it burn?";
		case Terrain.SIGN:
			return "You can't read the text from here.";
		case Terrain.TOXIC_TRAP:
		case Terrain.FIRE_TRAP:
		case Terrain.PARALYTIC_TRAP:
		case Terrain.POISON_TRAP:
		case Terrain.ALARM_TRAP:
		case Terrain.LIGHTNING_TRAP:
		case Terrain.GRIPPING_TRAP:
		case Terrain.SUMMONING_TRAP:
			return "Stepping onto a hidden pressure plate will activate the trap.";
		//case Terrain.FLEECING_TRAP:
			//return "Stepping onto a fleecing trap will destroy your armor or eject you from the level.";
		//case Terrain.CHANGE_SHEEP_TRAP:
			//return "This trap will change the form of any sheep.";
		//case Terrain.SOKOBAN_ITEM_REVEAL:
			//return "This switch creates an item somewhere on the level.";
		//case Terrain.SOKOBAN_PORT_SWITCH:
			//return "This switch turns on and off a portal somewhere.";
		//case Terrain.PORT_WELL:
			//return "This is a portal to another location on this level.";
		case Terrain.INACTIVE_TRAP:
			return "The trap has been triggered before and it's not dangerous anymore.";
		case Terrain.STATUE:
		case Terrain.STATUE_SP:
			return "Someone wanted to adorn this place, but failed, obviously.";
		case Terrain.ALCHEMY:
			return "Drop some seeds here to cook a potion.";
		case Terrain.EMPTY_WELL:
			return "The well has run dry.";
		//case Terrain.WOOL_RUG:
			//return "A plush wool rug. Very nice!";
		default:
			/*
			if(tile >= Terrain.WOOL_RUG){
				return "???";
			}
			*/			
			if (tile >= Terrain.WATER_TILES) {
				return tileDesc(Terrain.WATER);
			}
			if ((Terrain.flags[tile] & Terrain.PIT) != 0) {
				return tileDesc(Terrain.CHASM);
			}
			return "";
		}
	}
	
	/*

public static final int FLEECING_TRAP = 65;
	public static final int WOOL_RUG = 66;
	public static final int SOKOBAN_SHEEP = 67;
	public static final int CORNER_SOKOBAN_SHEEP = 68;
	public static final int SWITCH_SOKOBAN_SHEEP = 69;
	public static final int CHANGE_SHEEP_TRAP = 70;
	public static final int SOKOBAN_ITEM_REVEAL = 71;
	public static final int SOKOBAN_HEAP = 72;
	public static final int BLACK_SOKOBAN_SHEEP = 73;
	public static final int SOKOBAN_PORT_SWITCH = 75;
	public static final int PORT_WELL = 74;
*/


	public static int getWidth() {
		return WIDTH;
	}

	public static int getLength() {
		return LENGTH;
	}
}
