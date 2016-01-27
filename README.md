# EntityManager   
  
Author(제작자): **[SW-Team](https://github.com/SW-Team)**  
  
자매품(PMMP): [EntityManager-PMMP](https://github.com/milk0417/EntityManager)
  
**[NOTICE] This plug-in is not perfect, the entity may move abnormally (It was written in Java8)**
  
EntityManager is a plugin for managing entities, literally.  
Most entities are moving around, and jumps if needed.  
  
EntityManager also has simple API for developers,  
such as **clear()** or **create()**.  
  
See documentation page for details.  
  
**[알림] 이 플러그인은 완벽하지 않으며 엔티티가 비정상적으로 움직일 수 있습니다 (Java8로 작성되었습니다)**  
  
엔티티매니저는 말 그대로 엔티티를 관리하는 플러그인을 의미합니다.  
많은 엔티티들은 주위를 돌아다니거나 뛰어다닙니다.  

엔티티매니저는 또한 개발자 여러분을 위해  
**clear()** 또는 **create()** 와 같은 간단한 API가 제공됩니다.  
  
자세한 사항은 아래를 보시기 바랍니다

### YAML data
  * config.yml
    * entity.maximum: 엔티티의 최대 소환 한계치
    * entity.explode: 모든 폭발장치의 폭발 여부
    * spawn.rand: "min/max" min/max의 확률로 엔티티 소환
    * spawn.tick: 엔티티 소환 속도
    * autospawn.turn-on: 자동 소환 여부
    * autospawn.radius: 자동 소환 반지름(플레이어 기준)
  * spawner.yml
    * TODO
  * drops.yml
    * TODO
  
### Commands(명령어)
  * /entitymanager
    * usage: /entitymanager (check|remove|spawn)
    * permission: entitymanager.command
  * /entitymanager check
    * usage: /entitymanager check (Level="")
    * permission: entitymanager.command.check
    * description: Check the number of entities(If blank, it is set as a default Level)
  * /entitymanager remove
    * usage: /entitymanager remove (Level="")
    * permission: entitymanager.command.remove
    * description: Remove all entities in Level(If blank, it is set as a default Level)
  * /entitymanager spawn:
    * usage: /entitymanager spawn (type) (x="") (y="") (z="") (Level="")
    * permission: entitymanager.command.spawn
    * description: literally(If blank, it is set as a Player)

### Method(메소드)
  * EntityManager
    * public static Map<Integer, BaseEntity> getEntities()
    * public static Map<Integer, BaseEntity> getEntities(Level level)
    * 
    * public static void clear()
    * public static void clear(Class[] type, type)
    * public static void clear(Class[] type, Level level)
    * 
    * public static BaseEntity create(int type, Position pos, Object... args)
    * public static BaseEntity create(String type, Position pos, Object... args)
  * BaseEntity
    * public boolean isCreated()
    * public boolean isMovement()
    * public boolean isWallCheck()
    * public void setMovement(boolean value)
    * public void setWallCheck(boolean value)
  * Monster
    * public double getDamage();
    * public double getDamage(int difficulty)
    * 
    * public void setDamage(double damage)
    * public void setDamage(double[] damage)
    * public void setDamage(double damage, int difficulty)
  * PigZombie
    * public boolean isAngry()
    * public void setAngry(int angry)

### Method Examples(메소드 예시)
``` java
//Entity Method
EntityManager.getEntities().forEach((id, baseEntity) -> {
    if(!baseEntity.isMovement()){
        baseEntity.setMovement(true);
    }
    if(baseEntity instanceof Monster){
        Monster mob = (Monster) baseEntity;

        mob.setDamage(10);

        mob.setMaxDamage(10);
        mob.setMinDamage(10);
    }
});

//Create Entity
Arrow arrow = (Arrow) EntityManager.create("Arrow", position, player, true);
Zombie zombie = (Zombie) EntityManager.create("Zombie", position);

//Remove Entity
EntityManager.clear(new Class[]{BaseEntity.class, Projectile.class, Item.class});
```