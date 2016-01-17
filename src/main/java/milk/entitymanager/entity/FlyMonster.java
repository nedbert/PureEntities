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

    private int entityTick = 0;

    private double[] minDamage = new double[]{0, 0, 0, 0};
    private double[] maxDamage = new double[]{0, 0, 0, 0};

    public FlyMonster(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    public abstract void attackEntity(Entity player);

    public double getDamage(){
        return getDamage(null);
    }

    public double getDamage(Integer difficulty){
        return Utils.rand((int) this.getMinDamage(difficulty), (int) this.getMaxDamage(difficulty));
    }

    public double getMinDamage(){
        return getMinDamage(null);
    }

    public double getMinDamage(Integer difficulty){
        if(difficulty == null || difficulty > 3 || difficulty < 0){
            difficulty = Server.getInstance().getDifficulty();
        }
        return this.minDamage[difficulty];
    }

    public double getMaxDamage(){
        return getMaxDamage(null);
    }

    public double getMaxDamage(Integer difficulty){
        if(difficulty == null || difficulty > 3 || difficulty < 0){
            difficulty = Server.getInstance().getDifficulty();
        }
        return this.maxDamage[difficulty];
    }

    public void setDamage(double[] damage){
        this.setMinDamage(damage);
        this.setMaxDamage(damage);
    }

    public void setDamage(double damage, int difficulty){
        this.setMinDamage(damage, difficulty);
        this.setMaxDamage(damage, difficulty);
    }

    public void setMinDamage(double[] damage){
        if(damage.length < 4) return;
        minDamage[0] = Math.min(damage[0], maxDamage[0]);
        minDamage[1] = Math.min(damage[1], maxDamage[1]);
        minDamage[2] = Math.min(damage[2], maxDamage[2]);
        minDamage[3] = Math.min(damage[3], maxDamage[3]);
    }

    public void setMinDamage(double damage){
        setMinDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setMinDamage(double damage, int difficulty){
        if(difficulty >= 1 && difficulty <= 3){
            this.minDamage[difficulty] = Math.min(damage, this.maxDamage[difficulty]);
        }
    }

    public void setMaxDamage(double[] damage){
        if(damage.length < 4) return;
        maxDamage[0] = Math.min(damage[0], minDamage[0]);
        maxDamage[1] = Math.min(damage[1], minDamage[1]);
        maxDamage[2] = Math.min(damage[2], minDamage[2]);
        maxDamage[3] = Math.min(damage[3], minDamage[3]);
    }

    public void setMaxDamage(double damage){
        setMinDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setMaxDamage(double damage, Integer difficulty){
        if(difficulty >= 1 && difficulty <= 3){
            this.maxDamage[difficulty] = Math.max(damage, this.minDamage[difficulty]);
        }
    }

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

        if(this.entityTick++ >= 5){
            this.entityTick = 0;
            this.entityBaseTick(5);
        }
    }

    public boolean entityBaseTick(int tickDiff){
        //Timings.timerEntityBaseTick.startTiming();

        if(!this.isCreated()){
            return false;
        }

        boolean hasUpdate;
        EntityDamageEvent ev;
        try{
            Class<?> clazz = this.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass();
            Method method = clazz.getMethod("entityBaseTick");
            hasUpdate = (boolean) method.invoke(this);
        }catch(Exception ignore){
            return false;
        }
        
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