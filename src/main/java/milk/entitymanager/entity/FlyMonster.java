package milk.entitymanager.entity;

import cn.nukkit.entity.Effect;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Creature;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

import java.lang.reflect.Method;

public abstract class FlyMonster extends FlyEntity{

    int attackDelay = 0;

    double[] minDamage;
    double[] maxDamage;

    public FlyMonster(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    public abstract void attackEntity(Entity player);

    //TODO: add Damage

    public void updateTick(){
        if(this.server.getDifficulty() < 1){
            this.close();
            return;
        }
        if(!this.isAlive()){
            if(++this.deadTicks >= 23) this.close();
            return;
        }

        --this.moveTime;
        ++this.attackDelay;
        Vector3 target = this.updateMove();
        if(target instanceof Entity){
            this.attackEntity((Entity) target);
        }else if(
            target != null &&
            (Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2)) <= 1
        ){
            this.moveTime = 0;
        }

        this.entityBaseTick();
    }

    public boolean entityBaseTick(int tickDiff){
        //Timings.timerEntityBaseTick.startTiming();

        if(!this.isCreated()){
            return false;
        }

        //TODO
        boolean hasUpdate = this.entityBaseTick2(tickDiff);
        EntityDamageEvent ev;
        
        if(this.atkTime > 0){
            this.atkTime -= tickDiff;
        }
        if(this.isInsideOfSolid()){
            hasUpdate = true;
            ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_SUFFOCATION, 1);
            this.attack(ev);
        }

        if(!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()){
            hasUpdate = true;
            int airTicks = this.getDataPropertyInt(DATA_AIR).getData() - tickDiff;
            if(airTicks <= -20){
                airTicks = 0;
                ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_DROWNING, 2);
                this.attack(ev);
            }
            this.setDataProperty(DATA_AIR, new ShortEntityData(airTicks));
        }else{
            this.setDataProperty(DATA_AIR, new ShortEntityData(300));
        }

        //Timings.timerEntityBaseTick.stopTiming();
        return hasUpdate;
    }

    public boolean targetOption(Creature creature, double distance){
        if(creature instanceof Player){
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.isSurvival() && distance <= 200;
        }
        return creature.isAlive() && !creature.closed && distance <= 200;
    }

}