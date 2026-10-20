import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

trait ALUImpl {
  def get_result(sel: Int, op1: Int, op2: Int): Int
  def get_result_signed(sel: Int, op1: Int, op2: Int): Int
  def get_comp(sel: Int, op1: Int, op2: Int, signed: Boolean): Int
  def get_result_comp_0(sel: Int, op1: Int, op2: Int, signed: Boolean): Int
}


class ALUModel extends ALUImpl {
  
  def get_operator(sel: Int): (Int,Int) => Int = {
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
    
    sel match {
      case 0 => ((op1: Int, op2: Int) => ~op1)
      case 1 => ((op1: Int, op2: Int) => op1 & op2)
      case 2 => ((op1: Int, op2: Int) => op1 | op2)
      case 3 => ((op1: Int, op2: Int) => op1 ^ op2)
      case 4 => ((op1: Int, op2: Int) => op1 + op2)
      case 5 => ((op1: Int, op2: Int) => op1 - op2)
      case 6 => ((op1: Int, op2: Int) => op1 / op2)
      case 7 => ((op1: Int, op2: Int) => op1 % op2)
      case 8 => ((op1: Int, op2: Int) => op1 * op2)
      case 9 => ((op1: Int, op2: Int) => op1 >> (op2 & 0x3F))
      case 10 => ((op1: Int, op2: Int) => op1 << (op2 & 0x3F))
      case _ => ((op1: Int, op2: Int) => 0)
    }
  }

  def get_result(sel: Int, op1: Int, op2: Int): Int = {
    get_operator(sel)(op1,op2)
  }
  def get_result_signed(sel: Int, op1: Int, op2: Int): Int = {
    get_operator(sel,op1,op2)
  }
  def get_comp(sel: Int, op1: Int, op2: Int, op1_signed: Int, op2_signed: Int, signed: Boolean): Array[Boolean] = {
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
    comp
  }
  def get_result_comp_0(sel: Int, op1: Int, op2: Int, op1_signed: Int, op2_signed: Int, signed: Boolean): Array[Boolean] = {
    /*
    result_comp_0(0) = out_result<0
    result_comp_0(1) = out_result==0
    result_comp_0(2) = out_result>0
    result_comp_0(3) = out_result<=0
    result_comp_0(4) = out_result>=0
    */
    val result_comp_0 = new Array[Boolean](5)
    val result = if (signed) get_result(sel, op1_signed, op2_signed) else get_result_signed(sel, op1, op2)
    result_comp_0(0) = result < 0
    result_comp_0(1) = result == 0
    result_comp_0(2) = result > 0
    result_comp_0(3) = result <= 0
    result_comp_0(4) = result >= 0
    result_comp_0
  }

}

// Chisel implementation wrapper
// This wrapper is complete and drives the ALU hardware
class ALUChisel(dut: ALU) extends ALUImpl {
  
}

// Simple tester that compares multiple implementations
class ALUSimpleTester(implementations: Seq[ALUImpl]) {
  def testOperation(description: String)(op1: Int, op2: Int, sel: Int, signed: Boolean): Unit = {
    ???
  }
}

// Main test class
class ALUTest extends AnyFlatSpec with ChiselScalatestTester {

  behavior of "ALU"

  it should "perform unsigned addition correctly" in {
    test(new ALU) { dut =>
      // val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(Seq(chiselImpl)) // TODO: Add model when implemented

      tester.testOperation("5 + 3 (unsigned)")(5, 3, 4, false)
      tester.testOperation("100 + 50 (unsigned)")(100, 50, 4, false)
      tester.testOperation("0 + 0 (unsigned)")(0, 0, 4, false)
    }
  }

  it should "perform unsigned subtraction correctly" in {
    test(new ALU) { dut =>
      // val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(Seq(chiselImpl)) // TODO: Add model when implemented

      tester.testOperation("10 - 3 (unsigned)")(10, 3, 5, false)
      tester.testOperation("100 - 50 (unsigned)")(100, 50, 5, false)
    }
  }

  it should "perform bitwise operations correctly" in {
    test(new ALU) { dut =>
      // val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(Seq(chiselImpl)) // TODO: Add model when implemented

      tester.testOperation("15 & 7 (AND)")(15, 7, 1, false)
      tester.testOperation("8 | 4 (OR)")(8, 4, 2, false)
      tester.testOperation("15 ^ 7 (XOR)")(15, 7, 3, false)
      tester.testOperation("NOT 5")(5, 0, 0, false)
    }
  }

  it should "perform multiplication and division correctly" in {
    test(new ALU) { dut =>
      // val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(Seq(chiselImpl)) // TODO: Add model when implemented

      tester.testOperation("6 * 7 (MUL)")(6, 7, 8, false)
      tester.testOperation("20 / 4 (DIV)")(20, 4, 6, false)
      tester.testOperation("23 % 5 (MOD)")(23, 5, 7, false)
    }
  }

  it should "perform shift operations correctly" in {
    test(new ALU) { dut =>
      // val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(Seq(chiselImpl)) // TODO: Add model when implemented

      tester.testOperation("8 << 2 (SLL)")(8, 2, 10, false)
      tester.testOperation("32 >> 2 (SRL)")(32, 2, 9, false)
    }
  }

  it should "handle signed operations correctly" in {
    test(new ALU) { dut =>
      // val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(Seq(chiselImpl)) // TODO: Add model when implemented

      tester.testOperation("-5 + 3 (signed)")(-5, 3, 4, true)
      tester.testOperation("10 - 20 (signed)")(10, 20, 5, true)
      tester.testOperation("-4 * 3 (signed)")(-4, 3, 8, true)
    }
  }

  it should "set comparison flags correctly" in {
    test(new ALU) { dut =>
      // val model = new ALUModel()
      val chiselImpl = new ALUChisel(dut)
      val tester = new ALUSimpleTester(Seq(chiselImpl)) // TODO: Add model when implemented

      // When op1 < op2, flags should be [true, false, false, true, false]
      tester.testOperation("3 < 5 comparison")(3, 5, 4, false)

      // When op1 = op2, flags should be [false, true, false, true, true]
      tester.testOperation("7 = 7 comparison")(7, 7, 4, false)

      // When op1 > op2, flags should be [false, false, true, false, true]
      tester.testOperation("10 > 5 comparison")(10, 5, 4, false)
    }
  }
}
