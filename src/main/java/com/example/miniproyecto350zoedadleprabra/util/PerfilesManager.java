package com.example.miniproyecto350zoedadleprabra.util;

import javafx.scene.image.Image;
import java.util.List;
import java.util.Random;

public class PerfilesManager {
    private static final List<String> PERFILES_CPU = List.of(

    );

    private static final Random random = new Random();

    public static Image obtenerPerfilAleatorioCPU() {
        String nombreArchivo = PERFILES_CPU.get(random.nextInt(PERFILES_CPU.size()));
        return cargarImagen("/com/example/miniproyecto350zoedadleprabra/images/perfiles/" + nombreArchivo + ".png");
    }

    private static Image cargarImagen(String ruta) {
        try {
            return new Image(PerfilesManager.class.getResourceAsStream(ruta));
        } catch (Exception e) {
            // Imagen por defecto en caso de error
            return new Image(PerfilesManager.class.getResourceAsStream(
                    "/com/example/miniproyecto350zoedadleprabra/images/perfiles/default.png"));
        }
    }
}