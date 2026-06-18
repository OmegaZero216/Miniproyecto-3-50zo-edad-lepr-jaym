package com.example.miniproyecto350zoedadleprabra.util;

import com.example.miniproyecto350zoedadleprabra.model.Carta;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RecursosImagen {

    // Cache de imágenes para mejor rendimiento
    private static final Map<String, Image> cacheImagenes = new ConcurrentHashMap<>();
    private static final Map<String, Image> cacheMiniaturas = new ConcurrentHashMap<>();

    // Dimensiones
    public static final double ANCHO_CARTA = 142.0;
    public static final double ALTO_CARTA = 190.0;
    public static final double ANCHO_MINIATURA = 71.0;
    public static final double ALTO_MINIATURA = 95.0;

    // Rutas base
    private static final String RUTA_BASE = "/imagenes/cartas/";
    private static final String RUTA_REVERSO = RUTA_BASE + "reverso.png";
    private static final String FORMATO = ".png";

    // Mapeo de símbolos a nombres de archivo
    private static final Map<String, String> MAPEO_SIMBOLOS = new HashMap<>();

    static {
        MAPEO_SIMBOLOS.put("A", "A");
        MAPEO_SIMBOLOS.put("2", "2");
        MAPEO_SIMBOLOS.put("3", "3");
        MAPEO_SIMBOLOS.put("4", "4");
        MAPEO_SIMBOLOS.put("5", "5");
        MAPEO_SIMBOLOS.put("6", "6");
        MAPEO_SIMBOLOS.put("7", "7");
        MAPEO_SIMBOLOS.put("8", "8");
        MAPEO_SIMBOLOS.put("9", "9");
        MAPEO_SIMBOLOS.put("10", "10");
        MAPEO_SIMBOLOS.put("J", "J");
        MAPEO_SIMBOLOS.put("Q", "Q");
        MAPEO_SIMBOLOS.put("K", "K");
    }

    /**
     * Carga la imagen de una carta específica
     */
    public static Image cargarImagenCarta(String simbolo, String palo) {
        String nombreArchivo = construirNombreArchivo(simbolo, palo);
        return cargarImagen(RUTA_BASE + nombreArchivo, ANCHO_CARTA, ALTO_CARTA);
    }

    /**
     * Carga la imagen de una carta desde el modelo Carta
     */
    public static Image cargarImagenCarta(Carta carta) {
        return cargarImagenCarta(
                carta.getValorCarta().getSimbolo(),
                carta.getPalo().name().toLowerCase()
        );
    }

    /**
     * Carga la imagen del reverso de la carta
     */
    public static Image cargarReverso() {
        return cargarImagen(RUTA_REVERSO, ANCHO_CARTA, ALTO_CARTA);
    }

    /**
     * Carga una miniatura de la carta (para mostrar en manos con muchas cartas)
     */
    public static Image cargarMiniaturaCarta(String simbolo, String palo) {
        String nombreArchivo = construirNombreArchivo(simbolo, palo);
        String claveCache = "mini_" + nombreArchivo;

        return cacheMiniaturas.computeIfAbsent(claveCache, k -> {
            try {
                InputStream is = RecursosImagen.class.getResourceAsStream(
                        RUTA_BASE + nombreArchivo);
                if (is == null) {
                    return cargarImagenError(ANCHO_MINIATURA, ALTO_MINIATURA);
                }
                return new Image(is, ANCHO_MINIATURA, ALTO_MINIATURA, true, true);
            } catch (Exception e) {
                return cargarImagenError(ANCHO_MINIATURA, ALTO_MINIATURA);
            }
        });
    }

    /**
     * Construye el nombre del archivo según la convención
     */
    private static String construirNombreArchivo(String simbolo, String palo) {
        String simboloArchivo = MAPEO_SIMBOLOS.getOrDefault(simbolo, simbolo);
        return simboloArchivo + "_" + palo + FORMATO;
    }

    /**
     * Carga una imagen con caché
     */
    private static Image cargarImagen(String ruta, double ancho, double alto) {
        return cacheImagenes.computeIfAbsent(ruta, k -> {
            try {
                InputStream is = RecursosImagen.class.getResourceAsStream(ruta);
                if (is == null) {
                    System.err.println("No se encontró la imagen: " + ruta);
                    return cargarImagenError(ancho, alto);
                }
                return new Image(is, ancho, alto, true, true);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + ruta + " - " + e.getMessage());
                return cargarImagenError(ancho, alto);
            }
        });
    }

    /**
     * Genera una imagen de error como placeholder
     */
    private static Image cargarImagenError(double ancho, double alto) {
        // Intentar cargar una imagen de error prediseñada
        try {
            InputStream is = RecursosImagen.class.getResourceAsStream(
                    RUTA_BASE + "carta_error.png");
            if (is != null) {
                return new Image(is, ancho, alto, true, true);
            }
        } catch (Exception e) {
            // Si no existe, se usará el placeholder generado
        }

        // Si no hay imagen de error, crear una imagen en blanco simple
        return crearPlaceholder(ancho, alto, "?");
    }

    /**
     * Crea un placeholder simple si no se encuentra la imagen
     */
    private static Image crearPlaceholder(double ancho, double alto, String texto) {
        // Crear un canvas simple con texto
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(ancho, alto);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

        // Fondo
        gc.setFill(javafx.scene.paint.Color.DARKRED);
        gc.fillRect(0, 0, ancho, alto);

        // Borde
        gc.setStroke(javafx.scene.paint.Color.GOLD);
        gc.setLineWidth(2);
        gc.strokeRect(1, 1, ancho - 2, alto - 2);

        // Texto
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(24));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.fillText(texto, ancho / 2, alto / 2);

        // Convertir a Image (esto requiere JavaFX, si no está disponible fallará)
        try {
            return canvas.snapshot(null, null);
        } catch (Exception e) {
            // Último recurso: devolver null
            return null;
        }
    }

    /**
     * Precarga todas las imágenes de cartas (útil al inicio del juego)
     */
    public static void precargarTodasLasCartas() {
        new Thread(() -> {
            for (String simbolo : MAPEO_SIMBOLOS.keySet()) {
                for (String palo : new String[]{"corazones", "diamantes", "treboles", "picas"}) {
                    cargarImagenCarta(simbolo, palo);
                    cargarMiniaturaCarta(simbolo, palo);
                }
            }
            cargarReverso();
            System.out.println("Precarga de imágenes completada");
        }, "Hilo-Precarga-Cartas").start();
    }

    /**
     * Verifica si existe el archivo de imagen para una carta
     */
    public static boolean existeImagen(String simbolo, String palo) {
        String nombreArchivo = construirNombreArchivo(simbolo, palo);
        return RecursosImagen.class.getResource(RUTA_BASE + nombreArchivo) != null;
    }

    /**
     * Obtiene estadísticas de la caché
     */
    public static String getEstadisticasCache() {
        return String.format("Imágenes en caché: %d cartas + %d miniaturas",
                cacheImagenes.size(), cacheMiniaturas.size());
    }

    /**
     * Limpia la caché de imágenes
     */
    public static void limpiarCache() {
        cacheImagenes.clear();
        cacheMiniaturas.clear();
    }
}