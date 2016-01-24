package milk.entitymanager.entity;

import cn.nukkit.item.Item;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.Creature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

public class Slime extends Animal{
    public static final int NETWORK_ID = 37;

    public Slime(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.2f;
    }

    @Override
    public float getHeight() {
        return 1.2f;
    }

    @Override
    public float getEyeHeight() {
        return 1.62f;
    }

    @Override
    public double getSpeed(){
        return 0.8;
    }

    public String getName(){
        return "Slime";
    }

    public void initEntity(){
        this.setMaxHealth(4);

        super.initEntity();
        this.created = true;
    }

    public boolean targetOption(Creature creature, double distance){
    	return false;
    }
    
    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
        	return new Item[]{Item.get(Item.SLIMEBALL, 0, Utils.rand(0, 2))};
        }
        return new Item[0];
    }

}