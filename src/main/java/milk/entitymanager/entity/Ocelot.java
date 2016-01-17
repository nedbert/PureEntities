package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.entity.Creature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Ocelot extends Monster{
    public static final int NETWORK_ID = 22;

    private int angry = 0;

    public Ocelot(FullChunk chunk, CompoundTag nbt){
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
    public float getLength() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 0.9f;
    }

    //TODO: IDK
    /*@Override
    public float getEyeHeight() {
        return 1.62f;
    }*/

    public double getSpeed(){
        return 1.5;
    }

    public void initEntity(){
        this.fireProof = true;

        this.setMaxHealth(10);
        if(this.namedTag.contains("Health")){
            this.setHealth(this.namedTag.getInt("Health"));
        }else{
            this.setHealth(this.getMaxHealth());
        }

        if(this.namedTag.contains("Angry")){
            this.angry = this.namedTag.getInt("Angry");
        }

        this.setDamage(new double[]{0, 2, 2, 2});

        super.initEntity();

        this.created = true;
    }

    public void saveNBT(){
        this.namedTag.putInt("Angry", this.angry);
        super.saveNBT();
    }

    public String getName(){
        return "Ocelot";
    }

    public boolean isAngry(){
        return this.angry > 0;
    }

    public void setAngry(int val){
        this.angry = val;
    }

    public boolean targetOption(Creature creature, double distance){
    	if(creature instanceof Player){
            Player player = (Player) creature;
            return (player.spawned && player.isAlive() && !player.closed && player.getInventory().getItemInHand().getId() == Item.RAW_FISH && distance <= 49) || this.isAngry();
        }
    	return super.targetOption(creature, distance);
    }

    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1.44){
            this.attackDelay = 0;

            EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, (float) this.getDamage());
            player.attack(ev);
        }
    }

    public Item[] getDrops(){
    	return new Item[0];
    }

}
