package reito

import chisel3._
import chisel3.util._
import freechips.rocketchip.rocket._
import freechips.rocketchip.config._
import freechips.rocketchip.tile._ 
 
case object WidthP extends Field[Int] 

class ReitoSafe(opcodes: OpcodeSet)(implicit p: Parameters) extends LazyRoCC(opcodes) {
	override lazy val module = new ReitoSafeModuleImp(this)
}

class ReitoSafeModuleImp(outer: ReitoSafe) extends LazyRoCCModuleImp(outer) with HasCoreParameters {
	val w = outer.p(WidthP) 
	io.resp.valid := false.B

	// control
	val ctrl = Module(new CtrlModule(w)(outer.p))

	ctrl.io.rocc_funct   <> io.cmd.bits.inst.funct
	ctrl.io.rocc_rs1     <> io.cmd.bits.rs1
	ctrl.io.rocc_rs2     <> io.cmd.bits.rs2
	ctrl.io.rocc_rd      <> io.cmd.bits.inst.rd
	ctrl.io.rocc_req_val <> io.cmd.valid
	ctrl.io.rocc_req_rdy <> io.cmd.ready
	ctrl.io.busy         <> io.busy
	ctrl.io.resp_data    <> io.resp.bits.data
	ctrl.io.resp_rd      <> io.resp.bits.rd
	ctrl.io.resp_valid   <> io.resp.valid
	when (io.cmd.fire) {
		ctrl.io.rocc_fire := true.B
	} .otherwise {
		ctrl.io.rocc_fire := false.B
	}
  ctrl.io.dmem_req_val <> io.mem.req.valid
	ctrl.io.dmem_req_rdy <> io.mem.req.ready
	ctrl.io.dmem_req_tag <> io.mem.req.bits.tag
	ctrl.io.dmem_req_cmd <> io.mem.req.bits.cmd 
	ctrl.io.dmem_req_addr<> io.mem.req.bits.addr

	ctrl.io.dmem_resp_val <> io.mem.resp.valid
	ctrl.io.dmem_resp_tag <> io.mem.resp.bits.tag
	ctrl.io.dmem_resp_data := io.mem.resp.bits.data

  io.mem.req.bits.data := 0.U
 
	io.interrupt := false.B 
}