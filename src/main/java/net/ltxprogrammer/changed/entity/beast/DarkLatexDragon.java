package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DarkLatexDragon extends ChangedEntity implements DarkLatexEntity, PatronOC {
    public DarkLatexDragon(EntityType<? extends ChangedEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public boolean isMaskless() {
        return true;
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.dragonLike(attributes);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.DARK_LATEX;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public Color3 getDripColor() {
        return level.random.nextInt(10) > 3 ? Color3.DARK : Color3.GRAY;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.DARK;
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.WHITE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.SHORT_MESSY.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of(HairStyle.SHORT_MESSY.get());
    }
}
