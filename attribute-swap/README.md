# Attribute Swap (Fabric 1.21.11, client-side)

One-click attribute swapping between a spear slot and an "other item" slot (for example a mace).

## Install

0. Download the jar: [attributeswap-1.0.0.jar](release/attributeswap-1.0.0.jar?raw=1)
1. Install Fabric Loader for Minecraft 1.21.11 (0.17 or newer).
2. Put these in your `.minecraft/mods` folder:
   - `attributeswap-1.0.0.jar`
   - Fabric API for 1.21.11
   - Mod Menu (optional, adds a config button on the mod's page)
3. Launch the Fabric 1.21.11 profile.

## Controls

| Key | What it does |
| --- | --- |
| **O** | Opens the settings menu |
| **J** | Turns the swap on/off (shows ON/OFF above the hotbar) |

Both can be rebound in Options > Controls > Key Binds > Attribute Swap.

## Settings menu

- **Spear slot**: hotbar slot (1-9) with your spear. Click to cycle.
- **Other item slot**: hotbar slot (1-9) with your mace (or sword/axe). Click to cycle.
- **Mace swap**: while holding the **spear slot**, left-clicking an entity lands the hit with the
  other item and puts the spear straight back in your hand. Clicking air or blocks still does a
  normal spear jab, so Lunge works as usual.
- **Lunge swap**: while holding the **other item slot**, left-click does a spear jab (Lunge dash)
  using the spear slot and swaps straight back. If the spear isn't charged enough to jab yet, the
  click does nothing.

Settings save to `config/attributeswap.properties`.

## How it works

The slot change, the attack and the slot change back are all sent in the same client tick.
The server updates held-item attributes and the attack cooldown on its own tick, so it still
treats the original item as held for those, while the hit itself uses the swapped-in item
(mace smash damage, Density/Breach, or the spear's jab and Lunge).

## Server rules

Many PvP servers ban attribute-swap macros and some anticheats flag same-tick slot swaps.
Check the server's rules before using it there.

## Building from source

Needs JDK 21. Run `./gradlew build` (Windows: `gradlew.bat build`). The jar ends up in `build/libs/`.
