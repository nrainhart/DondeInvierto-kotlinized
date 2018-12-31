package dondeInvierto.spring_controllers

import dondeInvierto.dominio.empresas.Cuenta
import dondeInvierto.dominio.empresas.Empresa
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Year

@RestController
@RequestMapping("/api/empresas")
class EmpresaController {

  @GetMapping
  fun get() = Empresa("10Pines", mutableListOf(Cuenta(Year.of(2018), "ebitda", 200)))

}