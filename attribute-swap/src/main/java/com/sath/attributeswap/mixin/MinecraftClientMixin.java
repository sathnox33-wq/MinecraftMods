package com.sath.attributeswap.mixin;

import com.sath.attributeswap.SwapHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow
	protected int attackCooldown;

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void attributeswap$doAttack(CallbackInfoReturnable<Boolean> cir) {
		// attackCooldown is vanilla's short lockout after swinging at air.
		if (this.attackCooldown <= 0 && SwapHandler.handleAttack((MinecraftClient) (Object) this)) {
			cir.setReturnValue(true);
		}
	}
}
