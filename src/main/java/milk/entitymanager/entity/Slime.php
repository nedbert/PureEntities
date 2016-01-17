package milk.entitymanager.entity;

import cn.nukkit.item.Item;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.entity.Creature;

class Slime extends Animal{
    const NETWORK_ID = 37;

    public width = 1.2;
    public height = 1.2;
    
    protected speed = 0.8;

    public function getName(){
        return "Slime";
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
    	return false;
    }
    
    public function getDrops(){
        drops = [];
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
        	drops[] = Item.get(Item.SLIMEBALL, 0, Utils.rand(0, 2));
        }
        return drops;
    }

}