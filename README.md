# TicTacToe

A Java Swing-based TicTacToe game with a MySQL backend for storing player data, game history, and scores.

## Table of Contents
- [Overview](#overview)
- [Key Features](#key-features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Overview
This project is a TicTacToe game built using Java Swing. It allows two players to play the game, tracks game history, and stores player statistics in a MySQL database.

## Key Features
- **User Interface**: Built with Java Swing, providing a clean and intuitive interface.
- **Player Management**: Players can enter their names, and the system tracks their game history and scores.
- **Game Logic**: Implements the classic TicTacToe game logic, including win and draw conditions.
- **Database Integration**: Uses MySQL to store player data, game history, and scores.
- **Admin Page**: Provides an admin interface for managing players and viewing game history.

## Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven
- MySQL Server

## Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/JustineDevs/TicTacToe.git
   cd TicTacToe
   ```

2. **Install Dependencies**:
   Ensure you have Maven installed. Run the following command to install the project dependencies:
   ```bash
   mvn install
   ```

## Database Setup
1. **Install MySQL**:
   - Download and install MySQL from [MySQL's official website](https://dev.mysql.com/downloads/).
   - Follow the installation instructions for your operating system.

2. **Create the Database**:
   - Open your MySQL client and run the following command to create the database:
     ```sql
     CREATE DATABASE tictactoe_db;
     ```

3. **Import the Schema**:
   - Use the provided SQL script to set up the database schema:
     ```bash
     mysql -u root -p tictactoe_db < tictactoe_db.sql
     ```

## Running the Application
1. **Run the Application**:
   - Use Maven to run the application:
     ```bash
     mvn exec:java
     ```

2. **Access the Game**:
   - The application will open, allowing you to start playing TicTacToe.

## Project Structure
- **src/main/java/com/tictactoe/**: Contains the main Java classes for the application.
  - **TicTacToe.java**: Main entry point.
  - **TicTacToePage.java**: Landing page for the game.
  - **GamePage.java**: Main game logic and UI.
  - **AdminPage.java**: Admin interface for managing players and viewing history.
  - **HistoryPage.java**: Displays game history.

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is licensed under the MIT License - see the LICENSE file for details. 