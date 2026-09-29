import chisel3._
import chisel3.util._
import help._

class FieldType extends ChiselEnum {
  val rw, ro, wotrg, rotrg, const = Value
}

class Block(
    name: String,
    interface: String,
    baseAddress: Int, // NOTE: from hex
    endAddress: Int, // NOTE: from hex
    cacheable: Boolean,  // NOTE: from yes/no
    executable: Boolean, // NOTE: from yes/no
    description: String
) {}
class Register(
    name: String,
    offset: Int, // NOTE: In bytes
    fields: Seq[Field]
) {}
class Field(
    name: String,
    typ: FieldType,
    range: (Int, Int), // bytes
    init: Any
) {}

// Class for parsing spreadsheet data containing register/block descriptions
class SheetParser(sheets: Map[String, Sheet]) {
  // Method to parse the "Map" sheet and extract row data
  def parseMap(): Unit = {
    // Retrieve the "Map" sheet from the sheets collection
    val map = sheets("Map")

    // Extract all rows from the Map sheet
    val blocks = map.rows

    // Print the blocks data to console for debugging
    println(blocks.toString())

  }

}

class ApbPort extends Bundle {
  val psel = Input(Bool())
  val penable = Input(Bool())
  val pwrite = Input(Bool())
  val paddr = Input(UInt(32.W))
  val pwdata = Input(UInt(32.W))
  val prdata = Output(UInt(32.W))
  val pready = Output(Bool())
  val pslverr = Output(Bool())
}

class CsrAdapter(descriptionSheetPath: String) extends Module {

  val sheets = Sheet.load(descriptionSheetPath)
  val map = sheets("Map")
  println(map)

  val apb = IO(new ApbPort)

  val csr = IO(
    new DynamicBundle(
      Seq(
        sheets(map.column("Block").head).column("Register").head -> Output(
          UInt(32.W)
        )
      )
    )
  )

  apb := DontCare
  apb.pready := 1.B
  apb.pslverr := 1.B
  csr := DontCare

}

object CsrAdapter extends App {
  emitVerilog(
    new CsrAdapter("soc.xlsx"),
    Array("--target-dir", "generated")
  )
}
