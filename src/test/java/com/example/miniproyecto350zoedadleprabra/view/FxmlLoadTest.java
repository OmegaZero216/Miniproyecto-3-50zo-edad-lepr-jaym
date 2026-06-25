package com.example.miniproyecto350zoedadleprabra.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import com.example.miniproyecto350zoedadleprabra.controller.JuegoController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FxmlLoadTest {
    private static final List<String> FXMLS = List.of(
            "/com/example/miniproyecto350zoedadleprabra/fxml/Inicio-view.fxml",
            "/com/example/miniproyecto350zoedadleprabra/fxml/Juego-view.fxml"
    );

    @BeforeAll
    static void iniciarJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            Platform.runLater(latch::countDown);
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS), "JavaFX no inicio a tiempo");
    }

    @Test
    void pantallasFXMLCarganSinExcepciones() throws Exception {
        for (String fxml : FXMLS) {
            cargarEnFxThread(fxml);
        }
    }

    @Test
    void juegoInicializaConfiguracionesValidasSinExcepciones() throws Exception {
        for (int jugadores = 2; jugadores <= 4; jugadores++) {
            inicializarJuegoEnFxThread(jugadores);
        }
    }

    private void cargarEnFxThread(String fxml) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(FxmlLoadTest.class.getResource(fxml));
                assertNotNull(loader.load(), "No se pudo cargar " + fxml);
            } catch (Throwable throwable) {
                error.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS), "FXML no cargo a tiempo: " + fxml);
        if (error.get() != null) {
            throw new AssertionError("Error cargando FXML: " + fxml, error.get());
        }
    }

    private void inicializarJuegoEnFxThread(int jugadores) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(FxmlLoadTest.class.getResource(
                        "/com/example/miniproyecto350zoedadleprabra/fxml/Juego-view.fxml"));
                assertNotNull(loader.load(), "No se pudo cargar Juego-view.fxml");
                JuegoController controller = loader.getController();
                controller.inicializarJuego(jugadores);
            } catch (Throwable throwable) {
                error.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS),
                "Juego no inicializo a tiempo con " + jugadores + " jugadores");
        if (error.get() != null) {
            throw new AssertionError("Error inicializando juego con " + jugadores + " jugadores", error.get());
        }
    }
}
