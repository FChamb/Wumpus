CS1006 Practical 3 - Hunt the Wumpus

Description:
This program contains a multitude of classes which work together to create a cave object that follows the
rules and specifications of Hunt the Wumpus. The user is given full control of the layout of the cave down
to the number of obstacles included. On top of this, a Screen Manager simplified the functionality of the
program and provides a retro feel to the game.


Usage:
1. Go to the lab pc
2. Open terminal and make sure it is full screen
3. cd into Wumpus/src directory
4. Clear terminal by running 'clear'

Run the following commands in order to operate game (with GUI) :
1. javac @files
2. java HuntTheWumpus.java

Run the following commands in order to operate game (without GUI) :
1. javac @files
2. java game/TextGame.java

The AI only plays the text based version of the game. To get the AI to play, run the above commands for
the text-based game and then, when prompted for a player name enter the name 'AI' (or 'ai') and the AI
will take over the game play

Keyboard Inputs:
N - new game
Q - quit game
Y/N - yes/no
?/h - help
M {direction} - move in a given direction
S {direction} - shoot in a given direction