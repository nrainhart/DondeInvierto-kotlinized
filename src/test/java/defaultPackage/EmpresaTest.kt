package defaultPackage

import dondeInvierto.dominio.empresas.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Year

class EmpresaTest {

    private val empresa: Empresa
    private val cuentas: List<Cuenta>
    private val empresasLibroDos: List<Empresa>

    init {
        val archivoLibroUno = ArchivoXLS("src/test/resources/LibroPrueba.xls")
        val archivoLibroDos = ArchivoXLS("src/test/resources/LibroPruebaEmpresas.xls")
        archivoLibroUno.leerEmpresas()
        archivoLibroDos.leerEmpresas()
        empresasLibroDos = archivoLibroDos.empresas
        empresa = archivoLibroUno.empresas[0]
        cuentas = empresa.getCuentas()
    }

    @Test
    fun elLectorXLSPuedeLeerUnArchivoConExtensionXLS() {
        val archivo = ArchivoXLS("src/test/resources/LibroPruebaEmpresas.xls")
        assertTrue(archivo.puedeLeerArchivo())
    }

    @Test
    fun elLectorCSVPuedeLeerUnArchivoConExtensionCSV() {
        val archivo = ArchivoCSV("src/test/resources/LibroPruebaEmpresas.csv")
        assertTrue(archivo.puedeLeerArchivo())
    }

    @Test
    fun elAnioDeLaUltimaCuentaEsDeLaPrimeraEmpresa2014() {
        assertEquals(cuentas.last().anio, obtenerAnio(2014))
    }

    @Test
    fun elValorDeLaCuentaFDSDel2017Es158960() {
        assertEquals(empresa.getValorCuenta("FDS", obtenerAnio(2017)), 158960)
    }

    @Test
    fun cargaCuentasCorrectamente() {
        val cuentasEsperadas = listOf(
                Cuenta(obtenerAnio(2017), "EBITDA", 35000),
                Cuenta(obtenerAnio(2017), "FDS", 158960),
                Cuenta(obtenerAnio(2016), "FDS", 144000),
                Cuenta(obtenerAnio(2015), "EBITDA", 120000),
                Cuenta(obtenerAnio(2015), "Free Clash Flow", 150000),
                Cuenta(obtenerAnio(2014), "EBITDA", 260000),
                Cuenta(obtenerAnio(2014), "FDS", 360000)
        )
        assertTrue(sonLasMismasCuentas(cuentasEsperadas, cuentas))
    }

    @Test
    fun ambosLectoresDevuelvenLoMismo() {
        val archivoXLS = ArchivoXLS("src/test/resources/LibroPruebaEmpresas.xls")
        val archivoCSV = ArchivoCSV("src/test/resources/LibroPruebaEmpresas.csv")
        archivoXLS.leerEmpresas()
        archivoCSV.leerEmpresas()
        assertTrue(tienenLasMismasEmpresas(archivoXLS.empresas, archivoCSV.empresas))
    }

    @Test
    fun hayTresEmpresasCargadas() {
        assertEquals(3, empresasLibroDos.size)
    }

    @Test
    fun elNombreDeLaSegundaEmpresaEsEmpresaLoca() {
        assertEquals("EmpresaLoca", empresasLibroDos[1].nombre)
    }

    @Test
    fun laPrimerEmpresaTieneSieteCuentas() {
        assertEquals(7, empresasLibroDos[0].cantidadDeCuentas())
    }

    @Test
    fun elArchivoXLSNoSobrecargaEmpresasAnteVariasEjecuciones() {
        val archivo = ArchivoXLS("src/test/resources/LibroPruebaEmpresas.xls")
        archivo.leerEmpresas()
        archivo.leerEmpresas()
        assertEquals(3, cantidadEmpresasEn(archivo))
    }

    @Test
    fun elArchivoCSVNoSobreCargaEmpresasAnteVariasEjecuciones() {
        val archivo = ArchivoCSV("src/test/resources/LibroPruebaEmpresas.csv")
        archivo.leerEmpresas()
        archivo.leerEmpresas()
        assertEquals(3, cantidadEmpresasEn(archivo))
    }


    /* ------------------------------- METODOS AUXILIARES  ------------------------------- */

    private fun cantidadEmpresasEn(archivo: ArchivoEmpresas) = archivo.empresas.size

    private fun tienenLasMismasEmpresas(primerListaEmpresas: List<Empresa>,
                                        segundaListaEmpresas: List<Empresa>): Boolean {
        for (i in primerListaEmpresas.indices) {
            if (!sonLasMismasEmpresas(primerListaEmpresas[i], segundaListaEmpresas[i])) {
                return false
            }
        }
        return true
    }

    private fun sonLasMismasEmpresas(unaEmpresa: Empresa, otraEmpresa: Empresa): Boolean {
        return unaEmpresa.seLlama(otraEmpresa.nombre) && sonLasMismasCuentas(unaEmpresa.getCuentas(), otraEmpresa.getCuentas())
    }

    private fun sonLasMismasCuentas(cuentasEsperadas: List<Cuenta>, cuentas: List<Cuenta>): Boolean {
        return cuentasEsperadas.size == cuentas.size &&
                cuentasEsperadas.withIndex().all {cuentaEsperada -> cuentasSonIguales(cuentas[cuentaEsperada.index], cuentaEsperada.value)}
    }

    private fun cuentasSonIguales(cuenta: Cuenta, cuentaEsperada: Cuenta): Boolean {
        return cuenta.anio == cuentaEsperada.anio &&
                cuenta.tipoCuenta == cuentaEsperada.tipoCuenta &&
                cuenta.valor == cuentaEsperada.valor
    }

    private fun obtenerAnio(anio: Int) = Year.of(anio)

}
