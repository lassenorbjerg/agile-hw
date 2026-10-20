import chisel3._

class prioListEnum extends ChiselEnum {
    val IDLE, PUSH, POP, PUSH_POP = Value
}

class prioListIO(keyWidth: Int, valueWidth: Int) extends Bundle {
    val keyOut = Output(UInt(keyWidth.W))
    val valueOut = Output(UInt(valueWidth.W))

    val keyIn = Input(UInt(keyWidth.W))
    val valueIn = Input(UInt(valueWidth.W))
    val cmd_in = Input(prioListEnum())
    val cmd_out = Output(prioListEnum())
    
}


class prioListNode(keyWidth: Int, valueWidth: Int) extends Module {
    val io = new prioListIO(keyWidth,valueWidth)
}

