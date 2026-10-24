package com.terraformersmc.modmenu.api;

/** Compile-only copy of Mod Menu's API so the build doesn't need Mod Menu. Not packaged in the jar. */
public interface ModMenuApi {
	default ConfigScreenFactory<?> getModConfigScreenFactory() {
		return null;
	}
}
