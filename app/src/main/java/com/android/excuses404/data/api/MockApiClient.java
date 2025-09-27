package com.android.excuses404.data.api;

import com.android.excuses404.data.api.model.ClassesResponse;
import com.android.excuses404.models.Class;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock ApiClient local - Simula la respuesta del servidor sin red
 * Ideal para testing sin necesidad de servidor externo
 */
public class MockApiClient {

    public static ClassesApiService getApi() {
        return new MockClassesApiService();
    }

    private static class MockClassesApiService implements ClassesApiService {

        @Override
        public Call<ClassesResponse> getAllClasses(String token) {
            return new MockCall();
        }
    }

    private static class MockCall implements Call<ClassesResponse> {

        @Override
        public Response<ClassesResponse> execute() {
            ClassesResponse mockResponse = createMockResponse();
            return Response.success(mockResponse);
        }

        @Override
        public void enqueue(Callback<ClassesResponse> callback) {
            // Simular respuesta del servidor con un pequeño delay
            new Thread(() -> {
                try {
                    Thread.sleep(500); // Simular latencia de red
                    ClassesResponse mockResponse = createMockResponse();
                    Response<ClassesResponse> response = Response.success(mockResponse);

                    // Ejecutar callback en el hilo principal usando post
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onResponse(this, response));
                } catch (InterruptedException e) {
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(() -> callback.onFailure(this, e));
                }
            }).start();
        }

        private ClassesResponse createMockResponse() {
            List<Class> classes = new ArrayList<>();

            // Clase 1
            Class class1 = new Class();
            class1.setClassId("1");
            class1.setProfessorFirstName("Ana");
            class1.setProfessorLastName("García");
            class1.setGymName("Gimnasio Central");
            class1.setGymCity("Buenos Aires");
            class1.setGymAddress("Av. Corrientes 1234");
            class1.setDisciplineName("Yoga");
            class1.setScheduledAt("2025-09-25 08:00:00");
            class1.setMaxParticipants(15);
            classes.add(class1);

            // Clase 2
            Class class2 = new Class();
            class2.setClassId("2");
            class2.setProfessorFirstName("Carlos");
            class2.setProfessorLastName("Rodríguez");
            class2.setGymName("Urban Gym Palermo");
            class2.setGymCity("Buenos Aires");
            class2.setGymAddress("Palermo 456");
            class2.setDisciplineName("Spinning");
            class2.setScheduledAt("2025-09-25 18:30:00");
            class2.setMaxParticipants(18);
            classes.add(class2);

            // Clase 3
            Class class3 = new Class();
            class3.setClassId("3");
            class3.setProfessorFirstName("María");
            class3.setProfessorLastName("López");
            class3.setGymName("FitZone Córdoba");
            class3.setGymCity("Córdoba");
            class3.setGymAddress("San Martín 567");
            class3.setDisciplineName("CrossFit");
            class3.setScheduledAt("2025-09-25 19:00:00");
            class3.setMaxParticipants(20);
            classes.add(class3);

            // Clase 4
            Class class4 = new Class();
            class4.setClassId("4");
            class4.setProfessorFirstName("Juan");
            class4.setProfessorLastName("Pérez");
            class4.setGymName("PowerGym Rosario");
            class4.setGymCity("Rosario");
            class4.setGymAddress("Mitre 890");
            class4.setDisciplineName("Pilates");
            class4.setScheduledAt("2025-09-26 07:30:00");
            class4.setMaxParticipants(12);
            classes.add(class4);

            // Clase 5
            Class class5 = new Class();
            class5.setClassId("5");
            class5.setProfessorFirstName("Laura");
            class5.setProfessorLastName("Fernández");
            class5.setGymName("Fitness Club Mendoza");
            class5.setGymCity("Mendoza");
            class5.setGymAddress("San Juan 321");
            class5.setDisciplineName("Zumba");
            class5.setScheduledAt("2025-09-26 20:00:00");
            class5.setMaxParticipants(25);
            classes.add(class5);

            ClassesResponse response = new ClassesResponse();
            response.setSuccess(true);
            response.setMessage("Clases obtenidas correctamente (mock local)");
            response.setClasses(classes);

            return response;
        }

        // Métodos requeridos por la interfaz Call pero no utilizados en este mock
        @Override public boolean isExecuted() { return false; }
        @Override public void cancel() {}
        @Override public boolean isCanceled() { return false; }
        @Override public Call<ClassesResponse> clone() { return new MockCall(); }
        @Override public Request request() { return null; }
        @Override public okio.Timeout timeout() { return okio.Timeout.NONE; }
    }
}
