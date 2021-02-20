import scala.sys.process._

name := "blinky"

val packageName = "de.ideasinlogic.fpga.spinal.blink"
val className = "Blink"

scalaVersion := "2.12.13"
val spinalVersion = "1.4.3"

fork := true

libraryDependencies ++= Seq(
  "com.github.spinalhdl" % "spinalhdl-core_2.12" % spinalVersion,
  "com.github.spinalhdl" % "spinalhdl-lib_2.12" % spinalVersion,
  compilerPlugin("com.github.spinalhdl" % "spinalhdl-idsl-plugin_2.12" % spinalVersion)
)

scalacOptions ++= Seq(
  "-language:postfixOps"
)

lazy val synth = taskKey[Unit]("Synthesize bitstream")

lazy val prog = taskKey[Unit]("Program ICE40 via SRAM")

lazy val progFlash = taskKey[Unit]("Program ICE40 via Flash")

lazy val svg = taskKey[Unit]("Run visualization")

synth := {
  val yoSys = Seq("yosys", "-p", s"synth_ice40 -top $className -blif target/$className.blif", s"target/$className.v")
  val arachne = Seq("arachne-pnr", "-d", "8k", "-P", "ct256", "-o", s"target/$className.asc", "-p", s"$className.pcf", s"target/$className.blif")
  val icepack = Seq("icepack", s"target/$className.asc", s"target/$className.bin")

  (runMain in Compile).toTask(s" $packageName.$className").value

  yoSys #&& arachne #&& icepack !
}

svg := {
  val yoSys = Seq("yosys", "-p", s"prep -top $className -flatten; write_json target/$className.json", s"target/$className.v")
  val runSvg = Seq("netlistsvg", "-o", s"target/$className.svg", s"target/$className.json")

  (runMain in Compile).toTask(s" $packageName.$className").value

  yoSys #&& runSvg !
}

prog := {
  val iceprog = Seq("iceprog", "-S", s"target/$className.bin")

  iceprog !
}

progFlash := {
  val iceprog = Seq("iceprog", s"target/$className.bin")

  iceprog !
}
