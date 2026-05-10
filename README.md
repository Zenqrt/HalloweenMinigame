# Clown Chase
A maze-runner minigame where players must collect the most candies while being chased by their clowns! 
Some candies include special items that you can use on other players or effects to boost yourself.

## Gameplay
[![Watch the video](https://img.youtube.com/vi/I4x3FpFjvuE/hqdefault.jpg)](https://www.youtube.com/embed/I4x3FpFjvuE)

## How to install
1. Download the .jar file from [Releases](https://github.com/Zenqrt/ClownChase/releases) page.
2. Put the .jar file in the `plugins` folder of your server.
3. Start/restart the server.
4. That's all! You can set the lobby by doing `/setworldspawn` in your default world and adding maps in the `plugins/ClownChase/maps` folder (command coming soon).

## Commands
`/clownchase autojoin` - Join the next available game

`/clownchase game create <map> <game_time> <min_players> <max_players>` - Create a game with the specified game settings

`/clownchase game join <game_id>` - Join the game

`/clownchase game info [game_id]` - Get info about the game

`/clownchase game state <next/prev> [game_id]` - Go forward or backward in the game's state sequence

`/clownchase game list` - List all active games

`/clownchase map list` - List all maps

`/clownchase map reload` - Reload maps from config

## Map Creation
To create a map, create a `.json` file in `/plugins/ClownChase/maps`. The name of the file will be the map id.

### JSON Format
#### Example
```json
{
    "display_name": "Prototype",
    "maze_theme": {
        "ground_decoration": {
            "type": "solid",
            "block": "minecraft:black_concrete"
        },
        "wall_decoration": {
            "type": "solid",
            "block": "minecraft:white_concrete",
            "length": 6,
            "width": 4,
            "height": 5
        }
    },
    "biome": "minecraft:plains"
}
```

`display_name` - The display name shown during games

`maze_theme` - The block palette of the maze

`biome` - The biome of the world in game

### Maze Themes
#### "solid"
`block` - The block id
