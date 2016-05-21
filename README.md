# PureEntities
  
**[주의]이 플러그인은 개발중입니다. 따라서 비정상적으로 작동할 수 있습니다.**  
  
제작: **[SW-Team](https://github.com/SW-Team)**  
  
PMMP(PHP Server) Version: [PureEntities-PMMP](https://github.com/milk0417/PureEntities)  
(그러나 이 제품은 더이상 지원할 예정이 없습니다)  
  
Sub Module: [EntityManager](https://github.com/SW-Team/EntityManager)  
이 플러그인과 함께 사용하시면 더욱 편합니다.  
(엔티티 자동 제거, 폭발 방지, 드롭 아이템 수정 등등)  
  
PureEntities는 엔티티를 구현시켜주는 플러그인입니다.  
이 플러그인은 간단한 Entity AI를 제공합니다.  
아직까진 많이 미흡할 수 있습니다.  
  
원하시는 내역이나 문제점이 있으면 바로 알려주세요.  
적극 반영하도록 하겠습니다.  
  
### 메소드 목록
  * PureEntities
    * static BaseEntity create(int type, Position pos, Object... args)
    * static BaseEntity create(String type, Position pos, Object... args)
  * BaseEntity
    * boolean isMovement()
    * boolean isFriendly()
    * boolean isWallCheck()
    * void setMovement(boolean value)
    * void setFriendly(boolean value)
    * void setWallCheck(boolean value)
  * Animal
    * boolean isBaby()
  * Monster
    * double getDamage()
    * double getDamage(int difficulty)
    * double getMinDamage()
    * double getMinDamage(int difficulty)
    * double getMaxDamage()
    * double getMaxDamage(int difficulty)
    * void setDamage(double damage)
    * void setDamage(double[] damage)
    * void setDamage(double damage, int difficulty)
  * Zombie
    * boolean isBaby()
  * PigZombie, Wolf, Ocelot
    * boolean isAngry()
    * void setAngry(int angry)

### Entity 사용 예시
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