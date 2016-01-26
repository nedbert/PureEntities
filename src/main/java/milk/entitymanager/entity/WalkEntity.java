package milk.entitymanager.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.entity.Creature;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

import java.util.Arrays;

public abstract class WalkEntity extends BaseEntity{

    public WalkEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    void checkTarget(){
        Vector3 target = this.baseTarget;
        if(!(target instanceof Creature) || !this.targetOption((Creature) target, this.distanceSquared(target))){
            double near = Integer.MAX_VALUE;

            for(Entity ent : this.getLevel().getEntities()){
                if(!(ent instanceof Creature) || ent instanceof Animal || ent == this) continue;
                Creature creature = (Creature) ent;

                if(creature instanceof BaseEntity && ((BaseEntity) creature).isFriendly() == this.isFriendly()){
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
            this.baseTarget instanceof Creature
            && ((Creature) this.baseTarget).isAlive()
        ){
            return;
        }

        int x, z;
        if(this.stayTime > 0){
            if(Utils.rand(1, 125) > 4){
                return;
            }

            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            this.baseTarget = this.add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        }else if(Utils.rand(1, 320) == 1){
            this.stayTime = Utils.rand(90, 400);

            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            this.baseTarget = this.add(Utils.rand() ? x : -x, Utils.rand(-20, 20) / 10, Utils.rand() ? z : -z);
        }else if(this.moveTime <= 0 || this.baseTarget == null){
            this.stayTime = 0;
            this.moveTime = Utils.rand(300, 1200);

            x = Utils.rand(25, 80);
            z = Utils.rand(25, 80);
            this.baseTarget = this.add(Utils.rand() ? x : -x, 0, Utils.rand() ? z : -z);
        }
    }

    public Vector3 updateMove(){
        if(!this.isMovement()){
            return null;
        }
        
        if(this.isKnockback()){
            this.knockback--;
            this.motionY -= 0.23;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.updateMovement();
            return null;
        }
        
        Vector3 before = this.baseTarget;
        this.checkTarget();
        if(this.baseTarget instanceof Creature || before != this.baseTarget){
            double x = this.baseTarget.x - this.x;
            double y = this.baseTarget.y - this.y;
            double z = this.baseTarget.z - this.z;
            if(this.stayTime > 0 || x * x + z * z < 0.7){
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
        
        Vector3 target = this.mainTarget != null ? this.mainTarget : this.baseTarget;
        if(this.stayTime > 0){
            --this.stayTime;
        }else{
            boolean isJump = false;
            double dx = this.motionX;
            double dy = this.motionY;
            double dz = this.motionZ;

            Vector2 be = new Vector2(this.x + dx, this.z + dz);
            this.move(dx, dy, dz);
            Vector2 af = new Vector2(this.x, this.z);

            if(be.x != af.x || be.y != af.y){
                int x = 0;
                int z = 0;
                if(be.x - af.x != 0) x += be.x - af.x > 0 ? 1 : -1;
                if(be.y - af.y != 0) z += be.y - af.y > 0 ? 1 : -1;

                Block block = this.level.getBlock((new Vector3(NukkitMath.floorDouble(be.x) + x, this.y, NukkitMath.floorDouble(af.y) + z)).floor());
                Block block2 = this.level.getBlock((new Vector3(NukkitMath.floorDouble(be.x) + x, this.y + 1, NukkitMath.floorDouble(af.y) + z)).floor());
                if(!block.canPassThrough()){
                    AxisAlignedBB bb = block2.getBoundingBox();
                    if(block2.canPassThrough() || (bb == null || bb.maxY - this.y <= 1)){
                        isJump = true;
                        this.motionY = 0.22;
                    }else{
                        if(this.level.getBlock(block.add(-x, 0, -z)).getId() == Item.LADDER){
                            isJump = true;
                            this.motionY = 0.22;
                        }
                    }
                }
                if(!isJump){
                    this.moveTime -= 80;
                }
            }

            if(this.onGround && !isJump){
                this.motionY = 0;
            }else if(!isJump){
                this.motionY -= 0.22;
            }
        }
        this.updateMovement();
        return target;
    }

}