package milk.entitymanager.entity.monster.flying;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.EntityManager;
import milk.entitymanager.entity.monster.FlyingMonster;
import milk.entitymanager.entity.projectile.EntityFireBall;
import milk.entitymanager.util.Utils;

public class Ghast extends FlyingMonster{
    public static final int NETWORK_ID = 41;

    public Ghast(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 4;
    }

    @Override
    public float getHeight(){
        return 4;
    }

    @Override
    public double getSpeed(){
        return 1.2;
    }

    public void initEntity(){
        super.initEntity();

        this.fireProof = true;
        this.setMaxHealth(10);
        this.setDamage(new int[]{0, 0, 0, 0});
    }

    public String getName(){
        return "Ghast";
    }

	public void attackEntity(Entity player){
        if(this.attackDelay > 30 && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 10000){
            this.attackDelay = 0;

            double f = 2;
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
            Entity k = EntityManager.create("FireBall", pos, this);
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
        return new Item[0];
    }

}
