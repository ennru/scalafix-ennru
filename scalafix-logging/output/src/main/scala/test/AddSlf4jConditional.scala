package test

import org.slf4j.LoggerFactory

object AddSlf4jConditional {
  val log = LoggerFactory.getLogger(getClass)

  if (log.isTraceEnabled) log.trace("message {}", 15)
  if (log.isDebugEnabled) log.debug("message")
  if (log.isInfoEnabled) log.info("message {}", 15)
  if (log.isWarnEnabled) log.warn("message {}", 15)
  if (log.isErrorEnabled) log.error("message {}", 15)

  def logger = log
  if (logger.isErrorEnabled) logger.error("message {}", 15)
}
