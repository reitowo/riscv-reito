package Reito
 
import Vivado._

class WithRetio(mesh_size: Int, bus_bits: Int) extends Config((site, here, up) => {
  case BuildRoCC => up(BuildRoCC) ++ Seq(
    (p: Parameters) => {
      implicit val q = p
      implicit val v = implicitly[ValName]
      LazyModule(new gemmini.Gemmini(gemmini.GemminiConfigs.defaultConfig.copy(
        meshRows = mesh_size, meshColumns = mesh_size, dma_buswidth = bus_bits)))
    }
  )
  case SystemBusKey => up(SystemBusKey).copy(beatBytes = bus_bits/8)
})

class Reito64b1 extends Rocket64b1(
  new WithReito)

class Reito64b2 extends Rocket64b2(
  new WithReito)