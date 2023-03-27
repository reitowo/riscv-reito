package reito

import Chisel._
import freechips.rocketchip.config._
import freechips.rocketchip.subsystem._
import freechips.rocketchip.system._
import freechips.rocketchip.tile._
import freechips.rocketchip.diplomacy._
import reito._

class WithReito extends Config ((site, here, up) => { 
    case WidthP => 64 
    case BuildRoCC => Seq(
        (p: Parameters) => {
            val sha3 = LazyModule.apply(new ReitoSafe(OpcodeSet.all)(p))
            sha3
        }
    )
})