Ejercicio 1 — N instancias del Bloc de notas, avisando de cada cierre

Escribe un programa que pida un número entero por teclado, lance ese número de instancias del Bloc de notas, y a medida que el usuario las vaya cerrando (en el orden real en que las cierre, no necesariamente el de lanzamiento), muestre por consola cuál se ha cerrado — identificándola por su PID o por el número de orden en que se lanzó.

Como ejecutable puedes usar cualquiera que esté ya en el path de Windows: calc, notepad, cmd, powershell, etc...

Ojo aquí con el waitFor()! (;-P

Ejercicio 2 — Menú de tres programas por ruta absoluta

Escribe un programa que:

Localiza 3 ejecutables que tengas en Windows, que estén en rutas diferentes.
Haz que tu programa pregunte una ruta por teclado.
Y luego un nombre de ejecutable.
A continuación construya la ruta absoluta completa (carpeta + nombre de ejecutable correspondiente) y lance ese programa.
Cuando el usuario cierre la aplicación lanzada, el programa debe notificarlo por consola junto con el código de salida.
El programa debe funcionar en bucle, es decir, debe poder lanzar tantos ejecutables como se quiera, hasta que se cierre.

Ojo aquí con los escapados y comillas!

Ejercicio 3 — Igual que el anterior, pero con reintentos y salida controlada

Coge el ejercicio 2 y transfórmalo en un bucle: si la ruta introducida no corresponde a un ejecutable real, captura la excepción correspondiente, avisa de que probablemente la ruta esté mal escrita o el programa no exista ahí, y vuelve a pedir carpeta.

El programa debe terminar en dos casos: cuando el lanzamiento tiene éxito, o cuando el usuario escribe 'exit' en el campo de la carpeta.

Ejercicio 4.1 — Calculadora como proceso aparte

Escribe una clase Calculadora que reciba tres argumentos por línea de comandos: primer operando, operador ('+', '-', '*', '/') y segundo operando. Sin validar nada de lo recibido, realiza la operación indicada e imprime el resultado por consola.

Escribe también un lanzador que haga tres preguntas por teclado (primer operando, operación, segundo operando), lance tu clase Calculadora pasándole esas tres respuestas como parámetros, y deje que el resultado se vea directamente en la consola.

Ejercicio 4.2 — Calculadora "especializada" y "acumulativa"

Una variación del anterior. Simplemente en vez de una clase que hace las 4 operaciónes, tendrás 4 clases: Sumador, Restador, Divisor, Multiplicador. Cada una hará una operación.

El main, en primera vuelta pide un primer operando y comienza un bucle:
pide operador -> pide segundo operando -> lanza la clase que hace esa operación -> recibe resultado como código de salida del subproceso ->...

ese código es el resultado parcial, y en el bucle vuelve a pedir otra operación y segundo operando.

La clase que sea devolverá el resultado como código de Salida.
NOTA: haced operaciones que devuelvan enteros, y pequeños.

NOTA: Si te animas, en vez de hacer 2 preguntas, operador y 2º operando, puedes leer una expresión tipo "x5", y coger el primer caracter como operador, y el resto, casteado a entero, como operando. Sería más pro.
