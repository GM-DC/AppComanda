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
import com.example.apppedido.Imprimir
import com.example.apppedido.R
import com.example.apppedido.domain.DataClassPedidoBorrador
import com.example.apppedido.domain.Model.DCCategoriaItem
import com.example.apppedido.domain.Model.DCLoginDatosExito
import com.example.apppedido.domain.Model.DCPlatoItem
import com.example.apppedido.domain.Model.DCPrecuenta
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
import java.text.DecimalFormat
import java.time.LocalDateTime


class FrgCatPlat: Fragment() {

    //***************INICIO ATRIBUTOS********************
    private lateinit var adapterCategoria: AdapterCategoria
    private lateinit var adapterPlato: AdapterPlato
    private lateinit var adapterPlatoFiltrado: AdapterPlatoFiltrado
    private lateinit var adapterPedido: AdapterPedido
    private val listaCategoria = ArrayList<DCCategoriaItem>()
    private val listaPlato = ArrayList<DCPlatoItem>()
    private val listaPlatoBuscado = ArrayList<DCPlatoItem>()
    private val listaPedido = ArrayList<DataClassPedido>()
    private val listaDetalleOrdenPedido = ArrayList<Detalle>()
    private val listaPedidoFiltrado = ArrayList<DataClassPedido>()

    private var listaPedidoBorrador = ArrayList<DataClassPedidoBorrador>()

    lateinit var idpedido:String

    var apiInterface: APIService? = null

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
        //val bt_cancelar = view?.findViewById<Button>(R.id.bt_cancelar)
        val bt_precuenta = view?.findViewById<Button>(R.id.bt_precuenta)
        val bt_borrador = view?.findViewById<Button>(R.id.bt_guardarCambio)
        val bt_atras = view?.findViewById<Button>(R.id.bt_atras)


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
        bt_enviar_comanda?.setOnClickListener { enviarComanda() }
        //bt_cancelar?.setOnClickListener { regregarZonaPisoCancelado() }
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

