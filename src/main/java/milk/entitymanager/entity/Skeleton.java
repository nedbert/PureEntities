package milk.entitymanager.entity;

import cn.nukkit.entity.Arrow;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Projectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.FloatTag;
import milk.entitymanager.util.Utils;

public class Skeleton extends Monster{
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

    protected void initEntity(){
        super.initEntity();

    }

    public String getName(){
        return "Skeleton";
    }

    public void attackEntity(Entity player){
        if(this.attackDelay > 30 && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 55){
            this.attackDelay = 0;
        
            double f = 1.2;
            double yaw = this.yaw + Utils.rand(-220, 220) / 10;
            double pitch = this.pitch + Utils.rand(-120, 120) / 10;
            CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                    .add(new DoubleTag("", this.x + (-Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * 0.5)))
                    .add(new DoubleTag("", this.getEyeHeight()))
                    .add(new DoubleTag("", this.z +(Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * 0.5))))
                .putList(new ListTag<DoubleTag>("Motion")
                    .add(new DoubleTag("", -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f))
                    .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI) * f))
                    .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f)))
                .putList(new ListTag<FloatTag>("Rotation")
                    .add(new FloatTag("", (float) yaw))
                    .add(new FloatTag("", (float) pitch)));

            Arrow arrow = (Arrow) Entity.createEntity("Arrow", this.chunk, nbt, this);
            EntityShootBowEvent ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), arrow, f);

            this.server.getPluginManager().callEvent(ev);

            Projectile projectile = ev.getProjectile();
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
    public boolean entityBaseTick(int tickDiff){
        //Timings.timerEntityBaseTick.startTiming();

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        int time = this.getLevel().getTime() % Level.TIME_FULL;
        if(time < Level.TIME_NIGHT || time > Level.TIME_SUNRISE){
            this.setOnFire(5);
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
