import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

// Proceso hijo.
// Suma 1 al número guardado en el fichero, muchas veces.
// Recibe dos argumentos:
//   args[0] -> nombre del fichero
//   args[1] -> "true" para usar FileLock, "false" para no usarlo
public class Hijo {

    // Número de veces que cada hijo suma 1
    static final int NUMERO_DE_SUMAS = 1000;

    public static void main(String[] args) throws Exception {

        // Leer los argumentos que manda el padre
        String nombreFichero = args[0];
        boolean usarBloqueo = Boolean.parseBoolean(args[1]);

        // Abrir el fichero en modo lectura y escritura
        RandomAccessFile fichero = new RandomAccessFile(nombreFichero, "rw");

        // Obtener el canal del fichero (necesario para el FileLock)
        FileChannel canal = fichero.getChannel();

        // Repetir la suma varias veces
        for (int i = 0; i < NUMERO_DE_SUMAS; i++) {

            // Aquí se guardará el bloqueo, si se usa
            FileLock bloqueo = null;

            // Pedir el bloqueo antes de entrar en la sección crítica
            // Si otro proceso lo tiene, este proceso espera aquí
            if (usarBloqueo) {
                bloqueo = canal.lock();
            }

            // ---------- INICIO DE LA SECCIÓN CRÍTICA ----------

            // Ir al principio del fichero
            fichero.seek(0);

            // Leer la línea con el número
            String linea = fichero.readLine();

            // Convertir el texto a número (0 si está vacío)
            int valorActual = 0;
            if (linea != null && !linea.isBlank()) {
                valorActual = Integer.parseInt(linea.trim());
            }

            // Calcular el nuevo valor
            int valorNuevo = valorActual + 1;

            // Volver al principio para sobrescribir
            fichero.seek(0);

            // Escribir el nuevo valor (con espacios hasta 10 caracteres
            // para que siempre ocupe lo mismo)
            fichero.writeBytes(String.format("%-10d", valorNuevo));

            // ---------- FIN DE LA SECCIÓN CRÍTICA ----------

            // Liberar el bloqueo para que entre otro proceso
            if (bloqueo != null) {
                bloqueo.release();
            }
        }

        // Cerrar el fichero
        fichero.close();
    }
}