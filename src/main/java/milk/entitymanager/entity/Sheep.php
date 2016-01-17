package milk.entitymanager.entity;

import cn.nukkit.entity.Colorable;
import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.Creature;

class Sheep extends Animal implements Colorable{
    const NETWORK_ID = 13;

    public width = 1.6;
    public length = 0.8;
    public height = 1.12;

    public function getName(){
        return "Sheep";
    }

    public function initEntity(){
        this.setMaxHealth(8);
        if(isset(this.namedtag.Health)){
            this.setHealth((int) this.namedtag["Health"]);
        }else{
            this.setHealth(this.getMaxHealth());
        }
        super.initEntity();
        this.created = true;
    }

    public function targetOption(Creature creature, distance){
    	if(creature instanceof Player)
        	return creature.spawned && creature.isAlive() && !creature.closed && creature.getInventory().getItemInHand().getId() == Item.SEEDS && distance <= 49;
        return false;
    }

    public function getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            return [
                Item.get(Item.WOOL, Utils.rand(0, 15), 1)
            ];
        }
        return [];
    }

}