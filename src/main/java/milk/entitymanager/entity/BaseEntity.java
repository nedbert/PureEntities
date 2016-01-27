package milk.entitymanager.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Creature;
import cn.nukkit.entity.Effect;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEntity extends Creature{

    int stayTime = 0;
    int moveTime = 0;
    
    Vector3 baseTarget = null;
    Vector3 mainTarget = null;

    int knockback = 0;

    List<Block> blocksAround = new ArrayList<>();

    private boolean movement = true;
    private boolean friendly = false;
    private boolean wallcheck = true;

    public BaseEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    public abstract Vector3 updateMove();

    public abstract boolean targetOption(Creature creature, double distance);

    public boolean isFriendly(){
    	return this.friendly;
    }

    public boolean isMovement(){
        return this.movement;
    }

    public boolean isKnockback(){
        return this.knockback > 0;
    }

    public boolean isWallCheck(){
        return this.wallcheck;
    }

    public void setFriendly(boolean bool){
        this.friendly = bool;
    }

    public void setMovement(boolean value){
        this.movement = value;
    }

    public void setWallCheck(boolean value){
        this.wallcheck = value;
    }

    public String getSaveId(){
        return this.getClass().getSimpleName();
    }

    public double getSpeed(){
        return 1;
    }

    @Override
    protected void initEntity(){
        super.initEntity();

        if(this.namedTag.contains("Movement")){
            this.setMovement(this.namedTag.getBoolean("Movement"));
        }
        this.setDataProperty(DATA_NO_AI, new ByteEntityData((byte) 1));
    }

    public void saveNBT(){
        super.saveNBT();
        this.namedTag.putBoolean("Movement", this.isMovement());
    }

    @Override
    public void spawnTo(Player player){
        if(
            !this.hasSpawned.containsKey(player.getLoaderId())
            && player.usedChunks.containsKey(Level.chunkHash(this.chunk.getX(), this.chunk.getZ()))
        ){
            AddEntityPacket pk = new AddEntityPacket();
            pk.eid = this.getId();
            pk.type = this.getNetworkId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = pk.speedY = pk.speedZ = 0;
            pk.yaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);

            this.hasSpawned.put(player.getLoaderId(), player);
        }
    }

    @Override
    public void updateMovement(){
        if(
            this.lastX != this.x
            || this.lastY != this.y
            || this.lastZ != this.z
            || this.lastYaw != this.yaw
            || this.lastPitch != this.pitch
        ){
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;

            this.level.addEntityMovement(this.chunk.getX(), this.chunk.getZ(), this.id, this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }

    @Override
    public List<Block> getBlocksAround(){
        if(this.blocksAround == null){
            int minX = NukkitMath.floorDouble(this.boundingBox.minX);
            int minY = NukkitMath.floorDouble(this.boundingBox.minY);
            int minZ = NukkitMath.floorDouble(this.boundingBox.minZ);
            int maxX = NukkitMath.ceilDouble(this.boundingBox.maxX);
            int maxY = NukkitMath.ceilDouble(this.boundingBox.maxY);
            int maxZ = NukkitMath.ceilDouble(this.boundingBox.maxZ);

            this.blocksAround = new ArrayList<>();

            for(int z = minZ; z <= maxZ; ++z){
                for(int x = minX; x <= maxX; ++x){
                    for(int y = minY; y <= maxY; ++y){
                        Block block = this.level.getBlock(this.temporalVector.setComponents(x, y, z));
                        if(block.hasEntityCollision()){
                            this.blocksAround.add(block);
                        }
                    }
                }
            }
        }

        return this.blocksAround;
    }

    public boolean entityBaseTick2(int tickDiff){
        this.blocksAround = null;
        this.justCreated = false;

        if(!this.effects.isEmpty()){
            for(Effect effect : this.effects.values()){
                if(effect.canTick()){
                    effect.applyEffect(this);
                }
                effect.setDuration(effect.getDuration() - tickDiff);

                if(effect.getDuration() <= 0){
                    this.removeEffect(effect.getId());
                }
            }
        }

        boolean hasUpdate = false;

        if(this.y <= -16 && this.isAlive()){
            EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_VOID, 10);
            this.attack(ev);
            hasUpdate = true;
        }

        if(this.fireTicks > 0){
            if(this.fireProof){
                this.fireTicks -= 4 * tickDiff;
                if(this.fireTicks < 0){
                    this.fireTicks = 0;
                }
            }else{
                if(!this.hasEffect(Effect.FIRE_RESISTANCE) && (this.fireTicks % 20) == 0 || tickDiff > 20){
                    EntityDamageEvent ev = new EntityDamageEvent(this, EntityDamageEvent.CAUSE_FIRE_TICK, 1);
                    this.attack(ev);
                }
                this.fireTicks -= tickDiff;
            }

            if(this.fireTicks <= 0){
                this.extinguish();
            }else{
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, true);
                hasUpdate = true;
            }
        }

        if(this.attackTime > 0){
            this.attackTime = 0;
        }

        if(this.noDamageTicks > 0){
            this.noDamageTicks -= tickDiff;
            if(this.noDamageTicks < 0){
                this.noDamageTicks = 0;
            }
        }

        this.age += tickDiff;
        this.ticksLived += tickDiff;

        return hasUpdate;
    }

    @Override
    public void attack(EntityDamageEvent source){
        if(this.isKnockback()) return;

        super.attack(source);

        if(source.isCancelled() || !(source instanceof EntityDamageByEntityEvent)){
            return;
        }

        this.stayTime = 0;
        this.knockback = 11;

        Entity damager = ((EntityDamageByEntityEvent) source).getDamager();
        Vector3 motion = new Vector3(this.x - damager.x, this.y - damager.y, this.z - damager.z).normalize();
        this.motionX = motion.x * 0.19;
        this.motionZ = motion.z * 0.19;
        if(this instanceof FlyEntity){
            this.motionY = motion.y * 0.19;
        }else{
            this.motionY = 0.58;
        }

        if(this instanceof PigZombie){
            ((PigZombie) this).setAngry(1000);
        }else if(this instanceof Ocelot){
            ((Ocelot) this).setAngry(1000);
        }else if(this instanceof Wolf){
            ((Wolf) this).setAngry(1000);
        }
    }

    @Override
    public void knockBack(Entity attacker, float damage, double x, double z, float base){

    }

    @Override
    public boolean move(double dx, double dy, double dz){
        //Timings.entityMoveTimer.startTiming();

        double movX = dx;
        double movY = dy;
        double movZ = dz;

        AxisAlignedBB[] list = this.level.getCollisionCubes(this, this.level.getTickRate() > 1 ? this.boundingBox.getOffsetBoundingBox(dx, dy, dz) : this.boundingBox.addCoord(dx, dy, dz));

        for(AxisAlignedBB bb : list){
            dy = bb.calculateYOffset(this.boundingBox, dy);
        }
        this.boundingBox.offset(0, dy, 0);

        for(AxisAlignedBB bb : list){
            if(
                this.isWallCheck()
                && this.boundingBox.maxY >= bb.minY
                && this.boundingBox.minY <= bb.maxY
                && this.boundingBox.maxZ >= bb.minZ
                && this.boundingBox.minZ <= bb.maxZ
            ){
                double x1;
                if(this.boundingBox.maxX + dx >= bb.minX && this.boundingBox.maxX <= bb.minX){
                    if((x1 = bb.minX - (this.boundingBox.maxX + dx)) < 0) dx += x1;
                }
                if(this.boundingBox.minX + dx <= bb.maxX && this.boundingBox.minX >= bb.maxX){
                    if((x1 = bb.maxX - (this.boundingBox.minX + dx)) > 0) dx += x1;
                }
            }
        }
        this.boundingBox.offset(dx, 0, 0);

        for(AxisAlignedBB bb : list){
            if(
                this.isWallCheck()
                && this.boundingBox.maxY >= bb.minY
                && this.boundingBox.minY <= bb.maxY
                && this.boundingBox.maxX >= bb.minX
                && this.boundingBox.minX <= bb.maxX
            ){
                double z1;
                if(this.boundingBox.maxZ + dz >= bb.minZ && this.boundingBox.maxZ <= bb.minZ){
                    if((z1 = bb.minZ - (this.boundingBox.maxZ + dz)) < 0) dz += z1;
                }
                if(this.boundingBox.minZ + dz <= bb.maxZ && this.boundingBox.minZ >= bb.maxZ){
                    if((z1 = bb.maxZ - (this.boundingBox.minZ + dz)) > 0) dz += z1;
                }
            }
        }
        this.boundingBox.offset(0, 0, dz);

        this.setComponents(this.x + dx, this.y + dy, this.z + dz);
        this.checkChunks();

        this.checkGroundState(movX, movY, movZ, dx, dy, dz);
        this.updateFallState((float) dy, this.onGround);

        //Timings.entityMoveTimer.stopTiming();
        return true;
    }

}