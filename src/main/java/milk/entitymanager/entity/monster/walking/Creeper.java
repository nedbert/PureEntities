package milk.entitymanager.entity.monster.walking;

/**
 _______ .__   __. .___________. __  .___________.____    ____ .___  ___.      ___      .__   __.      ___       _______  _______ .______
 |   ____||  \ |  | |           ||  | |           |\   \  /   / |   \/   |     /   \     |  \ |  |     /   \     /  _____||   ____||   _  \
 |  |__   |   \|  | `---|  |----`|  | `---|  |----` \   \/   /  |  \  /  |    /  ^  \    |   \|  |    /  ^  \   |  |  __  |  |__   |  |_)  |
 |   __|  |  . `  |     |  |     |  |     |  |       \_    _/   |  |\/|  |   /  /_\  \   |  . `  |   /  /_\  \  |  | |_ | |   __|  |      /
 |  |____ |  |\   |     |  |     |  |     |  |         |  |     |  |  |  |  /  _____  \  |  |\   |  /  _____  \ |  |__| | |  |____ |  |\  \.
 |_______||__| \__|     |__|     |__|     |__|         |__|     |__|  |__| /__/     \__\ |__| \__| /__/     \__\ \______| |_______|| _| `._|

 */

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import milk.entitymanager.entity.monster.WalkingMonster;
import milk.entitymanager.util.Utils;

public class Creeper extends WalkingMonster implements EntityExplosive{
    public static final int NETWORK_ID = 33;

    private int bombTime = 0;

    public Creeper(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.72f;
    }

    @Override
    public float getHeight(){
        return 1.8f;
    }

    @Override
    public double getSpeed(){
        return 0.9;
    }

    @Override
    public String getName(){
        return "Creeper";
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

        if(!(this.baseTarget instanceof EntityCreature)){
            this.bombTime -= tickDiff;
            if(this.bombTime < 0){
                this.bombTime = 0;
            }
        }

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
            if(distance <= 4.5){
                if(target instanceof EntityCreature){
                    this.bombTime += tickDiff;
                    if(this.bombTime >= 64){
                        this.explode();
                    }
                }else if(Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2) <= 1){
                    this.moveTime = 0;
                }
            }else{
                this.bombTime -= tickDiff;
                if(this.bombTime < 0){
                    this.bombTime = 0;
                }

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
        return true;
    }

    @Override
    public Vector3 updateMove(int tickDiff){
        return null;
    }

    @Override
    public void explode(){
        ExplosionPrimeEvent ev = new ExplosionPrimeEvent(this, 2.8);
        this.server.getPluginManager().callEvent(ev);

        if(!ev.isCancelled()){
            Explosion explosion = new Explosion(this, (float) ev.getForce(), this);
            if(ev.isBlockBreaking()){
                explosion.explodeA();
            }
            explosion.explodeB();
            this.close();
        }
    }

    public void attackEntity(Entity player){

    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0:
                    return new Item[]{Item.get(Item.FLINT, 0, 1)};
                case 1:
                    return new Item[]{Item.get(Item.GUNPOWDER, 0, 1)};
                case 2:
                    return new Item[]{Item.get(Item.REDSTONE_DUST, 0, 1)};
            }
        }
        return new Item[0];
    }

}