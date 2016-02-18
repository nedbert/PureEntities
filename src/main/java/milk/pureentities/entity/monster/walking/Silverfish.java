package milk.pureentities.entity.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.monster.WalkingMonster;

public class Silverfish extends WalkingMonster{
    public static final int NETWORK_ID = 39;

    public Silverfish(FullChunk chunk, CompoundTag nbt){
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
        return 0.2f;
    }

    @Override
    public double getSpeed(){
        return 1.4;
    }

    @Override
    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(8);
        this.setDamage(new int[]{0, 1, 1, 1});
    }

    @Override
    public String getName(){
        return "Silverfish";
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1){
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, this.getDamage()));
        }
    }

    @Override
    public Item[] getDrops(){
    	return new Item[0];
    }

}
