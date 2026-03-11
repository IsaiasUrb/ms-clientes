package com.uteq.edu.ec.ms_clientes.controller;

import com.uteq.edu.ec.ms_clientes.model.Cliente;
import com.uteq.edu.ec.ms_clientes.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.uteq.edu.ec.ms_clientes.service.EntregaService; // nuevo para el msentregas
import com.uteq.edu.ec.ms_clientes.service.FacturaService; // nuevo para el msfacturas

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;
    private final EntregaService entregaService; // nuevo para el msentregas
    private final FacturaService facturaService; // nuevo para el msfacturas

    public ClienteController(ClienteService service, EntregaService entregaService, FacturaService facturaService) {
        this.service = service;
        this.entregaService = entregaService; // nuevo para el msentregas
        this.facturaService = facturaService; // nuevo para el msfacturas
    }

    // ======================
    // VISTA PRINCIPAL
    // ======================
    @GetMapping("/web")
    public String vista(Model model) {

        if (!model.containsAttribute("cliente")) {
            model.addAttribute("cliente", new Cliente());
        }

        if (!model.containsAttribute("clienteEncontrado")) {
            model.addAttribute("clienteEncontrado", null);
        }

        if (!model.containsAttribute("mensajeBusqueda")) {
            model.addAttribute("mensajeBusqueda", null);
        }

        if (!model.containsAttribute("mensajeExito")) {
            model.addAttribute("mensajeExito", null);
        }

        if (!model.containsAttribute("cedulaBusqueda")) {
            model.addAttribute("cedulaBusqueda", "");
        }

        if (!model.containsAttribute("busquedaRealizada")) {
            model.addAttribute("busquedaRealizada", false);
        }

        if (!model.containsAttribute("mostrarFormularioRegistro")) {
            model.addAttribute("mostrarFormularioRegistro", false);
        }

        model.addAttribute("clientes", service.listarClientes());
        model.addAttribute("tabActiva", "tab-clientes");// nuevo para mantener la pestaña activa

        return "clientes";
    }

    // ======================
    // BUSCAR POR CÉDULA
    // ======================
    @PostMapping("/web/buscar")
    public String buscarPorCedulaWeb(
            @RequestParam("cedulaBusqueda") String cedulaBusqueda,
            RedirectAttributes redirectAttributes) {

        String cedula = cedulaBusqueda == null ? "" : cedulaBusqueda.trim();

        if (cedula.isEmpty()) {
            redirectAttributes.addFlashAttribute("cliente", new Cliente());
            redirectAttributes.addFlashAttribute("clienteEncontrado", null);
            redirectAttributes.addFlashAttribute("mensajeBusqueda",
                    "Ingrese una cédula para realizar la búsqueda.");
            redirectAttributes.addFlashAttribute("cedulaBusqueda", cedula);
            redirectAttributes.addFlashAttribute("busquedaRealizada", true);
            redirectAttributes.addFlashAttribute("mostrarFormularioRegistro", false);
            return "redirect:/clientes/web";
        }

        if (ClienteService.CEDULA_CONSUMIDOR_FINAL.equals(cedula)) {
            redirectAttributes.addFlashAttribute("cliente", new Cliente());
            redirectAttributes.addFlashAttribute("clienteEncontrado", null);
            redirectAttributes.addFlashAttribute("mensajeBusqueda",
                    "La cédula 9999999999 corresponde a Consumidor Final. Use el botón Consumidor Final.");
            redirectAttributes.addFlashAttribute("cedulaBusqueda", cedula);
            redirectAttributes.addFlashAttribute("busquedaRealizada", true);
            redirectAttributes.addFlashAttribute("mostrarFormularioRegistro", false);
            return "redirect:/clientes/web";
        }

        Cliente clienteEncontrado = service.buscarPorCedula(cedula);

        if (clienteEncontrado != null) {
            redirectAttributes.addFlashAttribute("cliente", new Cliente());
            redirectAttributes.addFlashAttribute("clienteEncontrado", clienteEncontrado);
            redirectAttributes.addFlashAttribute("mensajeBusqueda",
                    "Cliente encontrado correctamente.");
            redirectAttributes.addFlashAttribute("cedulaBusqueda", cedula);
            redirectAttributes.addFlashAttribute("busquedaRealizada", true);
            redirectAttributes.addFlashAttribute("mostrarFormularioRegistro", false);
        } else {
            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setCedula(cedula);

            redirectAttributes.addFlashAttribute("cliente", nuevoCliente);
            redirectAttributes.addFlashAttribute("clienteEncontrado", null);
            redirectAttributes.addFlashAttribute("mensajeBusqueda",
                    "Cliente no encontrado. Complete los campos para registrar un nuevo cliente.");
            redirectAttributes.addFlashAttribute("cedulaBusqueda", cedula);
            redirectAttributes.addFlashAttribute("busquedaRealizada", true);
            redirectAttributes.addFlashAttribute("mostrarFormularioRegistro", true);
        }

        return "redirect:/clientes/web";
    }

    // ======================
    // GUARDAR / EDITAR CLIENTE
    // ======================
    @PostMapping("/web/guardar")
    public String guardarWeb(
            @ModelAttribute Cliente cliente,
            @RequestParam(name = "consumidorFinal", defaultValue = "false") boolean consumidorFinal,
            RedirectAttributes redirectAttributes) {

        try {
            boolean esEdicion = cliente.getId() != null;

            if (esEdicion) {
                Cliente clienteDB = service.buscarPorId(cliente.getId());

                if (clienteDB != null) {
                    cliente.setFechaRegistro(clienteDB.getFechaRegistro());
                }
            }

            service.guardarCliente(cliente, consumidorFinal);

            if (esEdicion) {
                redirectAttributes.addFlashAttribute("mensajeExito", "Cliente actualizado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("mensajeExito", "Cliente registrado correctamente.");
            }

            redirectAttributes.addFlashAttribute("cliente", new Cliente());
            redirectAttributes.addFlashAttribute("clienteEncontrado", null);
            redirectAttributes.addFlashAttribute("cedulaBusqueda", "");
            redirectAttributes.addFlashAttribute("busquedaRealizada", false);
            redirectAttributes.addFlashAttribute("mostrarFormularioRegistro", false);

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("cliente", cliente);
            redirectAttributes.addFlashAttribute("clienteEncontrado", null);
            redirectAttributes.addFlashAttribute("errorMensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("cedulaBusqueda",
                    cliente.getCedula() != null ? cliente.getCedula() : "");
            redirectAttributes.addFlashAttribute("busquedaRealizada", false);
            redirectAttributes.addFlashAttribute("mostrarFormularioRegistro", true);
        }

        return "redirect:/clientes/web";
    }

    // ======================
    // EDITAR
    // ======================
    @GetMapping("/web/editar/{id}")
    public String editarWeb(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Cliente cliente = service.buscarPorId(id);

        redirectAttributes.addFlashAttribute("cliente", cliente != null ? cliente : new Cliente());
        redirectAttributes.addFlashAttribute("clienteEncontrado", null);
        redirectAttributes.addFlashAttribute("mensajeBusqueda", "Modo edición de cliente.");
        redirectAttributes.addFlashAttribute("cedulaBusqueda", "");
        redirectAttributes.addFlashAttribute("busquedaRealizada", false);
        redirectAttributes.addFlashAttribute("mostrarFormularioRegistro", true);

        return "redirect:/clientes/web";
    }

    // ======================
    // ELIMINAR
    // ======================
    @GetMapping("/web/eliminar/{id}")
    public String eliminarWeb(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.eliminarCliente(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Cliente eliminado correctamente.");
        redirectAttributes.addFlashAttribute("cliente", new Cliente());
        redirectAttributes.addFlashAttribute("clienteEncontrado", null);
        redirectAttributes.addFlashAttribute("cedulaBusqueda", "");
        redirectAttributes.addFlashAttribute("busquedaRealizada", false);
        redirectAttributes.addFlashAttribute("mostrarFormularioRegistro", false);
        return "redirect:/clientes/web";
    }

    // ======================
    // ACTUALIZAR SOLO ESTADO
    // ======================
    @PostMapping("/web/estado/{id}")
    public String actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {

        Cliente cliente = service.buscarPorId(id);

        if (cliente != null) {
            cliente.setEstado(estado);
            boolean consumidorFinal = service.esConsumidorFinal(cliente.getCedula());
            service.guardarCliente(cliente, consumidorFinal);
            redirectAttributes.addFlashAttribute("mensajeExito", "Estado actualizado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMensaje", "No se encontró el cliente.");
        }

        return "redirect:/clientes/web";
    }

        // postman o método para obtener las entregas por cédula desde el msentregas
    @PostMapping("/web/entregas")
    public String buscarEntregasPorCedula(@RequestParam String cedulaEntrega, Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("clienteEncontrado", null);
        model.addAttribute("mensajeBusqueda", null);
        model.addAttribute("mensajeExito", null);
        model.addAttribute("cedulaBusqueda", "");
        model.addAttribute("busquedaRealizada", false);
        model.addAttribute("mostrarFormularioRegistro", false);
        model.addAttribute("clientes", service.listarClientes());

        model.addAttribute("entregasEncontradas", entregaService.obtenerEntregasPorCedula(cedulaEntrega));
        model.addAttribute("cedulaEntrega", cedulaEntrega);
        model.addAttribute("tabActiva", "tab-entregas");

        return "clientes";
    }

    // postman o método para obtener las facturas por cédula desde el msfacturas    @PostMapping("/web/facturas")
    public String buscarFacturasPorCedula(@RequestParam String cedulaFactura, Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("clienteEncontrado", null);
        model.addAttribute("mensajeBusqueda", null);
        model.addAttribute("mensajeExito", null);
        model.addAttribute("cedulaBusqueda", "");
        model.addAttribute("busquedaRealizada", false);
        model.addAttribute("mostrarFormularioRegistro", false);
        model.addAttribute("clientes", service.listarClientes());

        model.addAttribute("facturasEncontradas", facturaService.obtenerFacturasPorCedula(cedulaFactura));
        model.addAttribute("cedulaFactura", cedulaFactura);
        model.addAttribute("tabActiva", "tab-facturas");

        return "clientes";
    }
}