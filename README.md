# PureEntities
Development: **[SW-Team](https://github.com/SW-Team)**

PureEntities is a Plug-in that makes implement the entity.

This Plug-in provides a simple Entity AI.

## Notice
#### Welcome Github issue!

This plug-in is in development. Therefore, It is possible to function abnormally.

#### About PMMP
PocketMine-MP Version : [PureEntities-PMMP](https://github.com/milk0417/PureEntities)  
(However, This project was deprecated.)


## Sub Module
[EntityManager](https://github.com/SW-Team/EntityManager)  

## Method list
  * PureEntities
    * `static BaseEntity create(int type, Position pos, Object... args)`
    * `static BaseEntity create(String type, Position pos, Object... args)`
  * BaseEntity
    * `boolean isMovement()`
    * `boolean isFriendly()`
    * `boolean isWallCheck()`
    * `void setMovement(boolean value)`
    * `void setFriendly(boolean value)`
    * `void setWallCheck(boolean value)`
  * Monster
    * `double getDamage()`
    * `double getDamage(int difficulty)`
    * `double getMinDamage()`
    * `double getMinDamage(int difficulty)`
    * `double getMaxDamage()`
    * `double getMaxDamage(int difficulty)`
    * `void setDamage(double damage)`
    * `void setDamage(double[] damage)`
    * `void setDamage(double damage, int difficulty)`
  * Animal, Zombie
    * `boolean isBaby()`
  * PigZombie, Wolf, Ocelot
    * `boolean isAngry()`
    * `void setAngry(int angry)`

## Example
``` java
Server.getInstance().getDefaultLevel().getEntities().forEach((id, entity) -> {
    entity.setWallCheck(false);
    entity.setMovement(!entity.isMovement());

    if(entity instanceof Monster){
        Monster mob = (Monster) entity;

        mob.setDamage(10);

        mob.setMaxDamage(10);
        mob.setMinDamage(10);
    }
});

Zombie zombie = (Zombie) PureEntities.create("Zombie", position);
if(zombie != null){
    zombie.spawnToAll(); //if you don't use this method, you couldn't see this
}

EntityArrow arrow = (EntityArrow) PureEntities.create("Arrow", position, player, true);
if(arrow != null){
    arrow.spawnToAll();
}
```
