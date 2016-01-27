package milk.entitymanager.entity;

import cn.nukkit.entity.Ageable;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.Player;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class Animal extends WalkEntity implements Ageable{

    public Animal(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    public double getSpeed(){
        return 0.7;
    }

    protected void initEntity(){
        if(this.getDataProperty(DATA_AGEABLE_FLAGS) == null){
            this.setDataProperty(DATA_AGEABLE_FLAGS, new ByteEntityData((byte) 0));
        }
        super.initEntity();
    }

    public boolean isBaby(){
        return this.getDataFlag(DATA_AGEABLE_FLAGS, DATA_FLAG_BABY);
    }

    public boolean onUpdate(int currentTick){
        if(!this.isAlive()){
            if(++this.deadTicks >= 23){
                this.close();
                return false;
            }
            return true;
        }

        --this.moveTime;

        Vector3 target = this.updateMove();
        if(target instanceof Player){
            if(this.distance(target) <= 2){
                this.pitch = 22;
                this.x = this.lastX;
                this.y = this.lastY;
                this.z = this.lastZ;
            }
        }else if(!(target instanceof Entity)){
            if(this.distanceSquared(target) <= 1) this.moveTime = 0;
        }

        this.entityBaseTick();
        return true;
    }

}
