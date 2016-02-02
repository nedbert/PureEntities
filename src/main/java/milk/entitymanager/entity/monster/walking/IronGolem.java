package milk.entitymanager.entity.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.Player;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.entitymanager.util.Utils;

public class IronGolem extends WalkingMonster{
    public static final int NETWORK_ID = 20;

    public IronGolem(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 1.9f;
    }

    @Override
    public float getHeight(){
        return 2.1f;
    }

    @Override
    public void initEntity(){
        this.setMaxHealth(100);
        super.initEntity();

        this.setFriendly(true);

        this.setDamage(new int[]{0, 21, 21, 21});
        this.setMinDamage(new int[]{0, 7, 7, 7});
    }

    public String getName(){
        return "IronGolem";
    }

    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 4){
            this.attackDelay = 0;

            EntityDamageEvent ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, this.getDamage());
            player.attack(ev);
            player.setMotion(new Vector3(0, 0.7, 0));
        }
    }

    public boolean targetOption(EntityCreature creature, double distance){
        return !(creature instanceof Player) && creature.isAlive() && distance <= 60;
    }

    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            switch(Utils.rand(0, 2)){
                case 0:
                    return new Item[]{Item.get(Item.FEATHER, 0, 1)};
                case 1:
                    return new Item[]{Item.get(Item.CARROT, 0, 1)};
                case 2:
                    return new Item[]{Item.get(Item.POTATO, 0, 1)};
            }
        }
        return new Item[0];
    }

}
