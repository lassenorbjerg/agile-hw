import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

trait ALUImpl {
  def compute(
      sel: Int,
      op1: Int,
      op2: Int,
      signed: Boolean
  ): (Int, Int, Array[Boolean], Array[Boolean])
}

class ALUModel extends ALUImpl {

  def compute(
      sel: Int,
      op1: Int,
      op2: Int,
      signed: Boolean
  ): (Int, Int, Array[Boolean], Array[Boolean]) = {
    var op1_signed = if (signed) op1 else op1 & 0xffffffff
    var op2_signed = if (signed) op2 else op2 & 0xffffffff
    /*
      0. not op1
      1. op1 & op2
      2. op1 | op2
      3. op1 ^ op2
      4. op1 + op2
      5. op1 - op2
      6. op1 / op2
      7. op1 % op2
      8. op1 * op2
      9. op1 >> op2(5,0)
      10. op1 << op2(5,0)
      11-15. nop
     */

    var op = sel match {
      case 0  => ((op1: Int, op2: Int) => ~op1)
      case 1  => ((op1: Int, op2: Int) => op1 & op2)
      case 2  => ((op1: Int, op2: Int) => op1 | op2)
      case 3  => ((op1: Int, op2: Int) => op1 ^ op2)
      case 4  => ((op1: Int, op2: Int) => op1 + op2)
      case 5  => ((op1: Int, op2: Int) => op1 - op2)
      case 6  => ((op1: Int, op2: Int) => op1 / op2)
      case 7  => ((op1: Int, op2: Int) => op1 % op2)
      case 8  => ((op1: Int, op2: Int) => op1 * op2)
      case 9  => ((op1: Int, op2: Int) => op1 >> (op2 & 0x3f))
      case 10 => ((op1: Int, op2: Int) => op1 << (op2 & 0x3f))
      case _  => ((op1: Int, op2: Int) => 0)
    }
    /*
    comp(0) = op1<op2
    comp(1) = op1==op2
    comp(2) = op1>op2
    comp(3) = op1<=op2
    comp(4) = op1>=op2
     */
    val comp = new Array[Boolean](5)
    if (signed) {
      comp(0) = op1_signed < op2_signed
      comp(1) = op1_signed == op2_signed
      comp(2) = op1_signed > op2_signed
      comp(3) = op1_signed <= op2_signed
      comp(4) = op1_signed >= op2_signed
    } else {
      comp(0) = op1 < op2
      comp(1) = op1 == op2
      comp(2) = op1 > op2
      comp(3) = op1 <= op2
      comp(4) = op1 >= op2
    }
    /*
    result_comp_0(0) = out_result<0
    result_comp_0(1) = out_result==0
    result_comp_0(2) = out_result>0
    result_comp_0(3) = out_result<=0
    result_comp_0(4) = out_result>=0
     */
    val result_comp_0 = new Array[Boolean](5)
    val result = op(op1, op2)
    val result_signed = op(op1_signed, op2_signed)
    result_comp_0(0) = result < 0
    result_comp_0(1) = result == 0
    result_comp_0(2) = result > 0
    result_comp_0(3) = result <= 0
    result_comp_0(4) = result >= 0
    (result, result_signed, comp, result_comp_0)
  }

}

// Chisel implementation wrapper
// This wrapper is complete and drives the ALU hardware
class ALUChisel(dut: ALU) extends ALUImpl {
  def compute(
      sel: Int,
      op1: Int,
      op2: Int,
      signed: Boolean
  ): (Int, Int, Array[Boolean], Array[Boolean]) = {
    dut.io.in_signed.poke(signed.B)
    dut.io.in_sel.poke(sel.U)
    dut.io.in_op1.poke(op1.U)
    dut.io.in_op2.poke(op2.U)
    dut.io.in_op1_signed.poke(op1.S)
    dut.io.in_op2_signed.poke(op2.S)
    dut.clock.step(1)
    val result = dut.io.out_result.peek().litValue.toInt
    val result_signed = dut.io.out_result_signed.peek().litValue.toInt
    val comp = dut.io.out_comp.map(_.peek().litToBoolean).toArray
    val result_comp_0 =
      dut.io.out_result_comp_0.map(_.peek().litToBoolean).toArray
    (result, result_signed, comp, result_comp_0)
  }
}

