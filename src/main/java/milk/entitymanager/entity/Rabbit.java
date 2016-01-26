package milk.entitymanager.entity;

import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.entity.Creature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Rabbit extends Animal{
    public static final int NETWORK_ID = 18;

    public Rabbit(FullChunk chunk, CompoundTag nbt){
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

    @Override
    public double getSpeed(){
        return 1.2;
    }
    
    public String getName(){
        return "Rabbit";
    }

    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(4);
    }

    public boolean targetOption(Creature creature, double distance){
    	if(creature instanceof Player){
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.SEEDS && distance <= 49;
        }
        return false;
    }

    public Item[] getDrops(){
    	return new Item[0];
    }

}