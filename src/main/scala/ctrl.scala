//see LICENSE for license

package reito

import chisel3._
import chisel3.util._
import freechips.rocketchip.rocket._
import freechips.rocketchip.config._

class CtrlModule(val w: Int)(implicit p: Parameters) extends Module() {  
	val io = IO(new Bundle {
		val rocc_req_val = Input(Bool())
		val rocc_req_rdy = Output(Bool())
		val rocc_funct   = Input(Bits(2.W))
		val rocc_rs1     = Input(Bits(w.W))
		val rocc_rs2     = Input(Bits(w.W))
		val rocc_rd      = Input(Bits(5.W))
		val rocc_fire    = Input(Bool())
		val resp_data    = Output(UInt(w.W))
		val resp_rd      = Output(Bits(5.W))
		val resp_valid   = Output(Bool()) 

		val dmem_req_val = Output(Bool())
		val dmem_req_rdy = Input(Bool())
		val dmem_req_tag = Output(UInt(7.W))
		val dmem_req_cmd = Output(UInt(0.W)) 
		val dmem_req_addr = Output(UInt(32.W))

		val dmem_resp_val = Input(Bool())
		val dmem_resp_tag = Input(UInt(7.W))
		val dmem_resp_data = Input(UInt(w.W))

		val busy   = Output(Bool())     
	})
 
	val busy  = RegInit(false.B)  
     
	io.busy   := busy   

	io.rocc_req_rdy := !busy
	io.resp_rd := io.rocc_rd
	io.resp_valid := io.rocc_req_val
    io.resp_data := 0.U(w.W)
     
    io.dmem_req_val := false.B
	io.dmem_req_tag := 0.U
	io.dmem_req_cmd := 0.U 
	io.dmem_req_addr:= 0.U(w.W)

	// decode the rocc instruction
	when (io.rocc_req_val && !busy) {
		io.busy := true.B
		when (io.rocc_funct === 10.U) {
			val result = io.rocc_rs1 + io.rocc_rs2 
            io.resp_data := result
		} .elsewhen (io.rocc_funct === 11.U) {
			val result = io.rocc_rs1 ^ io.rocc_rs2 
            io.resp_data := result
		} 
	}  
}