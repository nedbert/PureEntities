/*
 *
 *  ____            _        _   __  __ _                  __  __ ____  
 * |  _ . ___   ___| | _____| |_|  ./  (_)_ __   ___      |  ./  |  _ .
 * | |_) / _ . / __| |/ / _ . __| |./| | | '_ . / _ ._____| |./| | |_) |
 * |  __/ (_) | (__|   <  __/ |_| |  | | | | | |  __/_____| |  | |  __/ 
 * |_|   .___/ .___|_|._.___|.__|_|  |_|_|_| |_|.___|     |_|  |_|_|
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @author cn.nukkit Team
 * @link http://www.cn.nukkit.net/
 * 
 *
*/

package milk.entitymanager.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.CriticalParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.Player;
import cn.nukkit.entity.Projectile;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Explosion;
import cn.nukkit.event.entity.ExplosionPrimeEvent;

class FireBall extends Projectile{
	const NETWORK_ID = 85;

	public width = 0.5;
	public length = 0.5;
	public height = 0.5;

	protected gravity = 0.05;
	protected drag = 0.01;

	protected damage = 4;

	protected isCritical;
	protected canExplode = false;

	public function __construct(FullChunk chunk, CompoundTag nbt, Entity shootingEntity = null, critical = false){
		this.isCritical = (bool) critical;
		super.__construct(chunk, nbt, shootingEntity);
	}
	public function isExplode(){
		return this.canExplode;
	}
	public function setExplode(bool){
		this.canExplode = bool;
	}
	public function onUpdate(currentTick){
		if(this.closed){
			return false;
		}

		this.timings.startTiming();

		hasUpdate = super.onUpdate(currentTick);

		if(!this.hadCollision and this.isCritical){
			this.level.addParticle(new CriticalParticle(this.add(
				this.width / 2 + Utils.rand(-100, 100) / 500,
				this.height / 2 + Utils.rand(-100, 100) / 500,
				this.width / 2 + Utils.rand(-100, 100) / 500)));
		}elseif(this.onGround){
			this.isCritical = false;
		}

		if(this.age > 1200 or this.isCollided){
			if(this.isCollided and this.canExplode){
				this.server.getPluginManager().callEvent(ev = new ExplosionPrimeEvent(this, 2.8));
				if(!ev.isCancelled()){
					explosion = new Explosion ( this, ev.getForce (), this.shootingEntity );
					//if(ev.isBlockBreaking())
					//	explosion.explodeA();
					explosion.explodeB();
				}
			}
			this.kill();
			hasUpdate = true;
		}

		this.timings.stopTiming();

		return hasUpdate;
	}

	public function spawnTo(Player player){
		pk = new AddEntityPacket();
		pk.type = FireBall.NETWORK_ID;
		pk.eid = this.getId();
		pk.x = this.x;
		pk.y = this.y;
		pk.z = this.z;
		pk.speedX = this.motionX;
		pk.speedY = this.motionY;
		pk.speedZ = this.motionZ;
		pk.metadata = this.dataProperties;
		player.dataPacket(pk);

		super.spawnTo(player);
	}
}