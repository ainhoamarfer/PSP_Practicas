package ejercicioBuscaPalabra;

import java.io.IOException;
import java.util.Scanner;

public class Lanzador {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        System.out.print("Introduce el nombre del fichero: ");
        String fichero = teclado.nextLine();

        System.out.print("Introduce las palabras separadas por comas: ");
        String entrada = teclado.nextLine();

        // Separamos las palabras utilizando las comas
        String[] palabras = entrada.split(",");

        // Lanzamos un proceso por cada palabra, secuencialmente
        for (String palabra : palabras) {

            palabra = palabra.trim();

            if (palabra.isEmpty()) {
                continue;
            }

            System.out.println("\nBuscando: " + palabra);

            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "java",
                        "-cp",
                        System.getProperty("java.class.path"),
                        "C:\\Users\\ainho\\Desktop\\2ºDAM\\PSP\\PSP_Practicas\\src\\ejercicioBuscaPalabra\\BuscaPalabra.java",
                        fichero,
                        palabra
                );

                // El hijo muestra su salida directamente en la consola
                pb.inheritIO();

                Process proceso = pb.start();

                // Esperamos a que termine el proceso actual
                int resultado = proceso.waitFor();

                System.out.println(
                        "El proceso ha terminado. Código devuelto: "
                                + resultado
                );

            } catch (IOException e) {
                System.err.println(
                        "No se pudo iniciar el proceso: " + e.getMessage()
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Se interrumpió la espera del proceso.");
                return;
            }
        }

        teclado.close();
        System.out.println("\nBúsquedas finalizadas.");
    }
}