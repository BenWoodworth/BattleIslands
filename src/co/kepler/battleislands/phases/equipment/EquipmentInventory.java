package co.kepler.battleislands.phases.equipment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import co.kepler.battleislands.BattleIslands;

public class EquipmentInventory extends CraftInventory  {
	private final static int INVENTORY_SIZE = 54;

	private BattleIslands bi;
	private EquipmentSelection selection;
	private String title, name;
	private Player player;
	private ItemStack[] contents;

	public EquipmentInventory(BattleIslands bi, String title, Player player) {
		this.name = "EquipmentInventory-" + player.getUniqueId();
		this.title = title;
		this.player = player;
		selection = new EquipmentSelection();
		contents = new ItemStack[INVENTORY_SIZE];
		
		//TODO Remove teh debug codes
		contents[11] = new ItemStack(Material.NETHER_STAR);
	}
	
	public EquipmentSelection getSelection() {
		return selection;
	}

	public class EquipmentSelection {
		private int helmet, chestplate, leggings, boots, sword, bow, pickaxe;
		public int getChestplateSelection() { return chestplate; }
		public int getLeggingsSelection() { return leggings; }
		public int getPickaxeSelection() { return pickaxe; }
		public int getHelmetSelection() { return helmet; }
		public int getBootsSelection() { return boots; }
		public int getSwordSelectio() { return sword; }
		public int getBowSelection() { return bow; }
	}

	// Implemented Inventory Methods ----------------------------------------------------
	@Override public ItemStack[] getContents()
	{ return contents; }

	@Override public InventoryHolder getHolder()
	{ return player; }

	@Override public ItemStack getItem(int arg0)
	{ return contents[arg0]; }

	@Override public String getName()
	{ return name; }

	@Override public int getSize()
	{ return INVENTORY_SIZE; }

	@Override public String getTitle()
	{ return title; }

	@Override public InventoryType getType()
	{ return InventoryType.CHEST; }

	@Override public List<HumanEntity> getViewers()
	{ return Arrays.asList(player); }

	@Override public ListIterator<ItemStack> iterator()
	{ return Arrays.asList(contents).listIterator(); }
	@Override public ListIterator<ItemStack> iterator(int arg0)
	{ return Arrays.asList(contents).listIterator(arg0); }

	// Redundant Methods ----------------------------------------------------------------
	@Override public HashMap<Integer, ItemStack> addItem(ItemStack... arg0) { return null; }
	@Override public HashMap<Integer, ? extends ItemStack> all(int arg0) { return null; }
	@Override public HashMap<Integer, ? extends ItemStack> all(Material arg0) { return null; }
	@Override public HashMap<Integer, ? extends ItemStack> all(ItemStack arg0) { return null; }

	@Override public void clear() {}
	@Override public void clear(int arg0) {}

	@Override public boolean contains(int arg0) { return false; }
	@Override public boolean contains(Material arg0) { return false; }
	@Override public boolean contains(ItemStack arg0) { return false; }
	@Override public boolean contains(int arg0, int arg1) { return false; }
	@Override public boolean contains(Material arg0, int arg1) { return false; }
	@Override public boolean contains(ItemStack arg0, int arg1) { return false; }

	@Override public boolean containsAtLeast(ItemStack arg0, int arg1) { return false; }

	@Override public int first(int arg0) { return -1; }
	@Override public int first(Material arg0) { return -1; }
	@Override public int first(ItemStack arg0) { return -1; }
	@Override public int firstEmpty() { return -1; }

	@Override public int getMaxStackSize() { return 0; }

	@Override public void remove(int arg0) {}
	@Override public void remove(Material arg0) {}
	@Override public void remove(ItemStack arg0) {}

	@Override public HashMap<Integer, ItemStack> removeItem(ItemStack... arg0) { return null; }

	@Override public void setContents(ItemStack[] arg0) {}

	@Override public void setItem(int arg0, ItemStack arg1) {}

	@Override public void setMaxStackSize(int arg0) {}
}
