package de.ideasinlogic.fpga.spinal.blink

import spinal.core._
import spinal.lib._

class Divider(divider: BigInt) extends Component {
  val io = new Bundle {
    val clkOut = out Bool
  }
  // Create own clock-domain for 12 Mhz Clock from FTDI
  // within external clocked time-domain
  // private val counter = Reg(UInt(log2Up(divider) bits)) init 0

  private val counter = CounterFreeRun(divider)

  when(counter.willOverflow) {
    io.clkOut := True
  }.otherwise {
    io.clkOut := False
  }
}
