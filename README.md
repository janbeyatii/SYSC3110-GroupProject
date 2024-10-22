# SYSC3110-GroupProject

Welcome to our Scrabble game! This repository contains the progress and implementation of the Scrabble game as described in the project outline. The game allows 2 to 4 players to compete in a traditional game of Scrabble, complete with word validation, tile management, and scoring.

## Table of Contents
- [Project Members](#project-members)
- [Installation](#installation)
- [Rules](#rules)
- [How To Play](#how-to-play)

## Project Members
- **Kaif Ali** - 101180909
- **Milad Zazai** - 101185228
- **Jan Beyati** - 101186335

### Objective
The goal of Scrabble is to create words on the game board using letter tiles and score points. The player with the most points at the end of the game wins!

### Number of Players
2-4 players can play the game. Each player takes turns to place a word on the board, scoring points based on the letters used and their placement.

## Installation

1. **Clone the Repository**: 
   Clone this GitHub repository to your local machine and open it using your IDE.

2. **Build the Project**: 
   Once opened in your IDE, build the project to ensure all dependencies and files are compiled.

3. **Run the Game**: 
   Run the `Main` class to start the game.

## How To Play

1. Select the number of players and give each player a name
   
2. To place a word use the following format (word, row#, col#, direction (using H or V))

## Rules

1. **Player Turns**: 
   The game supports 2 to 4 players. Each player takes turns placing words on the board.

2. **Placing Words**:
   - Words must connect to existing words on the board, either horizontally or vertically.
   - The first word must cross the center of the board (8,8).

3. **Tile Usage**: 
   Players can only use tiles from their current hand of 7 tiles. If a letter is already on the board, it can be reused.

4. **Scoring**: 
   Points are awarded based on the letter values (bonus squares will be availbale in milestone 3).

5. **Winning**: 
   The game ends when a player reaches 150 total points, good luck!
