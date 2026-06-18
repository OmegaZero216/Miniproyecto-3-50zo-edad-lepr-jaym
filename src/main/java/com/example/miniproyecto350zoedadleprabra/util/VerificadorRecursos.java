package com.example.miniproyecto350zoedadleprabra.util;

import java.util.ArrayList;
import java.util.List;

public class VerificadorRecursos {

    /**
     * Verifica que todas las imágenes necesarias existan
     * @return Lista de recursos faltantes
     */
    public static List<String> verificarRecursosCartas() {
        List<String> faltantes = new ArrayList<>();

        String[] simbolos = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] palos = {"corazones", "diamantes", "treboles", "picas"};

        for (String simbolo : simbolos) {
            for (String palo : palos) {
                if (!RecursosImagen.existeImagen(simbolo, palo)) {
                    faltantes.add(simbolo + "_" + palo + ".png");
                }
            }
        }

        // Verificar reverso
        if (RecursosImagen.class.getResource("/imagenes/cartas/reverso.png") == null) {
            faltantes.add("reverso.png");
        }

        return faltantes;
    }

    /**
     * Imprime el reporte de verificación
     */
    public static void imprimirReporte() {
        System.out.println("=== VERIFICACIÓN DE RECURSOS DE CARTAS ===");

        List<String> faltantes = verificarRecursosCartas();

        if (faltantes.isEmpty()) {
            System.out.println("✓ Todas las imágenes de cartas están presentes (52 cartas + reverso)");
        } else {
            System.out.println("✗ Faltan " + faltantes.size() + " imágenes:");
            faltantes.forEach(f -> System.out.println("  - " + f));
        }

        System.out.println("==========================================");
    }
}