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
		File f = new File("salida_ficheros/", nombreFichero);

		if (f.exists()) {
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "rw");

				raf.seek(18);
				int tamanoImagen = Integer.reverseBytes(raf.readInt());
				int tamanoFigura = recibeTamFig(tamanoImagen);

				System.out.println("\n--- Color de la Figura ---");
				int figuraRo = recibeColor("Rojo");
				int figuraVe = recibeColor("Verde");
				int figuraAz = recibeColor("Azul");

				int inicioX = (tamanoImagen - tamanoFigura) / 2;
				int inicioY = (tamanoImagen - tamanoFigura) / 2;
				int finX = inicioX + tamanoFigura - 1;
				int finY = inicioY + tamanoFigura - 1;

				int bytesFilaSinRelleno = tamanoImagen * 3;
				int relleno = (4 - (bytesFilaSinRelleno % 4)) % 4;
				long posicionActual = 54;

				for (int y = 0; y < tamanoImagen; y++) {
					for (int x = 0; x < tamanoImagen; x++) {

						if ((y == inicioY || y == finY) && x >= inicioX && x <= finX) {

							raf.seek(posicionActual);
							raf.write(figuraAz);
							raf.write(figuraVe);
							raf.write(figuraRo);

						} else if ((x == inicioX || x == finX) && y >= inicioY && y <= finY) {

							raf.seek(posicionActual);
							raf.write(figuraAz);
							raf.write(figuraVe);
							raf.write(figuraRo);
						}

						posicionActual += 3;
					}

					posicionActual += relleno;
				}

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

		int tamanoFigura = recibeTamFig(tamanoImagen);

		System.out.println("\n--- Color del Fondo ---");
		int fondoRo = recibeColor("Rojo");
		int fondoVe = recibeColor("Verde");
		int fondoAz = recibeColor("Azul");

		System.out.println("\n--- Color de la Figura ---");
		int figuraRo = recibeColor("Rojo");
		int figuraVe = recibeColor("Verde");
		int figuraAz = recibeColor("Azul");

		generarArchivoBMP(nombreFichero, tamanoImagen, tamanoFigura, fondoRo, fondoVe, fondoAz, figuraRo, figuraVe,
				figuraAz);

		System.out.println("\nImagen generada correctamente!");
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
				// Nos aseguramos que el programa no entre un bucle si el usuario pone un tipo
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

	private static int recibeTamFig(int tamanoImagen) {
		int tamanoFigura = 0;
		while (tamanoFigura < 1)
			try {
				System.out.print("Introduce el tamaño del cuadrado a introducir (1-" + (tamanoImagen - 2) + "): ");
				tamanoFigura = scanner.nextInt();
				scanner.nextLine();
				if (tamanoFigura < 1 || tamanoFigura > tamanoImagen - 2) {
					tamanoFigura = 0;
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Introduce un numero entre 1 y " + (tamanoImagen - 2) + "\n");
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
		System.out.print("Introduce el nombre de la imagen a generar: ");
		String nombreFichero = scanner.nextLine();
		if (!nombreFichero.toLowerCase().endsWith(".bmp")) {
			nombreFichero += ".bmp";
		}
		return nombreFichero;
	}

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

	private static void escribir2Bytes(FileOutputStream fos, int valor) {
		try {
			fos.write(valor % 256);
			fos.write((valor / 256) % 256);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void generarArchivoBMP(String nombreFichero, int tamanoImagen, int tamanoFigura, int fondoRo,
			int fondoVe, int fondoAz, int figuraRo, int figuraVe, int figuraAz) {

		File f = new File("salida_ficheros", nombreFichero);

		int bytesFilaSinRelleno = tamanoImagen * 3;
		int relleno = (4 - (bytesFilaSinRelleno % 4)) % 4;
		int tamPixeles = (bytesFilaSinRelleno + relleno) * tamanoImagen;
		int tamFichero = 14 + 40 + tamPixeles;

		int inicioX = (tamanoImagen - tamanoFigura) / 2;
		int inicioY = (tamanoImagen - tamanoFigura) / 2;
		int finX = inicioX + tamanoFigura - 1;
		int finY = inicioY + tamanoFigura - 1;

		try {
			FileOutputStream fos = new FileOutputStream(f);
			// Cabecera principal (14 bytes)
			// La firma que pide al iniciar
			fos.write('B');
			fos.write('M');

			// Cuánto pesa el archivo total
			escribir4Bytes(fos, tamFichero);
			escribir4Bytes(fos, 0);
			// Reservado, siempre 0
			escribir4Bytes(fos, 54);

			escribir4Bytes(fos, 40);
			// Ancho
			escribir4Bytes(fos, tamanoImagen);
			// Alto
			escribir4Bytes(fos, tamanoImagen);

			escribir2Bytes(fos, 1);
			escribir2Bytes(fos, 24);
			escribir4Bytes(fos, 0);
			escribir4Bytes(fos, tamPixeles);
			escribir4Bytes(fos, 2835);
			escribir4Bytes(fos, 2835);
			escribir4Bytes(fos, 0);
			escribir4Bytes(fos, 0);

			for (int y = tamanoImagen - 1; y >= 0; y--) {
				for (int x = 0; x < tamanoImagen; x++) {

					if ((y == inicioY || y == finY) && x >= inicioX && x <= finX) { // Pintando techo o base del
																					// cuadrado
						fos.write(figuraAz);
						fos.write(figuraVe);
						fos.write(figuraRo);

					} else if ((x == inicioX || x == finX) && y >= inicioY && y <= finY) { // Pintando laterales del
																							// cuadrado
						fos.write(figuraAz);
						fos.write(figuraVe);
						fos.write(figuraRo);

					} else { // Rellenando fondo de la imagen
						fos.write(fondoAz);
						fos.write(fondoVe);
						fos.write(fondoRo);
					}
				}
				for (int p = 0; p < relleno; p++) // Rellena los margenes de la imagen si necesita padding
					fos.write(0);
			}

			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}