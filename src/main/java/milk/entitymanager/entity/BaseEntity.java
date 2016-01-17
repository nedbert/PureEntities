package milk.entitymanager.entity;

import cn.nukkit.entity.Creature;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.Server;

import java.lang.reflect.Method;

public abstract class BaseEntity extends Creature{

    int stayTime = 0;
    int moveTime = 0;

    boolean created = false;
    
    Vector3 baseTarget = null;
    Vector3 mainTarget = null;

    Entity attacker = null;
    int atkTime = 0;

    private boolean movement = true;
    private boolean wallcheck = true;

    private boolean friendly = false;

    public BaseEntity(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    public boolean isFriendly(){
    	return this.friendly;
    }

    public void setFriendly(boolean bool){
    	this.friendly = bool;
    }

    public boolean onUpdate(int currentTick){
        return false;
    }

    public abstract void updateTick();

    public abstract Vector3 updateMove();

    public abstract boolean targetOption(Creature creature, double distance);

    public String getSaveId(){
        return this.getClass().getSimpleName();
    }

    public boolean isCreated(){
        return this.created;
    }

    public boolean isMovement(){
        return this.movement;
    }

    public void setMovement(boolean value){
        this.movement = value;
    }

    public boolean isWallCheck(){
        return this.wallcheck;
    }

    public void setWallCheck(boolean value){
        this.wallcheck = value;
    }

    public double getSpeed(){
        return 1;
    }

    public void initEntity(){
        if(this.namedTag.contains("Movement")){
            this.setMovement(this.namedTag.getBoolean("Movement"));
        }
        this.setDataProperty(DATA_NO_AI, new ByteEntityData((byte) 1));

        try{
            Class<?> clazz = this.getClass().getSuperclass().getSuperclass().getSuperclass();
            Method method = clazz.getMethod("initEntity");
            method.invoke(this);
        }catch(Exception ignore){
            ignore.printStackTrace();
            //getServer().getLogger().info(ignore.getMessage());
        }
    }

    public void saveNBT(){
        this.namedTag.putBoolean("Movement", this.isMovement());
        super.saveNBT();
    }

    @Override
    public void updateMovement(){
        if(this.lastX == this.x && this.lastY == this.y && this.lastZ == this.z && this.lastYaw == this.yaw && this.lastPitch == this.pitch) return;
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;

        this.level.addEntityMovement(this.chunk.getX(), this.chunk.getZ(), this.id, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    @Override
    public void attack(EntityDamageEvent source){
        if(this.attackTime > 0 || this.noDamageTicks > 0){
            EntityDamageEvent lastCause = this.getLastDamageCause();
            if(lastCause != null && lastCause.getDamage() >= source.getDamage()){
                source.setCancelled();
            }
        }

        try{
            Class<?> clazz = this.getClass().getSuperclass().getSuperclass().getSuperclass();
            Method method = clazz.getMethod("attack");
            method.invoke(this, source);
        }catch(Exception ignore){
            ignore.printStackTrace();
            //getServer().getLogger().info(ignore.getMessage());
        }

        if(source.isCancelled()) return;

        if(source instanceof EntityDamageByEntityEvent){
            this.atkTime = 16;
            this.stayTime = 0;
            this.attacker = ((EntityDamageByEntityEvent) source).getDamager();
            if(this instanceof PigZombie){
                ((PigZombie) this).setAngry(1000);
            }else if(this instanceof Ocelot){
                ((Ocelot) this).setAngry(1000);
            }else if(this instanceof Wolf){
                ((Wolf) this).setAngry(1000);
            }
        }

        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = (byte) (this.isAlive() ? 2 : 3);
        Server.broadcastPacket(this.hasSpawned.values(), pk);
    }

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
                && this.boundingBox.maxY > bb.minY
                && this.boundingBox.minY < bb.maxY
                && this.boundingBox.maxZ > bb.minZ
                && this.boundingBox.minZ < bb.maxZ
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
                && this.boundingBox.maxY > bb.minY
                && this.boundingBox.minY < bb.maxY
                && this.boundingBox.maxX > bb.minX
                && this.boundingBox.minX < bb.maxX
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

    public void close(){
        this.created = false;
        super.close();
    }

}