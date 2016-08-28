package milk.pureentities.newphysics;

import cn.nukkit.Player;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;

public class EntityNormal extends EntityMob{
    //새로운 물리 체계 구현을 위해 새로 제작중인 몹
    public static final int NETWORK_ID = 32;

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    public EntityNormal(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    public boolean onUpdate(int currentTick){
        if(this.closed){
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if(tickDiff <= 0 && !this.justCreated){
            return true;
        }
        this.lastUpdate = currentTick;

        this.entityBaseTick(tickDiff);
        if(!this.isAlive()){
            return true;
        }
        this.move(this.motionX, this.motionY, this.motionZ);

        double friction = 1d - this.getDrag();
        if(this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)){
            friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getFrictionFactor();
        }

        this.motionX *= friction;
        this.motionZ *= friction;

        if(!this.onGround){
            this.motionY -= this.getGravity();
        }

        this.updateMovement();
        return true;
    }

    protected void updateMovement(){
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;

        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;

        this.addMovement(this.x, this.y + this.getEyeHeight(), this.z, this.yaw, this.pitch, this.yaw);
    }

    @Override
    public void spawnTo(Player player){
        AddEntityPacket pk = new AddEntityPacket();
        pk.type = this.getNetworkId();
        pk.eid = this.getId();
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.metadata = this.dataProperties;
        player.dataPacket(pk);

        super.spawnTo(player);
    }

}