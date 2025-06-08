/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.tictactoe;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
/**
 *
 * @author TraderG
 */
public class GamePage extends javax.swing.JFrame {
    private JButton[] buttons; // Array to hold the 3x3 grid buttons
    private char currentPlayer; // 'X' or 'O'
    private char[][] board; // 3x3 game board
    private int moveCount; // Track number of moves for draw condition
    private boolean gameEnded; // Track if game has ended
    private ImageIcon xIcon; // Icon for 'X'
    private ImageIcon oIcon; // Icon for 'O'
    private static String playerXName;
    private static String playerOName;
    private static int playerXId;
    private static int playerOId;
    private String moves = ""; // Track moves (e.g., "X1,O2,X3,...")

    private static final String DB_URL = "jdbc:mysql://localhost:3306/tictactoe_db?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin123";

    public static void setPlayerData(String xName, String oName, int xId, int oId) {
        playerXName = xName;
        playerOName = oName;
        playerXId = xId;
        playerOId = oId;
    }
    /**
     * Creates new form GamePage1
     */
    public GamePage() {
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException ex) {
        JOptionPane.showMessageDialog(this, "MySQL driver not found: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
    initComponents();
    initializeGame();
    }
    private void initializeGame() {
    buttons = new JButton[]{btngrid1, btngrid2, btngrid3, btngrid4, btngrid5, 
                           btngrid6, btngrid7, btngrid8, btngrid9};
    board = new char[3][3];
    currentPlayer = 'X';
    moveCount = 0;
    gameEnded = false;
    moves = ""; // Reset moves

    // Load icons with error handling
    try {
        xIcon = new ImageIcon("C:\\Users\\TraderG\\Downloads\\PROJECTS\\TicTacToe\\src\\main\\java\\com\\tictactoe\\icons\\x_120.png");
        oIcon = new ImageIcon("C:\\Users\\TraderG\\Downloads\\PROJECTS\\TicTacToe\\src\\main\\java\\com\\tictactoe\\icons\\o_120.png");
        if (xIcon.getIconWidth() == -1 || oIcon.getIconWidth() == -1) {
            System.err.println("Failed to load one or more images.");
        }
    } catch (Exception e) {
        System.err.println("Error loading images: " + e.getMessage());
    }

    // Initialize board and buttons
    for (int i = 0; i < 9; i++) {
        buttons[i].setIcon(null);
        buttons[i].setEnabled(true);
        buttons[i].setBackground(Color.BLACK);
        buttons[i].setContentAreaFilled(false); // Make button transparent
        buttons[i].setBorderPainted(false); // Remove border
        final int index = i;
        buttons[i].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonClick(index);
            }
        });
    }

    // Set player names and initial turn
    playername1.setText(playerXName != null ? playerXName : "Player 1");
    playername2.setText(playerOName != null ? playerOName : "Player 2");
    lblplayerorwin.setText((playerXName != null ? playerXName : "Player X") + "'s Turn");
}

