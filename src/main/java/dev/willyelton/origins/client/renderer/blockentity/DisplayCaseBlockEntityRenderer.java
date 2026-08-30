package dev.willyelton.origins.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.willyelton.origins.Config;
import dev.willyelton.origins.common.block.entity.DisplayCaseBlockEntity;
import dev.willyelton.origins.common.entity.data.EntityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DisplayCaseBlockEntityRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity, DisplayCaseBlockEntityRenderer.DisplayCaseRenderState> {
    private final EntityRenderDispatcher entityRenderer;

    public DisplayCaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    public DisplayCaseRenderState createRenderState() {
        return new DisplayCaseRenderState();
    }

    @Override
    public void extractRenderState(DisplayCaseBlockEntity blockEntity, DisplayCaseRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        if (blockEntity.getLevel() != null) {
            state.entityData = blockEntity.entityData();
            if (blockEntity.displayEntity() != null) {
                state.displayEntity = entityRenderer.extractEntity(blockEntity.displayEntity(), partialTicks);
                state.displayEntity.lightCoords = state.lightCoords;
            }
            if (Config.DISPLAY_CASE_LOOK_AT_PLAYER.get()) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    double dX = state.blockPos.getX() + 0.5 - player.getX();
                    double dZ = state.blockPos.getZ() + 0.5 - player.getZ();

                    double dXOld = state.blockPos.getX() + 0.5 - player.xOld;
                    double dZOld = state.blockPos.getZ() + 0.5 - player.zOld;

                    state.rotation = (float) Math.atan2(dX, dZ) + Mth.PI;
                    state.lastRotation = (float) Math.atan2(dXOld, dZOld) + Mth.PI;
                }
            } else {
                state.rotation = blockEntity.placedRotation();
                state.lastRotation = blockEntity.placedRotation();
            }
        }

        state.partialTick = partialTicks;
    }

    @Override
    public void submit(DisplayCaseRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.entityData != null && state.displayEntity != null) {
            EntityData.Sizes sizes = state.entityData.sizes();
            int maxSize = Math.max(Math.max(sizes.maxX(), sizes.maxY()), sizes.maxZ());

            float scale = 16.0F / maxSize;
            float rotation;
            if (Mth.abs(state.lastRotation - state.rotation) > 6F) {
                rotation = state.rotation;
            } else {
                rotation = Mth.lerp(state.partialTick, state.lastRotation, state.rotation);
            }

            poseStack.pushPose();
            poseStack.translate(0.5, 0.3, 0.5);

            poseStack.mulPose(Axis.YP.rotation(rotation));
            poseStack.scale(scale, scale, scale);
            poseStack.scale(0.7F, 0.7F, 0.7F);
            entityRenderer.submit(state.displayEntity, camera, 0.0, 0.0, 0.0, poseStack, submitNodeCollector);
            poseStack.popPose();
        }
    }

    public static class DisplayCaseRenderState extends BlockEntityRenderState {
        public @Nullable EntityData entityData;
        public @Nullable EntityRenderState displayEntity;
        public boolean water = false;
        public float rotation;
        public float lastRotation;
        public float partialTick;
    }
}
