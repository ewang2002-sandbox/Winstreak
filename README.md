# Winstreak
Winstreak is a program designed to ensure you, the player, can maintain a winstreak in Hypixel's Bedwars mini-game.

## Purpose
Often times, players are put in a Bedwars lobby with a bunch of tryhards. A tryhard is essentially a player that puts all their effort into the game; in other words, the tryhard tries to end the game as fast as possible by destroying all the enemy beds and killing all players within a certain time period.

Yes, Bedwars is designed to be a competitive game. However, there is a fine line between your generic player and the tryhard. Whereas the tryhard tries to win the game as soon as possible, the generic player plays the game to relax. 

My friends and I have often dealt with tryhards. Speaking from experience, it is not fun being in a game with a tryhard -- or worse, a team of tryhards. 

## How Does This Program Work?
Given a screenshot, the program will parse all the names found in the player list (the list that shows up when you press `[TAB]`). After the names are parsed, the following happens.
1. The program sends a request to a public Hypixel API, essentially asking the API for each player's (in the list) stats. 
2. The program will then use the data it receives from the API to determine if a lobby has any tryhards.
3. If there is, the program will warn you. Use this opportunity to `/hub` and get into a new lobby.

## What I Learned
- I learned more about the binary system (0101010).
- I learned about `byte` and how it corresponds to an image.
- I learned a bit more about image processing in Java.
- I took the time to learn more about Gradle. 

## Credit
- I came up with the original idea.
- Dakota Frost ([@icicl](https://github.com/icicl/)) implemented the idea first. 
    - He also took the time to explain how his program worked. My program is based off his program, with adjustments made to the codebase so the program accepts more screenshots (Dakota's program hardcodes the values, whereas my program attempts to find them from the screenshot given). 