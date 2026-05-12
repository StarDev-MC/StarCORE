# StarCORE

StarCore is a modular Paper plugin ecosystem built for scalable Minecraft networks.

## Project modules

- `starcore-api` - shared API interfaces for all StarCore modules
- `starcore-common` - common implementations: database, config, caching, event bus, GUI utilities
- `starcore-plugin` - core plugin base class and command framework
- `starecon` - economy system with async balance, pay, and top balance support
- `starbank` - bank subsystem with wallet separation, deposit/withdraw, and persistence
- `starboard` - optimized scoreboard/tablist system
- `starshop` - GUI shop foundation with item configs and sell support
- `starcmds` - modular utility command framework

## Build

Requires Java 25 and Maven.

```bash
cd /workspaces/StarCORE
mvn clean package -DskipTests
```

## Download & Install

Pre-built plugin JAR files are available in the [releases/](releases/) directory. The system uses a shaded runtime module that contains all shared libraries and dependencies.

**Installation:**
1. Download `starcore-runtime-1.0.0-SNAPSHOT.jar` first - this contains all shared libraries (HikariCP, SQLite, Guava)
2. Download any plugin JARs you want (starecon, starbank, starboard, starshop, starcmds)
3. Place all JARs in your Paper server's `plugins/` directory
4. Restart your server

**Required:** Always install `starcore-runtime-1.0.0-SNAPSHOT.jar`

**Available plugins:**
- `starecon-1.0.0-SNAPSHOT.jar` (11 KB) - Economy system with balance, pay, and top balance commands
- `starbank-1.0.0-SNAPSHOT.jar` (8.7 KB) - Bank subsystem with wallet management
- `starboard-1.0.0-SNAPSHOT.jar` (5.9 KB) - Scoreboard and tablist system
- `starshop-1.0.0-SNAPSHOT.jar` (9.4 KB) - GUI-based shop system
- `starcmds-1.0.0-SNAPSHOT.jar` (11 KB) - Modular utility commands framework

**Total size:** ~16 MB (runtime) + plugin JARs (~45 KB total for all plugins)

## Development

To build the project and generate new JAR files:

```bash
cd /workspaces/StarCORE
mvn clean package -DskipTests
```

The generated JARs will be available in each module's `target/` directory and automatically copied to `releases/`.

## Notes

- Uses `HikariCP` for async-ready database pooling
- Defaults to SQLite but is ready for MySQL by changing JDBC configuration
- Includes service registry, async scheduler wrapper, player data cache, and placeholder manager
- Designed for clean API-driven plugin integration across modules
