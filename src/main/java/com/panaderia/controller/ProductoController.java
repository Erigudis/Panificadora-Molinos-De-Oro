package com.panaderia.controller;

import com.panaderia.model.Producto;
import com.panaderia.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador JavaFX de la pantalla de productos (v2).
 * Incluye CRUD, búsqueda por nombre, filtro por categoría y ajuste de stock.
 */
public class ProductoController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colDescripcion;

    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private TextArea txtDescripcion;

    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    // Controles nuevos v2
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroCategoria;
    @FXML private TextField txtCantidadStock;

    private final ProductoService service = new ProductoService();
    private final ObservableList<Producto> datos = FXCollections.observableArrayList();
    private FilteredList<Producto> datosFiltrados;
    private Producto productoSeleccionado = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        ObservableList<String> categorias = FXCollections.observableArrayList(
                "Pan", "Pastel", "Galleta", "Otro"
        );
        cmbCategoria.setItems(categorias);

        // Filtro de categoría: incluye "Todas"
        cmbFiltroCategoria.setItems(FXCollections.observableArrayList(
                "Todas", "Pan", "Pastel", "Galleta", "Otro"
        ));
        cmbFiltroCategoria.setValue("Todas");

        datosFiltrados = new FilteredList<>(datos, p -> true);
        tablaProductos.setItems(datosFiltrados);

        tablaProductos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        productoSeleccionado = newSel;
                        cargarFormulario(newSel);
                    }
                }
        );

        cargarTabla();
    }

    // ---------- CRUD ----------

    @FXML
    private void onNuevo() {
        limpiarFormulario();
        productoSeleccionado = null;
        tablaProductos.getSelectionModel().clearSelection();
        txtNombre.requestFocus();
    }

    @FXML
    private void onGuardar() {
        try {
            Producto p = leerFormulario();
            if (productoSeleccionado != null) {
                p.setId(productoSeleccionado.getId());
            }
            String msg = service.guardar(p);
            mostrarInfo(msg);
            cargarTabla();
            limpiarFormulario();
            productoSeleccionado = null;
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error al guardar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void onEliminar() {
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto de la tabla para eliminar.");
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar eliminación");
        conf.setHeaderText(null);
        conf.setContentText("¿Eliminar el producto \"" + productoSeleccionado.getNombre() + "\"?");
        conf.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    String msg = service.eliminar(productoSeleccionado.getId());
                    mostrarInfo(msg);
                    cargarTabla();
                    limpiarFormulario();
                    productoSeleccionado = null;
                } catch (Exception ex) {
                    mostrarError("Error al eliminar: " + ex.getMessage());
                }
            }
        });
    }

    // ---------- Búsqueda y filtro (v2) ----------

    @FXML
    private void onBuscar() {
        aplicarFiltros();
    }

    @FXML
    private void onFiltrar() {
        aplicarFiltros();
    }

    @FXML
    private void onLimpiarFiltros() {
        txtBuscar.clear();
        cmbFiltroCategoria.setValue("Todas");
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String categoria = cmbFiltroCategoria.getValue();

        datosFiltrados.setPredicate(p -> {
            boolean okNombre = texto.isEmpty()
                    || (p.getNombre() != null && p.getNombre().toLowerCase().contains(texto));
            boolean okCat = categoria == null || "Todas".equals(categoria)
                    || (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria));
            return okNombre && okCat;
        });
    }

    // ---------- Ajuste de stock (v2) ----------

    @FXML
    private void onEntradaStock() {
        ajustarStock(true);
    }

    @FXML
    private void onSalidaStock() {
        ajustarStock(false);
    }

    private void ajustarStock(boolean entrada) {
        if (productoSeleccionado == null) {
            mostrarError("Seleccione un producto de la tabla para ajustar el stock.");
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidadStock.getText().trim());
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            mostrarError("Ingrese una cantidad válida mayor a 0.");
            return;
        }

        try {
            String msg = service.ajustarStock(productoSeleccionado.getId(), cantidad, entrada);
            mostrarInfo(msg);
            cargarTabla();
            // Mantener selección y refrescar formulario
            for (Producto p : datos) {
                if (p.getId() == productoSeleccionado.getId()) {
                    productoSeleccionado = p;
                    cargarFormulario(p);
                    tablaProductos.getSelectionModel().select(p);
                    break;
                }
            }
            txtCantidadStock.clear();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error al ajustar stock: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ---------- Helpers ----------

    private void cargarTabla() {
        try {
            datos.setAll(service.listarTodos());
            aplicarFiltros();
        } catch (Exception ex) {
            mostrarError("No se pudo cargar la lista de productos.\n"
                    + "Verifique que MySQL esté activo y la base dbpanaderia exista.\n"
                    + "Detalle: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarFormulario(Producto p) {
        txtNombre.setText(p.getNombre());
        cmbCategoria.setValue(p.getCategoria());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        txtDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
    }

    private Producto leerFormulario() {
        Producto p = new Producto();
        p.setNombre(txtNombre.getText() != null ? txtNombre.getText().trim() : "");
        p.setCategoria(cmbCategoria.getValue() != null ? cmbCategoria.getValue() : "");

        try {
            p.setPrecio(Double.parseDouble(txtPrecio.getText().trim().replace(",", ".")));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número válido.");
        }
        try {
            p.setStock(Integer.parseInt(txtStock.getText().trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El stock debe ser un número entero válido.");
        }

        p.setDescripcion(txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "");
        return p;
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        cmbCategoria.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtStock.clear();
        txtDescripcion.clear();
        txtCantidadStock.clear();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
