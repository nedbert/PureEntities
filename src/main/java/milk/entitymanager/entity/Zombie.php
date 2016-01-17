package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;

class Zombie extends Monster{
    const NETWORK_ID = 32;

    public width = 0.72;
    public height = 1.8;

    protected speed = 1.1;

    public function initEntity(){
        if(isset(this.namedtag.Health)){
            this.setHealth((int) this.namedtag["Health"]);
        }else{
            this.setHealth(this.getMaxHealth());
        }
        this.setMinDamage([0, 3, 4, 6]);
        this.setMaxDamage([0, 3, 4, 6]);
        super.initEntity();
        this.created = true;
    }

    public function getName(){
        return "Zombie";
    }

    public function attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 2){
            this.attackDelay = 0;
            ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, this.getDamage());
            player.attack(ev.getFinalDamage(), ev);
        }
    }

    public function getDrops(){
        drops = [];
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0:
                    drops[] = Item.get(Item.FEATHER, 0, 1);
                    break;
                case 1:
                    drops[] = Item.get(Item.CARROT, 0, 1);
                    break;
                case 2:
                    drops[] = Item.get(Item.POTATO, 0, 1);
                    break;
            }
        }
        return drops;
    }

}
