package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.entity.ProjectileSource;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.entity.Projectile;

class Ghast extends FlyMonster implements ProjectileSource{
    const NETWORK_ID = 41;

    public width = 4;
    public height = 4;

    protected speed = 1.2;

    public function initEntity(){
        if(isset(this.namedtag.Health)){
            this.setHealth((int) this.namedtag["Health"]);
        }else{
            this.setHealth(this.getMaxHealth());
        }
        this.setMinDamage([0, 4, 6, 9]);
        this.setMaxDamage([0, 4, 6, 9]);
        super.initEntity();
        this.created = true;
    }

    public function getName(){
        return "Ghast";
    }

	public function attackEntity(Entity player){
        if(this.attackDelay > 30 && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 200){
            this.attackDelay = 0;
        
            f = 2;
            yaw = this.yaw + Utils.rand(-220, 220) / 10;
            pitch = this.pitch + Utils.rand(-120, 120) / 10;
            nbt = new CompoundTag("", [
                "Pos" => new ListTag("Pos", [
                    new DoubleTag("", this.x + (-sin(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI) * 2)),
                    new DoubleTag("", this.y + 2),
                    new DoubleTag("", this.z +(cos(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI) * 2))
                ]),
                "Motion" => new ListTag("Motion", [
                    new DoubleTag("", -sin(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI)),
                    new DoubleTag("", -sin(pitch / 180 * M_PI) * f),
                    new DoubleTag("", cos(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI))
                ]),
                "Rotation" => new ListTag("Rotation", [
                    new FloatTag("", yaw),
                    new FloatTag("", pitch)
                ]),
            ]);

            /** @var Projectile fireball */
            fireball = Entity.createEntity("FireBall", this.chunk, nbt, this);
            if(fireball instanceof FireBall)
            	fireball.setExplode(true);
            fireball.setMotion ( fireball.getMotion ().multiply ( f ) );
            ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), fireball, f);

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
        }
    }

    public function getDrops(){
        return [];
    }

}
