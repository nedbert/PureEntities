package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.entity.animal.Animal;
import milk.entitymanager.util.Utils;

public abstract class JumpingEntity extends BaseEntity{

    public JumpingEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    void checkTarget(){
        if(this.isKnockback()){
            return;
        }

        Vector3 target = this.baseTarget;
        if(!(target instanceof EntityCreature) || !this.targetOption((EntityCreature) target, this.distanceSquared(target))){
            double near = Integer.MAX_VALUE;

            for(Entity entity : this.getLevel().getEntities()){
                if(entity == this || !(entity instanceof EntityCreature) || entity instanceof Animal){
                    continue;
                }

                EntityCreature creature = (EntityCreature) entity;
                if(
                    creature instanceof BaseEntity
                    && ((BaseEntity) creature).isFriendly() == this.isFriendly()
                ){
                    continue;
                }

                double distance = this.distanceSquared(creature);
                if(distance > near || !this.targetOption(creature, distance)){
                    continue;
                }
                near = distance;

                this.moveTime = 0;
                this.baseTarget = creature;
            }
        }

        if(
            this.baseTarget instanceof EntityCreature
            && ((EntityCreature) this.baseTarget).isAlive()
        ){
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
            if(x * x + z * z < 0.7){
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
