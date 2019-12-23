package fix

import scalafix.v1._

import scala.meta._

final class Slf4jConditional extends SemanticRule("Slf4jConditional") {
  val LoggerMatcher = SymbolMatcher.exact("org/slf4j/Logger#")
  val guards = Map(
    "trace" -> "isTraceEnabled",
    "debug" -> "isDebugEnabled",
    "info" -> "isInfoEnabled",
    "warn" -> "isWarnEnabled",
    "error" -> "isErrorEnabled"
  )
  val logMethods = guards.keySet

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree
      .collect {
        case Term.Apply(term, _) if LoggerMatcher.matches(term.symbol.owner) && logMethods.contains(term.symbol.displayName) =>
          term.parent.collect {
            case Term.Apply(Term.Select(t, n), _) =>
              Patch.addLeft(term, s"if (${t.symbol.displayName}.${guards(term.symbol.displayName)}) ")
          }
      }
      .flatten
      .asPatch
  }
}