private void handleButtonClick(int index) {
    if (gameEnded || !buttons[index].isEnabled()) {
        return;
    }

    int row = index / 3;
    int col = index % 3;

    board[row][col] = currentPlayer;
    buttons[index].setIcon(currentPlayer == 'X' ? xIcon : oIcon);
    buttons[index].setDisabledIcon(currentPlayer == 'X' ? xIcon : oIcon); // Preserve color
    buttons[index].setEnabled(false);
    moveCount++;
    moves += currentPlayer + (index + 1) + ","; // e.g., "X1,O2,"

    if (checkWinner()) {
        String winnerName = currentPlayer == 'X' ? playerXName : playerOName;
        lblplayerorwin.setText("Winner: " + (winnerName != null ? winnerName : "Player " + currentPlayer));
        gameEnded = true;
        disableAllButtons();
        saveGame(currentPlayer);
        JOptionPane.showMessageDialog(this, "Winner: " + (winnerName != null ? winnerName : "Player " + currentPlayer), "Game Over", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    if (moveCount == 9) {
        lblplayerorwin.setText("Draw");
        gameEnded = true;
        saveGame(' ');
        JOptionPane.showMessageDialog(this, "Draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    String currentPlayerName = currentPlayer == 'X' ? playerXName : playerOName;
    lblplayerorwin.setText((currentPlayerName != null ? currentPlayerName : "Player " + currentPlayer) + "'s Turn");
}
private void saveGame(char winner) {
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        // Insert game
        PreparedStatement gameStmt = conn.prepareStatement(
                "INSERT INTO games (player_x_id, player_o_id, winner_id, game_date, moves) VALUES (?, ?, ?, NOW(), ?)");
        gameStmt.setInt(1, playerXId);
        gameStmt.setInt(2, playerOId);
        if (winner == 'X') {
            gameStmt.setInt(3, playerXId);
        } else if (winner == 'O') {
            gameStmt.setInt(3, playerOId);
        } else {
            gameStmt.setNull(3, Types.INTEGER);
        }
        gameStmt.setString(4, moves);
        gameStmt.executeUpdate();

        // Update scores
        if (winner == 'X') {
            updateScores(playerXId, 1, 0, 0);
            updateScores(playerOId, 0, 1, 0);
        } else if (winner == 'O') {
            updateScores(playerOId, 1, 0, 0);
            updateScores(playerXId, 0, 1, 0);
        } else {
            updateScores(playerXId, 0, 0, 1);
            updateScores(playerOId, 0, 0, 1);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error saving game: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void updateScores(int playerId, int wins, int losses, int draws) {
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE scores SET wins = wins + ?, losses = losses + ?, draws = draws + ? WHERE player_id = ?");
        stmt.setInt(1, wins);
        stmt.setInt(2, losses);
        stmt.setInt(3, draws);
        stmt.setInt(4, playerId);
        stmt.executeUpdate();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error updating scores: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private boolean checkWinner() {
        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return true;
        }
        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            return true;
        }

        return false;
    }

    private void disableAllButtons() {
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        PlayerProfile2 = new javax.swing.JLabel();
        playername2 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        PlayerProfile3 = new javax.swing.JLabel();
        playername1 = new javax.swing.JLabel();
        PlayerProfile7 = new javax.swing.JLabel();
        PlayerProfile8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btngrid1 = new javax.swing.JButton();
        lblplayerorwin = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btngrid2 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        btngrid3 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        btngrid5 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        btngrid4 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        btngrid6 = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        btngrid8 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        btngrid9 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        btngrid7 = new javax.swing.JButton();
        btnrestart = new javax.swing.JButton();
        btnquit1 = new javax.swing.JButton();
        btnrestart1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(500, 850));
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(500, 850));
        jPanel1.setMinimumSize(new java.awt.Dimension(500, 850));
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 850));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PlayerProfile2.setIcon(new javax.swing.ImageIcon("C:\\Users\\TraderG\\Downloads\\PROJECTS\\TicTacToe\\src\\main\\java\\com\\tictactoe\\icons\\Profile Resize.png")); // NOI18N
        jPanel11.add(PlayerProfile2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        playername2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        playername2.setForeground(new java.awt.Color(0, 0, 0));
        playername2.setText("Player 2");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(playername2)
                        .addGap(26, 26, 26)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playername2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 500, 180, 180));

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));

        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PlayerProfile3.setIcon(new javax.swing.ImageIcon("C:\\Users\\TraderG\\Downloads\\PROJECTS\\TicTacToe\\src\\main\\java\\com\\tictactoe\\icons\\Profile Resize.png")); // NOI18N
        jPanel20.add(PlayerProfile3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        playername1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        playername1.setForeground(new java.awt.Color(0, 0, 0));
        playername1.setText("Player 1");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(playername1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playername1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 500, 180, 170));

        PlayerProfile7.setIcon(new javax.swing.ImageIcon("C:\\Users\\TraderG\\Downloads\\PROJECTS\\TicTacToe\\src\\main\\java\\com\\tictactoe\\icons\\x.png")); // NOI18N
        jPanel1.add(PlayerProfile7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 700, -1, -1));

        PlayerProfile8.setIcon(new javax.swing.ImageIcon("C:\\Users\\TraderG\\Downloads\\PROJECTS\\TicTacToe\\src\\main\\java\\com\\tictactoe\\icons\\o.png")); // NOI18N
        jPanel1.add(PlayerProfile8, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 700, -1, -1));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid1.setBackground(new java.awt.Color(255, 255, 255));
        btngrid1.setForeground(new java.awt.Color(0, 0, 0));
        btngrid1.setBorder(null);
        btngrid1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, -1));

        lblplayerorwin.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblplayerorwin.setForeground(new java.awt.Color(255, 255, 255));
        lblplayerorwin.setText("Player X/O's Turn");
        jPanel2.add(lblplayerorwin, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 400, 40));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid2.setBackground(new java.awt.Color(255, 255, 255));
        btngrid2.setForeground(new java.awt.Color(0, 0, 0));
        btngrid2.setBorder(null);
        btngrid2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid2, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid2, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, -1, -1));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid3.setBackground(new java.awt.Color(255, 255, 255));
        btngrid3.setForeground(new java.awt.Color(0, 0, 0));
        btngrid3.setBorder(null);
        btngrid3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, -1, -1));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid5.setBackground(new java.awt.Color(255, 255, 255));
        btngrid5.setForeground(new java.awt.Color(0, 0, 0));
        btngrid5.setBorder(null);
        btngrid5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid5, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid5, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, -1, -1));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid4.setBackground(new java.awt.Color(255, 255, 255));
        btngrid4.setForeground(new java.awt.Color(0, 0, 0));
        btngrid4.setBorder(null);
        btngrid4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid4, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid4, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, -1, -1));

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid6.setBackground(new java.awt.Color(255, 255, 255));
        btngrid6.setForeground(new java.awt.Color(0, 0, 0));
        btngrid6.setBorder(null);
        btngrid6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid6, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid6, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 190, -1, -1));

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid8.setBackground(new java.awt.Color(255, 255, 255));
        btngrid8.setForeground(new java.awt.Color(0, 0, 0));
        btngrid8.setBorder(null);
        btngrid8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid8, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid8, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 320, -1, -1));

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid9.setBackground(new java.awt.Color(255, 255, 255));
        btngrid9.setForeground(new java.awt.Color(0, 0, 0));
        btngrid9.setBorder(null);
        btngrid9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid9, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid9, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 320, -1, -1));

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setPreferredSize(new java.awt.Dimension(120, 120));

        btngrid7.setBackground(new java.awt.Color(255, 255, 255));
        btngrid7.setForeground(new java.awt.Color(0, 0, 0));
        btngrid7.setBorder(null);
        btngrid7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btngrid7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid7, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btngrid7, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, -1, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 460, 460));

        btnrestart.setBackground(new java.awt.Color(0, 0, 0));
        btnrestart.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnrestart.setForeground(new java.awt.Color(255, 255, 255));
        btnrestart.setText("Admin");
        btnrestart.setBorder(null);
        btnrestart.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnrestart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrestartActionPerformed(evt);
            }
        });
        jPanel1.add(btnrestart, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 680, 120, 40));

        btnquit1.setBackground(new java.awt.Color(0, 0, 0));
        btnquit1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnquit1.setForeground(new java.awt.Color(255, 255, 255));
        btnquit1.setText("Back");
        btnquit1.setBorder(null);
        btnquit1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnquit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnquit1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnquit1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 780, 120, 40));

        btnrestart1.setBackground(new java.awt.Color(0, 0, 0));
        btnrestart1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btnrestart1.setForeground(new java.awt.Color(255, 255, 255));
        btnrestart1.setText("Restart");
        btnrestart1.setBorder(null);
        btnrestart1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnrestart1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrestart1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnrestart1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 730, 120, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnrestartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrestartActionPerformed
        // TODO add your handling code here:
        // Ensure GUI creation happens on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            AdminPage adminpage = new AdminPage(); // Create instance of LandingPage
            adminpage.setVisible(true); // Make the frame visible
            this.dispose();
        });
    }//GEN-LAST:event_btnrestartActionPerformed

    private void btngrid1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid1ActionPerformed

    private void btngrid2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid2ActionPerformed

    private void btngrid3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid3ActionPerformed

    private void btngrid4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid4ActionPerformed

    private void btngrid5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid5ActionPerformed

    private void btngrid6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid6ActionPerformed

    private void btngrid7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid7ActionPerformed

    private void btngrid8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid8ActionPerformed

    private void btngrid9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btngrid9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btngrid9ActionPerformed

    private void btnquit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnquit1ActionPerformed
        // TODO add your handling code here:
        // Ensure GUI creation happens on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            TicTacToePage firstpage = new TicTacToePage(); // Create instance of LandingPage
            firstpage.setVisible(true); // Make the frame visible
        });
        this.dispose();
    }//GEN-LAST:event_btnquit1ActionPerformed

    private void btnrestart1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrestart1ActionPerformed
        // TODO add your handling code here:
        initializeGame();
    }//GEN-LAST:event_btnrestart1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GamePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GamePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GamePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GamePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GamePage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel PlayerProfile2;
    private javax.swing.JLabel PlayerProfile3;
    private javax.swing.JLabel PlayerProfile7;
    private javax.swing.JLabel PlayerProfile8;
    private javax.swing.JButton btngrid1;
    private javax.swing.JButton btngrid2;
    private javax.swing.JButton btngrid3;
    private javax.swing.JButton btngrid4;
    private javax.swing.JButton btngrid5;
    private javax.swing.JButton btngrid6;
    private javax.swing.JButton btngrid7;
    private javax.swing.JButton btngrid8;
    private javax.swing.JButton btngrid9;
    private javax.swing.JButton btnquit1;
    private javax.swing.JButton btnrestart;
    private javax.swing.JButton btnrestart1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblplayerorwin;
    private javax.swing.JLabel playername1;
    private javax.swing.JLabel playername2;
    // End of variables declaration//GEN-END:variables
}
