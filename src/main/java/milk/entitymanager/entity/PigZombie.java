package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import milk.entitymanager.util.Utils;

public class PigZombie extends Monster{
    public static final int NETWORK_ID = 36;

    int angry = 0;

    public PigZombie(FullChunk chunk, CompoundTag nbt){
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
        return 1.8f;
    }

    @Override
    public float getEyeHeight(){
        return 1.62f;
    }

    @Override
    public double getSpeed(){
        return 1.15;
    }

    @Override
    protected void initEntity(){
        super.initEntity();

        this.fireProof = true;
        if(this.namedTag.contains("Angry")){
            this.angry = this.namedTag.getInt("Angry");
        }

        this.setDamage(new int[]{0, 5, 9, 13});
    }

    @Override
    public void saveNBT(){
        this.namedTag.putInt("Angry", this.angry);
        super.saveNBT();
    }

    @Override
    public String getName(){
        return "PigZombie";
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance){
        return this.isAngry() && super.targetOption(creature, distance);
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1.44){
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, (float) this.getDamage()));
        }
    }

    public boolean isAngry(){
        return this.angry > 0;
    }

    public void setAngry(int val){
        this.angry = val;
    }

    @Override
    public void attack(EntityDamageEvent ev){
        super.attack(ev);

        if(!ev.isCancelled()){
            this.setAngry(1000);
        }
    }

    @Override
    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0:
                    return new Item[]{Item.get(Item.FLINT, 0, 1)};
                case 1:
                    return new Item[]{Item.get(Item.GUNPOWDER, 0, 1)};
                case 2:
                    return new Item[]{Item.get(Item.REDSTONE_DUST, 0, 1)};
            }
        }
        return new Item[0];
    }

}
