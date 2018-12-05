package dominio.metodologias;

import java.util.stream.IntStream;

public enum OperacionAgregacion {
	
	Mediana{
		public int aplicarA(IntStream valores) {
			int[] vals = valores.sorted().toArray();
			if (!esCantidadPar(vals)) {
				return vals[middle(vals)];
			} else {
				return promedioDeValoresQueEstanEnElMedio(vals);
			}
		}
		private Boolean esCantidadPar(int[] vals) {		
			return vals.length%2 == 0;
		}
		private int promedioDeValoresQueEstanEnElMedio(int[] vals) {
			return (vals[middle(vals)-1] + vals[middle(vals)]) / 2;
		}
		private int middle(int[] vals) {
			return (vals.length / 2);
		}
		public String toString(){
			return "Mediana";
		}
	},
	
	Promedio{
		public int aplicarA(IntStream valores) {
			return (int) valores.average().orElseThrow(() -> new NoSePudoRealizarOperacionDeAgregacionError("No se pudo obtener promedio."));
		}
		public String toString() {
			return "Promedio";
		}
	},
	
	Sumatoria{
		public int aplicarA(IntStream valores) {
			return valores.sum();
		}
		public String toString() {
			return "Sumatoria";
		}
	},
	
	Ultimo{
		public int aplicarA(IntStream valores) {
			int vals[] = valores.toArray();
			return vals[vals.length-1];
		}
		public String toString() {
			return "Ultimo";
		}
	},
	
	Variacion{
		public int aplicarA(IntStream valores) {
			int variacionAcumulada = 0, i=0;
			int vals[] = valores.toArray();
			while(quedanValoresAEvaluar(i, vals)){
				variacionAcumulada += variacionEntre(vals[i], vals[i+1]);
				i++;
			}
			return variacionAcumulada;
		}
		public String toString() {
			return "Variacion";
		}
		private Boolean quedanValoresAEvaluar(int i, int[] vals) {
			return i<vals.length-1;
		}
		private int variacionEntre(int valor1, int valor2) {
			return Math.abs(valor1 - valor2);
		}
	};
	
	public abstract int aplicarA(IntStream valores);
	public abstract String toString();

}

class NoSePudoRealizarOperacionDeAgregacionError extends RuntimeException{NoSePudoRealizarOperacionDeAgregacionError(String e){super(e);}}
