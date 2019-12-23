# Scalafix rules logging with SLF4J

## Slf4jConditional

Adds `if (log.isLevelEnabled)` to all SLF4J log statements.


## ScalaLoggingConditional

Replaces the macro-based `Logger` from [scala-logging](https://github.com/lightbend/scala-logging) with direct use of SLF4J.
