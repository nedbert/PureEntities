package milk.entitymanager.entity.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockFenceGate;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.entity.monster.WalkingMonster;
import milk.entitymanager.util.Utils;

public class Spider extends WalkingMonster{
    public static final int NETWORK_ID = 35;

    public Spider(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 1.3f;
    }

    @Override
    public float getHeight(){
        return 1.12f;
    }

    @Override
    public float getEyeHeight(){
        return 1;
    }

    @Override
    public double getSpeed(){
        return 1.13;
    }

    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(16);
        this.setDamage(new int[]{0, 2, 2, 3});
    }

    @Override
    public String getName(){
        return "Spider";
    }

    @Override
    public boolean onUpdate(int currentTick){
        if(this.server.getDifficulty() < 1){
            this.close();
            return false;
        }

        if(!this.isAlive()){
            if(++this.deadTicks >= 23){
                this.close();
                return false;
            }
            return true;
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;
        this.entityBaseTick(tickDiff);

        if(!this.isMovement()){
            return true;
        }

        if(this.isKnockback()){
            this.move(this.motionX * tickDiff, this.motionY, this.motionZ * tickDiff);
            this.motionY -= 0.15 * tickDiff;
            this.updateMovement();
            return true;
        }

        Vector3 before = this.baseTarget;
        this.checkTarget();
        if(this.baseTarget instanceof EntityCreature || before != this.baseTarget){
            double x = this.baseTarget.x - this.x;
            double y = this.baseTarget.y - this.y;
            double z = this.baseTarget.z - this.z;

            Vector3 target = this.baseTarget;
            double distance = Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2));
            if(distance <= 2){
                if(target instanceof EntityCreature){
                    if(distance <= (this.getWidth() + 0.0d) / 2 + 0.05){
                        if(this.attackDelay < 10){
                            double diff = Math.abs(x) + Math.abs(z);
                            this.motionX = this.getSpeed() * 0.1 * (x / diff);
                            this.motionZ = this.getSpeed() * 0.1 * (z / diff);
                        }else{
                            this.motionX = 0;
                            this.motionZ = 0;
                            this.attackEntity((Entity) target);
                        }
                    }else{
                        double diff = Math.abs(x) + Math.abs(z);
                        if(!this.isFriendly()){
                            this.motionY = 0.15;
                        }
                        this.motionX = this.getSpeed() * 0.15 * (x / diff);
                        this.motionZ = this.getSpeed() * 0.15 * (z / diff);
                    }
                }else if(Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2) <= 1){
                    this.moveTime = 0;
                }
            }else{
                double diff = Math.abs(x) + Math.abs(z);
                this.motionX = this.getSpeed() * 0.15 * (x / diff);
                this.motionZ = this.getSpeed() * 0.15 * (z / diff);
            }
            this.yaw = Math.toDegrees(-Math.atan2(this.motionX, this.motionZ));
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }

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
                }else{
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
        return true;
    }

    @Override
    public Vector3 updateMove(int tickDiff){
        return null;
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && ((this.isFriendly() && !(player instanceof Player)) || !this.isFriendly())){
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, this.getDamage()));
        }
    }

    @Override
    public Item[] getDrops(){
        return this.lastDamageCause instanceof EntityDamageByEntityEvent ? new Item[]{Item.get(Item.STRING, 0, Utils.rand(0, 3))} : new Item[0];
    }

}
