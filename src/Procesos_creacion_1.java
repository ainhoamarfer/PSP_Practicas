import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Procesos_creacion_1 {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner teclado = new Scanner(System.in);
        System.out.print("¿Cuántas instancias quieres abrir? ");
        int cantidad = Integer.parseInt(teclado.nextLine());

        String ejecutable = "mspaint.exe";

        List<Process> instancias = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            Process proceso = new ProcessBuilder(ejecutable).start();
            instancias.add(proceso);
            System.out.println("Instancia " + (i + 1) + " lanzada (PID " + proceso.pid() + ")");
        }


        boolean[] estado = new boolean[instancias.size()];
        int cerrados = 0;

        while (cerrados < instancias.size()) {
            for (int i = 0; i < instancias.size(); i++) {

                if (!estado[i] && !instancias.get(i).isAlive()) {
                    System.out.println("Instancia " + (i + 1)
                            + " (PID " + instancias.get(i).pid() + ") se ha cerrado");
                    estado[i] = true;
                    cerrados++;
                }
            }
            Thread.sleep(300);
        }

        System.out.println("Todas las instancias se han cerrado.");
    }
}
