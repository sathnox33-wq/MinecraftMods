package com.sath.attributeswap.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface ClientPlayerInteractionManagerInvoker {
	/** Sends the selected hotbar slot to the server right away if it changed. */
	@Invoker("syncSelectedSlot")
	void attributeswap$syncSelectedSlot();
}
