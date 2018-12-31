package scheduler;

import repo.RepositorioUsuarios;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;

public class SchedulerMain {

    public static void main(String[] args) throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail jobDetailEliminarIndicadoresPrecalculados = newJob(EliminarIndicadoresPrecalculados.class).build();

        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(repeatSecondlyForever(2592000))
                .build();

        scheduler.scheduleJob(jobDetailEliminarIndicadoresPrecalculados, trigger);
    }
    
    public static class EliminarIndicadoresPrecalculados implements Job {
    	@Override
        public void execute(JobExecutionContext jobExecutionContext) {
    		new RepositorioUsuarios().eliminarIndicadoresPrecalculados();
       }
    }
    
}


