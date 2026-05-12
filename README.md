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
mvn -am -pl starcore-api,starcore-common,starcore-plugin,starecon,starbank,starboard,starshop,starcmds compile -DskipTests
```

## Run

Build each plugin module as a JAR and place the resulting artifacts into your Paper server `plugins/` directory.

The expected output JARs are located under each module's `target/` directory.

## Notes

- Uses `HikariCP` for async-ready database pooling
- Defaults to SQLite but is ready for MySQL by changing JDBC configuration
- Includes service registry, async scheduler wrapper, player data cache, and placeholder manager
- Designed for clean API-driven plugin integration across modules
