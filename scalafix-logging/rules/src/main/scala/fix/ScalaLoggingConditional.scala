package fix

import scalafix.v1._

import scala.meta._

class ScalaLoggingConditional extends SemanticRule("ScalaLoggingConditional") {
  val LoggerObject = "com/typesafe/scalalogging/Logger."
  val LoggerObjectMatcher = SymbolMatcher.exact(LoggerObject)

  val StrictLoggingTrait = "com/typesafe/scalalogging/StrictLogging#"
  val StrictLoggingTraitMatcher = SymbolMatcher.exact(StrictLoggingTrait)

  val LoggerClass = "com/typesafe/scalalogging/Logger#"
  val Slf4jLoggerFqcn = "org.slf4j.LoggerFactory.getLogger"
  val Slf4jLogger = SymbolMatcher.exact("org/slf4j/LoggerFactory#getLogger(+1).")

  val guards = Map(
    "trace" -> "isTraceEnabled",
    "debug" -> "isDebugEnabled",
    "info" -> "isInfoEnabled",
    "warn" -> "isWarnEnabled",
    "error" -> "isErrorEnabled"
  )
  val logMethods = guards.keySet

  override def fix(implicit doc: SemanticDocument): Patch = {
    //    doc.tree.collect {
    //      case t: Defn.Object =>
    //        doc.removeParentFromTemplate(Symbol("_root_.scala.App."), t.templ) +
    //          wrapBodyInMain(t.templ)
    //    }.asPatch
    doc.tree
      .collect {
        case loggerImport@Importee.Name(Name("Logger")) if LoggerObjectMatcher.matches(loggerImport.symbol) =>
          Patch.removeImportee(loggerImport)

        case loggerImport@Importee.Name(Name("StrictLogging")) if StrictLoggingTraitMatcher.matches(loggerImport.symbol) =>
          Patch.removeImportee(loggerImport)

        case Defn.Class(_, cName, _, _, template) =>
          // TODO remove `extends` or `with`
          val extendsStrictLogging = template.inits.zipWithIndex collect {
            case init@(Init(Type.Name("StrictLogging"), _, _), idx) => init
          }
          if (extendsStrictLogging.nonEmpty) {
            val idx = extendsStrictLogging.head._2
            val patches = for {
              (tmpl, i) <- template.inits.zipWithIndex
            } yield
              if (i != idx) Patch.replaceTree(tmpl, tmpl.syntax)
              else Patch.replaceTree(tmpl, "???")
            patches.fold(Patch.empty)(_ + _) + Patch.addLeft(template.stats.head.tokens.head, s"val logger = org.slf4j.LoggerFactory.getLogger(classOf[${cName.symbol.displayName}])\n  ")
          } else Patch.empty

        case Defn.Val(_, _, _, apply@Term.Apply(d, List(arg))) if LoggerObjectMatcher.matches(d.symbol) =>
          if (Slf4jLogger.matches(arg.symbol)) Patch.replaceTree(apply, arg.syntax)
          else Patch.replaceTree(d, Slf4jLoggerFqcn)

        case Defn.Val(_, _, _, at@Term.ApplyType(d, tpe)) if LoggerObjectMatcher.matches(d.symbol) =>
          Patch.replaceTree(at, Slf4jLoggerFqcn + s"(classOf[${tpe.head.symbol.displayName}])")

        case Term.Apply(fun, args) =>
          if (SymbolMatcher.exact(LoggerClass).matches(fun.symbol.owner)
            && logMethods.contains(fun.symbol.displayName)) {
            val guard = fun.parent match {
              case Some(Term.Apply(Term.Select(t, n), _)) =>
                Patch.addLeft(fun, s"if (${t.symbol.displayName}.${guards(fun.symbol.displayName)}) ")
              case Some(other) =>
                println(s"hit $other")
                Patch.empty
              case _ =>
                Patch.empty
            }
            val interp = args collect {
              case interp@Term.Interpolate(Term.Name("s"), parts, vals) =>
                Patch.replaceTree(interp, "\"" + parts.map(_.syntax).mkString("{}") + "\", " + vals.map(_.syntax).mkString(", "))
            }
            guard + interp.fold(Patch.empty)(_ + _)
          } else Patch.empty

        case other =>
          Patch.empty
      }
      .asPatch
  }
}

