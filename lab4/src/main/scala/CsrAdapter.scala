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
    description: String,
    registers: Seq[Register]
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
  

  private def buildField(blockName: String): Field = {

  }
  private def buildRegister(rowData: Seq[String]): Register = {

  }

  private def buildBlock(blockName: String): Block = {
    val map = sheets("Map")

    val block, rest = for (row: Seq[String] <- map.rows if row(0) == blockName) yield (row)

  

    // val block, name, interface, baseAddress, endAddress, cacheable, executable, description = map.row(index)
    // val block, rest = sheets("Map").row(index)
    

    val blockObject = Block.tupled(rest :+ registers)

    // val blockObject = new Block(
    //   name=name,
    //   interface=interface,
    //   baseAddress=baseAddress,
    //   endAddress=endAddress,
    //   cacheable=cacheable,
    //   executable=executable,
    //   description=description
    //   // registers=new Seq(),
    // )
  }

  def parseMap(): Unit = {
    val map = sheets("Map")

    
    // val csr = IO(
    //   new DynamicBundle(
    //     Seq(
    //       sheets(map.column("Block").head).column("Register").head -> Output(
    //         UInt(32.W)
    //       )
    //     )
    //   )
    // )
    
    // val blocks = map.column("Block").zipWithIndex.foreach(
    //   (blockName: String, index: Int) => (map.row(index+1) 
    //   new Block(
    //     name=blockName,
    //     interface=
    //   ))
    // )

    // println(blocks.toString())

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
  val parser = new SheetParser(sheets)
  val map = sheets("Map")
  println(parser.parseMap())

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