    private fun guardarCambio() {
        //DECLARAR VARIABLES
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
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
                lista.add(DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,lt.observacion,lt.estadoPedido,lt.idProducto))
            }

            if(i == listaPedido.indices.last){
                listaPedidoBorrador.add(DataClassPedidoBorrador(IDZONA!!.toInt(),IDMESA!!,lista))
            }
        }

        fragment.arguments = reenviar
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)
        reenviar.putSerializable("BORRADOR",listaPedidoBorrador)

        if (listaPedido.size>0){
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"O")
        }else{
            cambiarEstadoMesa(IDZONA.toString(),IDMESA!!,"L")
        }

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frm_panel,fragment)?.commit()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun imprimirPrecuenta() {
        //OBTENER DATOS DE ZONA Y MESA
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val NAMEZONA = datosRecuperados.getString("NAMEZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()





        //AGREGA A LAS LISTA BORRADOR LOS ITEM DE LA LISTA PEDIDO
        val lista = ArrayList<DataClassPedido>()
        for(i in listaPedido.indices){

            if (listaPedido[i].estadoPedido == "ATENDIDO"){
                var lt = listaPedido[i]
                lista.add(DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,lt.observacion,lt.estadoPedido,lt.idProducto))
            }

        }

        //SUMAR CANTIDAD DE PEDIDOS
        var cantidadLista = 0f
        for (i in lista.indices){
            cantidadLista += lista[i].precioTotal.toFloat()
        }

        val formato= DecimalFormat()
        formato.maximumFractionDigits = 2 //Numero maximo de decimales a mostrar

        var Total = cantidadLista
        var igv = formato.format((cantidadLista*0.18).toFloat())
        var sub = formato.format(Total-igv.toFloat()).toFloat()

        //TERNER LA LISTA PARA LA PRECUENTA
        val listadetalleprecuenta = ArrayList<ListDetalle>()
        for (i in lista.indices){
            listadetalleprecuenta.add(i, ListDetalle(lista[i].cantidad,lista[i].namePlato,lista[i].precio,lista[i].precioTotal))
        }

        //BOLETA DE PRECUENTA PREPARADA

        if(listadetalleprecuenta.size>0){
            val boletaPreCuenta = DCPrecuenta(idpedido,DatosUsuario.nameMozo,NAMEZONA!!,IDMESA.toString()!!,"${LocalDateTime.now()}","","S/. $sub","S/. $igv","S/. $Total",listadetalleprecuenta.toList())
            val imprimir = Imprimir()
            imprimir.printTcp("192.168.1.114",9100, boletaPreCuenta)
        }else{
          //Toast.makeText(activity, "No hay cuenta", Toast.LENGTH_SHORT).show()
        }

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
            listaPedido.add(DataClassPedido(1,dataClassPlatoBuscado.nombre,dataClassPlatoBuscado.codigo,dataClassPlatoBuscado.preciO_VENTA,dataClassPlatoBuscado.preciO_VENTA,"","PENDIENTE",dataClassPlatoBuscado.iD_PRODUCTO))
            rv_pedido?.adapter?.notifyDataSetChanged()
            rv_pedido?.scrollToPosition(listaPedido.size-1)
        }else{
            val lt = listaPedido.get(pos)
            var cantidad = lt.cantidad+1
            var precioTotal = dataClassPlatoBuscado.preciO_VENTA*cantidad
            println("El precio es: $precioTotal")
            listaPedido.set(pos, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto))
            rv_pedido?.adapter?.notifyDataSetChanged()
        }
        //-------------------------------------------------------------------

        actualizarPrecioTotal()
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


    private fun iniZonaMesa() {
        val iniZona=view?.findViewById<TextView>(R.id.tv_TitleZona)
        val iniMesa=view?.findViewById<TextView>(R.id.tv_TitleMesa)

        //RECIBE DATOS Y USA DATOS
        val datosRecuperados = arguments
        iniZona?.text = "${ datosRecuperados?.getString("NAMEZONA") }"
        iniMesa?.text = "MESA ${ datosRecuperados?.getString("IDMESA") }"
    }

    //ENVIAR COMANDA
    @RequiresApi(Build.VERSION_CODES.O)
    fun enviarComanda(){

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
            if (listaPedido.size>0){
                getDataOrdenPedido()
                dialog?.hide()
            }else{
                Toast.makeText(activity, "NO HAY PEDIDOS", Toast.LENGTH_SHORT).show()
            }
            desaparecerBarraNavegacion()
        }

        bt_cancelarcomanda.setOnClickListener {
            dialog?.hide()
            desaparecerBarraNavegacion()
        }

        //****************************************************************
        //getDataOrdenPedido()
        //****************************************************************

    }
    //ENVIAR ORDEN PEDIDO API
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDataOrdenPedido() {

        //***********************
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val reenviar = Bundle()
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)
        val fragment = FrgZonaPiso()
        fragment.arguments = reenviar

        val NAMEZONA = datosRecuperados.getString("NAMEZONA")
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
                        )*lt.precio,lt.observacion,lt.estadoPedido,lt.idProducto)
            }
        }


        //SUMAR CANTIDAD DE PEDIDOS
        var cantidadLista = 0f
        for (i in listaPedidoFiltrado.indices){
            cantidadLista += listaPedidoFiltrado[i].precioTotal.toFloat()
        }

        val formato= DecimalFormat()
        formato.maximumFractionDigits = 3 //Numero maximo de decimales a mostrar

        var Total = cantidadLista
        var igv = formato.format((cantidadLista*18)/118).toFloat()
        var sub = formato.format(Total-igv).toFloat()

        for (i in listaPedidoFiltrado.indices){

                listaDetalleOrdenPedido.add(
                    Detalle(
                        0,
                        listaPedidoFiltrado[i].idProducto,
                        listaPedidoFiltrado[i].cantidad,
                        listaPedidoFiltrado[i].namePlato,
                        listaPedidoFiltrado[i].precio,
                        0,
                        (((listaPedidoFiltrado[i].cantidad*listaPedidoFiltrado[i].precio)*18)/188),
                        listaPedidoFiltrado[i].cantidad*listaPedidoFiltrado[i].precio,
                        0,
                        0,
                        listaPedidoFiltrado[i].observacion,
                        0,
                        0.0,
                        "",
                        0,
                        "",
                        0,
                        0,
                        "",
                        "",
                        "",
                        "0",
                        "",
                        "",
                        DatosUsuario.nameMozo,
                        "",
                        "",
                        0,
                        0,
                        0,
                        "",
                        "${LocalDateTime.now()}",
                        0,
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        "",
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
            DCOrdenPedido(0,
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
                0,
                igv.toDouble(),
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

                val IDZONA = datosRecuperados?.getString("IDZONA")
                val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()
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
                        println("*****************************************")
                        println("DATOS PREPEDIDOS: ${data}")
                        println("*****************************************")

                        for(i in data!!.indices){
                            listaPedido.add(DataClassPedido(data[i].cantidad,data[i].nombre,"",data[i].precio.toDouble(),data[i].importe.toDouble(),"","ATENDIDO",data[i].iD_PRODUCTO))
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
                            idpedido = response.body()?.get(0)?.idPedido.toString()
                            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"O")
                            getDataPreCuenta(idpedido)
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

          listaPedido.add(DataClassPedido(1,dataClassPlato.nombre,dataClassPlato.codigo,dataClassPlato.preciO_VENTA,dataClassPlato.preciO_VENTA,"","PENDIENTE",dataClassPlato.iD_PRODUCTO))
          rv_pedido?.adapter?.notifyDataSetChanged()
          adapterPedido.getItemId(pos)
          rv_pedido?.scrollToPosition(listaPedido.size-1)

        }else{

           val lt = listaPedido.get(pos)
           var cantidad = lt.cantidad+1
           var precioTotal = dataClassPlato.preciO_VENTA*cantidad
           println("El precio es: $precioTotal")
           listaPedido.set(pos, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto))
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


    //*********************   INICIAR PEDIDO  ***********************
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
                                listaPedido[index] = DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto)

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
                            listaPedido[index] = DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto)

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

                                listaPedido[index] = DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,detalle,"PENDIENTE",lt.idProducto)
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

    //*********************   OTRAS FUNCIONES  **************************
    private fun filter(text: String) {
        val filterdNamePlato: ArrayList<DCPlatoItem> = ArrayList()
        for (i in listaPlatoBuscado.indices) {
            if (listaPlatoBuscado[i].nombre.lowercase().contains(text.lowercase())) {
                filterdNamePlato.add(listaPlatoBuscado[i])
            }
        }
        adapterPlatoFiltrado.filterList(filterdNamePlato)
    }
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


    // ACTUALIZA EL PRECIO TOTAL A PAGAR
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

}

