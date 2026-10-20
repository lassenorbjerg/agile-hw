import chisel3._
import chisel3.util._

// TODO: Implement a VendingMachine module that matches the behavior of VendingMachineModel
//
// Requirements:
// - Accept a Map[Int, Int] of item IDs to prices (in Danish Crowns)
// - Support inserting coins and accumulating balance
// - Support selecting items for purchase
// - Dispense item when balance is sufficient
// - Return change when overpaying
// - Handle invalid item IDs gracefully
//
// Hints:
// - You need to design the IO interface yourself
// - Think about what inputs and outputs you need
// - Consider using registers to maintain state
// - The prices Map can be used in hardware with VecInit or MuxLookup
//
// Refer to VendingMachineModel in the test file to understand the expected behavior

class VendingMachine(prices: Map[Int, Int]) extends Module {
  // TODO: Define your IO bundle here
  val io = IO(new Bundle {
    val coin = Input(UInt(8.W))
    val insert = Input(Bool())
    val buy = Input(Bool())
    val itemId = Input(UInt(8.W)) // could check lg ceil of max of map
    val dispensed = Output(Bool())
    val change = Output(UInt(8.W))
  })

  // TODO: Implement your vending machine logic here
  val balance = RegInit(0.U(8.W))
  when(io.insert) {
    balance := balance + io.coin
  }
  io.dispensed := false.B
  io.change := 0.U
  val price = MuxLookup(io.itemId, 0.U)(prices.map { case (id, p) =>
    id.U -> p.U
  }.toSeq)
  when(io.buy) {
    when(balance >= price) {
      io.dispensed := true.B
      io.change := balance - price
      balance := 0.U
    }
  }

}
