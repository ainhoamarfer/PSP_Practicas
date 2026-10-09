package ejercicioBuscaPalabra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BuscaPalabra {

    public static void main(String[] args) {

        // Comprobamos que recibimos los dos argumentos
        if (args.length != 2) {
            System.out.println("Uso: BuscaPalabra fichero palabra");
            System.exit(1);
        }

        String nombreFichero = args[0];
        String palabraBuscada = args[1];

        int total = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(nombreFichero))) {

            String linea;

            // Leemos el fichero línea por línea
            while ((linea = lector.readLine()) != null) {

                String[] palabras = linea.split("\\s+");

                // Comprobamos cada palabra de la línea
                for (String palabra : palabras) {

                    // Quitamos algunos signos de puntuación
                    String limpia = palabra.replaceAll("^[.,;:!?¡¿()\\[\\]\"']+|[.,;:!?¡¿()\\[\\]\"']+$", "");

                    if (limpia.equalsIgnoreCase(palabraBuscada)) {
                        total++;
                    }
                }
            }

            System.out.println("Total de apariciones de " + palabraBuscada + ": " + total);

            // Devolvemos el total como código de salida
            System.exit(total);

        } catch (IOException e) {
            System.err.println("Error al leer el fichero: " + e.getMessage());
            System.exit(1);
        }
    }
}
