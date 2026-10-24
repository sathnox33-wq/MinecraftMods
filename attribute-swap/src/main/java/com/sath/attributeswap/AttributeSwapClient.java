package com.sath.attributeswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeSwapClient implements ClientModInitializer {
	public static final String MOD_ID = "attributeswap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// GLFW key codes, written out so the mod doesn't need LWJGL at compile time.
	private static final int KEY_O = 79;
	private static final int KEY_J = 74;

	private static KeyBinding menuKey;
	private static KeyBinding toggleKey;

	@Override
	public void onInitializeClient() {
		SwapConfig.get().load();

		KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
		menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.attributeswap.menu", KEY_O, category));
		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.attributeswap.toggle", KEY_J, category));

		ClientTickEvents.END_CLIENT_TICK.register(AttributeSwapClient::onEndTick);
	}

	private static void onEndTick(MinecraftClient client) {
		while (menuKey.wasPressed()) {
			client.setScreen(new AttributeSwapScreen(client.currentScreen));
		}
		while (toggleKey.wasPressed()) {
			SwapConfig config = SwapConfig.get();
			config.enabled = !config.enabled;
			config.save();
			if (client.player != null) {
				client.player.sendMessage(Text.literal("Attribute Swap: " + (config.enabled ? "ON" : "OFF")), true);
			}
		}
	}
}
