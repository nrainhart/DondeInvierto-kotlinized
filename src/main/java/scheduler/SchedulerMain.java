package scheduler;

import dominio.usuarios.RepositorioUsuarios;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.CargarEmpresas;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

public class SchedulerMain {

    final static Logger logger = LoggerFactory.getLogger(SchedulerMain.class);

    public static void main(String[] args) throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail jobDetailCargarEmpresas = newJob(CargarEmpresas.class).build();
        JobDetail jobDetailEliminarIndicadoresPrecalculados = newJob(EliminarIndicadoresPrecalculados.class).build();

        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(repeatSecondlyForever(2592000))
                .build();

        scheduler.scheduleJob(jobDetailCargarEmpresas, trigger);
        scheduler.scheduleJob(jobDetailEliminarIndicadoresPrecalculados, trigger);
    }
    
    public static class EliminarIndicadoresPrecalculados implements Job {
    	@Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    		new RepositorioUsuarios().eliminarIndicadoresPrecalculados();
       }
    }
    
}


