package de.ideasinlogic.fpga.spinal.blink

import spinal.core._

class Blink extends Component {
  val io = new Bundle {
    val led = out Bits (8 bits)
    val clk = in Bool
  }

  new ClockingArea(LocalClock(io.clk)) {
    val pwms =
      (0 to 7).map(i => {
        val p = new PWM(8)
        io.led(i) <> p.io.pwmOut
        p
      })

    private val div = new Divider(1000000)

    private val ledSel = Reg(UInt(3 bits))

    private val counter = Reg(UInt(4 bits))
    // Create own clock-domain for 12 Mhz Clock from FTDI
    // within external clocked time-domain
    when(div.io.clkOut) {
      counter := counter + 1
      ledSel := counter(2 downto 0)
    }
    private val ledOut = Reg(UInt(8 bits))


    (0 to 7).foreach(i => {
      pwms(i).io.pwmDuration := B"00000001".rotateLeft(i).rotateLeft(ledSel).asUInt
    })
  }
}

object Blink {
  // Let's go
  def main(args: Array[String]) {
    // Use Verilog for IceStorm instead of VHDL
    val report = SpinalConfig(
      mode = Verilog,
      targetDirectory = "target/").generate(new Blink)
    report.printPruned()
    report.printUnused()
    report.printPrunedIo()
  }
}
