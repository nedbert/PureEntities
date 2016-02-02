package milk.entitymanager.entity.animal.walking;

import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

public class Cow extends WalkingAnimal{
    public static final int NETWORK_ID = 11;

    public Cow(FullChunk chunk, CompoundTag nbt){
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
        return "Cow";
    }

    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(10);
    }

    public boolean targetOption(EntityCreature creature, double distance){
        if(creature instanceof Player){
            Player player = (Player) creature;
            return player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.WHEAT && distance <= 49;
        }
        return false;
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 1)){
                case 0 :
                    return new Item[]{Item.get(Item.RAW_BEEF, 0, 1)};
                case 1 :
                    return new Item[]{Item.get(Item.LEATHER, 0, 1)};
            }
        }
        return new Item[0];
    }
}