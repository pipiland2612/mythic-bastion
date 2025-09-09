package entity

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class StateTest extends AnyFunSuite with Matchers:

  test("State enum should have all expected values"):
    val allStates = State.values.toSet
    
    allStates should contain(State.IDLE)
    allStates should contain(State.RUN)
    allStates should contain(State.ATTACK)
    allStates should contain(State.DEAD)
    allStates should contain(State.PREPARE)
    
    allStates should have size 5

  test("State should support pattern matching"):
    def getStateDescription(state: State): String = state match
      case State.IDLE => "entity is idle"
      case State.RUN => "entity is running"
      case State.ATTACK => "entity is attacking"
      case State.DEAD => "entity is dead"
      case State.PREPARE => "entity is preparing"
    
    getStateDescription(State.IDLE) should be("entity is idle")
    getStateDescription(State.RUN) should be("entity is running")
    getStateDescription(State.ATTACK) should be("entity is attacking")
    getStateDescription(State.DEAD) should be("entity is dead")
    getStateDescription(State.PREPARE) should be("entity is preparing")

  test("State values should be unique"):
    val allValues = State.values
    allValues.toSet should have size allValues.length

  test("State should support equality comparisons"):
    State.IDLE should equal(State.IDLE)
    State.RUN should equal(State.RUN)
    State.ATTACK should equal(State.ATTACK)
    State.DEAD should equal(State.DEAD)
    State.PREPARE should equal(State.PREPARE)
    
    State.IDLE should not equal State.RUN
    State.ATTACK should not equal State.DEAD

  test("State should have consistent string representations"):
    State.values.foreach { state =>
      state.toString should not be empty
      state.toString should not be null
    }

  test("State should support ordinal values"):
    State.values.foreach { state =>
      state.ordinal should be >= 0
      state.ordinal should be < State.values.length
    }

  test("State should support collections operations"):
    val activeStates = List(State.RUN, State.ATTACK, State.PREPARE)
    val inactiveStates = List(State.IDLE, State.DEAD)
    
    activeStates should not contain State.IDLE
    activeStates should not contain State.DEAD
    
    inactiveStates should not contain State.RUN
    inactiveStates should not contain State.ATTACK
    inactiveStates should not contain State.PREPARE

  test("State values should be consistent across calls"):
    val states1 = State.values
    val states2 = State.values
    
    states1 should contain theSameElementsInOrderAs states2

  test("State should handle set operations correctly"):
    val combatStates = Set(State.ATTACK, State.PREPARE)
    val movementStates = Set(State.RUN, State.IDLE)
    val allEntityStates = Set(State.IDLE, State.RUN, State.ATTACK, State.DEAD, State.PREPARE)
    
    combatStates intersect movementStates should be(empty)
    combatStates union movementStates should have size 4
    allEntityStates should contain allElementsOf (combatStates union movementStates)

  test("State should work with filtering"):
    val allStates = State.values.toList
    
    val actionStates = allStates.filter(state => state == State.ATTACK || state == State.RUN)
    actionStates should contain(State.ATTACK)
    actionStates should contain(State.RUN)
    actionStates should have size 2