// Simple tester that compares multiple implementations
class ALUSimpleTester(implementations: Seq[ALUImpl]) {
  // Arrays use reference equality and print as [Z@hash, so compare and print formatted strings
  def format(result: (Int, Int, Array[Boolean], Array[Boolean])): String = {
    val (out, outSigned, comp, compOut) = result
    s"result=$out, result_signed=$outSigned, " +
      s"comp=${comp.mkString("[", ", ", "]")}, " +
      s"result_comp_0=${compOut.mkString("[", ", ", "]")}"
  }

  def testOperation(
      description: String
  )(op1: Int, op2: Int, sel: Int, signed: Boolean): Unit = {
    val results =
      implementations.map(impl => impl.compute(sel, op1, op2, signed))
    val formatted = results.map(format)
    val details = implementations.zip(formatted).map { case (impl, f) =>
      s"  ${impl.getClass.getSimpleName}: $f"
    }
    assert(
      formatted.distinct.size == 1,
      s"Results differ for operation $description with op1=$op1, op2=$op2, sel=$sel, signed=$signed:\n${details.mkString("\n")}"
    )
  }
}

// Main test class
class ALUTest extends AnyFlatSpec with ChiselScalatestTester {

  behavior of "ALU"

  it should "perform unsigned addition correctly" in {
    test(new ALU) { dut =>
      val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(
        Seq(chiselImpl, model)
      ) // TODO: Add model when implemented

      tester.testOperation("5 + 3 (unsigned)")(5, 3, 4, false)
      tester.testOperation("100 + 50 (unsigned)")(100, 50, 4, false)
      tester.testOperation("0 + 0 (unsigned)")(0, 0, 4, false)
    }
  }

  it should "perform unsigned subtraction correctly" in {
    test(new ALU) { dut =>
      val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(
        Seq(chiselImpl, model)
      ) // TODO: Add model when implemented

      tester.testOperation("10 - 3 (unsigned)")(10, 3, 5, false)
      tester.testOperation("100 - 50 (unsigned)")(100, 50, 5, false)
    }
  }

  it should "perform bitwise operations correctly" in {
    test(new ALU) { dut =>
      val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(
        Seq(chiselImpl, model)
      ) // TODO: Add model when implemented

      tester.testOperation("15 & 7 (AND)")(15, 7, 1, false)
      tester.testOperation("8 | 4 (OR)")(8, 4, 2, false)
      tester.testOperation("15 ^ 7 (XOR)")(15, 7, 3, false)
      tester.testOperation("NOT 5")(5, 0, 0, false)
    }
  }

  it should "perform multiplication and division correctly" in {
    test(new ALU) { dut =>
      val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(
        Seq(chiselImpl, model)
      ) // TODO: Add model when implemented

      tester.testOperation("6 * 7 (MUL)")(6, 7, 8, false)
      tester.testOperation("20 / 4 (DIV)")(20, 4, 6, false)
      tester.testOperation("23 % 5 (MOD)")(23, 5, 7, false)
    }
  }

  it should "perform shift operations correctly" in {
    test(new ALU) { dut =>
      val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(
        Seq(chiselImpl, model)
      ) // TODO: Add model when implemented

      tester.testOperation("8 << 2 (SLL)")(8, 2, 10, false)
      tester.testOperation("32 >> 2 (SRL)")(32, 2, 9, false)
    }
  }

  it should "handle signed operations correctly" in {
    test(new ALU) { dut =>
      val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(
        Seq(chiselImpl, model)
      ) // TODO: Add model when implemented

      tester.testOperation("-5 + 3 (signed)")(-5, 3, 4, true)
      tester.testOperation("10 - 20 (signed)")(10, 20, 5, true)
      tester.testOperation("-4 * 3 (signed)")(-4, 3, 8, true)
    }
  }

  it should "set comparison flags correctly" in {
    test(new ALU) { dut =>
      val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(
        Seq(chiselImpl, model)
      ) // TODO: Add model when implemented

      // When op1 < op2, flags should be [true, false, false, true, false]
      tester.testOperation("3 < 5 comparison")(3, 5, 4, false)

      // When op1 = op2, flags should be [false, true, false, true, true]
      tester.testOperation("7 = 7 comparison")(7, 7, 4, false)

      // When op1 > op2, flags should be [false, false, true, false, true]
      tester.testOperation("10 > 5 comparison")(10, 5, 4, false)
    }
  }
}
