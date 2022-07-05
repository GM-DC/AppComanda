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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.apppedido.DataBase.ComandaDB
import com.example.apppedido.DataBase.EntityZona
import com.example.apppedido.Imprimir
import com.example.apppedido.ImprimirComanda
import com.example.apppedido.R
import com.example.apppedido.ValidarConfiguracion
import com.example.apppedido.domain.DCComandaItem
import com.example.apppedido.domain.DCPedidoMesaItem
import com.example.apppedido.domain.DataClassPedidoBorrador
import com.example.apppedido.domain.Model.DCCategoriaItem
import com.example.apppedido.domain.Model.DCLoginDatosExito
import com.example.apppedido.domain.Model.DCPedidoXMesa
import com.example.apppedido.domain.Model.DCPlatoItem
import com.example.apppedido.domain.Model.DCPrecuenta
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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.Serializable
import java.text.DecimalFormat
import java.time.LocalDateTime
import kotlin.properties.Delegates


class FrgCatPlat: Fragment() {

    //***************INICIO ATRIBUTOS********************
    private lateinit var adapterCategoria: AdapterCategoria
    private lateinit var adapterPlato: AdapterPlato
    private lateinit var adapterPlatoFiltrado: AdapterPlatoFiltrado
    private lateinit var adapterPedido: AdapterPedido
    private val listaCategoria = ArrayList<DCCategoriaItem>()
    private val listaCategoriaInjeccion = ArrayList<DCCategoriaItem>()
    private val listaPlato = ArrayList<DCPlatoItem>()
    private val listaPlatoBuscado = ArrayList<DCPlatoItem>()
    private val listaPedido = ArrayList<DataClassPedido>()
    private val listaDetalleOrdenPedido = ArrayList<Detalle>()
    private val listaPedidoFiltrado = ArrayList<DataClassPedido>()

    private val listaComanda = ArrayList<DCComandaItem>()
    private var listaPedidoBorrador = ArrayList<DataClassPedidoBorrador>()


