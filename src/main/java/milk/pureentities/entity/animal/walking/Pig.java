package milk.pureentities.entity.animal.walking;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.animal.WalkingAnimal;

public class Pig extends WalkingAnimal implements EntityRideable{
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
        return 0.9f;
    }

    @Override
    public float getHeight(){
        return 0.9f;
    }

    @Override
    public float getEyeHeight(){
        return 0.9f;
    }

    public void initEntity(){
        super.initEntity();
        this.setMaxHealth(10);
    }

    public boolean targetOption(EntityCreature creature, double distance){
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