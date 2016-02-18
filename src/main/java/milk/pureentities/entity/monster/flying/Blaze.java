package milk.pureentities.entity.monster.flying;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.PureEntities;
import milk.pureentities.entity.monster.FlyingMonster;
import milk.pureentities.entity.projectile.EntityFireBall;
import milk.pureentities.util.Utils;

public class Blaze extends FlyingMonster{
    public static final int NETWORK_ID = 43;

    public Blaze(FullChunk chunk, CompoundTag nbt){
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
    public float getGravity(){
        return 0.04f;
    }

    @Override
    public double getSpeed(){
        return 1.2;
    }

    public void initEntity(){
        super.initEntity();

        this.fireProof = true;
        this.setDamage(new int[]{0, 0, 0, 0});
    }

    public String getName(){
        return "Blaze";
    }

	public void attackEntity(Entity player){
        if(this.attackDelay > 20 && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 100){
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_FIRE, this.getDamage()));

            double f = 1.2;
            double yaw = this.yaw + Utils.rand(-220, 220) / 10;
            double pitch = this.pitch + Utils.rand(-120, 120) / 10;
            Location pos = new Location(
                this.x - Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * 0.5,
                this.y + this.getHeight() - 0.18,
                this.z + Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * 0.5,
                yaw,
                pitch,
                this.level
            );
            Entity k = PureEntities.create("FireBall", pos, this);
            if(!(k instanceof EntityFireBall)){
                return;
            }

            EntityFireBall fireball = (EntityFireBall) k;
            fireball.setExplode(true);
            fireball.setMotion(new Vector3(
                -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f * f,
                -Math.sin(pitch / 180 * Math.PI) * f * f,
                Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f * f
            ));

            ProjectileLaunchEvent launch = new ProjectileLaunchEvent(fireball);
            this.server.getPluginManager().callEvent(launch);
            if(launch.isCancelled()){
                fireball.kill();
            }else{
                fireball.spawnToAll();
                this.level.addSound(new LaunchSound(this), this.getViewers().values());
            }
        }
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
        	return new Item[]{Item.get(Item.GLOWSTONE_DUST, 0, Utils.rand(0, 2))};
        }
        return new Item[0];
    }

}
