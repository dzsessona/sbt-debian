package me.dsnet.sbtdeb

import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigFactory


object Main extends App{

  lazy val config = ConfigFactory.load()
  lazy val logger = LoggerFactory.getLogger(getClass)

  args match{
    case a if(a.length > 0) => println("Hello debian: " + args.mkString(" "))
    case _ => println("Hello debian")
  }

  logger.debug("This is a dubug message")
  logger.info ("This is an info message")
  logger.info ("The envinronmet is set to development: " + config.getBoolean("Development"))
}
