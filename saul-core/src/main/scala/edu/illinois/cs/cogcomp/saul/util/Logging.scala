/** This software is released under the University of Illinois/Research and Academic Use License. See
  * the LICENSE file in the root folder for details. Copyright (c) 2016
  *
  * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
  * http://cogcomp.cs.illinois.edu/
  */
package edu.illinois.cs.cogcomp.saul.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.html.HTMLLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core._
import ch.qos.logback.core.encoder.{ Encoder, LayoutWrappingEncoder }
import org.slf4j.impl.StaticLoggerBinder
import org.slf4j.LoggerFactory

/** This trait is meant to be mixed into a class to provide logging and logging configuration.
  *
  * The enclosed methods provide a Scala-style logging signature where the
  * message is a block instead of a string.  This way the message string is
  * not constructed unless the message will be logged.
  */
trait Logging {
  lazy val internalLogger = LoggerFactory.getLogger(this.getClass)

  object logger {
    def trace(message: => String): Unit =
      if (internalLogger.isTraceEnabled) {
        internalLogger.trace(message)
      }

    def debug(message: => String): Unit =
      if (internalLogger.isDebugEnabled) {
        internalLogger.debug(message)
      }

    def info(message: => String): Unit =
      if (internalLogger.isInfoEnabled) {
        internalLogger.info(message)
      }

    def warn(message: => String): Unit =
      if (internalLogger.isWarnEnabled) {
        internalLogger.warn(message)
      }

    def warn(message: => String, throwable: Throwable): Unit =
      if (internalLogger.isWarnEnabled) {
        internalLogger.warn(message, throwable)
      }

    def error(message: => String): Unit =
      if (internalLogger.isErrorEnabled) {
        internalLogger.error(message)
      }

    def error(message: => String, throwable: Throwable): Unit =
      if (internalLogger.isErrorEnabled) {
        internalLogger.error(message, throwable)
      }
  }

  /** Simple logback configuration.
    * Hopefully this will be discoverable by just typing <code>loggerConfig.[TAB]</code>
    *
    * Examples:
    * format: OFF
    * {{{
    * loggerConfig.Logger("org.apache.spark").setLevel(Level.WARN)
    *
    * loggerConfig.Logger().addAppender(
    *   loggerConfig.newPatternLayoutEncoder("%-5level [%thread]: %message%n"),
    *   loggerConfig.newConsoleAppender
    * )
    * }}}
    * format: ON
    */
  object loggerConfig {
    case class Logger(loggerName: String = org.slf4j.Logger.ROOT_LOGGER_NAME) {
      private val logger: ch.qos.logback.classic.Logger = getLogger()

      private def getLogger(): ch.qos.logback.classic.Logger = {
        LoggerFactory.getLogger(loggerName) match {
          case (b: ch.qos.logback.classic.Logger) => b
          case _ => null
        }
      }

      private def reportBindingError(action: String): Unit = {
        val factory = StaticLoggerBinder.getSingleton().getLoggerFactory();
        val msg = String.format(
          "LoggerFactory is not a Logback LoggerContext because " +
            "another Logger implementation(%s) in the classpath " +
            "is used as default Logger.\n" +
            "So, cannot perform '%s'.",
          factory.getClass(), action);
        LoggerFactory.getLogger(loggerName).error(msg)
      }

      private def doAction[T](name: String, action: () => T): T = {
        if (logger == null) {
          reportBindingError(name)
          val default: T = null.asInstanceOf[T]
          default
        }
        else
          action()
      }

      /** Resets the logger. */
      def reset(): Logger = {
        doAction("reset", () => logger.getLoggerContext.reset())
        this
      }

      /** Simple log level setting. Example:
        * <code>
        * loggerConfig.Logger("org.apache.spark").setLevel(Level.WARN)
        * </code>
        */
      def setLevel(level: Level): Logger = {
        doAction("setLevel", () => logger.setLevel(level))
        this
      }

      def setLevelWarn(): Logger = {
        setLevel(Level.WARN)
        this
      }

      def setLevelDebug(): Logger = {
        setLevel(Level.DEBUG)
        this
      }

      def setLevelInfo(): Logger = {
        setLevel(Level.INFO)
        this
      }

      /** Simple way to get the log level. Example:
        * <code>
        * loggerConfig.Logger("org.apache.spark").getLevel()
        * </code>
        */
      def getLevel(): Level = {
        doAction("getLevel", () => logger.getLevel())
      }

      def isLevelWarn(): Boolean = {
        doAction("isLevelWarn", () => logger.getLevel() == Level.WARN)
      }

      def isLevelDebug(): Boolean = {
        doAction("isLevelDebug", () => logger.getLevel() == Level.DEBUG)
      }

      def isLevelInfo(): Boolean = {
        doAction("isLevelDebug", () => logger.getLevel() == Level.INFO)
      }

      /** Simple log appender creation. Example:
        * <code>
        * loggerConfig.Logger()
        * .addAppender(
        *     loggerConfig.newPatternLayoutEncoder("%-5level [%thread]: %message%n"),
        *     loggerConfig.newConsoleAppender)
        * .addAppender(
        *     loggerConfig.newHtmlLayoutEncoder("%relative%thread%level%logger%msg"),
        *     loggerConfig.newFileAppender("./log.html"))
        * </code>
        */
      def addAppender(
                       encoder: Encoder[ILoggingEvent],
                       appender: OutputStreamAppender[ILoggingEvent]
                     ): Logger = {
        doAction("addAppender", () => {
          val loggerContext = logger.getLoggerContext

          encoder.setContext(loggerContext)
          encoder.start()
          appender.setContext(loggerContext)
          appender.setEncoder(encoder)
          appender.start()
          logger.addAppender(appender)
        })
        this
      }
    }

    def newPatternLayoutEncoder(pattern: String): Encoder[ILoggingEvent] = {
      val encoder = new PatternLayoutEncoder()
      encoder.setPattern(pattern)
      encoder
    }

    def newHtmlLayoutEncoder(pattern: String): Encoder[ILoggingEvent] = {
      new LayoutWrappingEncoder[ILoggingEvent] {
        private val htmlLayout = new HTMLLayout()
        htmlLayout.setPattern(pattern)
        super.setLayout(htmlLayout)

        override def setLayout(layout: Layout[ILoggingEvent]) = {
          throw new Exception("Layout set via Logging.logger.config.htmlLayoutEncoder")
        }

        override def setContext(loggerContext: Context) = {
          htmlLayout.setContext(loggerContext)
          super.setContext(loggerContext)
        }

        override def start() = {
          htmlLayout.start()
          super.start()
        }
      }
    }

    def newConsoleAppender(): OutputStreamAppender[ILoggingEvent] = {
      new ConsoleAppender[ILoggingEvent]()
    }

    def newFileAppender(fileName: String): OutputStreamAppender[ILoggingEvent] = {
      val appender = new FileAppender[ILoggingEvent]()
      appender.setAppend(false)
      appender.setFile(fileName)
      appender
    }
  }
}
