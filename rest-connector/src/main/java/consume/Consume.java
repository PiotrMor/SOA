package consume;

import model.Student;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import utils.Base64Utils;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Consume {
    private static final String URL = "http://localhost:8080/lab-web/student";

    public static void main(String[] args) {
        List<String> courses = new ArrayList<>();
        courses.add("SOA");
        courses.add("JAVA");

        Student s1 = new Student(123123, "Karol", "Kowalski", 21, courses);
        courses.add("C++");
        Student s2 = new Student(213123, "Łukasz", "Polak", 22, courses);
        System.out.println(addStudent(s1));

        System.out.println(addStudent(s2));

        System.out.println(getStudentList());

        s1.setLastName("Nowak");

        System.out.println(updateStudent(s1));

        System.out.println(getStudent(123123));

        System.out.println(deleteStudent(123123));

        System.out.println(getStudentList());

        getAvatar();
    }

    private static Student getStudent(int id) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URL);
        Response response = target.path(id + "").request().get();
        System.out.println(response.getStatus());
        Student student = null;
        if (response.getStatus() == 200) {
            student = response.readEntity(Student.class);
        }
        client.close();
        return student;
    }


    private static List<Student> getStudentList() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URL);
        Response response = target.request().get();
        List<Student> students = response.readEntity(new GenericType<List<Student>>() {});
        client.close();
        return students;
    }

    private static int addStudent(Student student) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URL);
        Response response = target.request().post(Entity.entity(student, MediaType.APPLICATION_JSON ));
        client.close();
        return response.getStatus();
    }

    private static int deleteStudent(int id) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URL);
        Response response = target.path(id + "").request().delete();
        client.close();
        return response.getStatus();
    }

    private static int updateStudent(Student student) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URL);
        Response response = target.request().put(Entity.entity(student, MediaType.APPLICATION_JSON ));
        client.close();
        return response.getStatus();
    }

    private static void getAvatar() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URL);
        Response response = target.path("avatar").request().get();
        String codedAvatar = response.readEntity(String.class);
        client.close();
        Base64Utils.decoder(codedAvatar, "rest_avatar.png");
        try {
            Runtime.getRuntime().exec("shotwell rest_avatar.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}