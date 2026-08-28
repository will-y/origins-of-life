# Origins of Life Changelog
## 26.2.1.0
- Adds breeding
  - Each creature gets a random "Food" tag that it uses to breed. These are pulled from all vanilla entity's food tags.
  - They are attracted to this item, like vanilla mobs
  - When two creatures breed, the offspring is a combination of the two parents
    - It selects a random model
    - It selects random behaviors from the parents
    - It selects random colors (or averages them, see configs below)
    - It averages attribute values +/- a random percentage
    - It selects random animation data between the parents, with small changes
- Adds 2 new config values:
  - `average_body_color` (false): Average parent's colors when breeding instead of picking one
  - `average_eye_color` (false): Average parent's eye colors when breeding instead of picking one
- Fossil blocks can now be put inside sulfur cubes
  - Causes the same behavior has their non-fossil version

## 26.2.0.6-beta
- Changed cage stack size to 1
- Fixes an issue where display cases would get locked to displaying one entity
- Adds the Magnifying Glass
  - Right-click on creature (or creature in display case) to get stats about them

## 26.2.0.5-beta
- Fixes lighting in display cases
- Fixes creatures despawning
- Fixes DNA Sample -> Fossil recipe not working as intended

## 26.2.0.4-beta
- Adds Mystery Meat
  - Eating raw meat will apply poison and hunger
  - Can be cooked
- Adds DNA Sample and Scalpel
  - DNA Samples are collected by right-clicking a creature with a scalpel
    - This does 0.5 hearts of damage
  - Combine with cobblestone, cobbled deepslate, or blackstone to turn into a fossil

## 26.2.0.3-alpha
- Adds the Display Case!
  - Allows you to display your creatures in a nice little glass box
  - Right-click a filled cage to insert an entity
  - Right-click with an empty cage to retrieve it
  - If you break the Display Case, the entity will spawn where you broke it
- Fixes some misaligned hitboxes

## 26.2.0.2-alpha
- Fossil blocks now mine correctly
  - Pickaxe for deepslate and sulfur
  - Shovel for clay
- Adds more information to the mod screen

## 26.2.0.1-alpha
- Adds some config options
- Some new textures for
  - Cage
  - Primordial Soup
  - Aquatic Creature Spawn Egg

## 26.2.0.0-alpha
- Initial Release