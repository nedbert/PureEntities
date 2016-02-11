package milk.entitymanager.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.entity.animal.WalkingAnimal;
import milk.entitymanager.entity.monster.walking.PigZombie;
import milk.entitymanager.util.Utils;

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

                this.moveTime = 0;
                this.baseTarget = creature;
            }
        }

        if(this.baseTarget instanceof EntityCreature && ((EntityCreature) this.baseTarget).isAlive()){
            return;
        }

        if(this.moveTime <= 0 || this.baseTarget == null){
            int x = Utils.rand(20, 100);
            int z = Utils.rand(20, 100);
            this.moveTime = Utils.rand(300, 1200);
            this.baseTarget = this.add(Utils.rand() ? x : -x, 0, Utils.rand() ? z : -z);
        }
    }

    public Vector3 updateMove(int tickDiff){
        if(!this.isMovement()){
            return null;
        }
        
        if(this.isKnockback()){
            this.move(this.motionX * tickDiff, this.motionY, this.motionZ * tickDiff);
            this.motionY -= 0.15 * tickDiff;
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
                double diff = Math.abs(x) + Math.abs(z);
                this.motionX = this.getSpeed() * 0.15 * (x / diff);
                this.motionZ = this.getSpeed() * 0.15 * (z / diff);
            }
            this.yaw = -Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }
        
        Vector3 target = this.baseTarget;
        boolean isJump = false;
        double dx = this.motionX * tickDiff;
        double dy = this.motionY * tickDiff;
        double dz = this.motionZ * tickDiff;

        Vector2 be = new Vector2(this.x + dx, this.z + dz);
        this.move(dx, dy, dz);
        Vector2 af = new Vector2(this.x, this.z);

        if(be.x != af.x || be.y != af.y){
            int x = 0;
            int z = 0;
            if(be.x - af.x != 0){
                x = be.x > af.x ? 1 : -1;
            }
            if(be.y - af.y != 0){
                z = be.y > af.y ? 1 : -1;
            }

            Vector3 vec = new Vector3(NukkitMath.floorDouble(be.x), this.y, NukkitMath.floorDouble(be.y));
            Block block = this.level.getBlock(vec.add(x, 0, z));
            Block block2 = this.level.getBlock(vec.add(x, 1, z));
            if(!block.canPassThrough()){
                AxisAlignedBB bb = block2.getBoundingBox();
                if(
                    this.motionY > -this.getGravity() * 4
                    && (block2.canPassThrough() || (bb == null || bb.maxY - this.y <= 1))
                ){
                    isJump = true;
                    if(this.motionY >= 0.3){
                        this.motionY += this.getGravity();
                    }else{
                        this.motionY = 0.3;
                    }
                }else if(this.level.getBlock(vec).getId() == Item.LADDER){
                    isJump = true;
                    this.motionY = 0.15;
                }
            }

            if(!isJump){
                this.moveTime -= 90 * tickDiff;
            }
        }

        if(this.onGround && !isJump){
            this.motionY = 0;
        }else if(!isJump){
            if(this.motionY > -this.getGravity() * 4){
                this.motionY = -this.getGravity() * 4;
            }else{
                this.motionY -= this.getGravity();
            }
        }
        this.updateMovement();
        return target;
    }

}