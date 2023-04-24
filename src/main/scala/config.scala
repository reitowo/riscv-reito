package reito

import Chisel._
import freechips.rocketchip.config._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.system._
import freechips.rocketchip.tile._
import freechips.rocketchip.diplomacy._
import reito._

class WithReito extends Config ((site, here, up) => {  
    case BuildRoCC => up(BuildRoCC) ++ Seq(
        (p: Parameters) => {
            val reito = LazyModule(new ReitoSafe(OpcodeSet.all)(p))
            reito
        }
    )
})