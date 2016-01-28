package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

public abstract class FlyEntity extends BaseEntity{

    public FlyEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    void checkTarget(){
    	if(this.getViewers().isEmpty()){
            return;
        }

        Vector3 target = this.baseTarget;
        if(!(target instanceof EntityCreature) || !this.targetOption((EntityCreature) target, this.distanceSquared(target))){
            double near = Integer.MAX_VALUE;

            for(Entity entity : this.getLevel().getEntities()){
                if(entity == this || !(entity instanceof EntityCreature) || entity instanceof Animal) continue;
                EntityCreature creature = (EntityCreature) entity;

                if(
                    creature instanceof BaseEntity
                    && ((BaseEntity) creature).isFriendly() == this.isFriendly()
                ){
                    continue;
                }

                double distance;
                if((distance = this.distanceSquared(creature)) > near || !this.targetOption(creature, distance)){
                    continue;
                }
                near = distance;

                this.stayTime = 0;
                this.baseTarget = creature;
            }
        }
        if(
            this.baseTarget instanceof EntityCreature
            && ((EntityCreature) this.baseTarget).isAlive()
        ){
            return;
        }

        int x, y, z;
        if(this.stayTime > 0){
            if(Utils.rand(1, 125) > 4) return;
            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            if(this.y > this.getLevel().getHighestBlockAt((int) this.x, (int) this.z) + 10){
            	y = Utils.rand(-10, -7);
            }else{
            	y = Utils.rand(-2, 2);
            }
            this.baseTarget = this.add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
        }else if(Utils.rand(1, 420) == 1){
            this.stayTime = Utils.rand(95, 420);
            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            if(this.y > this.getLevel().getHighestBlockAt((int) this.x, (int) this.z) + 10){
            	y = Utils.rand(-10, -7);
            }else{
            	y = Utils.rand(-2, 2);
            }
            this.baseTarget = this.add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
        }else if(this.moveTime <= 0 || !(this.baseTarget instanceof Vector3)){
            this.moveTime = Utils.rand(100, 1000);
            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            if(this.y > this.getLevel().getHighestBlockAt((int) this.x, (int) this.z) + 10){
            	y = Utils.rand(-10, -7);
            }else{
            	y = Utils.rand(-2, 2);
            }
            this.baseTarget = this.add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
        }
    }

    @Override
    public Vector3 updateMove(){
        if(!this.isMovement()){
            return null;
        }

        if(this.isKnockback()){
            this.knockback--;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.updateMovement();
            return null;
        }
        
        Vector3 before = this.baseTarget;
        this.checkTarget();
        if(this.baseTarget instanceof Player || before != this.baseTarget){
            double x = this.baseTarget.x - this.x;
            double y = this.baseTarget.y - this.y;
            double z = this.baseTarget.z - this.z;
            if(this.stayTime > 0 || x * x + z * z < 0.7){
                this.motionX = 0;
                this.motionZ = 0;
            }else{
                double diff = Math.abs(x) + Math.abs(z);
                this.motionX = this.getSpeed() * 0.15 * (x / diff);
                this.motionY = this.getSpeed() * 0.15 * (y / diff);
                this.motionZ = this.getSpeed() * 0.15 * (z / diff);
            }
            this.yaw = (-Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }

        Vector3 target = this.mainTarget != null ? this.mainTarget : this.baseTarget;
        if(this.stayTime > 0){
            --this.stayTime;
        }else{
            double dx = this.motionX;
            double dy = this.motionY;
            double dz = this.motionZ;

            this.move(dx, dy, dz);
        }
        this.updateMovement();
        return target;
    }

}