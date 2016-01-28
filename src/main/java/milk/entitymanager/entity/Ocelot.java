package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Ocelot extends Monster{
    public static final int NETWORK_ID = 22;

    int angry = 0;

    public Ocelot(FullChunk chunk, CompoundTag nbt){
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
        return 0.9f;
    }

    @Override
    public double getSpeed(){
        return 1.5;
    }

    @Override
    protected void initEntity(){
        super.initEntity();

        this.fireProof = true;
        this.setMaxHealth(10);

        if(this.namedTag.contains("Angry")){
            this.angry = this.namedTag.getInt("Angry");
        }

        this.setDamage(new int[]{0, 2, 2, 2});
    }

    @Override
    public void saveNBT(){
        super.saveNBT();
        this.namedTag.putInt("Angry", this.angry);
    }

    @Override
    public String getName(){
        return "Ocelot";
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance){
    	if(creature instanceof Player){
            Player player = (Player) creature;
            return (player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.RAW_FISH && distance <= 49) || this.isAngry();
        }
    	return super.targetOption(creature, distance);
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

    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1.44){
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, (float) this.getDamage()));
        }
    }

    public Item[] getDrops(){
    	return new Item[0];
    }

}
