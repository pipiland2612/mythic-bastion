package game

import java.awt.Dimension
import javax.swing.*

// RUN GAME HERE
object GameApp extends App :
  val frame = new JFrame("Mythic Bastion")
  val gamePanel = new GamePanel()

  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.add(gamePanel, java.awt.BorderLayout.CENTER)
  frame.setSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight))

  frame.pack()
  frame.setResizable(false)
  frame.setVisible(true)
  frame.setFocusable(true)
  frame.setLocationRelativeTo(null)

  gamePanel.setUpGame()
  gamePanel.startGameThread()