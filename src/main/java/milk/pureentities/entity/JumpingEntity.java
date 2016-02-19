package milk.pureentities.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class JumpingEntity extends BaseEntity{

    public JumpingEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    protected void checkTarget(){
        //TODO
    }

    @Override
    public Vector3 updateMove(int tickDiff){
        if(!this.isMovement()){
            return null;
        }

        if(this.isKnockback()){
            this.move(this.motionX * tickDiff, this.motionY * tickDiff, this.motionZ * tickDiff);
            this.updateMovement();
            return null;
        }
        //TODO
        return null;
    }

}
