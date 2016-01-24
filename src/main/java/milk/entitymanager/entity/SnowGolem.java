package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Projectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.Player;
import cn.nukkit.entity.Creature;
import milk.entitymanager.util.Utils;

public class SnowGolem extends Monster{
    public static final int NETWORK_ID = 21;

    public SnowGolem(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.65f;
    }

    @Override
    public float getHeight() {
        return 2.1f;
    }

    @Override
    public float getEyeHeight() {
        return 1.92f;
    }

    public void initEntity(){
        super.initEntity();

        this.setFriendly(true);
        this.created = true;
    }

    public String getName(){
        return "SnowGolem";
    }

    public void attackEntity(Entity player){
        /*if(this.attackDelay > 23  && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 55){
            this.attackDelay = 0;
        
            double f = 1.2;
            yaw = this.yaw + Utils.rand(-220, 220) / 10;
            pitch = this.pitch + Utils.rand(-120, 120) / 10;
            nbt = new CompoundTag("", [
                "Pos" => new ListTag("Pos", [
                    new DoubleTag("", this.x + (-sin(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI) * 0.5)),
                    new DoubleTag("", this.y + 1),
                    new DoubleTag("", this.z +(cos(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI) * 0.5))
                ]),
                "Motion" => new ListTag("Motion", [
                    new DoubleTag("", -sin(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI)),
                    new DoubleTag("", -sin(pitch / 180 * M_PI)),
                    new DoubleTag("", cos(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI))
                ]),
                "Rotation" => new ListTag("Rotation", [
                    new FloatTag("", yaw),
                    new FloatTag("", pitch)
                ]),
            ]);

            snowball = Entity.createEntity("Snowball", this.chunk, nbt, this);
            snowball.setMotion ( snowball.getMotion ().multiply ( f ) );

            property = (new .ReflectionClass ( snowball )).getProperty ( "damage" );
            property.setAccessible(true);
            property.setValue ( snowball, 2 );
            
            ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), snowball, f);

            this.server.getPluginManager().callEvent(ev);

            projectile = ev.getProjectile();
            if(ev.isCancelled()){
                projectile.kill();
            }elseif(projectile instanceof Projectile){
                this.server.getPluginManager().callEvent(launch = new ProjectileLaunchEvent(projectile));
                if(launch.isCancelled()){
                    projectile.kill();
                }else{
                    projectile.spawnToAll();
                    this.level.addSound(new LaunchSound(this), this.getViewers());
                }
            }
        }*/
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            return new Item[]{Item.get(Item.SNOWBALL, 0, 15)};
        }
        return new Item[0];
    }
    
    public boolean targetOption(Creature creature, double distance){
        return !(creature instanceof Player) && creature.isAlive() && distance <= 60;
    }
}
