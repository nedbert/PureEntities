package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;

class MagmaCube extends Monster{
    const NETWORK_ID = 42;

    public width = 1.2;
    public height = 1.2;

    protected speed = 0.8;

    public function initEntity(){
        if(isset(this.namedtag.Health)){
            this.setHealth((int) this.namedtag["Health"]);
        }else{
            this.setHealth(16);
        }
        this.setMinDamage([0, 3, 4, 6]);
        this.setMaxDamage([0, 3, 4, 6]);
        super.initEntity();
        this.created = true;
    }

    public function getName(){
        return "MagmaCube";
    }

    public function attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1){
            this.attackDelay = 0;
            ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, this.getDamage());
            player.attack(ev.getFinalDamage(), ev);
        }
    }

    public function getDrops(){
    	return [];
    }

}
