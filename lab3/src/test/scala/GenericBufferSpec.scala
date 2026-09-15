import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import chisel3.simulator.PeekPokeAPI
import org.scalatest.matchers.should.Matchers

class GenerifBufferSpec
    extends AnyFlatSpec
    with ChiselScalatestTester
    with Matchers {
  "Buffer" should "redirect data from write to read" in {
    test(new GenericBuffer(UInt(4.W))) { dut =>
      dut.io.read.expect(0.U)
      dut.io.empty.expect(true.B)
      dut.io.full.expect(false.B)
      dut.clock.step()

      dut.io.write.poke(10.U)
      dut.io.read.expect(0.U)
      dut.io.empty.expect(true.B)
      dut.io.full.expect(false.B)
      dut.clock.step()
      dut.io.read.expect(10.U)
      dut.io.empty.expect(false.B)
      dut.io.full.expect(true.B)
    }
  }
}
