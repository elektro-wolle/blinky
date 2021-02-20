package de.ideasinlogic.fpga.spinal.blink

import spinal.core._
import spinal.lib._

object LocalClock {
  def apply(clkIn: Bool) = ClockDomain(
    clock = clkIn,
    reset = True,
    config = ClockDomainConfig(
      clockEdge = RISING,
      resetKind = ASYNC,
      resetActiveLevel = LOW
    )
  )
}
