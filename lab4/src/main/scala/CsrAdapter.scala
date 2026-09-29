// Import custom helper classes and utilities
import help._

// Import Chisel3 hardware description library
import chisel3._
// Import Chisel3 utility functions and classes
import chisel3.util._

// Class for parsing spreadsheet data containing register/block descriptions
class SheetParser(sheets: Map[String,Sheet]) {
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

// APB (Advanced Peripheral Bus) protocol port definition as a Chisel Bundle
class ApbPort extends Bundle {
  // Chip select input signal
  val psel = Input(Bool())
  // Enable signal input
  val penable = Input(Bool())
  // Write enable input signal
  val pwrite = Input(Bool())
  // 32-bit address input for memory/register access
  val paddr = Input(UInt(32.W))
  // 32-bit write data input
  val pwdata = Input(UInt(32.W))
  // 32-bit read data output
  val prdata = Output(UInt(32.W))
  // Ready signal output indicating device is ready
  val pready = Output(Bool())
  // Slave error output signal
  val pslverr = Output(Bool())
}

// Main CSR (Control and Status Register) adapter module that bridges APB bus to CSR interface
class CsrAdapter(descriptionSheetPath: String) extends Module {

  // Load spreadsheet data from the provided file path
  val sheets = Sheet.load(descriptionSheetPath)
  // Extract the "Map" sheet containing block and register mappings
  val map = sheets("Map")
  // Commented out debug print statement for the map data
  // println(map)
  // Create a SheetParser instance to parse the sheets
  val t = new SheetParser(sheets)
  // Parse the Map sheet to extract block information
  t.parseMap()

  // Create APB port interface for external bus communication
  val apb = IO(new ApbPort)

  // Create dynamic CSR bundle with registers extracted from the spreadsheet
  // Gets the block name from Map sheet and fetches its corresponding Register
  val csr = IO(new DynamicBundle(
    Seq(sheets(map.column("Block").head).column("Register").head -> Output(UInt(32.W)))
  ))


  // Initialize all APB signals to don't-care (undefined) values
  apb := DontCare
  // Set APB ready signal to 1 (device is always ready)
  apb.pready := 1.B
  // Set APB slave error signal to 1 (always indicate error state)
  apb.pslverr := 1.B
  // Initialize all CSR signals to don't-care values
  csr := DontCare

}

// Scala object for generating Verilog from the CsrAdapter Chisel module
object CsrAdapter extends App {
  // Generate Verilog RTL code from the CsrAdapter module
  emitVerilog(
    // Instantiate CsrAdapter with the SOC spreadsheet file
    new CsrAdapter("soc.xlsx"),
    // Specify output directory for generated Verilog files
    Array("--target-dir", "generated")
  )
}
