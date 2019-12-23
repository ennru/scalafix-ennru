/*
rule = ScalaLoggingConditional
*/
package test

import com.typesafe.scalalogging.{Logger, StrictLogging}

class UsingScalaLogging {
  val log = Logger(getClass)

  log.trace("message {}", 15)
  log.debug("message")
  log.info("message {}", 15)
  log.warn("message {}", 15)
  log.error("message {}", 15)

  def logger = log
  logger.error("message {}", 15)

  val logger3 = Logger[UsingScalaLogging]
  logger3.error("message")

  val logger4 = Logger(org.slf4j.LoggerFactory.getLogger(getClass))
  logger4.error("message")
}

class WithStrictLogger extends Object with StrictLogging {
  logger.info("this was strict")
}

class UsingInterpolation {
  val log = Logger(getClass)

  val i = 15
  log.trace(s"message [$i]")
  val exception = new RuntimeException
  log.error(s"message [$i]", exception)
}
