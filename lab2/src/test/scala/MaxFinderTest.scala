import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import chisel3.simulator.PeekPokeAPI

class MaxFinderTest extends AnyFlatSpec with ChiselScalatestTester with Matchers {
  "MaxFinder" should "find the maximum value in a Vec" in {
    test(new MaxFinder(4, 8)) { dut =>
      dut.io.in(0).poke(0.U)
      dut.io.in(1).poke(73.U)
      dut.io.in(2).poke(12.U)
      dut.io.in(3).poke(2.U)
      dut.io.max.expect(73.U)
    }
  }
}
