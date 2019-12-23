/*
rule = Slf4jConditional
*/
package test

import org.slf4j.LoggerFactory

object AddSlf4jConditional {
  val log = LoggerFactory.getLogger(getClass)

  log.trace("message {}", 15)
  log.debug("message")
  log.info("message {}", 15)
  log.warn("message {}", 15)
  log.error("message {}", 15)

  def logger = log
  logger.error("message {}", 15)
}
