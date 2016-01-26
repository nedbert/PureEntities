package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Enderman extends Monster{
    public static final int NETWORK_ID = 38;

    public Enderman(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.72f;
    }

    @Override
    public float getHeight(){
        return 2.8f;
    }

    @Override
    public float getEyeHeight(){
        return 2.62f;
    }

    @Override
    public double getSpeed(){
        return 1.21;
    }

    protected void initEntity(){
        super.initEntity();
        this.setDamage(new int[]{0, 1, 2, 3});
    }

    public String getName(){
        return "Enderman";
    }

    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1){
            this.attackDelay = 0;

            EntityDamageEvent ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, (float) this.getDamage());
            player.attack(ev);
        }
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            return new Item[]{Item.get(Item.END_STONE, 0, 1)};
        }
        return new Item[0];
    }

}
