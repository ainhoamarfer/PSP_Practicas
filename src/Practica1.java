import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Practica1 {
    //Ejercicio 1 — N instancias del Bloc de notas, avisando de cada cierre
    //Escribe un programa que pida un número entero por teclado, lance ese número de instancias del Bloc de notas,
    // y a medida que el usuario las vaya cerrando (en el orden real en que las cierre, no necesariamente el de lanzamiento),
    // muestre por consola cuál se ha cerrado — identificándola por su PID o por el número de orden en que se lanzó.
    //Como ejecutable puedes usar cualquiera que esté ya en el path de Windows: calc, notepad, cmd, powershell, etc...
    //Ojo aquí con el waitFor()! (;-P

    static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Cuantas instancias quieres abrir");
        Scanner sc = new Scanner(System.in);
        int cantidad = Integer.parseInt(sc.nextLine());

        String ejecutable = "mspaint.exe";

        List<Process> instancias = new ArrayList<>();

        for(int i = 0; i < cantidad; ++i){
            Process process = new ProcessBuilder(ejecutable).start();
            instancias.add(process);
            System.out.println("Instancia " + (i + 1) + " lanzada PID: " + process.pid());
        }

    }
}
