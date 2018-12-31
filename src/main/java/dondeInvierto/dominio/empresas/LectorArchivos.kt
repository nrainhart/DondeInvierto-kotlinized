package dondeInvierto.dominio.empresas

import excepciones.NoSePudoLeerEseTipoDeArchivoError

class LectorArchivos(ruta: String) {
    private val lectores = listOf(ArchivoXLS(ruta), ArchivoCSV(ruta))

    fun getEmpresas(): List<Empresa> {
        val archivo = obtenerLectorApropiado()
        archivo.leerEmpresas()
        return archivo.getEmpresas()
    }

    private fun obtenerLectorApropiado(): ArchivoEmpresas {
        return lectores.firstOrNull { it.puedeLeerArchivo() }
                ?: throw NoSePudoLeerEseTipoDeArchivoError("No se pudo leer el archivo.")
    }

}