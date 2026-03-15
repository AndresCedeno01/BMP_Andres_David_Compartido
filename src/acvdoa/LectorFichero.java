package acvdoa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class LectorFichero {

	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		int opcion = 0;

		// Primero comprobamos si el directorio de salida existe, si no, lo creamos para
		// evitar excepciones al escribir.
		File carpeta = new File("salida_ficheros");
		if (!carpeta.exists())
			carpeta.mkdir();

		while (opcion != 3) {
			opcion = menu();
			if (opcion == 1)
				generarImagen();
			else if (opcion == 2)
				sobreescribirImagen();
		}

		scanner.close();
	}

	private static void sobreescribirImagen() {
		String nombreFichero = recogerNombre();
		// Instanciamos un objeto File apuntando a la ruta relativa del archivo dentro
		// de la carpeta
		File f = new File("salida_ficheros/", nombreFichero);

		// Comprobamos que el fichero realmente existe en el sistema antes
		if (f.exists()) {
			try {
				// Para este apartado usamos RandomAccessFile en modo "rw" porque nos permite
				// mover el puntero a bytes específicos y no tener que rehacer toda la imagen
				RandomAccessFile raf = new RandomAccessFile(f, "rw");

				// Movemos el puntero a la parte del ancho y alto de la imagen que empieza a
				// partir del byte 18.
				raf.seek(18);

				// Leemos 4 bytes, tenemos que invertir los bytes leídos para obtener el tamaño
				// real.
				int tamanoImagen = Integer.reverseBytes(raf.readInt());
				
				
				int tamanoFigura = recibeTamFig(tamanoImagen, false);

				System.out.println("\n--- Color de la Figura ---");
				int figuraRo = recibeColor("Rojo");
				int figuraVe = recibeColor("Verde");
				int figuraAz = recibeColor("Azul");

				// Calculamos las coordenadas (x, y) donde empieza y termina la figura cuadrada
				// para que quede exactamente centrada en la imagen.
				int inicioX = (tamanoImagen - tamanoFigura) / 2;
				int inicioY = (tamanoImagen - tamanoFigura) / 2;
				int finX = inicioX + tamanoFigura - 1;
				int finY = inicioY + tamanoFigura - 1;

				// Cada fila de píxeles debe ocupar un número de bytes múltiplo de 4
				// obligatoriamente.
				// Si los píxeles no suman un múltiplo de 4, hay que añadir bytes de relleno
				// (padding).
				int bytesFilaSinRelleno = tamanoImagen * 3;
				int relleno = (4 - (bytesFilaSinRelleno % 4)) % 4;

				// La cabecera siempre ocupa 54 bytes
				long posicionActual = 54;

				for (int y = 0; y < tamanoImagen; y++) {
					for (int x = 0; x < tamanoImagen; x++) {

						// Comprobamos si las coordenadas corresponden a los bordes de la figura
						// llamando a los métodos auxiliares correspondientes según la elección del usuario
						if (bordeCuadrado(inicioX, inicioY, x, y, finX, finY)) {

							// Movemos el puntero exactamente al byte del píxel que queremos modificar
							raf.seek(posicionActual);
							// Escribimos los colores al reves
							raf.write(figuraAz);
							raf.write(figuraVe);
							raf.write(figuraRo);

						}

						// Avanzamos 3 bytes que es lo que ocupa un pixel
						posicionActual += 3;
					}

					// Al terminar la fila, sumamos al puntero los bytes de relleno para saltarlos
					// y que la siguiente iteración empiece en el primer píxel real de la siguiente
					// fila.
					posicionActual += relleno;
				}

				// cerramos el flujo
				raf.close();
				System.out.println("\nImagen sobrescrita correctamente!\n");

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("El fichero no existe \n");
		}
	}

	private static void generarImagen() {
		String nombreFichero = recogerNombre();

		int tamanoImagen = recibeTamImg();

		boolean esCirculo = preguntaFigura();

		int tamanoFigura = recibeTamFig(tamanoImagen, esCirculo);

		System.out.println("\n--- Color del Fondo ---");
		int fondoRo = recibeColor("Rojo");
		int fondoVe = recibeColor("Verde");
		int fondoAz = recibeColor("Azul");

		System.out.println("\n--- Color de la Figura ---");
		int figuraRo = recibeColor("Rojo");
		int figuraVe = recibeColor("Verde");
		int figuraAz = recibeColor("Azul");

		generarArchivoBMP(nombreFichero, tamanoImagen, tamanoFigura, fondoRo, fondoVe, fondoAz, figuraRo, figuraVe,
				figuraAz, esCirculo);

		System.out.println("\nImagen generada correctamente!");
	}

	private static boolean preguntaFigura() {
		int opcion = 0;
		while (opcion <= 0)
			try {
				System.out.println("\n--- FORMA DE LA FIGURA ---");
				System.out.println("1. Cuadrado");
				System.out.println("2. Círculo");
				System.out.print("Selecciona una opción: ");
				opcion = scanner.nextInt();
				scanner.nextLine();
				if (opcion <= 0 || opcion > 2) {
					opcion = 0;
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Introduce 1 o 2\n");
			}
		return opcion == 2;
	}

	private static int menu() {
		int opcion = 0;
		while (opcion <= 0)
			try {
				System.out.println("\n--- GENERADOR DE ARCHIVOS BMP ---");
				System.out.println("1. Crear nueva imagen");
				System.out.println("2. Sobreescribir imagen existente");
				System.out.println("3. Salir");

				System.out.print("Selecciona una opción: ");
				opcion = scanner.nextInt();
				scanner.nextLine();
				if (opcion <= 0 || opcion > 3) {
					opcion = 0;
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Introduce un numero entre 1 y 3\n");
			}
		return opcion;
	}

	private static int recibeColor(String color) {
		int colorSeleccionado = -1;
		while (colorSeleccionado < 0)
			try {
				System.out.print(color + " (0-255): ");
				colorSeleccionado = scanner.nextInt();
				scanner.nextLine();
				// Nos aseguramos que el programa no entre en un bucle si el usuario pone un
				// tipo
				// que no sea Int
				if (colorSeleccionado < 0 || colorSeleccionado > 255) {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Introduce un numero entre 0 y 255\n");
				colorSeleccionado = -1; // Volvemos a setear bien la variable
			}
		return colorSeleccionado;
	}

	private static int recibeTamFig(int tamanoImagen, boolean esCirculo) {
		int tamanoFigura = 0;
		while (tamanoFigura < 1)
			try {
				System.out.print("Introduce el tamaño" + (esCirculo ? (" del radio(1-" + (tamanoImagen / 2) + "): ")
						: ("del cuadrado a introducir(1-" + (tamanoImagen - 2) + "): ")));
				tamanoFigura = scanner.nextInt();
				scanner.nextLine();
				if (tamanoFigura < 1 || tamanoFigura > (esCirculo ? (tamanoImagen / 2) : (tamanoImagen - 2))) {
					tamanoFigura = 0;
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Introduce un numero entre 1 y "
						+ (esCirculo ? (tamanoImagen / 2) : (tamanoImagen - 2)) + "\n");
				tamanoFigura = 0;
			}
		return tamanoFigura;
	}

	private static int recibeTamImg() {
		int tamanoImagen = 0;
		while (tamanoImagen < 3)
			try {
				System.out.print("Introduce el tamaño de la imagen: ");
				tamanoImagen = scanner.nextInt();
				scanner.nextLine();
				if (tamanoImagen < 3) {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Introduce un numero mayor que 3\n");
				tamanoImagen = 0;
			}
		return tamanoImagen;
	}

	private static String recogerNombre() {
		System.out.print("Introduce el nombre de la imagen: ");
		String nombreFichero = scanner.nextLine();
		// Comprobamos la extensión para asegurar que siempre guardamos un archivo
		// manejable por la aplicación
		if (!nombreFichero.toLowerCase().endsWith(".bmp")) {
			nombreFichero += ".bmp";
		}
		return nombreFichero;
	}

	// Vamos desplazando los bits y haciendo módulo 256 para escribir desde el byte
	// menos significativo al más significativo.
	private static void escribir4Bytes(FileOutputStream fos, int valor) {

		try {
			fos.write(valor % 256);
			fos.write((valor / 256) % 256);
			fos.write((valor / 65536) % 256);
			fos.write((valor / 16777216) % 256);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Método auxiliar idéntico al anterior pero para datos que en BMP ocupan solo 2
	// bytes
	private static void escribir2Bytes(FileOutputStream fos, int valor) {
		try {
			fos.write(valor % 256);
			fos.write((valor / 256) % 256);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void generarArchivoBMP(String nombreFichero, int tamanoImagen, int tamanoFigura, int fondoRo,
			int fondoVe, int fondoAz, int figuraRo, int figuraVe, int figuraAz, boolean esCirculo) {

		File f = new File("salida_ficheros", nombreFichero);

		// Volvemos a calcular el padding
		int bytesFilaSinRelleno = tamanoImagen * 3;
		int relleno = (4 - (bytesFilaSinRelleno % 4)) % 4;

		// Calculamos el tamaño total del array de píxeles sumando los bytes de píxel
		// reales más el padding por fila
		int tamPixeles = (bytesFilaSinRelleno + relleno) * tamanoImagen;
		// El peso total del fichero
		int tamFichero = 14 + 40 + tamPixeles;

		int inicioX = (tamanoImagen - tamanoFigura) / 2;
		int inicioY = (tamanoImagen - tamanoFigura) / 2;
		int finX = inicioX + tamanoFigura - 1;
		int finY = inicioY + tamanoFigura - 1;

		try {

			// FileOutputStream es estrictamente necesario aquí porque estamos generando un
			// archivo binario, no texto.
			FileOutputStream fos = new FileOutputStream(f);

			// Cabecera principal
			// La firma que pide al iniciar. 2 bytes
			fos.write('B');
			fos.write('M');

			// Cuánto pesa el archivo total
			escribir4Bytes(fos, tamFichero);

			// Reservado (4 bytes)
			escribir4Bytes(fos, 0);

			// Offset donde empiezan los datos de la imagen en sí
			escribir4Bytes(fos, 54);

			// Cabecera de información
			// Tamaño de esta cabecera
			escribir4Bytes(fos, 40);
			// Ancho
			escribir4Bytes(fos, tamanoImagen);
			// Alto
			escribir4Bytes(fos, tamanoImagen);

			// Planos de color, siempre debe ser 1 (2 bytes)
			escribir2Bytes(fos, 1);
			// la profundidad de color son 24 bits por píxel, 8 por canal BGR
			escribir2Bytes(fos, 24);
			// Compresión de la imagen
			escribir4Bytes(fos, 0);
			// Tamaño de los datos, incluyendo padding
			escribir4Bytes(fos, tamPixeles);
			// Resoluciones de impresión.
			escribir4Bytes(fos, 0);
			escribir4Bytes(fos, 0);
			// Número de colores en la paleta, 0 por defecto en 24bits y colores importantes
			// igual 0
			escribir4Bytes(fos, 0);
			escribir4Bytes(fos, 0);

			// Comienza el pintado de píxeles.
			// Empezamos el bucle 'y' desde tamanoImagen - 1 bajando hasta 0 por que el BMP
			// comienza da abajo hacia arriba
			for (int y = tamanoImagen - 1; y >= 0; y--) {
				for (int x = 0; x < tamanoImagen; x++) {
					// Base de la figura (circulo o cuadrado)
					
					// Comprobamos si el píxel forma parte de la figura haciendo uso de los métodos auxiliares
					if ((esCirculo && bordeCirculo(x, y, tamanoFigura, tamanoImagen / 2))
							|| (!esCirculo && bordeCuadrado(inicioX, inicioY, x, y, finX, finY))) {
						// Al igual que al sobrescribir, escribimos en orden BGR , Azul, Verde, Rojo
						// ,por especificación
						fos.write(figuraAz);
						fos.write(figuraVe);
						fos.write(figuraRo);
						// Rellenando fondo de la imagen
					} else {
						fos.write(fondoAz);
						fos.write(fondoVe);
						fos.write(fondoRo);
					}
				}
				// Al finalizar una fila completa de píxeles, si el ancho en bytes no era
				// múltiplo de 4,
				// el bucle escribe tantos ceros como calculamos en el relleno
				for (int p = 0; p < relleno; p++)
					fos.write(0);
			}

			// Cerramos el stream
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean bordeCuadrado(int inicioX, int inicioY, int x, int y, int finX, int finY) {
		return ((y == inicioY || y == finY) && x >= inicioX && x <= finX)
				|| ((x == inicioX || x == finX) && y >= inicioY && y <= finY);
	}

	private static boolean bordeCirculo(int x, int y, int radio, int centro) {
		// Aplicamos el teorema de Pitágoras para conocer la distancia exacta del píxel al centro.
		// Para evitar el uso de Math y mejorar el rendimiento de la CPU, multiplicamos en vez de usar raíces cuadradas.
		int cX = x - centro;
		int cY = y - centro;
		int distanciaCuadrada = (cX * cX) + (cY * cY);
		
		// Como en una cuadrícula de píxeles es difícil dar en el valor exacto, 
		// creamos un margen de tolerancia (anillo) para asegurar que el círculo se pinte sólido y sin cortes.
		int radioInterior = radio - 1;
		
		return distanciaCuadrada >= (radioInterior * radioInterior) && distanciaCuadrada <= (radio * radio);
	}
}