package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.inventory.HairStyleRadialMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;

public class SelectHairstyleAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getValidHairStyles() != null &&
                entity.getValidHairStyles().size() > 1;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        entity.openMenu(new SimpleMenuProvider((id, inv, plr) ->
                new HairStyleRadialMenu(id, inv, null), HairStyleRadialMenu.CONTAINER_TITLE));
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.saveData(tag, entity);
        tag.putInt("HairStyle", ChangedRegistry.HAIR_STYLE.get().getID(entity.getHairStyle()));
    }

    @Override
    public void readData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.readData(tag, entity);
        if (tag.contains("HairStyle"))
            entity.setHairStyle(ChangedRegistry.HAIR_STYLE.get().getValue(tag.getInt("HairStyle")));
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.MENU;
    }
}
