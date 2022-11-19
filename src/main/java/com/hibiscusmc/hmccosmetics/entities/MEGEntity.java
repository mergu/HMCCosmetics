package com.hibiscusmc.hmccosmetics.entities;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.generator.Hitbox;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.IModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.impl.DefaultBodyRotationController;
import com.ticxo.modelengine.api.nms.entity.impl.EmptyRangeManager;
import com.ticxo.modelengine.api.nms.entity.impl.ManualRangeManager;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import com.ticxo.modelengine.api.nms.entity.wrapper.RangeManager;
import com.ticxo.modelengine.api.nms.world.IDamageSource;
import com.ticxo.modelengine.api.utils.data.EntityData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MEGEntity implements BaseEntity {
    private final UUID uuid;
    private final int entityId;
    private Vector velocity = new Vector(0, 0, 0);
    private Location location;
    private boolean alive;
    private BodyRotationController rotationController;
    private List<Entity> passengers;
    private RangeManager rangeManager;

    public MEGEntity(final UUID uuid, final int entityId, final Vector velocity, final Location location, final boolean alive) {
        this.uuid = uuid;
        this.entityId = entityId;
        this.velocity = velocity;
        this.location = location;
        this.alive = alive;
        this.rotationController = new DefaultBodyRotationController(this);
        this.passengers = new ArrayList<>();
        //this.rangeManager = new EmptyRangeManager();
        //this.rangeManager.setRenderDistance(32);
    }

    public MEGEntity(final UUID uuid, final int entityId) {
        this.uuid = uuid;
        this.entityId = entityId;
        this.alive = false;
    }

    public void update(BalloonEntity entity) {
        this.velocity = entity.getLocation().toVector();
        this.location = entity.getLocation();
        this.alive = entity.isAlive();
    }

    public void spawnModel(final String id) {
        if (ModelEngineAPI.api.getModelRegistry().getBlueprint(id) == null) {
            return;
        }
        final ActiveModel model = ModelEngineAPI.api.createActiveModelImpl(ModelEngineAPI.getBlueprint(id));
        ModeledEntity modeledEntity = ModelEngineAPI.api.createModeledEntityImpl(this);
        modeledEntity.addModel(model, false);
    }

    public void remove() {
        final ModeledEntity entity = ModelEngineAPI.api.getModeledEntity(getUniqueId());

        if (entity == null) return;

        for (final Player player : entity.getRangeManager().getPlayerInRange()) {
            entity.hideFromPlayer(player);
        }

        //ModelEngineAPI.removeModeledEntity(megEntity.getUniqueId());
        entity.destroy();
    }

    public void addPlayerToModel(final Player player, final String id) {
        final ModeledEntity model = ModelEngineAPI.api.getModeledEntity(getUniqueId());
        if (model == null) {
            this.spawnModel(id);
            return;
        }
        if (getRangeManager().getPlayerInRange().contains(player)) return;
        model.showToPlayer(player);
    }

    public void removePlayerFromModel(final Player player) {
        final ModeledEntity model = ModelEngineAPI.api.getModeledEntity(getUniqueId());

        if (model == null) return;

        model.hideFromPlayer(player);
    }

    public void setVelocity(final Vector velocity) {
        this.velocity = velocity;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public void setAlive(final boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public EntityData getEntityData() {
        return entityData;
    }

    final EntityData entityData = new EntityData();

    @Override
    public Object getOriginal() {
        return null;
    }

    @Override
    public MoveController wrapMoveControl() {
        return null;
    }

    @Override
    public LookController wrapLookControl() {
        return null;
    }

    @Override
    public BodyRotationController wrapBodyRotationControl() {
        return this.rotationController;
    }

    @Override
    public void wrapNavigation() {

    }

    @Override
    public RangeManager wrapRangeManager(IModel model) {
        return new ManualRangeManager(this, model);
    }

    @Override
    public RangeManager getRangeManager() {
        if (this.rangeManager == null) {
            RangeManager rangeMan = new EmptyRangeManager();
            rangeMan.setRenderDistance(16);
            return rangeMan;
        }
        return this.rangeManager;
    }

    @Override
    public boolean onHurt(IDamageSource damageSource, float damage) {
        return false;
    }

    @Override
    public void onInteract(Player player, EquipmentSlot hand) {

    }

    @Override
    public void setHitbox(Hitbox hitbox) {

    }

    @Override
    public Hitbox getHitbox() {
        return null;
    }

    @Override
    public void setStepHeight(double height) {

    }

    @Override
    public Double getStepHeight() {
        return null;
    }

    @Override
    public void setCollidableToLiving(LivingEntity living, boolean isRemove) {

    }

    @Override
    public void broadcastSpawnPacket() {

    }

    @Override
    public void broadcastDespawnPacket() {

    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public World getWorld() {
        return this.getWorld();
    }

    @Override
    public boolean isDead() {
        return alive;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public boolean isOnGround() {
        return false;
    }

    @Override
    public boolean isMoving() {
        return false;
    }

    @Override
    public void setYHeadRot(float rot) {

    }

    @Override
    public float getYHeadRot() {
        return 0;
    }

    @Override
    public float getXHeadRot() {
        return 0;
    }

    @Override
    public void setYBodyRot(float rot) {
        location.setYaw(rot);
    }

    @Override
    public float getYBodyRot() {
        return location.getYaw();
    }

    @Override
    public List<Entity> getPassengers() {
        return this.passengers;
    }

    public Vector getVelocity() {
        return velocity;
    }
}