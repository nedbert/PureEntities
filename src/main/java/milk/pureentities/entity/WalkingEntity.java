package milk.pureentities.entity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.block.BlockSlab;
import cn.nukkit.block.BlockStairs;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.animal.WalkingAnimal;
import milk.pureentities.util.Utils;

public abstract class WalkingEntity extends BaseEntity{

    public WalkingEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
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
                if(distance > near || !this.targetOption(creature, distance)){
                    continue;
                }
                near = distance;

                this.stayTime = 0;
                this.moveTime = 0;
                this.baseTarget = creature;
            }
        }

        if(this.baseTarget instanceof EntityCreature && ((EntityCreature) this.baseTarget).isAlive()){
            return;
        }

        int x, z;
        if(this.stayTime > 0){
            if(Utils.rand(1, 100) > 5){
                return;
            }
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            this.baseTarget = this.add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        }else if(Utils.rand(1, 410) == 1){
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            this.stayTime = Utils.rand(90, 400);
            this.baseTarget = this.add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        }else if(this.moveTime <= 0 || this.baseTarget == null){
            x = Utils.rand(20, 100);
            z = Utils.rand(20, 100);
            this.stayTime = 0;
            this.moveTime = Utils.rand(300, 1200);
            this.baseTarget = this.add(Utils.rand() ? x : -x, 0, Utils.rand() ? z : -z);
        }
    }

    protected boolean checkJump(double dx, double dz){
        if(this.motionY == this.getGravity() * 2){
            return this.level.getBlock(new Vector3(NukkitMath.floorDouble(this.x), (int) this.y, NukkitMath.floorDouble(this.z))) instanceof BlockLiquid;
        }else{
            if(this.level.getBlock(new Vector3(NukkitMath.floorDouble(this.x), (int) (this.y + 0.8), NukkitMath.floorDouble(this.z))) instanceof BlockLiquid){
                this.motionY = this.getGravity() * 2;
                return true;
            }
        }

        if(this.stayTime > 0){
            return false;
        }

        Block block = this.level.getBlock(this.add(dx, 0, dz));
        if(block instanceof BlockSlab || block instanceof BlockStairs){
            this.motionY = 0.5;
            return true;
        }
        return false;
    }

    public Vector3 updateMove(int tickDiff){
        if(!this.isMovement()){
            return null;
        }
        
        if(this.isKnockback()){
            this.move(this.motionX * tickDiff, this.motionY * tickDiff, this.motionZ * tickDiff);
            this.motionY -= 0.25 * tickDiff;
            this.updateMovement();
            return null;
        }
        
        Vector3 before = this.baseTarget;
        this.checkTarget();
        if(this.baseTarget instanceof EntityCreature || before != this.baseTarget){
            double x = this.baseTarget.x - this.x;
            double y = this.baseTarget.y - this.y;
            double z = this.baseTarget.z - this.z;

            double diff = Math.abs(x) + Math.abs(z);
            if(this.stayTime > 0 || this.distance(this.baseTarget) <= (this.getWidth() + 0.0d) / 2 + 0.05){
                this.motionX = 0;
                this.motionZ = 0;
            }else{
                this.motionX = this.getSpeed() * 0.15 * (x / diff);
                this.motionZ = this.getSpeed() * 0.15 * (z / diff);
            }
            this.yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }

        double dx = this.motionX * tickDiff;
        double dz = this.motionZ * tickDiff;
        if(this.stayTime > 0){
            boolean isJump = this.checkJump(dx, dz);
            this.stayTime -= tickDiff;

            this.move(0, this.motionY * tickDiff, 0);
            if(!isJump){
                if(this.onGround){
                    this.motionY = 0;
                }else if(this.motionY > -this.getGravity() * 4){
                    this.motionY = -this.getGravity() * 4;
                }else{
                    this.motionY -= this.getGravity() * tickDiff;
                }
            }
        }else{
            boolean isJump = this.checkJump(dx, dz);

            Vector2 be = new Vector2(this.x + dx, this.z + dz);
            this.move(dx, this.motionY * tickDiff, dz);
            Vector2 af = new Vector2(this.x, this.z);

            if((be.x != af.x || be.y != af.y) && !isJump){
                this.moveTime -= 90 * tickDiff;
            }

            if(!isJump){
                if(this.onGround){
                    this.motionY = 0;
                }else if(this.motionY > -this.getGravity() * 4){
                    this.motionY = -this.getGravity() * 4;
                }else{
                    this.motionY -= this.getGravity() * tickDiff;
                }
            }
        }
        this.updateMovement();
        return this.baseTarget;
    }

}