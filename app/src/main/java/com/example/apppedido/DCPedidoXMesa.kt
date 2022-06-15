class DCPedidoXMesa : ArrayList<DCPedidoXMesaItem>()

data class DCPedidoXMesaItem(
    val idPedido: Int,
    val numeroPedido: String,
    val codigoVendedor: String,
    val codigoCpago: String,
    val codigoMoneda: String,
    val fechaPedido: String,
    val numeroOcliente: String,
    val importeStot: Double,
    val valorVenta: Double,
    val importeIgv: Double,
    val importeDescuento: Double,
    val importeTotal: Double,
    val porcentajeDescuento: Double,
    val porcentajeIgv: Double,
    val observacion: String,
    val estado: String,
    val idCliente: Int,
    val importeIsc: Double,
    val usuarioCreacion: String,
    val fechaCreacion: String,
    val usuarioModificacion: String,
    val fechaModificacion: String,
    val codigoEmpresa: String,
    val codigoSucursal: String,
    val idClienteFactura: Int,
    val codigoVendedorAsignado: String,
    val fechaProgramada: String,
    val facturaAdelantada: String,
    val contacto: String,
    val emailContacto: String,
    val usuarioAutoriza: Any,
    val fechaAutorizacion: Any,
    val lugarEntrega: String,
    val idCotizacion: Int,
    val estadoFactura: Any,
    val comision: Double,
    val puntoVenta: String,
    val redondeo: String,
    val validez: String,
    val motivo: String,
    val correlativo: String,
    val centroCosto: String,
    val tipoCambio: Double,
    val sucursal: String,
    val costo: Any,
    val idOportunidad: Any,
    val valPercepcion: Any,
    val tipoDocumento: Any,
    val fechaProgramadaEntrega: Any,
    val observacion2: String,
    val pax: Int,
    val mesa: String,
    val piso: String,
    val swtEsta: String,
    val delivery: String,
    val cdgMensajero: Any,
    val fechaFinalizacion: Any,
    val inicial: Any,
    val fechaInicio: Any,
    val fechaVenc: Any,
    val sinicial: Any,
    val tipoDscto: Any,
    val icbper: Double,
    val swtPd: Any,
    val numpain: Any,
    val idProyecto: Any,
    val porcentajeInicial: Any,
    val tipoContrato: Any,
    val puntos: Any,
    val precioPunto: Any,
    val calendario: Any,
    val montoInicialHoy: Any,
    val porcentajeInicialHoy: Any,
    val formaDePago1InicialHoy: Any,
    val fpGa: Any,
    val montoGa: Any,
    val vencGa: Any,
    val bancoGa: Any,
    val refGa: Any,
    val fechaProcesable: Any,
    val estatus: Any,
    val montoAFinanciar: Any,
    val porcentajeMontoFinanciar: Any,
    val numCuotaFinanciar: Any,
    val porcentajeInteresMensual: Any,
    val montoCuotaMensual: Any,
    val vencPrimeraCuota: Any,
    val swtWeb: Any,
    val primerAnoUso: Any,
    val cantidadAnoContrato: Any,
    val cdgSceco: Any,
    val idOrder: Any,
    val orderGuid: Any,
    val customerId: Any,
    val ordeStatus: Any,
    val paymentStatusId: Any,
    val paymentMethodSystemName: Any,
    val orderTotal: Any,
    val createdOnUtc: Any,
    val paidDateUtc: Any,
    val customerIp: Any,
    val paymentMethodAdditionalFeeInclTax: Any,
    val paymentMethodAdditionalFeeExclTax: Any,
    val estadoc: Any,
    val opeAnexoPedidos: List<Any>
)