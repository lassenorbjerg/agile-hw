import chisel3._

class prioListIO(keyWidth: Int, valueWidth: Int) extends Bundle {
    val key = Input(UInt(keyWidth.W))
    val value = Input(UInt(valueWidth.W))
    
    // val cmd = Input(UInt(valueWidth.W))
}


class prioListNode(keyWidth: Int, valueWidth: Int) {
    val io = new prioListIO {
        
    }
}

