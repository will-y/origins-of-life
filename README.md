# Origins of Life
A mod about procedurally generated creatures using the DNA and fossil remains of long ago.

Download on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/origins-of-life)

## Features
### Fossils
Deep underground and underwater, you can find fossilised fragments of creatures from the past.

Fossils are more likely to be found in sulfur caves and lush caves.

![Clay Fossil](https://github.com/will-y/origins-of-life/raw/main/img/fossil_clay.png)
![Sulfur Fossil](https://github.com/will-y/origins-of-life/raw/main/img/fossil_sulfur.png)
![Deepslate Fossil](https://github.com/will-y/origins-of-life/raw/main/img/fossil_deepslate.png)

### Primordial Soup
Deep underground, there are leftover pools of primordial soup, filled with microscopic life.

![Primordial Soup](https://github.com/will-y/origins-of-life/raw/main/img/primordial_soup.png)

### Clean Fossils
Fossils can be cleaned off by right-clicked a cauldron filled with water.

### Regrow the Mobs of the Past
If you throw your cleaned off fossils in a pool of primordial soup, it will eventually transform into a random creature!

### Capture your new creature in a cage!
![Cage Crafting](https://github.com/will-y/origins-of-life/raw/main/img/cage.png)

### Display Case
Displace cases are blocks that you can use to display any creature you want.

To put a creature in the case, just right-click a filled cage on the display case.

To remove a creature, either break the case (this will spawn the entity in the world), or use an empty cage.

![Display Case Crafting](https://github.com/will-y/origins-of-life/raw/main/img/display_case_crafting.png)
![Display Case](https://github.com/will-y/origins-of-life/raw/main/img/display_case.png)

### Cloning
You can clone your creatures!

First craft a scalpel

![Scalpel Crafting](https://github.com/will-y/origins-of-life/raw/main/img/scalpel_crafting.png)

Right-clicking on a creature will deal half a heart of damage to them and give you a DNA Sample.

Combine this DNA sample with cobblestone, cobbled deepslate, or blackstone to get a fossil.

![DNA Fossil Crafting](https://github.com/will-y/origins-of-life/raw/main/img/dna_fossil_crafting.png)

This fossil can then be used to grow an exact copy of your creature in primordial soup.

### Magnifying Glass
Craft a magnifying glass to be able to display stats about your creature. (It works with creatures in display cases as well!)

![Magnifying Glass Crafting](https://github.com/will-y/origins-of-life/raw/main/img/magnifying_glass_crafting.png)
![Magnifying Glass](https://github.com/will-y/origins-of-life/raw/main/img/magnifying_glass.png)

### Breeding
You can breed two aquatic creatures together!

Each creature is assigned a random "food" tag that it uses to breed. The pool that they can select from is all vanilla entity's food tags.
Creatures are attracted to the item in your hand, like vanilla mobs.

The offspring will combine features from their parents in the following ways:
- It selects a random model from a parent
- It selects random behaviors from the parents
- It selects random colors (or averages them, see breeding configs)
- It averages attribute values +/- a random percentage
- It selects random animation data between the parents, with small changes

### Configurations
Some features of how a creature is generated (and a few other aspects of the mod) can be configured:
#### General
- `cage_pickup_other_entities` (false): Allows the player to use the cage to pick up entities not from this mod
- `fossil_transform_ticks` (400): Ticks that it takes to transform a fossil into a creature

#### Creature Generation
- `top_fin_probability` (0.7): Probability that a creature has top fins
- `top_fin_per_segment_probability` (0.9): Probability that a specific body segment has a top fin, given they have any
- `side_fin_probability` (0.6): Probability that a creature has side fins
- `side_fin_per_segment_probability` (0.7): Probability that a specific body segment has side fins, given they have any
- `nose_probability` (0.7): Probability that a creature has a nose
- `tail_probability` (0.4): Probability that a creature has a tail
- `eyes_probability` (0.9): Probability that a creature has eyes
- `one_eye_probability` (0.2): Probability that a creature has only one eye, given it has any
- `body_colors`: Possible Colors for Creature Bodies. Must be an integer in the form AARRGGBB
- `eye_colors`: Possible Colors for Creature Eyes. Must be an integer in the form AARRGGBB

#### Breeding
- `average_body_color` (false): Average parent's colors when breeding instead of picking one
- `average_eye_color` (false): Average parent's eye colors when breeding instead of picking one

## Creature Information
Currently, only aquatic creatures are implemented. This means that every time you spawn a new random creature, it will 
have to live underwater.

### Body
Creatures currently can have up to 10 body segments. These body segments get progressively smaller and all have the same texture.

### Decorations
There are currently a few decorations implemented:
- Eyes
  - Creatures can either have one big eye, or two smaller eyes
- Top Fins
  - These fins go on the top of the body
- Side Fins
  - These are on the side of body segments, and animate when swimming
- Tails
  - These are at the end of the body, and animate up and down for swimming

### Color
Creatures will get a random color for their body, and also a random color for their eyes.

### Animations
Some creatures can have different animations. Currently, this includes:
- Speed of the swim animation
- Direction of the swim animation (either horizontal or vertical)

### Behavior
All creatures have some of their behavior randomly generated. Currently implemented behaviors:
- Player attitude
  - Neural (ignore the player)
  - Aggressive (attack the player)
  - Afraid (run away from the player)

## Creature Gallery
![Creature 1](https://github.com/will-y/origins-of-life/raw/main/img/creatures/creature_1.png)
![Creature 2](https://github.com/will-y/origins-of-life/raw/main/img/creatures/creature_2.png)
![Creature 3](https://github.com/will-y/origins-of-life/raw/main/img/creatures/creature_3.png)
![Creature 4](https://github.com/will-y/origins-of-life/raw/main/img/creatures/creature_4.png)

## Possible Future Items