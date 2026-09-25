import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Practica1 {

    static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Escribe un numero entero");
        Scanner sc = new Scanner(System.in);
        int numero = sc.nextInt();

        ProcessBuilder constructor = new ProcessBuilder("");
        Process proceso = constructor.start();

        BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        String resultado = lector.readLine();

        proceso.waitFor();

        System.out.println(proceso.pid());

    }
}
