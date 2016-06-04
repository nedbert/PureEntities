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
  * Animal
    * `boolean isBaby()`
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
  * Zombie
    * `boolean isBaby()`
  * PigZombie, Wolf, Ocelot
    * `boolean isAngry()`
    * `void setAngry(int angry)`

## Example
``` java
this.getServer().getDefaultLevel().getEntities().forEach((id, baseEntity) -> {
    baseEntity.setWallCheck(false);
    baseEntity.setMovement(!baseEntity.isMovement());

    if(baseEntity instanceof Monster){
        Monster mob = (Monster) baseEntity;

        mob.setDamage(10);

        mob.setMaxDamage(10);
        mob.setMinDamage(10);
    }
});

Zombie arrow = (Zombie) PureEntities.create("Zombie", position);
EntityArrow arrow = (EntityArrow) PureEntities.create("Arrow", position, player, true);
```