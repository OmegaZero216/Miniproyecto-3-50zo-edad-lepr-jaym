package com.example.miniproyecto350zoedadleprabra.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class VerificadorRecursosTest {
    @Test
    void todasLasImagenesDeCartasEstanDisponibles() {
        List<String> faltantes = VerificadorRecursos.verificarRecursosCartas();
        assertTrue(faltantes.isEmpty(), () -> "Recursos faltantes: " + faltantes);
    }
}
