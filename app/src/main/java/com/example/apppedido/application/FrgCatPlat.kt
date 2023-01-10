package com.example.apppedido.application.View

import DCOrdenPedido
import Detalle
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.*
import com.example.apppedido.DataBase.EntityCategoria
import com.example.apppedido.ValidarConfiguracion.Companion.prefs
import com.example.apppedido.domain.DCComandaItem
import com.example.apppedido.domain.DataClassPedidoBorrador
import com.example.apppedido.domain.Model.DCCategoriaItem
import com.example.apppedido.domain.Model.DCLoginDatosExito
import com.example.apppedido.domain.Model.DCPlatoItem
import com.example.apppedido.domain.Model.DCZonaItem
import com.example.apppedido.domain.Model.DataClassPedido
import com.example.apppedido.domain.Model.ListDetalle
import com.example.apppedido.infraestruture.adapters.AdapterCategoria
import com.example.apppedido.infraestruture.adapters.AdapterPedido
import com.example.apppedido.infraestruture.adapters.AdapterPlato
import com.example.apppedido.infraestruture.adapters.AdapterPlatoFiltrado
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDateTime


class FrgCatPlat: Fragment() {

    //***************INICIO ATRIBUTOS********************
    private lateinit var adapterCategoria: AdapterCategoria
    private lateinit var adapterPlato: AdapterPlato
    private lateinit var adapterPlatoFiltrado: AdapterPlatoFiltrado
    private lateinit var adapterPedido: AdapterPedido
    private var listaCategoria = ArrayList<DCCategoriaItem>()
    private val listaCategoriaInjeccion = ArrayList<DCCategoriaItem>()
    private val listaPlato = ArrayList<DCPlatoItem>()
    private val listaPlatoBuscado = ArrayList<DCPlatoItem>()
    private val listaPedido = ArrayList<DataClassPedido>()
    private val listaDetalleOrdenPedido = ArrayList<Detalle>()
    private val listaPedidoFiltrado = ArrayList<DataClassPedido>()
    private val listaComanda = ArrayList<DCComandaItem>()
    private val listaDetalleComanda = ArrayList<com.example.apppedido.domain.Detalle>()
    private var listaPedidoBorrador = ArrayList<DataClassPedidoBorrador>()

