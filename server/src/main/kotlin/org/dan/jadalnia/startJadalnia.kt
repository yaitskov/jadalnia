package org.dan.jadalnia

import org.dan.jadalnia.sys.jetty.EmbeddedJetty
import org.slf4j.LoggerFactory

import java.util.Optional


fun main() {
  val log = LoggerFactory.getLogger("jadalnia app")
  log.info("Launching Jadalnia...")
  EmbeddedJetty.ensureServerRunningOn(
      Optional.of(System.getProperty("jadalina.http.port", "80"))
          .map(Integer::parseInt)
          .orElseThrow {
            throw IllegalStateException("property jadalina.http.port is set")
          })
}
