package milk.entitymanager.entity;

import cn.nukkit.entity.Rideable;
import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.Creature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Pig extends Animal implements Rideable{
    public static final int NETWORK_ID = 12;

    public Pig(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 1.6f;
    }

    @Override
    public float getHeight(){
        return 1.12f;
    }

    public String getName(){
        return "Pig";
    }

    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(10);
    }

    public boolean targetOption(Creature creature, double distance){
    	if(creature instanceof Player){
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.CARROT && distance <= 49;
        }
    	return false;
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            return new Item[]{Item.get(Item.RAW_PORKCHOP, 0, 1)};
        }
        return new Item[0];
    }

}