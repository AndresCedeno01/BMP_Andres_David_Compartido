
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class IdeaEsceleto {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Introduce el nombre de la imagen a generar (ej: imagen.bmp): ");
		String nombreFichero = scanner.nextLine();
		if (!nombreFichero.toLowerCase().endsWith(".bmp")) {
			nombreFichero += ".bmp";
		}

		System.out.print("Introduce el tamaño de la imagen (píxeles, será cuadrada): ");
		int tamanoImagen = scanner.nextInt();

		System.out.print("Introduce el tamaño de la figura (línea cuadrada centrada): ");
		int tamanoFigura = scanner.nextInt();

		System.out.println("\n--- Color del Fondo ---");
		System.out.print("Rojo (0-255): ");
		int bgR = scanner.nextInt();
		System.out.print("Verde (0-255): ");
		int bgG = scanner.nextInt();
		System.out.print("Azul (0-255): ");
		int bgB = scanner.nextInt();

		System.out.println("\n--- Color de la Figura (Línea) ---");
		System.out.print("Rojo (0-255): ");
		int fgR = scanner.nextInt();
		System.out.print("Verde (0-255): ");
		int fgG = scanner.nextInt();
		System.out.print("Azul (0-255): ");
		int fgB = scanner.nextInt();

		generarArchivoBMP(nombreFichero, tamanoImagen, tamanoFigura, bgR, bgG, bgB, fgR, fgG, fgB);

		System.out.println("\n¡Imagen generada con éxito en: " + nombreFichero + "!");
		scanner.close();
	}

	private static void generarArchivoBMP(String nombre, int tamImagen, int tamFigura, int bgR, int bgG, int bgB,
			int fgR, int fgG, int fgB) {

		// El formato BMP requiere que cada fila de píxeles sea múltiplo de 4 bytes.
		// Calculamos los bytes de relleno (padding) necesarios al final de cada fila.
		int padding = (4 - ((tamImagen * 3) % 4)) % 4;

		// 14 bytes de cabecera general + 40 bytes de cabecera DIB + datos de píxeles
		int tamanoFichero = 54 + ((tamImagen * 3) + padding) * tamImagen;

		try (FileOutputStream fos = new FileOutputStream(nombre)) {
			// ==========================================
			// 1. CABECERA DEL ARCHIVO BMP (14 bytes)
			// ==========================================
			fos.write('B'); // Firma
			fos.write('M'); // Firma
			escribirIntLittleEndian(fos, tamanoFichero); // Tamaño total del archivo
			escribirIntLittleEndian(fos, 0); // Reservado
			escribirIntLittleEndian(fos, 54); // Offset (donde empiezan los píxeles)

			// ==========================================
			// 2. CABECERA DIB / BITMAPINFOHEADER (40 bytes)
			// ==========================================
			escribirIntLittleEndian(fos, 40); // Tamaño de esta cabecera
			escribirIntLittleEndian(fos, tamImagen); // Ancho de la imagen
			escribirIntLittleEndian(fos, tamImagen); // Alto de la imagen
			fos.write(1);
			fos.write(0); // Planos de color (siempre 1)
			fos.write(24);
			fos.write(0); // Bits por píxel (24 bits = RGB sin canal alfa)
			escribirIntLittleEndian(fos, 0); // Compresión (0 = sin compresión)
			escribirIntLittleEndian(fos, 0); // Tamaño de la imagen (se puede dejar en 0)
			escribirIntLittleEndian(fos, 0); // Resolución horizontal
			escribirIntLittleEndian(fos, 0); // Resolución vertical
			escribirIntLittleEndian(fos, 0); // Colores en la paleta
			escribirIntLittleEndian(fos, 0); // Colores importantes

			// ==========================================
			// 3. DATOS DE LOS PÍXELES
			// ==========================================
			// Calculamos los límites para dibujar el cuadrado centrado
			int inicioCuadrado = (tamImagen - tamFigura) / 2;
			int finCuadrado = inicioCuadrado + tamFigura - 1;

			// BMP guarda los píxeles de abajo hacia arriba y de izquierda a derecha
			for (int y = 0; y < tamImagen; y++) {
				for (int x = 0; x < tamImagen; x++) {

					// Condición para saber si estamos en el borde del cuadrado
					boolean esLineaHorizontal = (y == inicioCuadrado || y == finCuadrado)
							&& (x >= inicioCuadrado && x <= finCuadrado);
					boolean esLineaVertical = (x == inicioCuadrado || x == finCuadrado)
							&& (y >= inicioCuadrado && y <= finCuadrado);

					if (esLineaHorizontal || esLineaVertical) {
						// Color de la figura. NOTA: BMP usa el orden BGR (Azul, Verde, Rojo)
						fos.write(fgB);
						fos.write(fgG);
						fos.write(fgR);
					} else {
						// Color del fondo (BGR)
						fos.write(bgB);
						fos.write(bgG);
						fos.write(bgR);
					}
				}

				// Añadir los bytes de padding al final de la fila
				for (int p = 0; p < padding; p++) {
					fos.write(0);
				}
			}
		} catch (IOException e) {
			System.out.println("Ocurrió un error al escribir el archivo: " + e.getMessage());
		}
	}

	/**
	 * El formato BMP requiere que los enteros se guarden en formato "Little-Endian"
	 * (el byte menos significativo primero). Java por defecto usa "Big-Endian".
	 * Este método auxiliar convierte y escribe el entero correctamente byte a byte.
	 */
	private static void escribirIntLittleEndian(FileOutputStream fos, int valor) throws IOException {
		fos.write(valor & 0xFF);
		fos.write((valor >> 8) & 0xFF);
		fos.write((valor >> 16) & 0xFF);
		fos.write((valor >> 24) & 0xFF);
	}
}