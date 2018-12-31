package dondeInvierto.dominio.metodologias

import java.util.stream.IntStream

enum class OperacionAgregacion {

    Mediana {
        override fun aplicarA(valores: IntStream): Int {
            val vals = valores.sorted().toArray()
            return if (!esCantidadPar(vals)) {
                vals[middle(vals)]
            } else {
                promedioDeValoresQueEstanEnElMedio(vals)
            }
        }

        private fun esCantidadPar(valores: IntArray) = valores.size % 2 == 0

        private fun promedioDeValoresQueEstanEnElMedio(vals: IntArray) = (vals[middle(vals) - 1] + vals[middle(vals)]) / 2

        private fun middle(vals: IntArray) = vals.size / 2
    },

    Promedio {
        override fun aplicarA(valores: IntStream): Int {
            return valores.average()
                    .orElseThrow { NoSePudoRealizarOperacionDeAgregacionError("No se pudo obtener promedio.") }
                    .toInt()
        }
    },

    Sumatoria {
        override fun aplicarA(valores: IntStream): Int = valores.sum()
    },

    Ultimo {
        override fun aplicarA(valores: IntStream): Int {
            val vals = valores.toArray()
            return vals[vals.size - 1]
        }
    },

    Variacion {
        override fun aplicarA(valores: IntStream): Int {
            var variacionAcumulada = 0
            var i = 0
            val vals = valores.toArray()
            while (quedanValoresAEvaluar(i, vals)) {
                variacionAcumulada += variacionEntre(vals[i], vals[i + 1])
                i++
            }
            return variacionAcumulada
        }

        private fun quedanValoresAEvaluar(i: Int, vals: IntArray) = i < vals.size - 1

        private fun variacionEntre(valor1: Int, valor2: Int) = Math.abs(valor1 - valor2)
    };

    abstract fun aplicarA(valores: IntStream): Int

}

internal class NoSePudoRealizarOperacionDeAgregacionError(e: String) : RuntimeException(e)
