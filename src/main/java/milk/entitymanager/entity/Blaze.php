package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.entity.ProjectileSource;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.FloatTag;

class Blaze extends FlyMonster implements ProjectileSource{
    const NETWORK_ID = 43;

    public width = 0.72;
    public height = 1.8;

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
        return "Blaze";
    }

	public function attackEntity(Entity player){
        if(this.attackDelay > 20 && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 100){
            this.attackDelay = 0;
        
            f = 1.2;
            yaw = this.yaw + Utils.rand(-220, 220) / 10;
            pitch = this.pitch + Utils.rand(-120, 120) / 10;
            nbt = new CompoundTag("", [
                "Pos" => new ListTag("Pos", [
                    new DoubleTag("", this.x + (-sin(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI) * 0.5)),
                    new DoubleTag("", this.y + 1.62),
                    new DoubleTag("", this.z +(cos(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI) * 0.5))
                ]),
                "Motion" => new ListTag("Motion", [
                    new DoubleTag("", -sin(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI) * f),
                    new DoubleTag("", -sin(pitch / 180 * M_PI) * f),
                    new DoubleTag("", cos(yaw / 180 * M_PI) * cos(pitch / 180 * M_PI) * f)
                ]),
                "Rotation" => new ListTag("Rotation", [
                    new FloatTag("", yaw),
                    new FloatTag("", pitch)
                ]),
            ]);

            /** @var Projectile fireball */
            fireball = Entity.createEntity("FireBall", this.chunk, nbt, this);
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
        drops = [];
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
        	drops[] = Item.get(Item.GLOWSTONE_DUST, 0, Utils.rand(0, 2));
        }
        return drops;
    }

}
