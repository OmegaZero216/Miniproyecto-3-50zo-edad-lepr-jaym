package com.example.miniproyecto350zoedadleprabra.util;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class PerfilesManager {
    private static final String RUTA_PERFILES =
            "/com/example/miniproyecto350zoedadleprabra/images/perfiles/";
    private static final List<String> PERFILES_CPU = List.of(
            "p1", "p2", "p3", "p4", "p5",
            "p6", "p7", "p8", "p9", "p10",
            "p11", "p12", "p13", "p14", "p15",
            "p16", "p17", "p18", "p19", "p20"
    );

    private static final Random RANDOM = new Random();

    public static Image obtenerPerfilAleatorioCPU() {
        String nombreArchivo = PERFILES_CPU.get(RANDOM.nextInt(PERFILES_CPU.size()));
        Image perfil = cargarImagen(RUTA_PERFILES + nombreArchivo + ".jpeg");
        return perfil != null ? perfil : cargarImagen(RUTA_PERFILES + "default.png");
    }

    private static Image cargarImagen(String ruta) {
        InputStream inputStream = PerfilesManager.class.getResourceAsStream(ruta);
        return inputStream == null ? null : new Image(inputStream);
    }
}
