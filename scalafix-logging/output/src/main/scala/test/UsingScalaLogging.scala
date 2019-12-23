package test


class UsingScalaLogging {
  val log = org.slf4j.LoggerFactory.getLogger(getClass)

  if (log.isTraceEnabled) log.trace("message {}", 15)
  if (log.isDebugEnabled) log.debug("message")
  if (log.isInfoEnabled) log.info("message {}", 15)
  if (log.isWarnEnabled) log.warn("message {}", 15)
  if (log.isErrorEnabled) log.error("message {}", 15)

  def logger = log
  if (logger.isErrorEnabled) logger.error("message {}", 15)

  val logger3 = org.slf4j.LoggerFactory.getLogger(classOf[UsingScalaLogging])
  if (logger3.isErrorEnabled) logger3.error("message")

  val logger4 = org.slf4j.LoggerFactory.getLogger(getClass)
  if (logger4.isErrorEnabled) logger4.error("message")
}

class WithStrictLogger extends Object with ??? {
  val logger = org.slf4j.LoggerFactory.getLogger(classOf[WithStrictLogger])
  if (logger.isInfoEnabled) logger.info("this was strict")
}

class UsingInterpolation {
  val log = org.slf4j.LoggerFactory.getLogger(getClass)

  val i = 15
  if (log.isTraceEnabled) log.trace("message [{}]", i)
  val exception = new RuntimeException
  if (log.isErrorEnabled) log.error("message [{}]", i, exception)
}
