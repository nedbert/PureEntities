package milk.entitymanager.entity.animal.walking;

import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

public class Chicken extends WalkingAnimal{
    public static final int NETWORK_ID = 10;

    public Chicken(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.4f;
    }

    @Override
    public float getHeight(){
        return 0.75f;
    }

    public String getName(){
        return "Chicken";
    }

    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(4);
    }

    public boolean targetOption(EntityCreature creature, double distance){
    	if(creature instanceof Player){
            Player player = (Player) creature;
            return player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.SEEDS && distance <= 49;
        }
    	return false;
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0 :
                    return new Item[]{Item.get(Item.RAW_CHICKEN, 0, 1)};
                case 1 :
                    return new Item[]{Item.get(Item.EGG, 0, 1)};
                case 2 :
                    return new Item[]{Item.get(Item.FEATHER, 0, 1)};
            }
        }
        return new Item[0];
    }

}