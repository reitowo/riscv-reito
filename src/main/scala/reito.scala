package reito

import chisel3._
import chisel3.util._
import freechips.rocketchip.rocket._
import freechips.rocketchip.config._
import freechips.rocketchip.tile._  

class ReitoSafe(opcodes: OpcodeSet)(implicit p: Parameters) extends LazyRoCC(opcodes) {
	override lazy val module = new ReitoSafeModuleImp(this)
}

class ReitoSafeModuleImp(outer: ReitoSafe)(implicit p: Parameters) extends LazyRoCCModuleImp(outer) with HasCoreParameters { 
    val req_rd = Reg(chiselTypeOf(io.cmd.bits.inst.rd))  
    val req_rs1 = Reg(chiselTypeOf(io.cmd.bits.rs1))  
    val req_rs2 = Reg(chiselTypeOf(io.cmd.bits.rs2))  
    val req_funct = Reg(chiselTypeOf(io.cmd.bits.inst.funct))  

    val res_data = Reg(chiselTypeOf(io.resp.bits.data))  

    val s_idle :: s_req :: s_resp :: Nil = Enum(3)
    val state = RegInit(s_idle) 

    io.cmd.ready := (state === s_idle) 

    when (state === s_req) { 
        when (req_funct === 10.U) {
			res_data := req_rs1 + req_rs2
		} .elsewhen (req_funct === 11.U) {
			res_data := req_rs1 - req_rs2
		} .elsewhen (req_funct === 12.U) {
			res_data := req_rs1 ^ req_rs2
		} .elsewhen (req_funct === 13.U) {
			res_data := req_rs1 | req_rs2
		} .elsewhen (req_funct === 14.U) {
			res_data := req_rs1 & req_rs2
		} .otherwise { 
			res_data := 114514.U(32.W)
		} 
        state := s_resp
    }

    when (io.cmd.fire()) {
        req_rd := io.cmd.bits.inst.rd
        req_rs1 := io.cmd.bits.rs1
        req_rs2 := io.cmd.bits.rs2
        req_funct := io.cmd.bits.inst.funct 
        state := s_req
    }  

    when (io.resp.fire()) { 
        state := s_idle  
    }  

    io.resp.valid := (state === s_resp)
    io.resp.bits.rd := req_rd
    io.resp.bits.data := res_data

    io.busy := (state =/= s_idle)
    io.interrupt := false.B
    io.mem.req.valid := false.B 
}