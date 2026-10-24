package com.terraformersmc.modmenu.api;

import net.minecraft.client.gui.screen.Screen;

/** Compile-only copy of Mod Menu's API. Not packaged in the jar. */
@FunctionalInterface
public interface ConfigScreenFactory<S extends Screen> {
	S create(Screen parent);
}
