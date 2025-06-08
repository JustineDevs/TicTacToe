/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.tictactoe;

/**
 *
 * @author TraderG
 */
public class TicTacToe {

    public static void main(String[] args) {
        // Ensure GUI creation happens on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            TicTacToePage firstpage = new TicTacToePage(); // Create instance of LandingPage
            firstpage.setVisible(true); // Make the frame visible
        });
    }
}
