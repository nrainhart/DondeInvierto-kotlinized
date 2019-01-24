package server;

import dondeInvierto.dominio.empresas.Cuenta;
import dondeInvierto.dominio.empresas.Empresa;
import repo.RepositorioEmpresas;
import dondeInvierto.dominio.metodologias.*;
import repo.RepositorioUsuarios;
import dondeInvierto.dominio.Usuario;
import excepciones.EntidadExistenteError;
import org.uqbarproject.jpa.java8.extras.EntityManagerOps;
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;

import java.time.Year;
import java.util.Arrays;

public class Bootstrap implements WithGlobalEntityManager, EntityManagerOps, TransactionalOps {

	public void init() {
		withTransaction(() -> {
			cargarUsuarioAdministrador();
			cargarEmpresasPredefinidas();
			cargarIndicadoresPredefinidos();
			cargarMetodologiasPredefinidas();
		});
	}

	private void cargarUsuarioAdministrador() {
		try{
			new RepositorioUsuarios().agregar(new Usuario("admin", "admin"));
		}
		catch (EntidadExistenteError e){
			//Si ya existe el administrador, no hago nada
		}
	}

	private void cargarEmpresasPredefinidas() {
		Empresa sony = new Empresa("Sony", Arrays.asList(new Cuenta(Year.parse("2017"), "freecashflow", 10000),
				new Cuenta(Year.parse("2017"), "cashflow", 13550),
				new Cuenta(Year.parse("2016"), "netooperacionescontinuas", 9500),
				new Cuenta(Year.parse("2016"), "netooperacionesdiscontinuas", 6500),
				new Cuenta(Year.parse("2015"), "ebitda", 4800),
				new Cuenta(Year.parse("2014"), "cashflow", 3520),
				new Cuenta(Year.parse("2013"), "ebitda", 17500)));
		Empresa google = new Empresa("Google", Arrays.asList(new Cuenta(Year.parse("2017"), "freecashflow", 12500),
				new Cuenta(Year.parse("2017"), "oitdba", 13550),
				new Cuenta(Year.parse("2016"), "netooperacionescontinuas", 9500),
				new Cuenta(Year.parse("2016"), "netooperacionesdiscontinuas", 6500),
				new Cuenta(Year.parse("2015"), "ingresoneto", 4800),
				new Cuenta(Year.parse("2014"), "cashflow", 3520),
				new Cuenta(Year.parse("2013"), "ebitda", 17500)));
		Empresa apple = new Empresa("Apple", Arrays.asList(new Cuenta(Year.parse("2017"), "ebitda", 15500),
				new Cuenta(Year.parse("2017"), "cashflow", 13550),
				new Cuenta(Year.parse("2016"), "netooperacionescontinuas", 9500),
				new Cuenta(Year.parse("2016"), "netooperacionesdiscontinuas", 6500),
				new Cuenta(Year.parse("2015"), "ingresoneto", 4800),
				new Cuenta(Year.parse("2014"), "cashflow", 3520),
				new Cuenta(Year.parse("2013"), "ingresoneto", 17500)));
		Empresa amazon = new Empresa("Amazon", Arrays.asList(new Cuenta(Year.parse("2017"), "ebitda", 10200),
				new Cuenta(Year.parse("2017"), "ebitda", 13550),
				new Cuenta(Year.parse("2016"), "netooperacionescontinuas", 9500),
				new Cuenta(Year.parse("2016"), "netooperacionesdiscontinuas", 6500),
				new Cuenta(Year.parse("2015"), "ingresoneto", 4800),
				new Cuenta(Year.parse("2014"), "cashflow", 3520),
				new Cuenta(Year.parse("2013"), "ebitda", 17500)));
		Empresa accenture = new Empresa("Accenture", Arrays.asList(new Cuenta(Year.parse("2017"), "ebitda", 13350),
				new Cuenta(Year.parse("2017"), "oitdba", 15100),
				new Cuenta(Year.parse("2016"), "ebitda", 25500),
				new Cuenta(Year.parse("2015"), "freecashflow", 13500),
				new Cuenta(Year.parse("2014"), "fds", 8500),
				new Cuenta(Year.parse("2013"), "netooperacionescontinuas", 7500),
				new Cuenta(Year.parse("2013"), "netooperacionesdiscontinuas", 4500)));
		Empresa microsoft = new Empresa("Microsoft", Arrays.asList(new Cuenta(Year.parse("2017"), "ebitda", 11500),
				new Cuenta(Year.parse("2016"), "oix", 15400),
				new Cuenta(Year.parse("2015"), "fds", 13500),
				new Cuenta(Year.parse("2014"), "cashflow", 5500),
				new Cuenta(Year.parse("2013"), "freecashflow", 10500),
				new Cuenta(Year.parse("2012"), "ebitda", 4500),
				new Cuenta(Year.parse("2011"), "cashflow", 3500)));
		new RepositorioEmpresas().agregarMultiplesEmpresas(Arrays.asList(sony, google, apple, amazon, accenture, microsoft));
	}

	private void cargarIndicadoresPredefinidos() {
		Long id = new RepositorioUsuarios().obtenerId("admin", "admin");
		Usuario usuario = new RepositorioUsuarios().obtenerPorId(id);
		try{
		usuario.agregarIndicadores(
				Arrays.asList("INGRESONETO = netooperacionescontinuas + netooperacionesdiscontinuas",
						"INDICADORDOS = cuentarara + fds",
						"INDICADORTRES = INGRESONETO * 10 + ebitda",
						"A = 5 / 3",
						"PRUEBA = ebitda + 5"));
		} catch(EntidadExistenteError e) {
			//Si ya existen los indicadores, no hago nada
		}
	}
	
	private void cargarMetodologiasPredefinidas() {
		Metodologia metodologia = new Metodologia("Pay-back");
		metodologia.agregarCondicionPrioritaria(new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Ultimo, new Antiguedad(), 1), OperacionRelacional.Mayor));
		metodologia.agregarCondicionTaxativa(new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo, new Antiguedad(), 1), OperacionRelacional.Menor, 10));
		Metodologia metodologia2 = new Metodologia("VAN");
		metodologia2.agregarCondicionPrioritaria(new CondicionPrioritaria(new OperandoCondicion(OperacionAgregacion.Ultimo, new Antiguedad(), 6), OperacionRelacional.Mayor));
		metodologia2.agregarCondicionTaxativa(new CondicionTaxativa(new OperandoCondicion(OperacionAgregacion.Ultimo, new Antiguedad(), 6), OperacionRelacional.Menor, 6));
		
		Long id = new RepositorioUsuarios().obtenerId("admin", "admin");
		Usuario usuario = new RepositorioUsuarios().obtenerPorId(id);
		
		try{
			usuario.agregarMetodologia(metodologia);
			usuario.agregarMetodologia(metodologia2);
		} catch(EntidadExistenteError e) {
			//Si ya existen las metodologías, no hago nada
		}
	}
}
