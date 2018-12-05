package server;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import dominio.empresas.Empresa;
import dominio.empresas.LectorArchivos;
import dominio.empresas.RepositorioEmpresas;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager;
import org.uqbarproject.jpa.java8.extras.transaction.TransactionalOps;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class CargarEmpresas implements Job, WithGlobalEntityManager, TransactionalOps {

	AmazonS3 s3Client;
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		new CargarEmpresas().cargarArchivos();
	}

	private void cargarArchivos() {
		initAmazonClient();
		List<File> archivosALeer = this.getArchivosALeer();
		archivosALeer.forEach(archivo -> this.cargarArchivo(archivo));
	}

	private void initAmazonClient() {
		s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("FranGonzalezL", "ChinoRico123")))
				.withRegion("us-east-1")
				.build();
	}
	
	private List<File> getArchivosALeer() {
			return s3Client.listObjects(new ListObjectsRequest().withBucketName(nombreBucketPorLeer()))
					.getObjectSummaries()
					.stream()
					.map(summary -> this.descargarArchivo(summary.getKey()))
					.collect(Collectors.toList());
	}
	
	public File descargarArchivo(String nombreDelArchivo) {
		File localFile = new File(nombreDelArchivo);
		s3Client.getObject(new GetObjectRequest(nombreBucketPorLeer(), nombreDelArchivo), localFile);
		return localFile;
	}
	
	private void cargarArchivo(File archivo) {
		List<Empresa> empresas = new LectorArchivos(archivo.getAbsolutePath()).getEmpresas();
		withTransaction(() -> new RepositorioEmpresas().agregarMultiplesEmpresas(empresas));
		moverALeidos(archivo);
	}
	
	private void moverALeidos(File archivo) {
		s3Client.copyObject(nombreBucketPorLeer(), archivo.getName(), nombreBucketLeidos(), archivo.getName());
		s3Client.deleteObject(nombreBucketPorLeer(), archivo.getName());
	}
	
	private String nombreBucketPorLeer(){
		return "por-leer";
	}
	
	private String nombreBucketLeidos(){
		return "ya-leidos";
	}

}
