package de.ideasinlogic.fpga.spinal.blink

import spinal.core._
import spinal.lib._

class PWM(resolutionBits: Int) extends Component {
  val io = new Bundle {
    val pwmOut = out Bool
    val pwmDuration = in UInt (resolutionBits bits)
  }

  private val counter = CounterFreeRun(1 << resolutionBits)
  //   private val counter = Reg(UInt(resolutionBits bits)) init 0
  // val pwmValue = Reg(UInt(resolutionBits bits))
  private val pwmRegOut = Reg(Bool)

  io.pwmOut := pwmRegOut

  // counter := counter + 1
  // pwmValue := pwmWidth
  when(counter.value === 0) {
    pwmRegOut := True
  }.otherwise {
    when(counter.value === io.pwmDuration) {
      pwmRegOut := False
    }
  }
}
