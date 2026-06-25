package com.example.miniproyecto350zoedadleprabra.util;

import com.example.miniproyecto350zoedadleprabra.model.Carta;

import java.util.ArrayList;
import java.util.List;

public class VerificadorRecursos {
    private static final String RUTA_REVERSO =
            "/com/example/miniproyecto350zoedadleprabra/images/cartas/reverse.png";

    public static List<String> verificarRecursosCartas() {
        List<String> faltantes = new ArrayList<>();

        for (Carta.ValorCarta valor : Carta.ValorCarta.values()) {
            for (Carta.Palo palo : Carta.Palo.values()) {
                if (!RecursosImagen.existeImagen(valor.getSimbolo(), palo.name())) {
                    faltantes.add(valor.getSimbolo() + "_" + palo.name() + ".png");
                }
            }
        }

        if (RecursosImagen.class.getResource(RUTA_REVERSO) == null) {
            faltantes.add("reverse.png");
        }

        return faltantes;
    }

    public static void imprimirReporte() {
        List<String> faltantes = verificarRecursosCartas();

        System.out.println("=== VERIFICACION DE RECURSOS DE CARTAS ===");
        if (faltantes.isEmpty()) {
            System.out.println("Todas las imagenes de cartas estan presentes");
        } else {
            System.out.println("Faltan " + faltantes.size() + " imagenes:");
            faltantes.forEach(f -> System.out.println("  - " + f));
        }
        System.out.println("==========================================");
    }
}
