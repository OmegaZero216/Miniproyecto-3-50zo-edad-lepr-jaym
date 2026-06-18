package com.example.miniproyecto350zoedadleprabra.util;

import com.example.miniproyecto350zoedadleprabra.model.Carta;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecursosImagen {
    public static final double ANCHO_CARTA = 142.0;
    public static final double ALTO_CARTA = 190.0;
    public static final double ANCHO_MINIATURA = 71.0;
    public static final double ALTO_MINIATURA = 95.0;

    private static final String RUTA_BASE =
            "/com/example/miniproyecto350zoedadleprabra/images/cartas/";
    private static final String RUTA_REVERSO = RUTA_BASE + "reverse.png";
    private static final String FORMATO = ".png";

    private static final Map<String, Image> cacheImagenes = new ConcurrentHashMap<>();
    private static final Map<String, Image> cacheMiniaturas = new ConcurrentHashMap<>();

    public static Image cargarImagenCarta(String simbolo, String palo) {
        String nombreArchivo = construirNombreArchivo(simbolo, palo);
        return cargarImagen(RUTA_BASE + nombreArchivo, ANCHO_CARTA, ALTO_CARTA);
    }

    public static Image cargarImagenCarta(Carta carta) {
        return cargarImagenCarta(carta.getValorCarta().getSimbolo(), carta.getPalo().name());
    }

    public static Image cargarReverso() {
        return cargarImagen(RUTA_REVERSO, ANCHO_CARTA, ALTO_CARTA);
    }

    public static Image cargarMiniaturaCarta(String simbolo, String palo) {
        String nombreArchivo = construirNombreArchivo(simbolo, palo);
        String ruta = RUTA_BASE + nombreArchivo;
        return cacheMiniaturas.computeIfAbsent("mini_" + ruta,
                key -> cargarImagenSinCache(ruta, ANCHO_MINIATURA, ALTO_MINIATURA));
    }

    public static boolean existeImagen(String simbolo, String palo) {
        return RecursosImagen.class.getResource(RUTA_BASE + construirNombreArchivo(simbolo, palo)) != null;
    }

    public static String getEstadisticasCache() {
        return String.format("Imagenes en cache: %d cartas + %d miniaturas",
                cacheImagenes.size(), cacheMiniaturas.size());
    }

    public static void limpiarCache() {
        cacheImagenes.clear();
        cacheMiniaturas.clear();
    }

    public static void precargarTodasLasCartas() {
        new Thread(() -> {
            for (Carta.ValorCarta valor : Carta.ValorCarta.values()) {
                for (Carta.Palo palo : Carta.Palo.values()) {
                    cargarImagenCarta(valor.getSimbolo(), palo.name());
                    cargarMiniaturaCarta(valor.getSimbolo(), palo.name());
                }
            }
            cargarReverso();
        }, "Hilo-Precarga-Cartas").start();
    }

    private static Image cargarImagen(String ruta, double ancho, double alto) {
        return cacheImagenes.computeIfAbsent(ruta, key -> cargarImagenSinCache(ruta, ancho, alto));
    }

    private static Image cargarImagenSinCache(String ruta, double ancho, double alto) {
        InputStream inputStream = RecursosImagen.class.getResourceAsStream(ruta);
        if (inputStream == null) {
            System.err.println("No se encontro la imagen: " + ruta);
            return null;
        }
        return new Image(inputStream, ancho, alto, true, true);
    }

    private static String construirNombreArchivo(String simbolo, String palo) {
        return prefijoPalo(palo) + simbolo.toUpperCase() + FORMATO;
    }

    private static String prefijoPalo(String palo) {
        String normalizado = palo == null ? "" : palo.trim().toUpperCase();
        return switch (normalizado) {
            case "CORAZONES" -> "C";
            case "DIAMANTES" -> "D";
            case "PICAS" -> "P";
            case "TREBOLES" -> "T";
            default -> throw new IllegalArgumentException("Palo no soportado: " + palo);
        };
    }
}
