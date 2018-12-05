package dominio.metodologias;

public enum OperacionRelacional {
	
	Mayor{
		public boolean aplicarA(int num1, int num2) {
			return num1 > num2;
		}
		public String toString(){
			return ">";
		}
	},
	
	Menor{
		public boolean aplicarA(int num1, int num2) {
			return num1 < num2;
		}
		public String toString(){
			return "<";
		}
	},
	
	Igual{
		public boolean aplicarA(int num1, int num2) {
			return num1 == num2;
		}
		public String toString(){
			return "=";
		}
	};
	
	public abstract boolean aplicarA(int num1, int num2);
	public abstract String toString();

}
