package game

import java.awt.Dimension
import javax.swing.*

// RUN GAME HERE
object GameApp extends App :
  private val frame = new JFrame("Mythic Bastion")
  private val gamePanel = new GamePanel()

  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  frame.add(gamePanel, java.awt.BorderLayout.CENTER)
  frame.setSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight))

  frame.pack()
  frame.setResizable(false)
  frame.setVisible(true)
  frame.setFocusable(true)
  frame.setLocationRelativeTo(None.orNull)
  frame.addMouseListener(gamePanel.getSystemHandler.keyHandler)
  frame.addKeyListener(gamePanel.getSystemHandler.keyHandler)

  gamePanel.setUpGame()
  gamePanel.startGameThread()