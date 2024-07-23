package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TransfurProgressOverlay {
    private static final ResourceLocation DANGER_INDICATOR = Changed.modResource("textures/gui/danger.png");

    public static boolean getBlink(float danger, float morph) {
        if (danger < 0.5f)
            return false;
        if (danger < 0.8f)
            return (System.currentTimeMillis() / 250) % 2 == 0;
        else if (morph <= 0.0f)
            return (System.currentTimeMillis() / 100) % 2 == 0;
        else
            return true;
    }

    public static void renderDangerOverlay(Gui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Player player = EntityUtil.playerOrNull(Minecraft.getInstance().getCameraEntity());
        if (player == null) return;

        float dangerLevel = (float)(ProcessTransfur.getPlayerTransfurProgress(player) / ProcessTransfur.getEntityTransfurTolerance(player));
        float coverProgress = 0.0f;
        Color3 morphColor = Color3.WHITE;
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant != null) {
            dangerLevel = 1.0f;
            coverProgress = TransfurAnimator.getCoverProgression(variant.getTransfurProgression(partialTick));
            morphColor = variant.getTransfurColor();

            if (variant.getTransfurProgression(partialTick) >= 1.0f) return;
        }

        if (dangerLevel <= 0.0f && coverProgress <= 0.0f) return;

        dangerLevel = Mth.clamp(dangerLevel, 0.0f, 1.0f);
        coverProgress = Mth.clamp(coverProgress, 0.0f, 1.0f);

        RenderSystem.setShaderTexture(0, DANGER_INDICATOR);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int x = (screenWidth / 2) - 110;
        int y = screenHeight - 40;
        Gui.blit(poseStack, x, y, 0, 0, 8, 32, 48, 32);
        Gui.blit(poseStack, x, y + Mth.ceil((1.0f - dangerLevel) * 32.0f), 8,  Mth.ceil((1.0f - dangerLevel) * 32.0f), 8, Mth.floor(dangerLevel * 32.0f), 48, 32);
        if (coverProgress > 0.0f) {
            RenderSystem.setShaderColor(morphColor.red(), morphColor.green(), morphColor.blue(), 1.0f);
            Gui.blit(poseStack, x - 4, y, 32,  0, 16, Mth.floor(coverProgress * 32.0f), 48, 32);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (getBlink(dangerLevel, coverProgress))
            Gui.blit(poseStack, x - 4, y, 16, 0, 16, 32, 48, 32);
    }
}
