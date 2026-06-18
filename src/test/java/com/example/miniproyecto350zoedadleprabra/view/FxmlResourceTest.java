package com.example.miniproyecto350zoedadleprabra.view;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FxmlResourceTest {
    private static final List<String> FXMLS = List.of(
            "/com/example/miniproyecto350zoedadleprabra/fxml/Inicio-view.fxml",
            "/com/example/miniproyecto350zoedadleprabra/fxml/Juego-view.fxml"
    );

    @Test
    void fxmlTieneControladoresEventosYRecursosValidos() throws Exception {
        for (String fxml : FXMLS) {
            Document document = parse(fxml);
            Class<?> controllerClass = cargarControlador(document, fxml);
            verificarNodo(document.getDocumentElement(), controllerClass, fxml);
        }
    }

    private Document parse(String resourcePath) throws Exception {
        InputStream inputStream = FxmlResourceTest.class.getResourceAsStream(resourcePath);
        assertNotNull(inputStream, "No existe FXML: " + resourcePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        return factory.newDocumentBuilder().parse(inputStream);
    }

    private Class<?> cargarControlador(Document document, String fxml) throws ClassNotFoundException {
        String controller = document.getDocumentElement().getAttribute("fx:controller");
        assertTrue(!controller.isBlank(), "FXML sin controlador: " + fxml);
        return Class.forName(controller);
    }

    private void verificarNodo(Node node, Class<?> controllerClass, String fxml) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                verificarAtributo(attribute.getNodeName(), attribute.getNodeValue(), controllerClass, fxml);
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            verificarNodo(children.item(i), controllerClass, fxml);
        }
    }

    private void verificarAtributo(String name, String value, Class<?> controllerClass, String fxml) {
        if (name.startsWith("on") && value.startsWith("#")) {
            String methodName = value.substring(1);
            assertTrue(tieneMetodoSinArgumentos(controllerClass, methodName),
                    "Handler no encontrado en " + controllerClass.getName() + ": " + methodName);
        }

        if (("url".equals(name) || "stylesheets".equals(name)) && value.startsWith("@/")) {
            String resourcePath = value.substring(1);
            assertNotNull(FxmlResourceTest.class.getResource(resourcePath),
                    "Recurso no encontrado en " + fxml + ": " + resourcePath);
        }
    }

    private boolean tieneMetodoSinArgumentos(Class<?> controllerClass, String methodName) {
        return assertDoesNotThrow(() -> {
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
                    return true;
                }
            }
            return false;
        });
    }
}
