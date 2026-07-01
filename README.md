# TicTacToe

A console-based TicTacToe game developed for CSC214.

## Features

- Human versus human
- Human versus computer
- Computer versus human
- Input validation
- Win and tie detection
- Replay support
- JUnit tests for game logic

## Computer Player Rules

The computer applies its move rules in the required order:

1. Choose the center when appropriate.
2. Make a winning move.
3. Block an opponent's winning move.
4. Choose a random available square.

## Run the Program

From the repository root:

Powershell:
.\gradlew.bat run

Dos CMD prompt:
gradlew.bat run

Linux & Mac:  (only do the chmod the first time)
chmod +x gradlew
./gradlew run


* To run JUnit tests, replace "run" above with "clean test".
