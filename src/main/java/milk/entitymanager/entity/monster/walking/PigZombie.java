package milk.entitymanager.entity.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.item.ItemSwordGold;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import milk.entitymanager.entity.BaseEntity;
import milk.entitymanager.entity.animal.WalkingAnimal;
import milk.entitymanager.entity.monster.WalkingMonster;
import milk.entitymanager.util.Utils;

public class PigZombie extends WalkingMonster{
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

        if(this.namedTag.contains("Angry")){
            this.angry = this.namedTag.getInt("Angry");
        }

        this.fireProof = true;
        this.setDamage(new int[]{0, 5, 9, 13});
    }

    @Override
    public void saveNBT(){
        super.saveNBT();
        this.namedTag.putInt("Angry", this.angry);
    }

    protected void checkTarget(){
        if(this.isKnockback()){
            return;
        }

        Vector3 target = this.baseTarget;
        if(!(target instanceof EntityCreature) || !this.targetOption((EntityCreature) target, this.distanceSquared(target))){
            double near = Integer.MAX_VALUE;

            for(Entity entity : this.getLevel().getEntities()){
                if(entity == this || !(entity instanceof EntityCreature) || entity instanceof WalkingAnimal){
                    continue;
                }

                EntityCreature creature = (EntityCreature) entity;
                if(creature instanceof BaseEntity && ((BaseEntity) creature).isFriendly() == this.isFriendly()){
                    continue;
                }

                double distance = this.distanceSquared(creature);
                if(
                    distance <= 100 && this.isAngry()
                    && entity instanceof PigZombie && !((PigZombie) entity).isAngry()
                ){
                    ((PigZombie) entity).setAngry(1000);
                }

                if(distance > near || !this.targetOption(creature, distance)){
                    continue;
                }
                near = distance;

                this.moveTime = 0;
                this.baseTarget = creature;
            }
        }

        if(this.baseTarget instanceof EntityCreature && ((EntityCreature) this.baseTarget).isAlive()){
            return;
        }

        if(this.moveTime <= 0 || this.baseTarget == null){
            int x = Utils.rand(20, 100);
            int z = Utils.rand(20, 100);
            this.moveTime = Utils.rand(300, 1200);
            this.baseTarget = this.add(Utils.rand() ? x : -x, 0, Utils.rand() ? z : -z);
        }
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
    public void spawnTo(Player player){
        super.spawnTo(player);

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getId();
        pk.item = new ItemSwordGold();
        pk.slot = 10;
        pk.selectedSlot = 10;
        player.dataPacket(pk);
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
