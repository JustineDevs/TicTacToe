# TicTacToe
## UI Flow
| Step | Description |
|------|-------------|
| 1 | Landing Page - Initial screen for the game. |
| 2 | Player Name Input - Enter names for Player X and Player O. |
| 3 | Game Start - Begin the TicTacToe game. |
| 4 | Game Board - The main game interface where players make their moves. |
| 5 | Game Logic - Check for win or draw conditions. |
| 6 | Game Over - Display the winner or draw message. |
| 7 | Admin Page - Access to manage players and view game history. |
| 8 | Player Management - Interface for managing player data. |
| 9 | History Page - View past game results and statistics. |
| 10 | Data Export - Export game data for analysis. |
| 11 | Back to Game - Return to the main game interface. |
| 12 | Quit Game - Exit the application. |
| 13 | Restart Game - Reset the game for a new round. |
| 14 | Profile Management - Manage player profiles and settings. |

A Java Swing-based TicTacToe game with
 a MySQL backend for storing player data, game history, and scores.

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

![1](https://github.com/user-attachments/assets/382f9134-6455-492e-995e-835a1d55cd95)
![2](https://github.com/user-attachments/assets/07423588-0032-42a5-aca3-da47b7355bb0)
![3](https://github.com/user-attachments/assets/2d61a916-ede6-4a83-8343-cf7434d0ea52)
![4](https://github.com/user-attachments/assets/6340b793-20bb-4c5e-9da1-507d35041f49)
![5](https://github.com/user-attachments/assets/b637b5ce-5fd8-4bbc-81bb-728973eb6d21)
![6](https://github.com/user-attachments/assets/4f53dd56-a09c-432a-bd26-236102be97ce)
![7](https://github.com/user-attachments/assets/5ad17bae-e928-4a6c-865e-908a4b1cad43)
![8](https://github.com/user-attachments/assets/00201f3d-90de-4474-91d2-8265103d8f23)
![9](https://github.com/user-attachments/assets/a578f7cc-ab00-4235-8778-dbb602cf4798)
![10](https://github.com/user-attachments/assets/b26ab404-77f1-4afc-b58a-18bfce1c8ad6)
![11](https://github.com/user-attachments/assets/16f5efc7-568a-44a7-8cf4-e687e8247503)
![12](https://github.com/user-attachments/assets/059a1861-bfa1-4dda-a241-e4ec3931fec2)
![13](https://github.com/user-attachments/assets/6b31fe77-c102-49d5-9512-f4bc3198f385)
![14](https://github.com/user-attachments/assets/d8d9f624-0100-43d6-a2ef-114e437c1c4b)