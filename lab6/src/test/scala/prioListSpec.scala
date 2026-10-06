
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
// Slide "Getting Started": imports and the class header
import org.scalacheck.{Arbitrary, Gen, Shrink}
import org.scalatestplus.scalacheck._


class prioListSpec extends AnyFlatSpec
    with ChiselScalatestTester
    with ScalaCheckPropertyChecks {

  // Slide "Test-Driven Development (TDD)"
  "A node" should "start empty" in {
    test(new prioListNode(8,8)) { c =>
        c.key.expect(0.U)
        c.value.expect(0.U)
    }
  }

//   // Slide "Property-Based Testing in Chisel"
//   "A stage" should "pop what was pushed" in {
//     forAll(Gen.choose(0, 255)) { in =>
//       test(new prioListStage(8)) { c =>
//         c.io.push.poke(true.B)
//         c.io.input.poke(in.U)
//         c.clock.step()
//         c.io.push.poke(false.B)
//         c.io.pop.poke(true.B)
//         c.io.output.expect(in.U)
//         c.clock.step()
//         c.io.empty.expect(true.B)
//       }
//     }
//   }
    }
