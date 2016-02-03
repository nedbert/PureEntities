package milk.entitymanager.entity.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.entity.monster.WalkingMonster;
import milk.entitymanager.util.Utils;

public class CaveSpider extends WalkingMonster{
    public static final int NETWORK_ID = 40;

    public CaveSpider(FullChunk chunk, CompoundTag nbt){
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
        return 0.75f;
    }

    @Override
    public double getSpeed(){
        return 1.3;
    }

    @Override
    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(12);
        this.setDamage(new int[]{0, 2, 3, 3});
    }

    @Override
    public String getName(){
        return "CaveSpider";
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1.32){
            this.attackDelay = 0;

            EntityDamageEvent ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, this.getDamage());
            player.attack(ev);
        }
    }

    @Override
    public Item[] getDrops(){
        return this.lastDamageCause instanceof EntityDamageByEntityEvent ? new Item[]{Item.get(Item.STRING, 0, Utils.rand(0, 2))} : new Item[0];
    }

}
