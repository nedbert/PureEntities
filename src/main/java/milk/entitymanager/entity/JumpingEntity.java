package milk.entitymanager.entity;

import cn.nukkit.entity.EntityCreature;
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

        Vector3 before = this.baseTarget;
        this.checkTarget();
        if(this.baseTarget instanceof EntityCreature || before != this.baseTarget){
            double x = this.baseTarget.x - this.x;
            double y = this.baseTarget.y - this.y;
            double z = this.baseTarget.z - this.z;
            if(this.distance(this.baseTarget) <= this.getWidth() / 2 + 0.05){
                this.motionX = 0;
                this.motionZ = 0;
            }else{
                //TODO
            }
            this.yaw = -Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }
        return null;
    }

}
