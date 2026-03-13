import java.util.Scanner;

public class LectorFichero {

	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

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

		generarArchivoBMP(nombreFichero, tamanoImagen, tamanoFigura, fondoRo, fondoVe, fondoAz, figuraRo, figuraVe, figuraAz);

		System.out.println("\n¡Imagen generada con éxito en: " + nombreFichero + "!");
		scanner.close();
	}

	private static int recibeColor(String color) {
		int colorSeleccionado = 0;
		while (colorSeleccionado < 3)
		try {
			System.out.println(color + " (0-255): ");
			colorSeleccionado = scanner.nextInt();
			if(colorSeleccionado < 0 || colorSeleccionado > 255) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("Introduce un numero entre 0 y 255\n");
		}
		return colorSeleccionado;
	}

	private static int recibeTamFig(int tamanoImagen) {
		int tamanoFigura = 0;
		while (tamanoFigura < 3)
		try {
			System.out.print("Introduce el tamaño de la imagen (píxeles, será cuadrada): ");
			tamanoFigura = scanner.nextInt();
			if(tamanoFigura < 1 || tamanoFigura > tamanoImagen - 2) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("Introduce un numero entre 1 y " + (tamanoImagen - 2) + "\n");
		}
		return tamanoFigura;
	}

	private static int recibeTamImg() {
		int tamanoImagen = 0;
		while (tamanoImagen < 3)
		try {
			System.out.print("Introduce el tamaño de la imagen (píxeles, será cuadrada): ");
			tamanoImagen = scanner.nextInt();
			if(tamanoImagen < 3) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("Introduce un numero mayor que 3\n");
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

	private static void generarArchivoBMP(String nombreFichero, int tamanoImagen, int tamanoFigura, int bgR, int bgG,
			int bgB, int fgR, int fgG, int fgB) {
		// TODO Auto-generated method stub

	}

}
