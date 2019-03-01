package dondeInvierto

import dondeInvierto.dominio.empresas.Empresa
import dondeInvierto.repo.EmpresaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@ActiveProfiles("test")
class SpringDataPersistanceTest {

    @Autowired
    private lateinit var repoEmpresas: EmpresaRepository

    @Test
    fun unaEmpresaConIdInmutablePuedeSerGuardadaYRecuperada() {
        val empresa = Empresa("sassa", mutableListOf())
        repoEmpresas.save(empresa)
        assertThat(repoEmpresas.findAll()).contains(empresa)
    }
}