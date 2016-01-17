package milk.entitymanager.entity;

import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.Creature;

class Chicken extends Animal{
    const NETWORK_ID = 10;

    public width = 0.4;
    public height = 0.75;

    public function getName(){
        return "Chicken";
    }

    public function initEntity(){
        this.setMaxHealth(4);
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
        	return creature.isAlive() && !creature.closed && creature.getInventory().getItemInHand().getId() == Item.SEEDS && distance <= 49;
    	return false;
    }

    public function getDrops(){
        drops = [];
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0 :
                    drops[] = Item.get(Item.RAW_CHICKEN, 0, 1);
                    break;
                case 1 :
                    drops[] = Item.get(Item.EGG, 0, 1);
                    break;
                case 2 :
                    drops[] = Item.get(Item.FEATHER, 0, 1);
                    break;
            }
        }
        return drops;
    }

}