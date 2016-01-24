package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.Creature;

public class Wolf extends Monster{
    public static final int NETWORK_ID = 14;

    int angry = 0;

    public Wolf(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.72f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    @Override
    public double getSpeed(){
        return 1.2;
    }

    protected void initEntity(){
        super.initEntity();

        this.fireProof = true;
        this.setMaxHealth(8);

        if(this.namedTag.contains("Angry")){
            this.angry = this.namedTag.getInt("Angry");
        }

        this.setDamage(new int[]{0, 3, 4, 6});
        this.created = true;
    }

    public void saveNBT(){
        this.namedTag.putInt("Angry", this.angry);
        super.saveNBT();
    }

    public String getName(){
        return "Wolf";
    }

    public boolean isAngry(){
        return this.angry > 0;
    }

    public void setAngry(int val){
        this.angry = val;
    }

    public boolean targetOption(Creature creature, double distance){
    	return super.targetOption(creature, distance) && this.isAngry();
    }

    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1.6){
            this.attackDelay = 0;

            EntityDamageEvent ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, (float) this.getDamage());
            player.attack(ev);
        }
    }

    public Item[] getDrops(){
    	return new Item[0];
    }

}