    var apiInterface: APIService? = null
    var idpedido = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frg_cat_plat, container, false)

        return view

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //++++++++++++++++++    DECLARA COMPONENTE     +++++++++++++++++
        apiInterface = RetrofitCall.client?.create(APIService::class.java) as APIService


        val bt_enviar_comanda = view?.findViewById<Button>(R.id.bt_enviarComanda)
        val bt_cancelar = view?.findViewById<Button>(R.id.bt_cancelar)
        val bt_precuenta = view?.findViewById<Button>(R.id.bt_precuenta)
        val bt_borrador = view?.findViewById<Button>(R.id.bt_guardarCambio)
        val bt_atras = view?.findViewById<Button>(R.id.bt_atras)
        //val bt_agregar = view?.findViewById<Button>(R.id.bt_agregarpedido)

        //++++++++++++++++++   INICIA LAS FUNCIONES    +++++++++++++++++
        //INICIAR CATEGORIA
        initCategoria()
        getDataCategoria()
        //INICIAR CATEGORIA
        initPlato()
        //INICIAR PEDIDO
        initPedido()
        //Iniciar Datos
        getDataPlato("BARRA CERVEZAS")

        //Enviar Comanda
        bt_enviar_comanda?.setOnClickListener { enviarPedido() }
        bt_cancelar?.setOnClickListener {limpiarLista()}
        bt_precuenta?.setOnClickListener { imprimirPrecuenta() }
        bt_borrador?.setOnClickListener { guardarCambio() }
        bt_atras?.setOnClickListener{ guardarCambio() }

        //BUSCAR PLATOS
        consultaPedidosPendiente()

        //RECIBIR ZONA Y MESA
        iniZonaMesa()

        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()

    }

    //*************************       REVISAR y MEJORADO     *********************
    fun limpiarLista() {
        var size = listaPedido.size
        var cont = 0
        do{
            if (listaPedido[cont].estadoPedido=="PENDIENTE"){
                listaPedido.removeAt(cont)
                cont=0
                size-=1
            }else{
                cont+=1
            }
        }while(size != cont)
        adapterPedido.notifyDataSetChanged()
        actualizarPrecioTotal()
    }

    //***********************************************************************************************************************
    private fun guardarCambio() {
        //DECLARAR VARIABLES
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val DatosZonas: ArrayList<DCZonaItem> = datosRecuperados.getSerializable("ListaZona") as ArrayList<DCZonaItem>

        println("*********** DATOS RECIBIDOS ***************")
        println("$DatosZonas")
        println("********************************")


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
                lista.add(DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,lt.observacion,lt.estadoPedido,lt.idProducto,lt.camanda,lt.igv,lt.psigv))
            }

            if(i == listaPedido.indices.last){
                listaPedidoBorrador.add(DataClassPedidoBorrador(IDZONA!!.toInt(),IDMESA!!,lista))
            }
        }

        fragment.arguments = reenviar
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)
        reenviar.putSerializable("ListaZona",DatosZonas)
        reenviar.putSerializable("BORRADOR",listaPedidoBorrador)

        //CONSULTA SI HAY PEDIDO PARA COLOCAR LIBRE O OCUPADO
        if(listaPedido.size>0){
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!.toInt(),"O")
        }else{
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!.toInt(),"L")
        }

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frm_panel,fragment)?.commit()
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun imprimirPrecuenta() {
        //OBTENER DATOS DE ZONA Y MESA
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
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
                lista.add(DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,lt.observacion,lt.estadoPedido,lt.idProducto,lt.camanda,lt.igv,lt.psigv))
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
                    if(response.isSuccessful){

                        var igv = response.body()!!.igv
                        var sub = response.body()!!.subtotal
                        var Total = response.body()!!.total

                        if(listadetalleprecuenta.size>0){
                            val boletaPreCuenta = DCPrecuenta(idpedido.toString(),DatosUsuario.nameMozo,NAMEZONA!!,IDMESA.toString()!!,"${LocalDateTime.now()}","","${sub}","$igv","$Total",listadetalleprecuenta.toList())
                            val imprimir = Imprimir()
                            imprimir.printTcp("192.168.1.114",9100, boletaPreCuenta)

                            println("************************************************")
                            println("************************************************")
                            println("IDPEDIDO: $idPedido")
                            println("igv: $igv")
                            println("sub: $sub")
                            println("Total: $Total")
                            println("************************************************")
                            println("************************************************")

                        }

                    }else{
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }





        //Informacion de la Mesa
        fun getInfoMesa(mesa:String,piso:String){
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiInterface!!.getPedidoZonaMesa(" mesa eq '$mesa' and piso eq '$piso' and estado eq '0001' " )
                activity?.runOnUiThread {
                    if(response.isSuccessful){
                        idprecuenta = response.body()!![0].idPedido
                        getPreCuenta(idprecuenta.toString())
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
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)
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
                listaPedidoFiltrado[pos] = DataClassPedido(lt.cantidad+listaPedidoFiltrado[pos].cantidad,lt.namePlato,lt.categoria,lt.precio,(lt.cantidad+listaPedidoFiltrado[pos].cantidad
                        )*lt.precio,lt.observacion,lt.estadoPedido,lt.idProducto,lt.camanda,lt.igv,lt.psigv)
            }
        }


        var importeTotalLista = listaPedidoFiltrado.sumOf { t -> t.precioTotal } //****
        var TotalIgv = listaPedidoFiltrado.sumOf { t -> t.igv }             //******
        var subTotalLista= listaPedidoFiltrado.sumOf { t -> t.psigv}       //******


        val formato= DecimalFormat()
        formato.maximumFractionDigits = 2 //Numero maximo de decimales a mostrar

        importeTotalLista = formato.format(importeTotalLista).toDouble()
        TotalIgv = formato.format(TotalIgv).toDouble()
        subTotalLista = formato.format(subTotalLista).toDouble()


        println("****************************************")
        println("*************  ******************")
        println(listaPedidoFiltrado)
        println("****************************************")
        println("****************************************")

        for (i in listaPedidoFiltrado.indices){

                listaDetalleOrdenPedido.add(
                    Detalle(
                        IDPedido,
                        listaPedidoFiltrado[i].idProducto,
                        listaPedidoFiltrado[i].cantidad,
                        listaPedidoFiltrado[i].namePlato,
                        listaPedidoFiltrado[i].precio,
                        0,
                        listaPedidoFiltrado[i].igv,
                        listaPedidoFiltrado[i].cantidad*listaPedidoFiltrado[i].precio,
                        0,
                        0,
                        listaPedidoFiltrado[i].observacion,
                        i,
                        0.0,
                        "1",            //***
                        0,
                        "1",       //***
                        0,
                        0,
                        "",
                        "0",          //***
                        "",
                        "0",      //***
                        "",
                        listaPedidoFiltrado[i].camanda,
                        DatosUsuario.nameMozo,
                        "0001",      //***
                        "",
                        0,
                        0,
                        0,
                        "",
                        "${LocalDateTime.now()}",
                        1, //**
                        "",
                        "S",      //**
                        "",
                        0,
                        "N",     //**
                        "0",
                        "N",      //**
                        "",
                        0,
                        0,
                        0,
                        "",
                        "${LocalDateTime.now()}",
                        0,
                        0,
                        0)
                )
            }



        val call: Call<DCOrdenPedido> = apiInterface!!.postOrdenPedido(
            DCOrdenPedido(IDPedido,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                DatosUsuario.cdgpago,
                DatosUsuario.cdgmoneda,
                "${LocalDateTime.now()}",
                "",
                subTotalLista,
                TotalIgv,
                0,
                importeTotalLista,
                0,
                0,
                "",
                "",
                "0001",
                DatosUsuario.iD_CLIENTE,
                0,
                DatosUsuario.usuariocreacion,
                DatosUsuario.usuarioautoriza,
                "${LocalDateTime.now()}",
                "${LocalDateTime.now()}",
                DatosUsuario.codigO_EMPRESA,
                "",
                0,
                0,
                "",
                "${LocalDateTime.now()}",
                "",
                "",
                "",
                "",
                DatosUsuario.iD_COTIZACION.toInt(),
                0,
                "",
                DatosUsuario.redondeo,
                "",
                "",
                "",
                "",
                0,
                DatosUsuario.sucursal,
                "$IDMESA",
                "$IDZONA",
                listaDetalleOrdenPedido.toList()
            )
        )
        call.enqueue(object  : Callback<DCOrdenPedido>{
            override fun onResponse(call: Call<DCOrdenPedido>, response: Response<DCOrdenPedido>) {

                Toast.makeText(activity, "IDPEDIDO: ${response.body()?.iD_PEDIDO}", Toast.LENGTH_SHORT).show()

                val IDZONA = datosRecuperados?.getString("IDZONA")
                val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()

                //enviarComanda("103190")

                if (listaPedido.size > 0){
                    cambiarEstadoMesa(IDZONA!!,IDMESA!!,"O")
                }else{
                    cambiarEstadoMesa(IDZONA!!,IDMESA!!,"L")
                }

                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frm_panel,fragment)?.commit()

                println(response.body()?.iD_PEDIDO)
            }

            override fun onFailure(call: Call<DCOrdenPedido>, t: Throwable) {
            }
        })
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
                    if(response.isSuccessful){

                        var data = response.body()

                        for(i in data!!.indices){
                            listaPedido.add(DataClassPedido(data[i].cantidad,data[i].nombre,"",data[i].precio.toDouble(),data[i].importe.toDouble(),"","ATENDIDO",data[i].iD_PRODUCTO,"",data[i].igv,0.0))
                        }

                        adapterPedido.notifyDataSetChanged()
                        iniciarDatosGuardadosBorrador()
                        actualizarPrecioTotal()
                    }else{
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

                    if(response.isSuccessful){

                        if (!response.body()!!.isEmpty()){
                            idpedido = response.body()?.get(0)?.idPedido!!
                            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"O")
                            getDataPreCuenta(idpedido.toString())
                        }else{
                            iniciarDatosGuardadosBorrador()
                            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"L")
                        }
                    }
                }
            }


        }


        getInfoMesa(datoMesa.toString(),datoZona.toString())
        actualizarPrecioTotal()

    }


    //********************       CATEGORIA        ********************//
    fun initCategoria(){
        val rv_categoria = view?.findViewById<RecyclerView>(R.id.rv_categoria)
        rv_categoria?.layoutManager = GridLayoutManager(activity,2, RecyclerView.HORIZONTAL,false)
        adapterCategoria = AdapterCategoria(listaCategoria) { dataclassCategoria -> onItemDatosCategoria(dataclassCategoria) }
        rv_categoria?.adapter = adapterCategoria
    }
    fun onItemDatosCategoria(dataclassCategoria: DCCategoriaItem) {
        val iv_buscarPlato = view?.findViewById<ImageView>(R.id.iv_buscarPlato)
        val nameCategoria = dataclassCategoria.nameCategoria
        getDataPlato(nameCategoria)
        iv_buscarPlato?.setOnClickListener { iconbuscarPlato(nameCategoria) }
    }
    fun getDataCategoria() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getCategoria()
            activity?.runOnUiThread {
                if(response.isSuccessful){
                    listaCategoria.clear()
                    listaCategoria.addAll(response.body()!!)
                    adapterCategoria.notifyDataSetChanged()
                }else{
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
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
                if(response.isSuccessful){
                    listaPlato.clear()
                    listaPlato.addAll(response.body()!!)
                    adapterPlato.notifyDataSetChanged()
                }else{
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
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

        //----------------  AGREGA O AUMENTA LA CANTIDAD -------------------
        if (action == 0){

          listaPedido.add(DataClassPedido(1,datos.nombre,datos.codigo,datos.preciO_VENTA,datos.preciO_VENTA,"","PENDIENTE",datos.iD_PRODUCTO,datos.comanda,datos.igv.toDouble(),datos.psigv.toDouble()))
          rv_pedido?.adapter?.notifyDataSetChanged()
          adapterPedido.getItemId(pos)
          rv_pedido?.scrollToPosition(listaPedido.size-1)


        }else{

           val lt = listaPedido[pos]
           var cantidad = lt.cantidad+1
           var precioTotal = dataClassPlato.preciO_VENTA*cantidad
           println("El precio es: $precioTotal")
           listaPedido.set(pos, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto,lt.camanda,lt.igv,lt.psigv))
           rv_pedido?.adapter?.notifyDataSetChanged()

        }
        //-------------------------------------------------------------------


        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()

        if (listaPedido.size>0){
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"O")
        }else{
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"L")
        }


        actualizarPrecioTotal()
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
                if(response.isSuccessful){
                    listaPlatoBuscado.clear()
                    listaPlatoBuscado.addAll(response.body()!!)
                    adapterPlatoFiltrado.notifyDataSetChanged()
                }else{
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        bt_cerrarbusqueda.setOnClickListener { dialog.hide() }

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
        if (action.equals(0)){
            listaPedido.add(DataClassPedido(1,datos.nombre,datos.codigo,datos.preciO_VENTA,datos.preciO_VENTA,"","PENDIENTE",datos.iD_PRODUCTO,datos.comanda,datos.igv.toDouble(),datos.psigv.toDouble()))
            rv_pedido?.adapter?.notifyDataSetChanged()
            rv_pedido?.scrollToPosition(listaPedido.size-1)
        }else{
            val lt = listaPedido.get(pos)
            var cantidad = lt.cantidad+1
            var precioTotal = dataClassPlatoBuscado.preciO_VENTA*cantidad
            println("El precio es: $precioTotal")
            listaPedido.set(pos, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto,lt.camanda,lt.igv,lt.psigv))
            rv_pedido?.adapter?.notifyDataSetChanged()
        }
        //-------------------------------------------------------------------

        actualizarPrecioTotal()
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

                    rv_pedido?.adapter?.notifyDataSetChanged()

                    if (listaPedido.size>0){
                        cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"O")
                    }else{
                        cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"L")
                    }

                }else{
                    rv_pedido?.adapter?.notifyDataSetChanged()
                }

            }
        }


        val swap =  ItemTouchHelper(itemswipe)
        swap.attachToRecyclerView(rv_pedido)

        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()

        if (listaPedido.size>0){
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"O")
        }else{
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"L")
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

        //-----------BOTON ELIMINAR----------------
        bt_eliminar?.setOnClickListener {

            for (i in listaPedido.indices){
                if (datos.estadoPedido == "PENDIENTE"){

                    if (listaPedido[i].namePlato == datos.namePlato){

                        if (listaPedido[i].estadoPedido == "PENDIENTE"){

                            if (datos.namePlato== listaPedido[index].namePlato && listaPedido[index].cantidad>1){
                                val lt = listaPedido.get(index)
                                var cantidad = lt.cantidad-1
                                var precioTotal = lt.precio*cantidad
                                listaPedido[index] = DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto,lt.camanda,lt.igv,lt.psigv)

                                rv_pedido?.adapter?.notifyDataSetChanged()
                            }

                        }

                    }

                }else{
                    Toast.makeText(activity, "Pedido no modificable", Toast.LENGTH_SHORT).show()
                }

            }
            actualizarPrecioTotal()

        }



        //****************************** PRUEBA *******************************


        val viewHolder: RecyclerView.ViewHolder = rv_pedido!!.getChildViewHolder(rv_pedido.getChildAt(0))
        viewHolder.itemView.setBackgroundResource(R.drawable.effect_clic_pedido)






















        //*****************************************************************

        //-----------BOTON AUMENTAR---------------
        bt_aumentar?.setOnClickListener {

            for (i in listaPedido.indices){
                if (datos.estadoPedido == "PENDIENTE"){

                    if (listaPedido[i].namePlato == datos.namePlato ){

                        if (listaPedido[i].estadoPedido == "PENDIENTE"){

                            val lt = listaPedido[index]
                            var cantidad = lt.cantidad+1
                            var precioTotal = lt.precio*cantidad
                            listaPedido[index] = DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto,lt.camanda,lt.igv,lt.psigv)

                            rv_pedido?.adapter?.notifyDataSetChanged()

                        }


                    }

                }else{
                    Toast.makeText(activity, "Pedido no modificable", Toast.LENGTH_SHORT).show()
                }

            }
            actualizarPrecioTotal()
        }

        //-----------BOTON DETALLE-----------------
        bt_detalle?.setOnClickListener {
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

                            //*********** BOTON GUARDAR DEL DIALOGO ********
                            bt_guardarDetalle.setOnClickListener {
                                var detalle:String = et_detalle.text.toString()

                                listaPedido[index] = DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,detalle,"PENDIENTE",lt.idProducto,lt.camanda,lt.igv,lt.psigv)
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
        }


        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()

        if (listaPedido.size>0){
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"O")
        }else{
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"L")
        }

        actualizarPrecioTotal()


    }


    //******************** FUNCIONES ADICIONALES ***********************
    //INICIA DATOS DE LA ZONA Y MESA
        fun iniZonaMesa() {
        val iniZona=view?.findViewById<TextView>(R.id.tv_TitleZona)
        val iniMesa=view?.findViewById<TextView>(R.id.tv_TitleMesa)

        //RECIBE DATOS Y USA DATOS
        val datosRecuperados = arguments
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
        tv_PTotal?.text = "S/. ${formato.format(cantidadLista)}"
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
        private fun cambiarEstadoMesa(idZona:String,idMesa:Int,estadoMesa:String) {

        println("idZona: $idZona // idMesa: $idMesa  // estadoMesa: $estadoMesa")
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.putCambiarEstadoMesa(idZona,idMesa,estadoMesa)
            activity?.runOnUiThread {
                if(response.isSuccessful){
                    //Toast.makeText(activity, "Se cambio estado", Toast.LENGTH_SHORT).show()
                }else{
                    //Toast.makeText(activity, "Error en cambio de estado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //LIMPIAR LISTA (Falta mejorar)


    //************************* FUNCIONES SIN UTILIZAR ***********************
    /*
    val datosRecuperados = arguments
    val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito

    private val dataPreCuenta = DCPrecuenta("","","","","","","",""," ", listOf())
    private val setOrdenPedido = DCOrdenPedido(0,"","","","","","","",
        "","","","2022-06-18T21:31:14.558Z","",0.0,0.0,
        0.0,0.0,0.0,0.0,"","","",0,0,"",
        "","2022-06-18T21:31:14.558Z","2022-06-18T21:31:14.558Z","","",0.0,
        0,"","2022-06-18T21:31:14.558Z","string","","",
        "",0,0.0,"","",DatosUsuario.validez,"","","",0,"",
        listOf())

    private val listraprueba = Detalle(0,0,0,"",0.0,0.0,
        0.0,0.0,0,0,"",0,0.0,
        "",0.0,"",0,0,"","","",
        "","","","","","",0,0,
        0,"","",0,"","","",0,
        "","","","",0,0,0,"",
        "",0,0.0,0.0)
*/
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDataOrdenPedido2(idPedido:String) {
        //***********************
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val reenviar = Bundle()
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)
        val fragment = FrgZonaPiso()
        fragment.arguments = reenviar

        val IDZONA = datosRecuperados.getString("IDZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()
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
                listaPedidoFiltrado[pos] = DataClassPedido(lt.cantidad+listaPedidoFiltrado[pos].cantidad,lt.namePlato,lt.categoria,lt.precio,(lt.cantidad+listaPedidoFiltrado[pos].cantidad
                        )*lt.precio,lt.observacion,lt.estadoPedido,lt.idProducto,lt.camanda,lt.igv,lt.psigv)
            }
        }


        //SUMAR CANTIDAD DE PEDIDOS
        var cantidadLista = 0f
        var cantidadDespachada = 0
        var TotalIgv = 0.0
        var TotalSubtotal = 0.0
        for (i in listaPedidoFiltrado.indices){
            cantidadLista += listaPedidoFiltrado[i].precioTotal.toFloat()
            cantidadDespachada += listaPedidoFiltrado[i].cantidad
            TotalIgv += listaPedidoFiltrado[i].igv
            TotalSubtotal += listaPedidoFiltrado[i].psigv
        }

        TotalSubtotal= listaPedidoFiltrado.sumOf { t -> t.psigv}



        val formato= DecimalFormat()
        formato.maximumFractionDigits = 3 //Numero maximo de decimales a mostrar

        var Total = cantidadLista
        var igv = formato.format((Total*0.18).toFloat())
        TotalSubtotal = formato.format(TotalSubtotal).toDouble()
        TotalIgv = formato.format(TotalIgv).toDouble()
        TotalSubtotal = formato.format(TotalSubtotal).toDouble()



        println("****************************************")
        println("*************  ******************")
        println(listaPedidoFiltrado)
        println("****************************************")
        println("****************************************")

        for (i in listaPedidoFiltrado.indices){

            listaDetalleOrdenPedido.add(
                Detalle(
                    0,
                    listaPedidoFiltrado[i].idProducto,
                    listaPedidoFiltrado[i].cantidad,
                    listaPedidoFiltrado[i].namePlato,
                    listaPedidoFiltrado[i].precio,
                    0,
                    listaPedidoFiltrado[i].igv,
                    listaPedidoFiltrado[i].cantidad*listaPedidoFiltrado[i].precio,
                    0,
                    0,
                    listaPedidoFiltrado[i].observacion,
                    i,
                    0.0,
                    "1",            //***
                    0,
                    "1",       //***
                    0,
                    0,
                    "",
                    "0",          //***
                    "",
                    "0",      //***
                    "",
                    listaPedidoFiltrado[i].camanda,
                    DatosUsuario.nameMozo,
                    "0001",      //***
                    "",
                    0,
                    0,
                    0,
                    "",
                    "${LocalDateTime.now()}",
                    1, //**
                    "",
                    "S",      //**
                    "",
                    0,
                    "N",     //**
                    "0",
                    "N",      //**
                    "",
                    0,
                    0,
                    0,
                    "",
                    "${LocalDateTime.now()}",
                    0,
                    0,
                    0)
            )
        }



        val call: Call<DCOrdenPedido> = apiInterface!!.postOrdenPedido(
            DCOrdenPedido(idPedido.toInt(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                DatosUsuario.cdgpago,
                DatosUsuario.cdgmoneda,
                "${LocalDateTime.now()}",
                "",
                TotalSubtotal,
                TotalIgv,
                0,
                Total.toDouble(),
                0,
                0,
                "",
                "",
                "0001",
                DatosUsuario.iD_CLIENTE,
                0,
                DatosUsuario.usuariocreacion,
                DatosUsuario.usuarioautoriza,
                "${LocalDateTime.now()}",
                "${LocalDateTime.now()}",
                DatosUsuario.codigO_EMPRESA,
                "",
                0,
                0,
                "",
                "${LocalDateTime.now()}",
                "",
                "",
                "",
                "",
                DatosUsuario.iD_COTIZACION.toInt(),
                0,
                "",
                DatosUsuario.redondeo,
                "",
                "",
                "",
                "",
                0,
                DatosUsuario.sucursal,
                "$IDMESA",
                "$IDZONA",
                listaDetalleOrdenPedido.toList()
            )
        )
        call.enqueue(object  : Callback<DCOrdenPedido>{
            override fun onResponse(call: Call<DCOrdenPedido>, response: Response<DCOrdenPedido>) {

                Toast.makeText(activity, "IDPEDIDO: ${response.body()?.iD_PEDIDO}", Toast.LENGTH_SHORT).show()

                val IDZONA = datosRecuperados.getString("IDZONA")
                val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()

                //enviarComanda("103190")

                if (listaPedido.size > 0){
                    cambiarEstadoMesa(IDZONA!!,IDMESA!!,"O")
                }else{
                    cambiarEstadoMesa(IDZONA!!,IDMESA!!,"L")
                }

                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frm_panel,fragment)?.commit()

                println(response.body()?.iD_PEDIDO)
            }

            override fun onFailure(call: Call<DCOrdenPedido>, t: Throwable) {
            }
        })
    }
    fun enviarComanda(idPedido:String){
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getComanda("$idPedido" )
            activity?.runOnUiThread{
                if(response.isSuccessful){

                    val r = response.body()!!
                    val impComanda = ImprimirComanda()

                    for (i in r.indices ){
                        listaComanda.add(DCComandaItem(r[i].numerO_PEDIDO,r[i].destino,r[i].zona,r[i].mesa,r[i].mesero,r[i].fechayhora,r[i].detalle))
                        impComanda.printTcp("192.168.1.114",9100,listaComanda[i])
                    }

                }else{
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //Informacion de la Mesa
    fun getInfoMesa(mesa:String,piso:String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = apiInterface!!.getPedidoZonaMesa2(" mesa eq '$mesa' and piso eq '$piso' and estado eq '0001' " )
            call.enqueue(object : Callback<DCPedidoXMesa>{
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<DCPedidoXMesa>, response: Response<DCPedidoXMesa>) {
                    if (response.body()!!.isNotEmpty()){
                        response.body()!![0].idPedido.toString()
                    }else{
                        //getDataOrdenPedido()
                    }
                }
                override fun onFailure(call: Call<DCPedidoXMesa>, t: Throwable) {
                    Toast.makeText(activity, "Error $t", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    fun getDataMesa(idZona:String) {
        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getMesa("piso eq '$idZona'" )
            activity?.runOnUiThread{
                if(response.isSuccessful){

                    for (i in response.body()!!.indices){
                        if (response.body()!![i].idZona == IDZONA){
                            //estadoMesa = response.body()!![i].estadoTrans
                        }
                    }

                }else{
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun regregarZonaPisoCancelado() {
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val IDZONA = datosRecuperados.getString("IDZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()

        //************** EVALUA SI HAY PEDIDOS ************
        println("----------------------------${listaPedido.size}")


        //-------------Evalua POSICION Y ACCION DE AGREGAR-------------------
        //println("------- Evalua POSICION Y ACCION DE AGREGAR-------------")
        var numAtendido = 0

        for (i in listaPedido.indices){
            if(listaPedido[i].estadoPedido=="ATENDIDO"){
                numAtendido += 1
            }else{
                //listaPedido.clear()
            }
        }

        if (numAtendido==0){
            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"L")
        }else{
            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"O")
        }

        val reenviar = Bundle()
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)
        reenviar.putSerializable("BORRADOR",listaPedidoBorrador)

        val fragment = FrgZonaPiso()
        fragment.arguments = reenviar

        //***********  INFLA DIALOGO DE COMANDA
        val builder = activity?.let { AlertDialog.Builder(it) }
        val vista = layoutInflater.inflate(R.layout.dialogue_confirmar_cancelacion,null)
        vista.setBackgroundResource(R.color.trans)

        builder?.setView(vista)

        val dialog = builder?.create()
        dialog?.show()

        //*********** Declara elementos *****************
        var bt_confirmarcancelacion = vista.findViewById<Button>(R.id.bt_confirmarcancelacion)

        //*********** BOTON GUARDAR DEL DIALOGO ********
        bt_confirmarcancelacion.setOnClickListener {
            if (listaPedido.size>0){
                dialog?.hide()
            }else{
                Toast.makeText(activity, "NO HAY PEDIDOS", Toast.LENGTH_SHORT).show()
            }
            dialog?.hide()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.frm_panel,fragment)?.commit()
        }
    }
    private fun consultaPedidosPendienteLineaPrincipal() {
        //RECIBE DATOS Y USA DATOS
        val datosRecuperados = arguments
        val datoZona = datosRecuperados?.getString("IDZONA")
        val datoMesa = datosRecuperados?.getString("IDMESA")

        fun getDataPreCuenta2(idPedido: String) {
            val call: Call<List<DCPedidoMesaItem>> = apiInterface!!.getPrePedidos2("$idPedido")
            call.enqueue(object :Callback<List<DCPedidoMesaItem>>{
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<List<DCPedidoMesaItem>>,response: Response<List<DCPedidoMesaItem>>) {
                    if (response.body()!!.isNotEmpty()){

                        listaPedido.clear()
                        var data = response.body()
                        for(i in data!!.indices){
                            listaPedido.add(DataClassPedido(data[i].cantidad,data[i].nombre,"",data[i].precio.toDouble(),data[i].importe.toDouble(),"","ATENDIDO",data[i].iD_PRODUCTO,"",data[i].igv,0.0))
                        }
                        adapterPedido.notifyDataSetChanged()
                        Toast.makeText(activity, "MESA ESTA SIENDO OCUPADA", Toast.LENGTH_SHORT).show()
                        iniciarDatosGuardadosBorrador()
                        actualizarPrecioTotal()
                    }
                }

                override fun onFailure(call: Call<List<DCPedidoMesaItem>>, t: Throwable) {
                    Toast.makeText(activity, "Error $t", Toast.LENGTH_SHORT).show()
                }
            })
        }

        //Informacion de la Mesa
        fun getInfoMesa(mesa:String,piso:String){
            CoroutineScope(Dispatchers.IO).launch {
                val call = apiInterface!!.getPedidoZonaMesa2(" mesa eq '$mesa' and piso eq '$piso' and estado eq '0001' " )
                call.enqueue(object : Callback<DCPedidoXMesa>{
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(call: Call<DCPedidoXMesa>, response: Response<DCPedidoXMesa>) {
                        if (response.body()!!.isNotEmpty()){
                            getDataPreCuenta2(response.body()!![0].idPedido.toString())
                        }else{
                            //getDataOrdenPedido()
                        }
                    }
                    override fun onFailure(call: Call<DCPedidoXMesa>, t: Throwable) {
                        Toast.makeText(activity, "Error $t", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        getInfoMesa(datoMesa.toString(),datoZona.toString())
        actualizarPrecioTotal()

    }
    private fun consultaPedidosPendienteLineaPrincipal2() {
        //RECIBE DATOS Y USA DATOS
        val datosRecuperados = arguments
        val datoZona = datosRecuperados?.getString("IDZONA")
        val datoMesa = datosRecuperados?.getString("IDMESA")

        //Informacion de la Mesa
        fun getInfoMesa2(mesa:String,piso:String){
            CoroutineScope(Dispatchers.IO).launch {
                val call = apiInterface!!.getPedidoZonaMesa2(" mesa eq '$mesa' and piso eq '$piso' and estado eq '0001' " )
                call.enqueue(object : Callback<DCPedidoXMesa>{
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(call: Call<DCPedidoXMesa>, response: Response<DCPedidoXMesa>) {
                        if (response.body()!!.isEmpty() && listaPedido.size>0){
                            cambiarEstadoMesa(datoZona.toString(),datoMesa!!.toInt(),"O")
                        }else if (listaPedido.size>0){
                            cambiarEstadoMesa(datoZona.toString(),datoMesa!!.toInt(),"O")
                        }
                    }
                    override fun onFailure(call: Call<DCPedidoXMesa>, t: Throwable) {
                        Toast.makeText(activity, "Error $t", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        getInfoMesa2(datoMesa.toString(),datoZona.toString())

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun agregarpedidos() {

        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val IDZONA = datosRecuperados.getString("IDZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()
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

            //*******************************************************

            for(i in listaPedido.indices){

                println("******************************")
                println("Esta es la lista evaluada: ${listaPedido[i].estadoPedido}")
                println("******************************")

                if (listaPedido[i].estadoPedido=="PENDIENTE"){
                    println("******************************")
                    println("PASO POR AQUI")
                    println("******************************")
                    //getDataOrdenPedido()
                    dialog?.hide()
                    desaparecerBarraNavegacion()
                    break
                }else{
                    Toast.makeText(activity, "INGRESAR NUEVO PEDIDO", Toast.LENGTH_SHORT).show()
                    dialog?.hide()
                    desaparecerBarraNavegacion()
                }
            }

            if (listaPedido.isEmpty()){
                Toast.makeText(activity, "INGRESAR NUEVO PEDIDO", Toast.LENGTH_SHORT).show()
            }















            //*****************************************************************************************
            /*
            for(i in listaPedido.indices){
                if (listaPedido[i].estadoPedido=="ATENDIDO"){
                    getDataOrdenPedido()
                    dialog?.hide()
                    desaparecerBarraNavegacion()
                    break
                }else{
                    Toast.makeText(activity, "INGRESAR NUEVO PEDIDO", Toast.LENGTH_SHORT).show()
                    dialog?.hide()
                    desaparecerBarraNavegacion()
                }
            }
            if (listaPedido.isEmpty()){
                Toast.makeText(activity, "INGRESAR NUEVO PEDIDO", Toast.LENGTH_SHORT).show()
            }*/
            //*****************************************************************************************



        }

        bt_cancelarcomanda.setOnClickListener {
            dialog?.hide()
            desaparecerBarraNavegacion()
        }

        desaparecerBarraNavegacion()
    }

}

