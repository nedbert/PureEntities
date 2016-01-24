package milk.entitymanager.entity;

import cn.nukkit.block.Water;
import cn.nukkit.entity.Effect;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Creature;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

public abstract class Monster extends WalkEntity{

    int attackDelay = 0;

    int[] minDamage;
    int[] maxDamage;

    public Monster(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    public abstract void attackEntity(Entity player);

    public int getDamage(){
        return getDamage(null);
    }

    public int getDamage(Integer difficulty){
        return Utils.rand(this.getMinDamage(difficulty), this.getMaxDamage(difficulty));
    }

    public int getMinDamage(){
        return getMinDamage(null);
    }

    public int getMinDamage(Integer difficulty){
        if(difficulty == null || difficulty > 3 || difficulty < 0){
            difficulty = Server.getInstance().getDifficulty();
        }

        return this.minDamage[difficulty];
    }

    public int getMaxDamage(){
        return getMaxDamage(null);
    }

    public int getMaxDamage(Integer difficulty){
        if(difficulty == null || difficulty > 3 || difficulty < 0){
            difficulty = Server.getInstance().getDifficulty();
        }

        return this.maxDamage[difficulty];
    }

    public void setDamage(int damage){
        this.setDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setDamage(int damage, int difficulty){
        if(difficulty >= 1 && difficulty <= 3){
            this.minDamage[difficulty] = damage;
            this.maxDamage[difficulty] = damage;
        }
    }

    public void setDamage(int[] damage){
        if(damage.length < 4) return;

        if(minDamage == null || minDamage.length < 4){
            minDamage = new int[]{0, 0, 0, 0};
        }

        if(maxDamage == null || maxDamage.length < 4){
            maxDamage = new int[]{0, 0, 0, 0};
        }

        for(int i = 0; i < 4; i++){
            this.minDamage[i] = damage[i];
            this.maxDamage[i] = damage[i];
        }
    }

    public void setMinDamage(int[] damage){
        if(damage.length < 4) return;

        for(int i = 0; i < 4; i++){
            this.setMinDamage(Math.min(damage[i], this.getMaxDamage(i)), i);
        }
    }

    public void setMinDamage(int damage){
        this.setMinDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setMinDamage(int damage, int difficulty){
        if(difficulty >= 1 && difficulty <= 3){
            this.minDamage[difficulty] = Math.min(damage, this.getMaxDamage(difficulty));
        }
    }

    public void setMaxDamage(int[] damage){
        if(damage.length < 4) return;

        for(int i = 0; i < 4; i++){
            this.setMaxDamage(Math.max(damage[i], this.getMinDamage(i)), i);
        }
    }

    public void setMaxDamage(int damage){
        setMinDamage(damage, Server.getInstance().getDifficulty());
    }

    public void setMaxDamage(int damage, Integer difficulty){
        if(difficulty >= 1 && difficulty <= 3){
            this.maxDamage[difficulty] = Math.max(damage, this.getMinDamage(difficulty));
        }
    }

    public void updateTick(){
        if(this.server.getDifficulty() < 1){
            this.close();
            return;
        }

        if(!this.isAlive()){
            if(++this.deadTicks >= 23){
                this.close();
            }
            return;
        }

        --this.moveTime;
        ++this.attackDelay;

        Vector3 target = this.updateMove();
        if(this.isFriendly()){
        	if(!(target instanceof Player)){
        		if(target instanceof Entity){
        			this.attackEntity((Entity) target);
        		}else if(
                    target != null
                    && (Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2)) <= 1
                ){
                    this.moveTime = 0;
        		}
        	}
        }else{
		    if(target instanceof Entity){
		        this.attackEntity((Entity) target);
		    }else if(
                target != null
                && (Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2)) <= 1
            ){
                this.moveTime = 0;
            }
        }

        this.entityBaseTick();
    }

    @Override
    public boolean entityBaseTick(int tickDiff){
        //Timings.timerEntityBaseTick.startTiming();

        if(!this.isCreated()){
            return false;
        }

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
        if(this instanceof Enderman){
            if(this.level.getBlock(new Vector3(NukkitMath.floorDouble(this.x), (int) this.y, NukkitMath.floorDouble(this.z))) instanceof Water){
                ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_DROWNING, 2);

                this.attack(ev);
                this.move(Utils.rand(-20, 20), Utils.rand(-20, 20), Utils.rand(-20, 20));
            }
        }else{
            if(!this.hasEffect(Effect.WATER_BREATHING) && this.isInsideOfWater()){
                hasUpdate = true;
                int airTicks = this.getDataPropertyShort(DATA_AIR).getData() - tickDiff;
                if(airTicks <= -20){
                    airTicks = 0;
                    ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_DROWNING, 2);
                    this.attack(ev);
                }
                this.setDataProperty(DATA_AIR, new ShortEntityData(airTicks));
            }else{
                this.setDataProperty(DATA_AIR, new ShortEntityData(300));
            }
        }

        //Timings.timerEntityBaseTick.stopTiming();
        return hasUpdate;
    }

    @Override
    public boolean targetOption(Creature creature, double distance){
        if(creature instanceof Player){
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.isSurvival() && distance <= 81;
        }
        return creature.isAlive() && !creature.closed && distance <= 81;
    }

}