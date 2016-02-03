package milk.entitymanager.entity.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBow;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import milk.entitymanager.EntityManager;
import milk.entitymanager.entity.monster.WalkingMonster;
import milk.entitymanager.util.Utils;

public class Skeleton extends WalkingMonster{
    public static final int NETWORK_ID = 34;

    public Skeleton(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.65f;
    }

    @Override
    public float getHeight(){
        return 1.8f;
    }

    @Override
    public float getEyeHeight(){
        return 1.62f;
    }

    @Override
    public String getName(){
        return "Skeleton";
    }

    public void attackEntity(Entity player){
        if(this.attackDelay > 30 && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 55){
            this.attackDelay = 0;
        
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
            Entity k = EntityManager.create("EntityArrow", pos, this);
            if(!(k instanceof EntityArrow)){
                return;
            }

            EntityArrow arrow = (EntityArrow) k;
            arrow.setMotion(new Vector3(
                -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f * f,
                -Math.sin(pitch / 180 * Math.PI) * f * f,
                Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f * f
            ));

            EntityShootBowEvent ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), arrow, f);
            this.server.getPluginManager().callEvent(ev);

            EntityProjectile projectile = ev.getProjectile();
            if(ev.isCancelled()){
                projectile.kill();
            }else{
                ProjectileLaunchEvent launch = new ProjectileLaunchEvent(projectile);
                this.server.getPluginManager().callEvent(launch);
                if(launch.isCancelled()){
                    projectile.kill();
                }else{
                    projectile.spawnToAll();
                    this.level.addSound(new LaunchSound(this), this.getViewers().values());
                }
            }
        }
    }

    @Override
    public void spawnTo(Player player){
        super.spawnTo(player);

        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getId();
        pk.item = new ItemBow();
        pk.slot = 10;
        pk.selectedSlot = 10;
        player.dataPacket(pk);
    }

    @Override
    public boolean entityBaseTick(int tickDiff){
        //Timings.timerEntityBaseTick.startTiming();

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        int time = this.getLevel().getTime() % Level.TIME_FULL;
        if(
            !this.isOnFire()
            && !this.level.isRaining()
            && (time < Level.TIME_NIGHT || time > Level.TIME_SUNRISE)
        ){
            this.setOnFire(100);
        }

        //Timings.timerEntityBaseTick.stopTiming();
        return hasUpdate;
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            return new Item[]{
                Item.get(Item.BONE, 0, Utils.rand(0, 2)),
                Item.get(Item.ARROW, 0, Utils.rand(0, 3)),
            };
        }
        return new Item[0];
    }

}
