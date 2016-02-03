package milk.entitymanager.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.entity.animal.Animal;
import milk.entitymanager.entity.monster.flying.Blaze;
import milk.entitymanager.util.Utils;

public abstract class FlyingEntity extends BaseEntity{

    public FlyingEntity(FullChunk chunk, CompoundTag nbt){
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
        int maxY = Math.max(this.getLevel().getHighestBlockAt((int) this.x, (int) this.z) + 15, 120);
        if(this.stayTime > 0){
            if(Utils.rand(1, 100) > 5){
                return;
            }

            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            if(this.y > maxY){
            	y = Utils.rand(-12, -4);
            }else{
            	y = Utils.rand(-10, 10);
            }
            this.baseTarget = this.add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
        }else if(Utils.rand(1, 370) == 1){
            x = Utils.rand(10, 30);
            z = Utils.rand(10, 30);
            if(this.y > maxY){
                y = Utils.rand(-12, -4);
            }else{
                y = Utils.rand(-10, 10);
            }
            this.stayTime = Utils.rand(90, 400);
            this.baseTarget = this.add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
        }else if(this.moveTime <= 0 || !(this.baseTarget instanceof Vector3)){
            x = Utils.rand(20, 100);
            z = Utils.rand(20, 100);
            if(this.y > maxY){
                y = Utils.rand(-12, -4);
            }else{
                y = Utils.rand(-10, 10);
            }
            this.stayTime = 0;
            this.moveTime = Utils.rand(300, 1200);
            this.baseTarget = this.add(Utils.rand() ? x : -x, y, Utils.rand() ? z : -z);
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
            if(this.stayTime > 0 || x * x + z * z < 0.7){
                this.motionX = 0;
                this.motionZ = 0;
            }else{
                double diff = Math.abs(x) + Math.abs(z);
                if(this instanceof Blaze){
                    if(this.baseTarget instanceof EntityCreature){
                        this.motionX = this.getSpeed() * 0.15 * (x / diff);
                        this.motionZ = this.getSpeed() * 0.15 * (z / diff);
                    }else{
                        this.motionX = 0;
                        this.motionZ = 0;
                        this.motionY = 0;
                    }
                }else{
                    this.motionX = this.getSpeed() * 0.15 * (x / diff);
                    this.motionZ = this.getSpeed() * 0.15 * (z / diff);
                    this.motionY = this.getSpeed() * 0.27 * (y / diff);
                }
            }
            this.yaw = -Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }

        Vector3 target = this.baseTarget;
        if(this.stayTime > 0){
            this.stayTime -= tickDiff;

            double dx = this.motionX;
            double dy = this.motionY * tickDiff;
            double dz = this.motionZ;
            this.move(dx, dy, dz);

            if(this instanceof Blaze){
                if(this.onGround){
                    this.motionY = 0;
                }else{
                    if(this.motionY > -this.getGravity() * 4){
                        this.motionY = -this.getGravity() * 4;
                    }else{
                        this.motionY -= this.getGravity();
                    }
                }
            }
        }else{
            boolean isJump = false;
            double dx = this.motionX * tickDiff;
            double dy = this.motionY * tickDiff;
            double dz = this.motionZ * tickDiff;

            Vector2 be = new Vector2(this.x + dx, this.z + dz);
            this.move(dx, dy, dz);
            Vector2 af = new Vector2(this.x, this.z);

            if(be.x != af.x || be.y != af.y){
                if(this instanceof Blaze){
                    int x = 0;
                    int z = 0;
                    if(be.x - af.x != 0){
                        x = be.x > af.x ? 1 : -1;
                    }
                    if(be.y - af.y != 0){
                        z = be.y > af.y ? 1 : -1;
                    }

                    Vector3 vec = new Vector3(NukkitMath.floorDouble(be.x) + x, this.y, NukkitMath.floorDouble(be.y) + z);
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
                        }
                    }

                    if(!isJump){
                        this.moveTime -= 90 * tickDiff;
                    }
                }else{
                    this.moveTime -= 90 * tickDiff;
                }
            }

            if(this instanceof Blaze){
                if(this.onGround && !isJump){
                    this.motionY = 0;
                }else if(!isJump){
                    if(this.motionY > -this.getGravity() * 4){
                        this.motionY = -this.getGravity() * 4;
                    }else{
                        this.motionY -= this.getGravity();
                    }
                }
            }
        }
        this.updateMovement();
        return target;
    }

}