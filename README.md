# Winstreak
Winstreak is a program designed to ensure you, the player, can maintain a win-streak in Hypixel's Bedwars mini-game. 

It allows you to determine whether you should leave the Bedwars lobby or not, and tells you which teams are the most competitive (and should be taken out first).

## Purpose
Often, players are put in a Bedwars lobby with a bunch of tryhards. A tryhard is essentially a player that puts all their effort into the game; in other words, the tryhard tries to end the game as fast as possible by destroying all the enemy beds and killing all players within a certain time.

Yes, Bedwars is designed to be a competitive game. However, there is a fine line between your generic player and the tryhard. Whereas the tryhard tries to win the game as soon as possible, the generic player plays the game to relax and, most importantly, have fun. 

My friends and I have often dealt with tryhards. Speaking from experience, it is not fun being in a game with a tryhard -- or worse, a team of tryhards. Not only does a team of tryhards end a game in less than 5 minutes (an average game lasts around 15-20 minutes), but some tryhards are extremely toxic, which doesn't help at all.

## How Does This Program Work?
Given a screenshot, the program will do the following.
1. Determine what screenshot it will be working with: either an in-game screenshot or a lobby screenshot.
2. Parses the screenshot for names.
3. Grabs relevant data from a public API.
4. Displays them in a way that is easy to understand. 

## Technologies Used
- Java: So I can get some additional practice with the Java programming language. 
- Gradle: To learn more about what Gradle has to offer. 

## What I Learned
- I learned more about the binary system (0101010).
- I learned about `byte` and how it corresponds to an image.
- I learned a bit more about image processing in Java.
- I took the time to learn more about Gradle. 

## Credit
- I came up with the original idea.
- Dakota Frost ([@icicl](https://github.com/icicl/)) implemented the idea first. 
    - He also took the time to explain how his program worked. My program is based on his program, with adjustments made to the codebase so the program accepts more screenshots (Dakota's program hardcodes the values, whereas my program attempts to find them from the screenshot given). 
