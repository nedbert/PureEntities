package milk.pureentities.entity.animal.flying;

import cn.nukkit.entity.EntityCreature;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.animal.FlyingAnimal;

public class Bat extends FlyingAnimal{
    //TODO: This isn't implemented yet
    public static final int NETWORK_ID = 13;

    public Bat(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.3f;
    }

    @Override
    public float getHeight(){
        return 0.3f;
    }

    @Override
    public String getName(){
        return "Bat";
    }

    @Override
    public void initEntity(){
        super.initEntity();

        //TODO: I don't know bat's health
        //this.setMaxHealth(8);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance){
        return false;
    }

    @Override
    public Item[] getDrops(){
        return new Item[0];
    }

}