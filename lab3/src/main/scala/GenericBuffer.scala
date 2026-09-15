import chisel3._
import chisel3.util._

class GenericBuffer[T <: Data](gen: T) extends Module {
  val io = IO(new Bundle {
    val write = Input(gen)
    val read = Output(gen)
    val full = Output(Bool())
    val empty = Output(Bool())
  })

  val uint = 0.U
  val conved = uint.asTypeOf(gen)
  val buf = RegInit(conved)
  buf := io.write
  io.read := buf
  io.empty := (buf === conved)
  io.full := ~io.empty
}

object GenericBuffer extends App {
  emitVerilog(new GenericBuffer(UInt(4.W)))
}
