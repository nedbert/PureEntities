package milk.entitymanager.entity;

import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.Creature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

public class Sheep extends Animal{
    public static final int NETWORK_ID = 13;

    public Sheep(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.6f;
    }

    @Override
    public float getHeight() {
        return 1.12f;
    }

    public String getName(){
        return "Sheep";
    }

    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(8);
        this.created = true;
    }

    public boolean targetOption(Creature creature, double distance){
    	if(creature instanceof Player){
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.SEEDS && distance <= 49;
        }
        return false;
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            return new Item[]{Item.get(Item.WOOL, Utils.rand(0, 15), 1)};
        }
        return new Item[0];
    }

}