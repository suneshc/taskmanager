package it.fds.taskmanager;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.fds.taskmanager.dto.TaskDTO;

/**
 * Basic test suite to test the service layer, it uses an in-memory H2 database. 
 * 
 * TODO Add more and meaningful tests! :)
 *
 * @author <a href="mailto:damiano@searchink.com">Damiano Giampaoli</a>
 * @since 10 Jan. 2018
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TaskServiceJPATest extends Assert{

    @Autowired
    TaskService taskService;
    
    @Test
    public void writeAndReadOnDB() {
        TaskDTO t = new TaskDTO();
        t.setTitle("Test task1");
        t.setStatus(TaskState.NEW.toString().toUpperCase());
        TaskDTO t1 = taskService.saveTask(t);
        TaskDTO tOut = taskService.findOne(t1.getUuid());
        assertEquals("Test task1", tOut.getTitle());
        List<TaskDTO> list = taskService.showList();
        assertEquals(1, list.size());
        
    }
    
    @Test
    public void readUpdateOnDB() {
        TaskDTO t = new TaskDTO();
        t.setTitle("Check the update method");
        t.setPriority("5");
        t.setDescription("Updating the priority of a task");
        t.setStatus(TaskState.NEW.toString().toUpperCase());
        TaskDTO t1 = taskService.saveTask(t);
        TaskDTO t2 = taskService.updateTask(t1);
        TaskDTO tOut = taskService.findOne(t2.getUuid());
        assertNotEquals("null", tOut.getUpdatedat());
        List<TaskDTO> list = taskService.showList();
        assertEquals(1, list.size());
    }
    
   @Test
    public void readStatusPostponedOnDB() {
        TaskDTO t = new TaskDTO();
        t.setTitle("Check postponed status method");
        t.setStatus(TaskState.NEW.toString().toUpperCase());
        TaskDTO t1 = taskService.saveTask(t);
        TaskDTO tOut = taskService.findOne(t1.getUuid());
        Boolean t2 = taskService.postponeTask(tOut.getUuid(), 100);
        TaskDTO tOut1 = taskService.findOne(tOut.getUuid());
        assertEquals("Check postponed status method", tOut.getTitle());
        List<TaskDTO> list = taskService.showList();
        assertEquals(1, list.size());
        assert (tOut1.getStatus()==TaskState.POSTPONED.toString());
        
    }
    
   @Test
    public void readStatusResolvedOnDB(){
       TaskDTO t = new TaskDTO();
       t.setTitle("Check Resolved status method");
       t.setStatus(TaskState.NEW.toString().toUpperCase());
       TaskDTO t1 = taskService.saveTask(t);
       TaskDTO tOut = taskService.findOne(t1.getUuid());
       Boolean t2 = taskService.resolveTask(tOut.getUuid());
       TaskDTO t3 = taskService.findOne(tOut.getUuid());
       assertEquals("Check Resolved status method", tOut.getTitle());
       assert (t3.getStatus()==TaskState.RESOLVED.toString());
       List<TaskDTO> list = taskService.showList();
       assertEquals(1, list.size());
   }
   
    @Test
    public void readUnmarkedPostponeOnDB() {
        TaskDTO t = new TaskDTO();
        t.setTitle("Check unmarked postponed status method");
        t.setStatus(TaskState.POSTPONED.toString().toUpperCase());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 100);
        t.setPostponedat(c);
        TaskDTO t1 = taskService.saveTask(t);
        TaskDTO t2 = taskService.findOne(t1.getUuid());
        taskService.unmarkPostoned();
        assertEquals("Check unmarked postponed status method", t2.getTitle());
        assert (t2.getStatus() == TaskState.RESTORED.toString());
        assertEquals("null", t2.getPostponedat());
        
     }
    

    @EnableJpaRepositories
    @Configuration
    @SpringBootApplication
    public static class EndpointsMain{}
}
