package ExerciciosREPASOprocesos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Ejercicio 1
 * Escribe un programa en Java que reciba una dirección IP como argumento de línea de comandos,
 * cree un proceso para ejecutar un ping a la IP proporcionada y muestre la salida del ping en pantalla.
 */
public class Ej1 {

    public static void main(String[] args) throws Exception {

        System.out.println("Dime una IP");
        Scanner sc = new Scanner(System.in);
        String ip = sc.nextLine();

        if (args.length > 0) {
            ip = args[0];
        } else {
            java.util.Scanner sc = new java.util.Scanner(System.in);
            System.out.print("Dime una IP: ");
            ip = sc.nextLine();
        }

        ProcessBuilder pb = new ProcessBuilder("ping", ip);
        Process p = pb.start();

        BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream())
        );

        String linea;
        while ((linea = br.readLine()) != null) {
            System.out.println(linea);
        }

        int codigo = p.waitFor();
        System.out.println("Proceso terminado con código: " + codigo);
    }

    }
