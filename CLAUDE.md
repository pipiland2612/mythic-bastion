# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Mythic Bastion** is a tower defense game written in Scala, inspired by Kingdom Rush. It uses Scala 3.3.5 with Java Swing for the GUI framework. The game features strategic tower placement, wave-based enemy progression, and an upgrade system.

## Common Development Commands

### Build and Run
```bash
# Start the game
sbt run

# Reload build configuration 
sbt reload

# Clean build artifacts
sbt clean

# Run tests
sbt test

# Create executable JAR
sbt assembly
```

### Testing
```bash
# Run all tests
sbt test

# Run specific test class
sbt "testOnly entity.EntityTest"

# Run tests continuously (watch mode)
sbt ~test
```

## Code Architecture

### Core Game Structure

The application follows a component-based game architecture:

- **GameApp** (`src/main/scala/game/GameApp.scala`): Main entry point, initializes JFrame and GamePanel
- **GamePanel** (`src/main/scala/game/GamePanel.scala`): Central game loop and rendering engine
- **GameState** (`src/main/scala/game/GameState.scala`): Manages different game states (menu, playing, paused, etc.)

### Entity System

The entity system is built around an abstract `Entity` base class:

- **Entity** (`src/main/scala/entity/Entity.scala`): Base class for all game objects
- **Towers** (`src/main/scala/entity/tower/`): Different tower types with upgrade capabilities
- **Creatures** (`src/main/scala/entity/creature/`): Enemies and allied units
- **Weapons** (`src/main/scala/entity/weapon/`): Projectiles and attack systems

### Game Systems

- **StageManager** (`src/main/scala/system/stage/StageManager.scala`): Manages game levels and wave progression
- **SystemHandler** (`src/main/scala/system/SystemHandler.scala`): Coordinates input, sound, and data management
- **UpgradeManager** (`src/main/scala/system/upgrade/UpgradeManager.scala`): Handles tower and player upgrades

### Resource Management

- **JSON Configuration**: Game entities are configured via JSON files in `src/main/resources/json/`
- **Assets**: Images, sounds, and fonts are organized in `src/main/resources/`
- **Save System**: Player progress stored in `player_save.dat` and `upgrade_save.dat`

### Key Design Patterns

- **Entity-Component Pattern**: Entities have animations, states, and behaviors
- **State Pattern**: GameState manages different game modes
- **Observer Pattern**: SystemHandler coordinates between components
- **Strategy Pattern**: Different tower types implement attack strategies

## Development Guidelines

### File Organization
- Entity implementations go in respective subdirectories under `entity/`
- System-level components belong in `system/` with appropriate subdirectories
- Utility functions and constants are in `utils/`
- GUI components are in `gui/`

### Naming Conventions
- Class names use PascalCase (e.g., `GamePanel`, `StageManager`)
- Method names use camelCase (e.g., `getCurrentStage`, `updateHealth`)
- JSON configuration files match entity names (e.g., `ArrowTower01.json`)

### Asset Management
- Images follow naming pattern: `EntityName01.png`, `EntityName02.png`
- JSON configurations define entity properties, animations, and stats
- Sound files are referenced through `SoundConstant` for centralized management

### Testing Structure
- Test files mirror source structure in `src/test/scala/`
- Tests use ScalaTest framework with Mockito for mocking
- Key areas tested: entity behavior, stage loading, save/load functionality