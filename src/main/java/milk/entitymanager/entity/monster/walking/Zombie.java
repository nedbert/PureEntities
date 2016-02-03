package milk.entitymanager.entity.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.entity.monster.WalkingMonster;
import milk.entitymanager.util.Utils;

public class Zombie extends WalkingMonster implements EntityAgeable{
    public static final int NETWORK_ID = 32;

    public Zombie(FullChunk chunk, CompoundTag nbt){
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
        return 1.1;
    }

    @Override
    public String getName(){
        return "Zombie";
    }

    @Override
    protected void initEntity(){
        super.initEntity();

        if(this.getDataProperty(DATA_AGEABLE_FLAGS) == null){
            this.setDataProperty(new ByteEntityData(DATA_AGEABLE_FLAGS, (byte) 0));
        }
        this.setDamage(new int[]{0, 2, 3, 4});
    }

    @Override
    public boolean isBaby(){
        return this.getDataFlag(DATA_AGEABLE_FLAGS, DATA_FLAG_BABY);
    }

    @Override
    public void setHealth(float health){
        super.setHealth(health);

        if(this.isAlive()){
            if(20 * 3 / 4 < this.getHealth()){
                this.setDamage(new int[]{0, 2, 3, 4});
            }else if(20 / 2 < this.getHealth()){
                this.setDamage(new int[]{0, 3, 4, 6});
            }else if(20 / 4 < this.getHealth()){
                this.setDamage(new int[]{0, 3, 5, 7});
            }else{
                this.setDamage(new int[]{0, 4, 6, 9});
            }
        }
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 2){
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, (float) this.getDamage()));
        }
    }

    @Override
    public boolean entityBaseTick(int tickDiff){
        //Timings.timerEntityBaseTick.startTiming();

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        int time = this.getLevel().getTime() % Level.TIME_FULL;
        if(
            !this.isOnFire()
            && !this.level.isRaining()
            && (time < Level.TIME_NIGHT || time > Level.TIME_SUNRISE)
        ){
            this.setOnFire(100);
        }

        //Timings.timerEntityBaseTick.stopTiming();
        return hasUpdate;
    }

    @Override
    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0:
                    return new Item[]{Item.get(Item.FEATHER, 0, 1)};
                case 1:
                    return new Item[]{Item.get(Item.CARROT, 0, 1)};
                case 2:
                    return new Item[]{Item.get(Item.POTATO, 0, 1)};
            }
        }
        return new Item[0];
    }

}
