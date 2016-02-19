package milk.pureentities.entity.monster.flying;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
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

    public boolean targetOption(EntityCreature creature, double distance){
        if(creature instanceof Player){
            Player player = (Player) creature;
            return player.spawned && player.isAlive() && !player.closed && player.isSurvival() && distance <= 81;
        }
        return creature.isAlive() && !creature.closed && distance <= 81;
    }

	public void attackEntity(Entity player){
        if(this.attackDelay > 30 && Utils.rand(1, 32) < 4 && this.distance(player) <= 100){
            this.attackDelay = 0;

            double f = 2;
            double yaw = this.yaw + Utils.rand(-220, 220) / 10;
            double pitch = this.pitch + Utils.rand(-120, 120) / 10;
            Location pos = new Location(
                this.x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5,
                this.y + this.getHeight() - 0.18,
                this.z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.5,
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
                -Math.sin(Math.toDegrees(yaw)) * Math.cos(Math.toDegrees(pitch)) * f * f,
                -Math.sin(Math.toDegrees(pitch)) * f * f,
                Math.cos(Math.toDegrees(yaw)) * Math.cos(Math.toDegrees(pitch)) * f * f
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
