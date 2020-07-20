# Winstreak
Winstreak is a program designed to ensure you, the player, can maintain a win-streak in Hypixel's Bedwars mini-game. It allows you to determine whether you should leave the Bedwars lobby or not, and tells you which teams are the most competitive (and should be taken out first).

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

## Requirements
- Minecraft: Tested with 1.8.x. This should work with the latest version.
    - There should be no major modifications to the client; the Minecraft client you are using should look exactly the same as the default, unmodified client. In other words, no texture packs (especially ones that change the font) and refrain from mods (Optifine should work). Use the Vanilla client. 

## Technologies Used
- Java: So I can get some additional practice with the Java programming language. 
    - Compatible with Java 11+ (Oracle OpenJDK).  
- Gradle: To learn more about what Gradle has to offer. 
    - Compatible with Gradle v6.5.1. 

## Performance
I should mention that this program isn't necessarily designed with performance in mind. Remember, the goal of this application is to work with most, if not all, (clean vanilla) Minecraft settings. This means that the program has to work with any GUI scale and any resolution. Another version of the program, which was implemented by my friend (see Credits), takes less than 30 milliseconds to process the image and about the same time to make API calls; however, his program already knows where to start looking. In other words, his program would break if the GUI scale and/or resolution was changed. 

The performance of this application is dependent on your computer's specifications. In other words, the better your computer, the faster this program should function. The specifications of my laptop are:
- Intel Core i5-8400 CPI @ 2.80 GHz
- 12.0 GB RAM
- Intel UHD Graphics 630
- 1 TB HDD
- 1920 x 1800 Resolution 

The performance statistics are displayed below. I used Minecraft v1.8.9 with Optifine. No additional mods or texture packs were used, and no changes to graphics were made. I processed 20 screenshots, all of which had a player list with at least 14 people. A few outliers were present in the data, which I'll explain more in detail later. 

**Data of All Screenshots (Below)**

| Type | Without Minecraft Open | With Minecraft Open |
|---|---|---|
| Image Processing | ~0.078 S | ~0.115 S |
| API Requests* | ~1.919 S | ~5.071 S |

**With Outliers Removed**

| Type | Without Minecraft Open | With Minecraft Open |
|---|---|---|
| Image Processing | ~0.039 S (-1) | ~0.078 S (-1) |
| API Requests* | ~1.919 S (-0) | ~2.172 S (-2) |

In the case of outliers, the (-#) means the number of entries removed. In my case, outliers are simply values that were much higher or lower than normal. Outliers are rarely seen, which is why it's sensible to remove them for the next data set -- because the first table doesn't remove any outliers, it doesn't accurately reflect what normal gameplay would look like. 

For image processing, the outliers always come from the first screenshot. This could be due to everything needing to be loaded first (average time is usually 0.7 second) ; in future calls, everything the program needs is already cached so it takes less time (average time is usually around 0.02-0.08 second). 

For API requests, it depends solely on your network speed and the response of the website. Sometimes, the website will perform like normal; other times, the website will be extremely slow. 

\* This is dependent on your network connection and how responsive the API is. 

## Credit
- I came up with the original idea.
- Dakota Frost ([@icicl](https://github.com/icicl/)) implemented the idea first. 
    - He also took the time to explain how his program worked. My program is based on his program, with adjustments made to the codebase so the program accepts screenshots of any type (Dakota's program hardcodes the values, whereas my program attempts to find them from the screenshot given). 