    var apiInterface: APIService? = null
    var idpedido = 0
    private var NAMEMOZOTEMPORAL: String? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frg_cat_plat, container, false)
        return view
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //++++++++++++++++++    DECLARA COMPONENTE     +++++++++++++++++
        apiInterface = RetrofitCall.client?.create(APIService::class.java) as APIService

        //++++++++++++++++++   INICIA LAS FUNCIONES    +++++++++++++++++
        //INICIAR CATEGORIA
        initComponentCategoria()
        iniciarCategoria()
        //INICIAR CATEGORIA
        initPlato()
        //INICIAR PEDIDO
        initPedido()
        //Iniciar Datos


        //BUSCAR PLATOS
        consultaPedidosPendiente()

        //RECIBIR ZONA Y MESA
        iniZonaMesa()

        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()

        getNameMozo()

        // Eventos de botones
        eventsHanleds()
    }

    private fun eventsHanleds() {
        val bt_enviar_comanda = view?.findViewById<Button>(R.id.bt_enviarComanda)
        val bt_cancelar = view?.findViewById<Button>(R.id.bt_cancelar)
        val bt_precuenta = view?.findViewById<Button>(R.id.bt_precuenta)
        val bt_borrador = view?.findViewById<Button>(R.id.bt_guardarCambio)
        val bt_atras = view?.findViewById<Button>(R.id.bt_atras)

        //Enviar Comanda
        bt_enviar_comanda?.setOnClickListener { enviarPedido() }
        bt_cancelar?.setOnClickListener { limpiarLista() }
        bt_precuenta?.setOnClickListener { imprimirPrecuenta() }
        bt_borrador?.setOnClickListener { guardarCambio() }
        bt_atras?.setOnClickListener{ guardarCambio() }
    }

    private fun getNameMozo() {
        val iniMozo=view?.findViewById<TextView>(R.id.tv_Mozo)

        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getMesa2("piso eq '$IDZONA' and idMesa eq $IDMESA")
            activity?.runOnUiThread {
                if (response.code() == 200) {
                    println("***************************************************************")
                    println("Respuesta de Corrutina: ${response.body()?.get(0)?.NombreMozo}")
                    println("***************************************************************")
                    if (response.body()?.get(0)?.NombreMozo==null|| response.body()?.get(0)?.NombreMozo==""|| response.body()?.get(0)?.NombreMozo==" "){

                    }else{
                        NAMEMOZOTEMPORAL = response.body()?.get(0)?.NombreMozo
                        iniMozo?.text = "ATENDIDO: $NAMEMOZOTEMPORAL"
                        Toast.makeText(activity, "EN ATENCION POR: $NAMEMOZOTEMPORAL", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(activity, "Error: ${response.code()} // getNameMozo // piso eq '$IDZONA' and idMesa eq $IDMESA", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun iniciarCategoria() {
        CoroutineScope(Dispatchers.IO).launch {
            println("*********  Tabla Categoria exite  ************")
            println(ValidarConfiguracion.database.daoCategoria().isExistsCategoria())

            if (ValidarConfiguracion.database.daoCategoria().isExistsCategoria()){
                listaCategoria.clear()
                ValidarConfiguracion.database.daoCategoria().getAllCategoria().forEach {
                    listaCategoria.add(DCCategoriaItem(
                        nameCategoria = it.nameCategoria,
                        idCategoria = it.idCategoria)
                    )
                }
                activity?.runOnUiThread {
                    getDataPlato(listaCategoria[0].nameCategoria)
                    adapterCategoria.notifyDataSetChanged()
                }
            }else{
                activity?.runOnUiThread {
                    getDataCategoria()
                }
            }
        }
    }

    //*************************       REVISAR y MEJORADO     *********************
    fun limpiarLista() {
        var size = listaPedido.size
        var cont = 0

        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val IDZONA = datosRecuperados.getString("IDZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()

        if (listaPedido.size>0){
            do{
                if (listaPedido[cont].estadoPedido=="PENDIENTE"){
                    listaPedido.removeAt(cont)
                    cont=0
                    size-=1
                }else{
                    cont+=1
                }
            }while(size != cont)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getMesa2("piso eq '$IDZONA' and idMesa eq $IDMESA")
            activity?.runOnUiThread {
                if (response.code()==200) {

                    println("***************************************************************")
                    println("Respuesta de Corrutina: ${response.body()?.get(0)?.NombreMozo}")
                    println("***************************************************************")

                    if (response.body()?.get(0)?.NombreMozo==null|| response.body()?.get(0)?.NombreMozo==""|| response.body()?.get(0)?.NombreMozo==" "){
                        NAMEMOZOTEMPORAL = " "
                    }else{
                        NAMEMOZOTEMPORAL = response.body()?.get(0)?.NombreMozo
                    }

                    println("***************************************************************")
                    println("NAMEMOZOTEMPORAL : ${NAMEMOZOTEMPORAL}")
                    println("***************************************************************")


                    if (listaPedido.size<=0){
                        if (NAMEMOZOTEMPORAL==DatosUsuario.nombreMozo || NAMEMOZOTEMPORAL==" "){
                            cambiarEstadoMesa(IDZONA.toString(), IDMESA!!, "L", " ")
                        }
                    }

                    for (i in listaPedido.indices){
                        if (listaPedido[i].estadoPedido == "ATENDIDO"){
                            cambiarEstadoMesa(IDZONA.toString(), IDMESA!!, "O", NAMEMOZOTEMPORAL!!)
                            break
                        }
                    }
                }else{
                    Toast.makeText(activity, "Error: ${response.code()} // limpiarLista // piso eq '$IDZONA' and idMesa eq $IDMESA", Toast.LENGTH_SHORT).show()
                }
            }
        }

        adapterPedido.notifyDataSetChanged()
        actualizarPrecioTotal()
    }

    //***********************************************************************************************************************
    private fun guardarCambio() {
        //DECLARAR VARIABLES
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val DatosZonas: ArrayList<DCZonaItem> = datosRecuperados.getSerializable("ListaZona") as ArrayList<DCZonaItem>

        val reenviar = Bundle()
        val fragment = FrgZonaPiso()
        val IDZONA = datosRecuperados.getString("IDZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()


        //EVALUCAR SI HAY DATOS O NO
        if(datosRecuperados.getSerializable("BORRADOR")!=null){
            listaPedidoBorrador = datosRecuperados.getSerializable("BORRADOR") as ArrayList<DataClassPedidoBorrador>
        }

        //AGREGA A LAS LISTA BORRADOR LOS ITEM DE LA LISTA PEDIDO
        val lista = ArrayList<DataClassPedido>()
        for(i in listaPedido.indices){
            if (listaPedido[i].estadoPedido == "PENDIENTE"){
                var lt = listaPedido[i]
                lista.add(DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,lt.observacion,lt.estadoPedido,lt.idProducto,lt.camanda,utils().priceIGV(lt.precioTotal),utils().priceSubTotal(lt.precioTotal),0))
            }

            if(i == listaPedido.indices.last){
                listaPedidoBorrador.add(DataClassPedidoBorrador(IDZONA!!.toInt(),IDMESA!!,lista))
            }
        }

        println("*****************************************")
        println("Lista de borrador "+ listaPedidoBorrador)
        println("*****************************************")

        fragment.arguments = reenviar
        reenviar.putSerializable("DatosUsuario",DatosUsuario)
        reenviar.putSerializable("ListaZona",DatosZonas)
        reenviar.putSerializable("ListaCategoria",listaCategoria)
        reenviar.putSerializable("BORRADOR",listaPedidoBorrador)


        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getMesa2("piso eq '$IDZONA' and idMesa eq $IDMESA")
            activity?.runOnUiThread {
                if (response.code() == 200) {

                    println("***************************************************************")
                    println("Respuesta de Corrutina: ${response.body()?.get(0)?.NombreMozo}")
                    println("***************************************************************")

                    if (response.body()?.get(0)?.NombreMozo==null || response.body()?.get(0)?.NombreMozo==""|| response.body()?.get(0)?.NombreMozo==" "){
                        NAMEMOZOTEMPORAL = " "
                    }else{
                        NAMEMOZOTEMPORAL = response.body()?.get(0)?.NombreMozo
                    }

                    println("***************************************************************")
                    println("NAMEMOZOTEMPORAL : ${NAMEMOZOTEMPORAL}")
                    println("***************************************************************")


                    if (listaPedido.size<=0){
                        if (NAMEMOZOTEMPORAL==DatosUsuario.nombreUsuario || NAMEMOZOTEMPORAL==" "){
                            Toast.makeText(activity, "SE LIBERA MESA", Toast.LENGTH_SHORT).show()
                            cambiarEstadoMesa(IDZONA.toString(), IDMESA!!, "L", " ")
                        }
                    }else{
                        if (NAMEMOZOTEMPORAL==DatosUsuario.nombreUsuario || NAMEMOZOTEMPORAL==" "){
                            Toast.makeText(activity, "MESA RESERVADA", Toast.LENGTH_SHORT).show()
                            cambiarEstadoMesa(IDZONA.toString(), IDMESA!!.toInt(), "O", DatosUsuario.nombreUsuario)
                        }
                    }
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.frm_panel,fragment)!!.commit()
                }else{
                    Toast.makeText(activity, "Error: ${response.code()} // guardarCambio // piso eq '$IDZONA' and idMesa eq $IDMESA", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun iniciarDatosGuardadosBorrador() {
        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()
        if(datosRecuperados?.getSerializable("BORRADOR")!=null){
            listaPedidoBorrador = datosRecuperados.getSerializable("BORRADOR") as ArrayList<DataClassPedidoBorrador>
        }
        for (i in listaPedidoBorrador.indices){
            if (listaPedidoBorrador[i].zona==IDZONA?.toInt() && listaPedidoBorrador[i].mesa==IDMESA){
                listaPedido.addAll(listaPedidoBorrador[i].pedidoBorrador)
                adapterPedido.notifyDataSetChanged()
                listaPedidoBorrador.removeAt(i)
                break
            }
        }
        actualizarPrecioTotal()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun imprimirPrecuenta() {
        //OBTENER DATOS DE ZONA Y MESA
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados!!.getSerializable("DatosUsuario") as DCLoginDatosExito
        val NAMEZONA = datosRecuperados.getString("NAMEZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()
        val datoZona = datosRecuperados.getString("IDZONA")
        val datoMesa = datosRecuperados.getString("IDMESA")
        var idprecuenta = 0


        //AGREGA A LAS LISTA BORRADOR LOS ITEM DE LA LISTA PEDIDO
        val lista = ArrayList<DataClassPedido>()
        for(i in listaPedido.indices){

            if (listaPedido[i].estadoPedido == "ATENDIDO"){
                var lt = listaPedido[i]
                lista.add(DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,lt.observacion,lt.estadoPedido,lt.idProducto,lt.camanda,utils().priceIGV(lt.precioTotal),utils().priceSubTotal(lt.precioTotal),1))
            }

        }

        //SUMAR CANTIDAD DE PEDIDOS
        var cantidadLista = 0f
        for (i in lista.indices){
            cantidadLista += lista[i].precioTotal.toFloat()
        }

        val formato= DecimalFormat()
        formato.maximumFractionDigits = 2 //Numero maximo de decimales a mostrar

        //var Total = cantidadLista
        //var igv = formato.format((cantidadLista*0.18).toFloat())
        //var sub = formato.format(Total-igv.toFloat()).toFloat()

        //TERNER LA LISTA PARA LA PRECUENTA
        val listadetalleprecuenta = ArrayList<ListDetalle>()
        for (i in lista.indices){
            listadetalleprecuenta.add(i, ListDetalle(lista[i].cantidad,lista[i].namePlato,lista[i].precio,lista[i].precioTotal))
        }

        //BOLETA DE PRECUENTA PREPARADA

        /*
        if(listadetalleprecuenta.size>0){
            val boletaPreCuenta = DCPrecuenta(idpedido,DatosUsuario.nameMozo,NAMEZONA!!,IDMESA.toString()!!,"${LocalDateTime.now()}","","S/. $sub","S/. $igv","S/. $Total",listadetalleprecuenta.toList())
            val imprimir = Imprimir()
            imprimir.printTcp("192.168.1.114",9100, boletaPreCuenta)
        }else{
          //Toast.makeText(activity, "No hay cuenta", Toast.LENGTH_SHORT).show()
        }*/

        //****************************** NUEVO    /////////////////////////*************
        @RequiresApi(Build.VERSION_CODES.O)
        fun getPreCuenta(idPedido:String) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiInterface!!.getPreCuenta("$idPedido")
                activity?.runOnUiThread {
                    if(response.code()==200){
                        val datos = response.body()!!

                        println("**************** PRECUENTA ************")
                        println(datos.subtotal)
                        println(datos.igv)
                        println(datos.total)
                        println("**************** ******************")

                        val imprimir = Imprimir()
                        imprimir.printTcp("${prefs.getIPPrecuenta()}", 9100, datos)
                        Toast.makeText(activity, "Se envio con exito", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity, "Error: ${response.code()} // getPreCuenta // $idPedido ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //Informacion de la Mesa
        fun getInfoMesa(mesa:String,piso:String){
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiInterface!!.getPedidoZonaMesa(" mesa eq '$mesa' and piso eq '$piso' and estado eq '0001' " )
                activity?.runOnUiThread {
                    if(response.code()==200){
                        idprecuenta = response.body()!![0].idPedido
                        getPreCuenta(idprecuenta.toString())
                    }else{
                        Toast.makeText(activity, "Error: ${response.code()} // getInfoMesa // ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        getInfoMesa(datoMesa.toString(),datoZona.toString())

        //****************************** NUEVO    /////////////////////////*************

    }

    //ENVIAR COMANDA (revisado)
    @RequiresApi(Build.VERSION_CODES.O)
    fun  enviarPedido(){

        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val reenviar = Bundle()
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)
        val fragment = FrgZonaPiso()
        fragment.arguments = reenviar

        //***********  INFLA DIALOGO DE COMANDA
        val builder = activity?.let { AlertDialog.Builder(it) }
        val vista = layoutInflater.inflate(R.layout.dialogue_confirma_comanda,null)
        vista.setBackgroundResource(R.color.trans)

        builder?.setView(vista)

        val dialog = builder?.create()
        dialog?.show()

        //*********** Declara elementos *****************
        var bt_confirmarcomanda = vista.findViewById<Button>(R.id.bt_confirmarcomanda)
        val bt_cancelarcomanda = vista.findViewById<Button>(R.id.bt_cancelarcomanda)

        //*********** BOTON GUARDAR DEL DIALOGO ********
        bt_confirmarcomanda.setOnClickListener {

            for(i in listaPedido.indices){
                if (listaPedido[i].estadoPedido=="PENDIENTE"){
                    for(e in listaPedido.indices){
                        if(listaPedido[e].estadoPedido=="ATENDIDO"){
                            println("*****************************************")
                            println("HAY UN ATENDIDO Y PENDIENTE")
                            if (idpedido!=0){
                                getDataOrdenPedido(idpedido)
                            }else{
                                getDataOrdenPedido(0)
                            }
                            dialog?.hide()
                            break
                        }else{
                            if (listaPedido.indices.last == e){
                                println("*****************************************")
                                println("SOLO ATENDIDO")
                                if (idpedido!=0){
                                    getDataOrdenPedido(idpedido)
                                }else{
                                    getDataOrdenPedido(0)
                                }

                                dialog?.hide()
                                println("*****************************************")
                                break
                            }
                        }
                    }
                    break
                }
                if(i == listaPedido.indices.last){
                    Toast.makeText(activity, "INGRESAR MAS PEDIDOS NUEVOS", Toast.LENGTH_SHORT).show()
                    dialog?.hide()
                    desaparecerBarraNavegacion()
                }
            }
            if (listaPedido.isEmpty()){
                Toast.makeText(activity, "INGRESAR NUEVO PEDIDO", Toast.LENGTH_SHORT).show()
                dialog?.hide()
                desaparecerBarraNavegacion()
            }

            /*
                if (listaPedido[i].estadoPedido=="PENDIENTE"){
                    getDataOrdenPedido()
                    dialog?.hide()
                    desaparecerBarraNavegacion()
                    break
                }else{
                    if(listaPedido[i].estadoPedido=="ATENDIDO"){
                        println("*****************************************")
                        println("*****************************************")
                        println("HAY UN ATENDIDO")
                        println("$idpedido")
                        println("*****************************************")
                        println("*****************************************")
                        //getDataOrdenPedido2(idpedido)
                        break
                    }

                    if (listaPedido.isEmpty()){
                        Toast.makeText(activity, "INGRESAR NUEVO PEDIDO", Toast.LENGTH_SHORT).show()
                        dialog?.hide()
                        desaparecerBarraNavegacion()
                    }
                }
            }

            if (listaPedido.isEmpty()){
                Toast.makeText(activity, "INGRESAR NUEVO PEDIDO", Toast.LENGTH_SHORT).show()
                dialog?.hide()
                desaparecerBarraNavegacion()
            }









            if (listaPedido.size>0){
                getDataOrdenPedido()
                dialog?.hide()
            }else{
                Toast.makeText(activity, "NO HAY PEDIDOS", Toast.LENGTH_SHORT).show()
            }

                if (listaPedido.size>0){
                    for (i in listaPedido.indices){
                        if (listaPedido[i].estadoPedido=="ATENDIDO"){
                            println("*****************************************")
                            println("*****************************************")
                            println("HAY UN ATENDIDO")
                            println("$idpedido")
                            println("*****************************************")
                            println("*****************************************")
                            getDataOrdenPedido2(idpedido)
                            break
                        }else{
                            if (i == listaPedido.indices.last){
                                println("*****************************************")
                                println("*****************************************")
                                println("TODOS SON PENDIENTE")
                                println("*****************************************")
                                println("*****************************************")
                                getDataOrdenPedido()
                            }
                        }
                    }
                    //getDataOrdenPedido()
                    dialog?.hide()
                }else{
                    Toast.makeText(activity, "NO HAY PEDIDOS", Toast.LENGTH_SHORT).show()
                }
        */

            desaparecerBarraNavegacion()

        }

        bt_cancelarcomanda.setOnClickListener {
            dialog?.hide()
            desaparecerBarraNavegacion()
        }
        desaparecerBarraNavegacion()
    }
    //ENVIAR ORDEN PEDIDO
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDataOrdenPedido(IDPedido:Int) {
        //***********************
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val reenviar = Bundle()
        reenviar.putSerializable("DatosUsuario",DatosUsuario)
        val fragment = FrgZonaPiso()
        fragment.arguments = reenviar

        val IDZONA = datosRecuperados.getString("IDZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()

        println("***************************************")
        println("El idPedido: $IDPedido")
        println("***************************************")

        //***********************
        listaPedidoFiltrado.clear()
        for(i in listaPedido.indices){
            var lt = listaPedido[i]
            var conteo = 0
            var pos = 0
            for (e in listaPedidoFiltrado.indices){
                if (listaPedido[i].namePlato == listaPedidoFiltrado[e].namePlato){
                    conteo += 1
                }
                if (conteo == 1){
                    pos = e
                    break
                }
            }

            if (conteo == 0){
                listaPedidoFiltrado.add(listaPedido[i])
            }else{
                listaPedidoFiltrado[pos] = DataClassPedido(lt.cantidad+listaPedidoFiltrado[pos].cantidad,lt.namePlato,lt.categoria,lt.precio,((lt.cantidad+listaPedidoFiltrado[pos].cantidad
                        )*lt.precio),lt.observacion,lt.estadoPedido,lt.idProducto,lt.camanda,utils().priceIGV(((lt.cantidad+listaPedidoFiltrado[pos].cantidad
                        )*lt.precio)),utils().priceSubTotal(((lt.cantidad+listaPedidoFiltrado[pos].cantidad
                        )*lt.precio)),lt.flag_color)
            }
        }


        var importeTotalLista = listaPedidoFiltrado.sumOf { t -> t.precioTotal } //****
        var TotalIgv = listaPedidoFiltrado.sumOf { t -> t.igv }             //******
        var subTotalLista= listaPedidoFiltrado.sumOf { t -> t.psigv}       //******

        println("****************************************")
        println("Importe: $importeTotalLista")
        println("Total IGV:$TotalIgv")
        println("Total S-IGV$subTotalLista")

        println("LISTA PEDIDO: $listaPedidoFiltrado")

        println("****************************************")
        importeTotalLista = utils().pricetostringformat(importeTotalLista).toDouble()
        TotalIgv = utils().pricetostringformat(TotalIgv).toDouble()
        subTotalLista = utils().pricetostringformat(subTotalLista).toDouble()

        println("****************************************")
        println("Importe: $importeTotalLista")
        println("Total IGV: $TotalIgv")
        println("Total S-IGV: $subTotalLista")
        println("****************************************")


        println("****************************************")
        println("*************  ******************")
        println(listaPedidoFiltrado)
        println("****************************************")
        println("****************************************")

        for (i in listaPedidoFiltrado.indices){
            listaDetalleOrdenPedido.add(
                Detalle(
                    iD_PEDIDO=IDPedido,
                    iD_PRODUCTO=listaPedidoFiltrado[i].idProducto,
                    cantidad=listaPedidoFiltrado[i].cantidad,
                    nombre=listaPedidoFiltrado[i].namePlato,
                    precio=listaPedidoFiltrado[i].precio,
                    descuento=0,
                    igv=listaPedidoFiltrado[i].igv,
                    importe=listaPedidoFiltrado[i].cantidad*listaPedidoFiltrado[i].precio,
                    canT_DESPACHADA=0,
                    canT_FACTURADA=0,
                    observacion=listaPedidoFiltrado[i].observacion,
                    secuencia=i+1,
                    preciO_ORIGINAL=listaPedidoFiltrado[i].precio,
                    tipo="1",            //***
                    importE_DSCTO=0,
                    afectO_IGV="1",       //***
                    comision=0,
                    iD_PRESUPUESTO=0,
                    cdG_SERV="",
                    flaG_C="0",          //***
                    flaG_P="",
                    flaG_COLOR=listaPedidoFiltrado[i].flag_color.toString(),      //***
                    noM_UNIDAD="",
                    comanda=listaPedidoFiltrado[i].camanda,
                    mozo=DatosUsuario.nombreUsuario,
                    unidad="0001",      //***
                    codigO_BARRA="",
                    poR_PERCEPCION=0,
                    percepcion=0,
                    valoR_VEN=listaPedidoFiltrado[i].psigv, // total - igv
                    uniD_VEN="",
                    fechA_VEN="${LocalDateTime.now()}",
                    factoR_CONVERSION=1, //**
                    cdG_KIT="",
                    swT_PIGV="S",      //**
                    swT_PROM="N",
                    canT_KIT=0,
                    swT_DCOM="N",     //**
                    swT_SABOR="0",
                    swT_FREE="N",      //**
                    noM_IMP="",
                    seC_PROD=0,
                    poR_DETRACCION=0,
                    detraccion=0,
                    usuariO_ANULA="",
                    fechA_ANULA="${LocalDateTime.now()}",
                    margen=0,
                    importE_MARGEN=0,
                    costO_ADIC=0)
            )
        }


        val call: Call<DCOrdenPedido> = apiInterface!!.postOrdenPedido(
            DCOrdenPedido(iD_PEDIDO=IDPedido,
                numerO_PEDIDO="",
                noM_MON="",
                smB_MON="",
                conD_PAGO=DatosUsuario.cdgpago,
                persona="",
                ruc="",
                freC_DIAS="",
                codigO_VENDEDOR=DatosUsuario.cdG_VENDEDOR,
                codigO_CPAGO=DatosUsuario.cdgpago,
                codigO_MONEDA=DatosUsuario.cdgmoneda,
                fechA_PEDIDO="${LocalDateTime.now()}",
                numerO_OCLIENTE="",
                importE_STOT=subTotalLista,
                importE_IGV=TotalIgv,
                importE_DESCUENTO=0,
                importE_TOTAL=importeTotalLista,
                porcentajE_DESCUENTO=0,
                porcentajE_IGV=DatosUsuario.poR_IGV.toInt(),
                observacion = "",
                serie="",
                estado="0001",
                iD_CLIENTE=DatosUsuario.iD_CLIENTE,
                importE_ISC=0,
                usuariO_CREACION=DatosUsuario.usuariocreacion,
                usuariO_AUTORIZA=DatosUsuario.usuarioautoriza,
                fechA_CREACION="${LocalDateTime.now()}",
                fechA_MODIFICACION="${LocalDateTime.now()}",
                codigO_EMPRESA=DatosUsuario.codigO_EMPRESA,
                codigO_SUCURSAL=DatosUsuario.sucursal,
                valoR_VENTA=subTotalLista,
                iD_CLIENTE_FACTURA=DatosUsuario.iD_CLIENTE,
                codigO_VENDEDOR_ASIGNADO="",
                fechA_PROGRAMADA="${LocalDateTime.now()}",
                facturA_ADELANTADA="",
                contacto="",
                emaiL_CONTACTO="",
                lugaR_ENTREGA="",
                iD_COTIZACION=DatosUsuario.iD_COTIZACION.toInt(),
                comision=0,
                puntO_VENTA="",
                redondeo=DatosUsuario.redondeo,
                validez="",
                motivo="",
                correlativo="",
                centrO_COSTO="",
                tipO_CAMBIO=0,
                sucursal=DatosUsuario.sucursal,
                mesa="$IDMESA",
                piso="$IDZONA",
                detalle=listaDetalleOrdenPedido.toList()
            )
        )

        call.enqueue(object  : Callback<DCOrdenPedido>{
            override fun onResponse(call: Call<DCOrdenPedido>, response: Response<DCOrdenPedido>) {

                Toast.makeText(activity, "IDPEDIDO: ${response.body()?.iD_PEDIDO}", Toast.LENGTH_SHORT).show()

                val IDZONA = datosRecuperados.getString("IDZONA")
                val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()
                val NAMEMOZO = datosRecuperados?.getString("NAMEMOZO")

                //Toast.makeText(activity, "Envio de Pedido Bien", Toast.LENGTH_SHORT).show()

                println("********** EXITO DE ENVIO DE LISTA PEDIDOS **************")
                println("********** ${response.body()?.iD_PEDIDO} ****************")
                println("*********************************************************")

                var listaCodComanda: ArrayList<String> = ArrayList()
                response.body()?.detalle!!.forEach {
                    listaCodComanda.add(it.comanda)
                }

                //MANDA A IMPRIMIR
                enviarComanda("${response.body()?.iD_PEDIDO}",listaCodComanda)

                if (listaPedido.size > 0){
                    cambiarEstadoMesa(IDZONA.toString(),IDMESA!!.toInt(),"O",DatosUsuario.nombreUsuario)
                }else{
                    if (DatosUsuario.nombreUsuario == NAMEMOZOTEMPORAL || NAMEMOZO==null || NAMEMOZO==" ") {
                        cambiarEstadoMesa(IDZONA.toString(), IDMESA!!.toInt(), "L", " ")
                    }
                }

                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frm_panel,fragment)?.commit()

            }
            override fun onFailure(call: Call<DCOrdenPedido>, t: Throwable) {
                Toast.makeText(activity, "Error: ENVIO DE LISTA PEDIDOS'", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun cambiarColorComandado(comada:String,ippedido:Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getEstadoComandado(comada, ippedido)
            if(response.isSuccessful){

            }
        }
    }

    fun enviarComanda(idPedido:String, lista:ArrayList<String>){
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getComanda("$idPedido")
            response.enqueue(object  : Callback<List<DCComandaItem>>{
                override fun onResponse( call: Call<List<DCComandaItem>>,response: Response<List<DCComandaItem>>) {
                    val impComanda = ImprimirComanda()
                    val r = response.body()!!
                    var count = 0

                    r.forEach { cabezera ->

                        cabezera.detalle.forEach { detalleComanda ->
                            for (i in listaPedido.indices){
                                if(detalleComanda.producto == listaPedido[i].namePlato && listaPedido[i].estadoPedido == "PENDIENTE"){
                                    listaDetalleComanda.add(com.example.apppedido.domain.Detalle(
                                        detalleComanda.iD_PRODUCTO,
                                        detalleComanda.producto,
                                        listaPedido[i].cantidad,
                                        detalleComanda.precio,
                                        detalleComanda.precio*listaPedido[i].cantidad,
                                        listaPedido[i].observacion,
                                        detalleComanda.noM_IMP,
                                        secuencia = i,
                                    ))
                                }
                            }
                        }


                        println(" listaDetalleComanda: $listaDetalleComanda ")
                        println(" listaDetalleComanda: $listaDetalleComanda ")

                        listaComanda.add(DCComandaItem(cabezera.numerO_PEDIDO,cabezera.destino,cabezera.zona,cabezera.mesa,cabezera.mesero,cabezera.rutacomanda,cabezera.fechayhora,listaDetalleComanda))
                    }

                    //************************** CAMBIA DE COLOR EL ITEM
                    var init = " "
                    lista.forEach {
                        println("Lista: "+lista)
                        if (init != it){
                            println("$it y ${idPedido.toInt()}")
                            cambiarColorComandado(it,idPedido.toInt())
                            init = it
                        }
                    }

                    listaComanda.forEach {
                        impComanda.printTcp(it.rutacomanda,9100,it)
                    }
                }

                override fun onFailure(call: Call<List<DCComandaItem>>, t: Throwable) {
                    Toast.makeText(activity, "Error: ENVIO DE LISTA PEDIDOS", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }



    //LISTAR PRECUENTA
    fun consultaPedidosPendiente(){
        //RECIBE DATOS Y USA DATOS
        val datosRecuperados = arguments
        val datoZona = datosRecuperados?.getString("IDZONA")
        val datoMesa = datosRecuperados?.getString("IDMESA")

        fun getDataPreCuenta(idPedido: String) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiInterface!!.getPrePedidos("$idPedido")
                activity?.runOnUiThread {
                    if(response.code()==200){
                        var data = response.body()
                        for(i in data!!.indices){
                            listaPedido.add(DataClassPedido(data[i].cantidad,data[i].nombre,"",data[i].precio,data[i].importe,"","ATENDIDO",data[i].iD_PRODUCTO,data[i].comanda,data[i].igv,0.0,data[i].flaG_COLOR))
                        }
                        adapterPedido.notifyDataSetChanged()
                        iniciarDatosGuardadosBorrador()
                        actualizarPrecioTotal()
                    }else{
                        Toast.makeText(activity, "Error: ${response.code()} // getDataPreCuenta // '$idPedido", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        //Informacion de la Mesa
        fun getInfoMesa(mesa:String,piso:String){
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiInterface!!.getPedidoZonaMesa(" mesa eq '$mesa' and piso eq '$piso' and estado eq '0001' " )
                activity?.runOnUiThread {
                    val IDZONA = datosRecuperados?.getString("IDZONA")
                    val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()
                    val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
                    val NAMEMOZO = datosRecuperados?.getString("NAMEMOZO")

                    if(response.code()==200){
                        if (!response.body()!!.isEmpty()){
                            idpedido = response.body()?.get(0)?.idPedido!!
                            getDataPreCuenta(idpedido.toString())
                        }else{
                            if (DatosUsuario.nombreUsuario == NAMEMOZOTEMPORAL || NAMEMOZOTEMPORAL==null || NAMEMOZOTEMPORAL==" ") {

                            }
                            iniciarDatosGuardadosBorrador()
                        }
                    }else{
                        Toast.makeText(activity, "Error: ${response.code()} // getInfoMesa // mesa eq '$mesa' and piso eq '$piso' and estado eq '0001' ", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }


        getInfoMesa(datoMesa.toString(),datoZona.toString())
        actualizarPrecioTotal()

    }


    //********************       CATEGORIA        ********************//
    fun initComponentCategoria(){
        val rv_categoria = view?.findViewById<RecyclerView>(R.id.rv_categoria)
        rv_categoria?.layoutManager = GridLayoutManager(activity,2, RecyclerView.HORIZONTAL,false)
        adapterCategoria = AdapterCategoria(listaCategoria) { dataclassCategoria -> onItemDatosCategoria(dataclassCategoria) }
        rv_categoria?.adapter = adapterCategoria
    }
    fun onItemDatosCategoria(dataclassCategoria: DCCategoriaItem) {
        val iv_buscarPlato = view?.findViewById<ImageView>(R.id.iv_buscarPlato)
        val nameCategoria = dataclassCategoria.nameCategoria
        getDataPlato(nameCategoria)
        iv_buscarPlato?.setOnClickListener {
            iconbuscarPlato(nameCategoria)
        }
    }
    fun getDataCategoria() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getCategoria()
            if(response.code()==200){
                val dataResponse = response.body()!!
                listaCategoria.clear()
                ValidarConfiguracion.database.daoCategoria().deleteTableCategoria()
                ValidarConfiguracion.database.daoCategoria().clearPrimaryKeyCategoria()
                dataResponse.forEach {
                    ValidarConfiguracion.database.daoCategoria().insertCategoria(
                        EntityCategoria(
                            id=0,
                            idCategoria = it.idCategoria,
                            nameCategoria = it.nameCategoria)
                    )
                }
                println("Recoleccion de datos")
                println(ValidarConfiguracion.database.daoZona().getAllZonas())

                activity?.runOnUiThread {
                    dataResponse.forEach {
                        listaCategoria.add(
                            DCCategoriaItem(
                                it.nameCategoria,
                                it.idCategoria
                            )
                        )
                    }
                    getDataPlato(listaCategoria[0].nameCategoria)
                    adapterCategoria.notifyDataSetChanged()
                }
            }else{
                Toast.makeText(activity, "Error: ${response.code()} // getDataCategoria ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun inyectarDatosCategoria(lista: ArrayList<DCCategoriaItem>){

        ///*************   ROOM FUNCIONAL ******************************************************
        GlobalScope.launch(Dispatchers.Default) {
            //ValidarConfiguracion.database.daoCategoria().DAOdeleteTable()
            //ValidarConfiguracion.database.daoCategoria().clearPrimaryKey()

            lista.forEach { ValidarConfiguracion.database.daoCategoria().insertCategoria(EntityCategoria(0,it.idCategoria,it.nameCategoria)) }

            for (i in 1..lista.size){
                //val categoriaId = ValidarConfiguracion.database.daoCategoria().getCategoriaId(i)
                //val categoriaNombre = ValidarConfiguracion.database.daoCategoria().getCategoriaNombre(i)

                withContext(Dispatchers.Main) {
                    //listaCategoria.add(DCCategoriaItem(categoriaNombre,categoriaId))
                    if (i == lista.size){
                        adapterCategoria.notifyDataSetChanged()
                    }
                }
            }
        }

    }
    //********************        PLATOS        ********************//
    fun initPlato(){
        val rv_plato = view?.findViewById<RecyclerView>(R.id.rv_platillo)
        rv_plato?.layoutManager = GridLayoutManager(activity,3, RecyclerView.VERTICAL,false)
        adapterPlato = AdapterPlato(listaPlato) { dataClassPlato -> onItemDatosPlato(dataClassPlato) }
        rv_plato?.adapter = adapterPlato
    }
    fun getDataPlato(nombreCat:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getPlato("$nombreCat","0001")
            activity?.runOnUiThread {
                if(response.code()==200){
                    listaPlato.clear()
                    listaPlato.addAll(response.body()!!)
                    adapterPlato.notifyDataSetChanged()
                }else{
                    Toast.makeText(activity, "Error: ${response.code()} // getDataMesa ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun onItemDatosPlato(dataClassPlato: DCPlatoItem) {
        val rv_pedido = view?.findViewById<RecyclerView>(R.id.rv_pedido)
        val datos = dataClassPlato

        //-------------Evalua POSICION Y ACCION DE AGREGAR-------------------
        //println("------- Evalua POSICION Y ACCION DE AGREGAR-------------")
        var action = 0
        var pos = -1
        for (i in listaPedido.indices){
            if(listaPedido[i].namePlato==datos.nombre && listaPedido[i].estadoPedido=="PENDIENTE"){
                action += 1
            }
            if (action == 1){
                pos = i
                println("posicion: $pos")
                break
            }
        }
        //*******************   PRUEBA *******************************

        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()
        val NAMEMOZO = datosRecuperados?.getString("NAMEMOZO")
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito

        println("********** ANTES DE GET*************")
        println("Nombre del ${NAMEMOZOTEMPORAL}")
        println("IDZONA del ${IDZONA}")
        println("IDMESA del ${IDMESA}")
        println("************************************")

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getMesa2("piso eq '$IDZONA' and idMesa eq $IDMESA")
            activity?.runOnUiThread {
                if (response.code() == 200) {

                    println("***************************************************************")
                    println("Respuesta de Corrutina: ${response.body()?.get(0)?.NombreMozo}")
                    println("***************************************************************")

                    if (response.body()?.get(0)?.NombreMozo==null|| response.body()?.get(0)?.NombreMozo==""|| response.body()?.get(0)?.NombreMozo==" "){
                        NAMEMOZOTEMPORAL = " "
                    }else{
                        NAMEMOZOTEMPORAL = response.body()?.get(0)?.NombreMozo
                    }

                    println("***************************************************************")
                    println("NAMEMOZOTEMPORAL : ${NAMEMOZOTEMPORAL}")
                    println("***************************************************************")

                    if (NAMEMOZOTEMPORAL == DatosUsuario.nombreUsuario || NAMEMOZOTEMPORAL == " "){
                        //Agrega el producto
                        cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"O",DatosUsuario.nombreUsuario)
                        if (action == 0) {
                            listaPedido.add(DataClassPedido(1,datos.nombre,datos.codigo,datos.preciO_VENTA,datos.preciO_VENTA,"","PENDIENTE",datos.iD_PRODUCTO,datos.comanda,utils().priceIGV(datos.preciO_VENTA),utils().priceSubTotal(datos.preciO_VENTA),0))
                            adapterPedido.getItemId(pos)
                            rv_pedido?.scrollToPosition(listaPedido.size - 1)
                            rv_pedido?.adapter?.notifyDataSetChanged()
                        } else {
                            val lt = listaPedido[pos]
                            var cantidad = lt.cantidad + 1
                            var precioTotal = dataClassPlato.preciO_VENTA * cantidad
                            println("El precio es: $precioTotal")
                            listaPedido.set(pos,DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto,lt.camanda,utils().priceIGV(precioTotal),utils().priceSubTotal(precioTotal),0))
                            rv_pedido?.adapter?.notifyDataSetChanged()
                        }
                    }else{
                        Toast.makeText(activity, "La mesa esta siendo atendida por $NAMEMOZOTEMPORAL", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(activity, "Error: ${response.code()} // onItemDatosPlato // piso eq '$IDZONA' and idMesa eq $IDMESA", Toast.LENGTH_SHORT).show()
                }

                println("*************  LISTA DE PEDIDO ****************")
                println(listaPedido)
                println("*****************************")

                actualizarPrecioTotal()
            }
        }
        //-------------------------------------------------------------------
    }

    fun iconbuscarPlato(nameCategoria:String) {
        //Asignar Valores
        val builder = AlertDialog.Builder(requireActivity())
        val view = layoutInflater.inflate(R.layout.dialogue_buscador,null)
        var sv_buscador = view.findViewById<SearchView>(R.id.sv_buscador)
        var tv_cartelCategoria = view.findViewById<TextView>(R.id.tv_cartelCategoria)
        var bt_cerrarbusqueda = view.findViewById<Button>(R.id.bt_cerrarbusqueda)

        //Pasando la vista al builder
        builder.setView(view)

        //Creando dialog
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.TOP)
        dialog.show()

        //iniciar datos
        tv_cartelCategoria.text = "CATEGORIA: ${nameCategoria}"

        val rv_platoBuscar = view?.findViewById<RecyclerView>(R.id.rv_platoBuscar)
        rv_platoBuscar?.layoutManager = LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
        adapterPlatoFiltrado = AdapterPlatoFiltrado(listaPlatoBuscado) { dataClassPlatoBuscado -> onItemDatosPlatoBuscado(dataClassPlatoBuscado) }
        rv_platoBuscar?.adapter = adapterPlatoFiltrado

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getPlato("$nameCategoria","0001")
            activity?.runOnUiThread {
                if(response.code()==200){
                    listaPlatoBuscado.clear()
                    listaPlatoBuscado.addAll(response.body()!!)
                    adapterPlatoFiltrado.notifyDataSetChanged()
                }else{
                    Toast.makeText(activity, "Error: ${response.code()} // iconbuscarPlato // $nameCategoria 001", Toast.LENGTH_SHORT).show()
                }
            }
        }

        bt_cerrarbusqueda.setOnClickListener {
            dialog.hide()
            desaparecerBarraNavegacion()
        }

        sv_buscador?.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                println("$newText")
                filter(newText.toString())
                return false
            }
        })

    }
    fun filter(text: String) {
        val filterdNamePlato: ArrayList<DCPlatoItem> = ArrayList()
        for (i in listaPlatoBuscado.indices) {
            if (listaPlatoBuscado[i].nombre.lowercase().contains(text.lowercase())) {
                filterdNamePlato.add(listaPlatoBuscado[i])
            }
        }
        adapterPlatoFiltrado.filterList(filterdNamePlato)
    }
    fun onItemDatosPlatoBuscado(dataClassPlatoBuscado: DCPlatoItem){
        val rv_pedido = view?.findViewById<RecyclerView>(R.id.rv_pedido)
        val datos = dataClassPlatoBuscado
        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito

        //-------------Evalua POSICION Y ACCION DE AGREGAR-------------------
        //println("------- Evalua POSICION Y ACCION DE AGREGAR-------------")
        var action = 0
        var pos = -1
        for (i in listaPedido.indices){
            if(listaPedido[i].namePlato==datos.nombre){
                action += 1
            }
            if (action == 1){
                pos = i
                println("posicion: $pos")
                break
            }
        }

        //----------------  AGREGA O AUMENTA LA CANTIDAD -------------------
        println("********** ANTES DE GET*************")
        println("Nombre del ${NAMEMOZOTEMPORAL}")
        println("IDZONA del ${IDZONA}")
        println("IDMESA del ${IDMESA}")
        println("************************************")

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getMesa2("piso eq '$IDZONA' and idMesa eq $IDMESA")
            activity?.runOnUiThread {
                if (response.code() == 200) {

                    println("***************************************************************")
                    println("Respuesta de Corrutina: ${response.body()?.get(0)?.NombreMozo}")
                    println("***************************************************************")

                    if (response.body()?.get(0)?.NombreMozo==null|| response.body()?.get(0)?.NombreMozo==""|| response.body()?.get(0)?.NombreMozo==" "){
                        NAMEMOZOTEMPORAL = " "
                    }else{
                        NAMEMOZOTEMPORAL = response.body()?.get(0)?.NombreMozo
                    }

                    println("***************************************************************")
                    println("NAMEMOZOTEMPORAL : ${NAMEMOZOTEMPORAL}")
                    println("***************************************************************")

                    if (NAMEMOZOTEMPORAL == DatosUsuario.nombreUsuario || NAMEMOZOTEMPORAL == " "){
                        //Agrega el producto
                        cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"O",DatosUsuario.nombreUsuario)
                        if (action == 0) {
                            listaPedido.add(DataClassPedido(1,datos.nombre,datos.codigo,datos.preciO_VENTA,datos.preciO_VENTA,"","PENDIENTE",datos.iD_PRODUCTO,datos.comanda,utils().priceIGV(datos.preciO_VENTA),utils().priceSubTotal(datos.preciO_VENTA),0))
                            adapterPedido.getItemId(pos)
                            rv_pedido?.scrollToPosition(listaPedido.size - 1)
                            rv_pedido?.adapter?.notifyDataSetChanged()
                        } else {
                            val lt = listaPedido[pos]
                            var cantidad = lt.cantidad + 1
                            var precioTotal = datos.preciO_VENTA * cantidad
                            println("El precio es: $precioTotal")
                            listaPedido.set(pos,DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto,lt.camanda,utils().priceIGV(precioTotal),utils().priceSubTotal(precioTotal),0))
                            rv_pedido?.adapter?.notifyDataSetChanged()
                        }
                    }else{
                        Toast.makeText(activity, "La mesa esta siendo atendida por $NAMEMOZOTEMPORAL", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(activity, "Error: ${response.code()} // onItemDatosPlato // piso eq '$IDZONA' and idMesa eq $IDMESA", Toast.LENGTH_SHORT).show()
                }
                actualizarPrecioTotal()
            }
        }
    }
    //*********************   PEDIDO  ***********************
    fun initPedido(){
        val rv_pedido = view?.findViewById<RecyclerView>(R.id.rv_pedido)
        rv_pedido?.layoutManager = LinearLayoutManager(activity)
        adapterPedido = AdapterPedido(listaPedido) {dataclassPedido -> onIntemDatosPedido(dataclassPedido)}
        rv_pedido?.adapter = adapterPedido

        //////////////////////////////////////////////////////////////

        val itemswipe = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (listaPedido[viewHolder.bindingAdapterPosition].estadoPedido == "PENDIENTE"){
                    listaPedido.removeAt(viewHolder.bindingAdapterPosition)
                    actualizarPrecioTotal()
                    viewHolder.itemView.setBackgroundResource(R.drawable.effect_clic_pedido)

                    val datosRecuperados = arguments
                    val IDZONA = datosRecuperados?.getString("IDZONA")
                    val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()
                    val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
                    val NAMEMOZO = datosRecuperados?.getString("NAMEMOZO")

                    println("****************  SWIPE  ***************")
                    println("Nombre del Mozo: $NAMEMOZOTEMPORAL")
                    println("****************************************")

                    CoroutineScope(Dispatchers.IO).launch {
                        val response = apiInterface!!.getMesa2("piso eq '$IDZONA' and idMesa eq $IDMESA")
                        activity?.runOnUiThread {
                            if (response.code() == 200) {

                                println("***************************************************************")
                                println("Respuesta de Corrutina: ${response.body()?.get(0)?.NombreMozo}")
                                println("***************************************************************")

                                if (response.body()?.get(0)?.NombreMozo==null|| response.body()?.get(0)?.NombreMozo==""|| response.body()?.get(0)?.NombreMozo==" "){
                                    NAMEMOZOTEMPORAL = " "
                                }else{
                                    NAMEMOZOTEMPORAL = response.body()?.get(0)?.NombreMozo
                                }

                                println("***************************************************************")
                                println("NAMEMOZOTEMPORAL : ${NAMEMOZOTEMPORAL}")
                                println("***************************************************************")


                                if (listaPedido.size<=0){
                                    if (NAMEMOZOTEMPORAL==DatosUsuario.nombreUsuario || NAMEMOZOTEMPORAL==" "){
                                        cambiarEstadoMesa(IDZONA.toString(), IDMESA!!, "L", " ")
                                    }
                                }

                                for (i in listaPedido.indices){
                                    if (listaPedido[i].estadoPedido == "ATENDIDO" || listaPedido[i].estadoPedido == "PENDIENTE"){
                                        cambiarEstadoMesa(IDZONA.toString(), IDMESA!!, "O", NAMEMOZOTEMPORAL!!)
                                        break
                                    }
                                }
                            }else{
                                Toast.makeText(activity, "Error: ${response.code()} // onSwiped // piso eq '$IDZONA' and idMesa eq $IDMESA", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    rv_pedido?.adapter?.notifyDataSetChanged()
                }
                else{
                    rv_pedido?.adapter?.notifyDataSetChanged()
                }
            }
        }


        val swap =  ItemTouchHelper(itemswipe)
        swap.attachToRecyclerView(rv_pedido)

        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()

        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito


        val NAMEMOZO = datosRecuperados.getString("NAMEMOZO")


        if (listaPedido.size>0){
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"O",DatosUsuario.nombreUsuario)
        }else{
            if (DatosUsuario.nombreUsuario == NAMEMOZO || NAMEMOZO==null || NAMEMOZO==" ") {
                cambiarEstadoMesa(IDZONA.toString(), IDMESA!!, "L", "")
            }
        }


    }
    fun onIntemDatosPedido(dataclassPedido: DataClassPedido) {

        val rv_pedido = view?.findViewById<RecyclerView>(R.id.rv_pedido)
        val bt_eliminar = view?.findViewById<Button>(R.id.bt_disminuir)
        val bt_aumentar = view?.findViewById<Button>(R.id.bt_aumentar)
        val bt_detalle = view?.findViewById<Button>(R.id.bt_detalle)

        var datos = dataclassPedido.copy()

        //-------------Posicion--------------------------------------
        var index = -1
        for (i in listaPedido.indices) {
            if (listaPedido[i].namePlato == datos.namePlato && listaPedido[i].estadoPedido=="PENDIENTE"){
                index = i
                println("posicion: $index")
                break
            }
        }
        //-----------------------------------------------------------


        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()
        val NAMEMOZO = datosRecuperados?.getString("NAMEMOZO")
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito

        println("********** ANTES DE GET*************")
        println("Nombre del ${NAMEMOZOTEMPORAL}")
        println("IDZONA del ${IDZONA}")
        println("IDMESA del ${IDMESA}")
        println("************************************")

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getMesa2("piso eq '$IDZONA' and idMesa eq $IDMESA")
            activity?.runOnUiThread {
                if (response.code() == 200) {

                    println("***************************************************************")
                    println("Respuesta de Corrutina: ${response.body()?.get(0)?.NombreMozo}")
                    println("***************************************************************")

                    if (response.body()?.get(0)?.NombreMozo == null || response.body()
                            ?.get(0)?.NombreMozo == "" || response.body()?.get(0)?.NombreMozo == " "
                    ) {
                        NAMEMOZOTEMPORAL = " "
                    } else {
                        NAMEMOZOTEMPORAL = response.body()?.get(0)?.NombreMozo
                    }

                    println("***************************************************************")
                    println("NAMEMOZOTEMPORAL : ${NAMEMOZOTEMPORAL}")
                    println("***************************************************************")

                    bt_eliminar?.setOnClickListener {
                        if (NAMEMOZOTEMPORAL == DatosUsuario.nombreUsuario || NAMEMOZOTEMPORAL == " ") {
                            for (i in listaPedido.indices){
                                if (datos.estadoPedido == "PENDIENTE"){

                                    if (listaPedido[i].namePlato == datos.namePlato){

                                        if (listaPedido[i].estadoPedido == "PENDIENTE"){

                                            if (datos.namePlato== listaPedido[index].namePlato && listaPedido[index].cantidad>1){
                                                val lt = listaPedido.get(index)
                                                val cantidad = lt.cantidad-1
                                                val precioTotal = lt.precio*cantidad
                                                listaPedido[index] = DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto,lt.camanda,utils().priceIGV(precioTotal),utils().priceSubTotal(precioTotal),0)
                                                rv_pedido?.adapter?.notifyDataSetChanged()
                                            }

                                        }

                                    }

                                }else{
                                    Toast.makeText(activity, "Pedido no modificable", Toast.LENGTH_SHORT).show()
                                }

                            }
                            actualizarPrecioTotal()
                        }else{
                            Toast.makeText(activity, "La mesa esta siendo atendida por $NAMEMOZOTEMPORAL", Toast.LENGTH_SHORT).show()
                        }
                    }

                    bt_aumentar?.setOnClickListener {
                        if (NAMEMOZOTEMPORAL == DatosUsuario.nombreUsuario || NAMEMOZOTEMPORAL == " ") {
                            for (i in listaPedido.indices){
                                if (datos.estadoPedido == "PENDIENTE"){

                                    if (listaPedido[i].namePlato == datos.namePlato ){

                                        if (listaPedido[i].estadoPedido == "PENDIENTE"){

                                            val lt = listaPedido[index]
                                            var cantidad = lt.cantidad+1
                                            var precioTotal = lt.precio*cantidad
                                            listaPedido[index] = DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto,lt.camanda,utils().priceIGV(precioTotal),utils().priceSubTotal(precioTotal),0)
                                            rv_pedido?.adapter?.notifyDataSetChanged()

                                        }


                                    }

                                }else{
                                    Toast.makeText(activity, "Pedido no modificable", Toast.LENGTH_SHORT).show()
                                }

                            }
                            actualizarPrecioTotal()
                        }else{
                            Toast.makeText(activity, "La mesa esta siendo atendida por $NAMEMOZOTEMPORAL", Toast.LENGTH_SHORT).show()
                        }
                    }

                    bt_detalle?.setOnClickListener {
                        if (NAMEMOZOTEMPORAL == DatosUsuario.nombreUsuario || NAMEMOZOTEMPORAL == " ") {
                            for (i in listaPedido.indices){

                                if (datos.estadoPedido == "PENDIENTE"){

                                    if (listaPedido[i].namePlato == datos.namePlato) {

                                        if (listaPedido[i].estadoPedido == "PENDIENTE"){

                                            //***********  Alerta de Dialogo  ***********
                                            val builder = activity?.let { AlertDialog.Builder(it) }
                                            val vista = layoutInflater.inflate(R.layout.dialogue_detalle,null)
                                            vista.setBackgroundResource(R.color.trans)

                                            val lt = listaPedido[index]

                                            builder?.setView(vista)

                                            val dialog = builder?.create()
                                            dialog?.window!!.setGravity(Gravity.TOP)
                                            dialog?.show()

                                            //***********Declara elementos *****************
                                            var et_detalle = vista.findViewById<EditText>(R.id.et_detalle)
                                            val bt_guardarDetalle = vista.findViewById<Button>(R.id.bt_guardarDetalle)
                                            val tv_AlerObservacion = vista.findViewById<TextView>(R.id.tv_AlerObservacion)
                                            tv_AlerObservacion.text = listaPedido[index].observacion
                                            if (!listaPedido[index].observacion.isNullOrEmpty() || !listaPedido[index].observacion.isNullOrBlank()){
                                                et_detalle.setText(listaPedido[index].observacion)
                                            }
                                            //*********** BOTON GUARDAR DEL DIALOGO ********
                                            bt_guardarDetalle.setOnClickListener {
                                                var detalle:String = et_detalle.text.toString()

                                                listaPedido[index] = DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,detalle,"PENDIENTE",lt.idProducto,lt.camanda,utils().priceIGV(lt.precioTotal),utils().priceSubTotal(lt.precioTotal),0)
                                                dialog?.hide()

                                                desaparecerBarraNavegacion()
                                                adapterPedido.notifyDataSetChanged()
                                            }
                                        }


                                    }
                                }else{
                                    Toast.makeText(activity, "Pedido no modificable", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            Toast.makeText(activity, "La mesa esta siendo atendida por $NAMEMOZOTEMPORAL", Toast.LENGTH_SHORT).show()
                        }
                    }

                    actualizarPrecioTotal()

                }
            }
        }




    }

    //******************** FUNCIONES ADICIONALES ***********************
    //INICIA DATOS DE LA ZONA Y MESA
    fun iniZonaMesa() {
        val iniZona=view?.findViewById<TextView>(R.id.tv_TitleZona)
        val iniMesa=view?.findViewById<TextView>(R.id.tv_TitleMesa)
        val iniMozo=view?.findViewById<TextView>(R.id.tv_Mozo)

        //RECIBE DATOS Y USA DATOS
        val datosRecuperados = arguments

        iniMozo?.text = "ATENDIDO: "
        iniZona?.text = "${ datosRecuperados?.getString("NAMEZONA") }"
        iniMesa?.text = "MESA ${ datosRecuperados?.getString("IDMESA") }"
    }
    //ACTUALIZA EL PRECIO TOTAL A PAGAR
    fun actualizarPrecioTotal(){
        //------------------  SUMA DE PRECIO DE LA LISTA---------------
        var cantidadLista = 0f
        for (i in listaPedido.indices){
            cantidadLista += listaPedido[i].precioTotal.toFloat()
        }

        val tv_PTotal = view?.findViewById<TextView>(R.id.tv_PTotal)
        val formato= DecimalFormat()
        formato.maximumFractionDigits = 2 //Numero maximo de decimales a mostrar
        tv_PTotal?.text = "S/. ${utils().pricetostringformat(cantidadLista.toDouble())}"
        //-------------------------------------------------------------
    }
    //DESAPARECER BARRA DE NAVEGACION
    private fun desaparecerBarraNavegacion() {
        val decorView = view
        decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
    //CAMBIO DE ESTADO DE MESA
    private fun cambiarEstadoMesa(idZona:String,idMesa:Int,estadoMesa:String,nameMozo:String) {
        println("idZona: $idZona // idMesa: $idMesa  // estadoMesa: $estadoMesa")
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.putCambiarEstadoMesa(idZona,idMesa,estadoMesa,nameMozo)
            activity?.runOnUiThread {
                if(response.code() != 200){
                    //Toast.makeText(activity, "Error: ${response.code()} // cambiarEstadoMesa ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

