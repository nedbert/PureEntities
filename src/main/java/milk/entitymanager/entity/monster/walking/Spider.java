package milk.entitymanager.entity.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

public class Spider extends WalkingMonster{
    public static final int NETWORK_ID = 35;

    public Spider(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 1.5f;
    }

    @Override
    public float getHeight(){
        return 1.2f;
    }

    @Override
    public float getEyeHeight(){
        return 1;
    }

    @Override
    public double getSpeed(){
        return 1.13;
    }

    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(16);
        this.setDamage(new int[]{0, 2, 2, 3});
    }

    @Override
    public String getName(){
        return "Spider";
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1.32){
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, this.getDamage()));
        }
    }

    @Override
    public Item[] getDrops(){
        return this.lastDamageCause instanceof EntityDamageByEntityEvent ? new Item[]{Item.get(Item.STRING, 0, Utils.rand(0, 3))} : new Item[0];
    }

}
