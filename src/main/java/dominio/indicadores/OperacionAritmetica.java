package dominio.indicadores;

public enum OperacionAritmetica {
	
	Mas{
		public int applyAsInt(int operandoIzq, int operandoDer) {
			return operandoIzq + operandoDer;
		}
	},
	
	Menos{
		public int applyAsInt(int operandoIzq, int operandoDer) {
			return operandoIzq - operandoDer;
		}
	},
	
	Por{
		public int applyAsInt(int operandoIzq, int operandoDer) {
			return operandoIzq * operandoDer;
		}
	},
	
	Dividido{
		public int applyAsInt(int operandoIzq, int operandoDer) {
			return operandoIzq / operandoDer;
		}
	};
	
	public abstract int applyAsInt(int operandoIzq, int operandoDer);

}
