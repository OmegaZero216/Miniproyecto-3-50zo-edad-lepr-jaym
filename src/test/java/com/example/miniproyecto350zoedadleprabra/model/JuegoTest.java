package com.example.miniproyecto350zoedadleprabra.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JuegoTest {
    @Test
    void creaCantidadExactaDeJugadoresSeleccionada() {
        for (int cantidadJugadores = 2; cantidadJugadores <= 4; cantidadJugadores++) {
            Juego juego = new Juego(cantidadJugadores);

            assertEquals(cantidadJugadores, juego.getJugadores().size());
            assertEquals(1, juego.getJugadores().stream()
                    .filter(j -> j.getTipo() == Jugador.Tipo.HUMANO)
                    .count());
            assertEquals(cantidadJugadores - 1, juego.getJugadores().stream()
                    .filter(j -> j.getTipo() == Jugador.Tipo.MAQUINA)
                    .count());
        }
    }

    @Test
    void rechazaCantidadDeJugadoresFueraDelRango() {
        assertThrows(IllegalArgumentException.class, () -> new Juego(1));
        assertThrows(IllegalArgumentException.class, () -> new Juego(5));
    }

    @Test
    void preparacionInicialReparteCartasCorrectamente() {
        Juego juego = new Juego(4);
        juego.preparar();

        assertNotNull(juego.getCartaEnMesa());
        assertTrue(juego.getSumaMesa() >= -10 && juego.getSumaMesa() <= 10);
        juego.getJugadores().forEach(jugador ->
                assertEquals(Juego.CARTAS_INICIALES, jugador.getMano().size()));
        assertEquals(52 - (4 * Juego.CARTAS_INICIALES) - 1, juego.getMazo().cartasRestantes());
    }

    @Test
    void siguienteTurnoSaltaJugadoresEliminados() {
        Juego juego = new Juego(3);
        Jugador cpu1 = juego.getJugadores().get(1);

        juego.eliminarJugador(cpu1);
        juego.siguienteTurno();

        assertEquals(juego.getJugadores().get(2), juego.getJugadorActual());
        assertFalse(juego.isJuegoTerminado());
    }

    @Test
    void eliminarHastaUnJugadorDeclaraGanador() {
        Juego juego = new Juego(2);
        Jugador cpu = juego.getJugadores().get(1);

        juego.eliminarJugador(cpu);

        assertTrue(juego.hayGanador());
        assertEquals(juego.getJugadorHumano(), juego.getGanador());
    }
}
