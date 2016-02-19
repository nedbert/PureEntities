# PureEntities
  
Author(제작자): **[SW-Team](https://github.com/SW-Team)**  
  
자매품(PMMP): [PureEntities-PHP](https://github.com/milk0417/PureEntities)  
추가 플러그인(Sub Module): [EntityManager](https://github.com/SW-Team/EntityManager)  
  
**[NOTICE] This plug-in is not perfect, the entity may move abnormally (It was written in Java8)**
  
PureEntities is a plugin for implement entities.  
The plugin provides Mob AIs like walking, auto-jumping, etc.  
  
PureEntities also has simple API for developers, such as **isMovement()** or **isWallCheck()**.  
See documentation page for details.  
  
**[알림] 이 플러그인은 완벽하지 않으며 엔티티가 비정상적으로 움직일 수 있습니다 (Java8로 작성되었습니다)**  
  
PureEntities는 엔티티를 구현시켜주는 플러그인입니다.  
이 플러그인은 MobAI(움직임, 자동점프 및 기타 등등)을 제공합니다.  
  
PureEntities는 또한 개발자 여러분을 위해 **isMovement()** 또는 **isWallCheck()** 와 같은 간단한 API가 제공됩니다.  
자세한 사항은 아래를 보시기 바랍니다  
  
### Methods(메소드)
  * PureEntities
    * public static BaseEntity create(int type, Position pos, Object... args)
    * public static BaseEntity create(String type, Position pos, Object... args)
  * BaseEntity
    * public boolean isMovement()
    * public boolean isFriendly()
    * public boolean isWallCheck()
    * 
    * public void setMovement(boolean value)
    * public void setFriendly(boolean value)
    * public void setWallCheck(boolean value)
  * Animal
    * public boolean isBaby()
  * Monster
    * public double getDamage()
    * public double getDamage(int difficulty)
    * 
    * public double getMinDamage()
    * public double getMinDamage(int difficulty)
    * 
    * public double getMaxDamage()
    * public double getMaxDamage(int difficulty)
    * 
    * public void setDamage(double damage)
    * public void setDamage(double[] damage)
    * public void setDamage(double damage, int difficulty)
  * PigZombie, Wolf, Ocelot
    * public boolean isAngry()
    * public void setAngry(int angry)

### API Examples(API 예시)
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

EntityArrow arrow = (EntityArrow) PureEntities.create("Arrow", position, player, true);
Zombie arrow = (Zombie) PureEntities.create("Zombie", position);
```