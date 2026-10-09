import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Proceso padre.
// Pone el contador a 0, lanza varios hijos y muestra el resultado.
// Uso:
//   java Padre true    -> los hijos usan FileLock
//   java Padre false   -> los hijos no usan FileLock
public class Padre {

    // Número de procesos hijo que se lanzan
    static final int NUMERO_DE_HIJOS = 4;

    // Número de sumas que hace cada hijo (debe coincidir con Hijo.java)
    static final int SUMAS_POR_HIJO = 1000;

    // Fichero compartido por todos los hijos
    static final String NOMBRE_FICHERO = "contador.txt";

    public static void main(String[] args) throws Exception {

        // Leer si se usa bloqueo (por defecto, no)
        boolean usarBloqueo = true;
        if (args.length > 0) {
            usarBloqueo = Boolean.parseBoolean(args[0]);
        }

        // Escribir 0 en el fichero para empezar
        Files.writeString(Path.of(NOMBRE_FICHERO), String.format("%-10d", 0));

        // Obtener el classpath actual, para que los hijos encuentren Hijo.class
        String rutaClases = System.getProperty("java.class.path");

        // Lista para guardar los procesos hijo
        List<Process> listaHijos = new ArrayList<>();

        // Lanzar los hijos
        for (int i = 0; i < NUMERO_DE_HIJOS; i++) {

            // Preparar el comando: java -cp <ruta> Hijo <fichero> <usarBloqueo>
            ProcessBuilder constructor = new ProcessBuilder(
                    "java", "-cp", rutaClases, "Hijo",
                    NOMBRE_FICHERO, String.valueOf(usarBloqueo));

            // Mostrar en la consola del padre lo que escriban los hijos
            constructor.inheritIO();

            // Arrancar el hijo y guardarlo en la lista
            Process hijo = constructor.start();
            listaHijos.add(hijo);
        }

        // Esperar a que terminen todos los hijos
        for (Process hijo : listaHijos) {
            hijo.waitFor();
        }

        // Leer el resultado final del fichero
        String resultado = Files.readString(Path.of(NOMBRE_FICHERO)).trim();

        // Calcular el resultado que debería salir
        int resultadoEsperado = NUMERO_DE_HIJOS * SUMAS_POR_HIJO;

        // Mostrar los resultados
        System.out.println("Con bloqueo: " + usarBloqueo);
        System.out.println("Esperado:    " + resultadoEsperado);
        System.out.println("Obtenido:    " + resultado);
    }
}