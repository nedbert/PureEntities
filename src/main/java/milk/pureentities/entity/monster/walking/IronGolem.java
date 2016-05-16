package milk.pureentities.entity.monster.walking;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.Player;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.monster.WalkingMonster;
import milk.pureentities.util.Utils;

import java.util.HashMap;

public class IronGolem extends WalkingMonster{
    public static final int NETWORK_ID = 20;

    public IronGolem(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
        this.setFriendly(true);
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
    public double getSpeed(){
        return 0.8;
    }

    @Override
    public void initEntity(){
        this.setMaxHealth(100);
        super.initEntity();

        this.setDamage(new int[]{0, 21, 21, 21});
        this.setMinDamage(new int[]{0, 7, 7, 7});
    }

    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 4){
            this.attackDelay = 0;
            HashMap<Integer, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.MODIFIER_BASE, (float) this.getDamage());

            if(player instanceof Player){
                HashMap<Integer, Float> armorValues = new HashMap<Integer, Float>(){{
                    put(Item.LEATHER_CAP, 1f);
                    put(Item.LEATHER_TUNIC, 3f);
                    put(Item.LEATHER_PANTS, 2f);
                    put(Item.LEATHER_BOOTS, 1f);
                    put(Item.CHAIN_HELMET, 1f);
                    put(Item.CHAIN_CHESTPLATE, 5f);
                    put(Item.CHAIN_LEGGINGS, 4f);
                    put(Item.CHAIN_BOOTS, 1f);
                    put(Item.GOLD_HELMET, 1f);
                    put(Item.GOLD_CHESTPLATE, 5f);
                    put(Item.GOLD_LEGGINGS, 3f);
                    put(Item.GOLD_BOOTS, 1f);
                    put(Item.IRON_HELMET, 2f);
                    put(Item.IRON_CHESTPLATE, 6f);
                    put(Item.IRON_LEGGINGS, 5f);
                    put(Item.IRON_BOOTS, 2f);
                    put(Item.DIAMOND_HELMET, 3f);
                    put(Item.DIAMOND_CHESTPLATE, 8f);
                    put(Item.DIAMOND_LEGGINGS, 6f);
                    put(Item.DIAMOND_BOOTS, 3f);
                }};

                float points = 0;
                for(Item i : ((Player) player).getInventory().getArmorContents()){
                    points += armorValues.getOrDefault(i.getId(), 0f);
                }
                damage.put(EntityDamageEvent.MODIFIER_ARMOR, (float) (damage.getOrDefault(EntityDamageEvent.MODIFIER_ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.MODIFIER_BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, damage));
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
