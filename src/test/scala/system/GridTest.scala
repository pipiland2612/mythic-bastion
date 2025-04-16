package system

import entity.creature.alliance.{Alliance, Soldier}
import entity.creature.enemy.{Creep, Enemy}
import game.GamePanel
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import system.stage.{Grid, GridCell}

import java.awt.geom.Rectangle2D

class StubGamePanel extends GamePanel:
  override val screenWidth: Int = 960  // 10 cells at 96px each
  override val screenHeight: Int = 768 // 8 cells at 96px each

class GridTest extends AnyFlatSpec with Matchers:
  val gamePanel = new StubGamePanel
  val grid = new Grid(gamePanel)
  val cellSize = 96

  def makeCreep(pos: (Double, Double)): Enemy =
    Creep(
      name = "TestCreep",
      maxHealth = 100.0,
      playerDamage = 10.0,
      apDmg = 5.0,
      apDefense = 2.0,
      adDmg = 5.0,
      adDefense = 2.0,
      range = 50.0,
      speed = 2.0,
      maxAttackCoolDown = 1.0,
      maxDeadCounter = 2.0,
      coin = 10.0,
      jsonPath = "enemies/Boss01.json",
      imagePath = "enemies/Boss01.png",
      rect = new Rectangle2D.Double(pos._1, pos._2, 10, 10),
      gp = gamePanel,
      healthOffSet = (0, 0),
      pos = pos
    )

  def makeSoldier(pos: (Double, Double)): Alliance =
    Soldier(
      name = "TestSoldier",
      maxHealth = 100.0,
      apDmg = 5.0,
      apDefense = 2.0,
      adDmg = 5.0,
      adDefense = 2.0,
      range = 50.0,
      speed = 2.0,
      maxAttackCoolDown = 1.0,
      maxDeadCounter = 2.0,
      jsonPath = "alliances/Soldier01.json",
      imagePath = "alliances/Soldier01.png",
      rect = new Rectangle2D.Double(pos._1, pos._2, 10, 10),
      gp = gamePanel,
      healthOffSet = (0, 0),
      pos = pos
    )

  "GridCell" should "add and remove creatures correctly" in {
    val cell = new GridCell
    val creep = makeCreep((0, 0))
    val soldier = makeSoldier((0, 0))

    cell.addCreature(creep)
    cell.getEnemies should contain only creep
    cell.getAlliances shouldBe empty

    cell.addCreature(soldier)
    cell.getEnemies should contain only creep
    cell.getAlliances should contain only soldier

    cell.removeCreature(creep)
    cell.getEnemies shouldBe empty
    cell.getAlliances should contain only soldier
  }

  "Grid" should "initialize with correct dimensions" in {
    grid.getRows shouldBe 10  // 960 / 96
    grid.getCols shouldBe 8   // 768 / 96
  }

  it should "update creature positions correctly" in {
    val creep = makeCreep((50, 50))
    val prevPos = (0, 0)

    grid.updateCreaturePosition(creep, prevPos)
    grid.scanForEnemiesInRange(creep) should contain(creep)

    val newPos = (cellSize + 10, cellSize + 10)
    grid.updateCreaturePosition(creep, (50, 50))
    grid.scanForEnemiesInRange(creep) should contain(creep)
  }
