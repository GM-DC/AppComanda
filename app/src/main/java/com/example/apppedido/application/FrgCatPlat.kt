package com.example.apppedido.application.View

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.*
import com.example.apppedido.domain.Model.*
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.infraestruture.adapters.AdapterCategoria
import com.example.apppedido.infraestruture.adapters.AdapterPedido
import com.example.apppedido.infraestruture.adapters.AdapterPlato
import com.example.apppedido.infraestruture.adapters.AdapterPlatoFiltrado
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.collections.ArrayList


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
    var apiInterface: APIService? = null
    //*************** FIN ATRIBUTOS ********************

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frg_cat_plat, container, false)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //++++++++++++++++++    DECLARA COMPONENTE     +++++++++++++++++
        apiInterface = RetrofitCall.client?.create(APIService::class.java) as APIService

        val bt_enviar_comanda = view?.findViewById<Button>(R.id.bt_enviarComanda)
        val bt_cancelar = view?.findViewById<Button>(R.id.bt_cancelar)
        val iv_buscarPlato = view?.findViewById<ImageView>(R.id.iv_buscarPlato)
0

        //++++++++++++++++++   INICIA LAS FUNCIONES    +++++++++++++++++
        //INICIAR CATEGORIA
        initCategoria()
        getDataCategoria()
        //INICIAR CATEGORIA
        initPlato()
        //INICIAR PEDIDO
        initPedido()
        //Iniciar Datos
        getDataPlato("0001")
        //Enviar Comanda
        bt_enviar_comanda?.setOnClickListener { enviarComanda() }
        bt_cancelar?.setOnClickListener { regregarZonaPiso() }
        iv_buscarPlato?.setOnClickListener { iconbuscarPlato() }

        //BUSCAR PLATOS
        consultaPrecuenta()


        //Recibir datos de mesa y Zona
        iniZonaMesa()
    }

    fun iconbuscarPlato() {
        //Asignar Valores
        val builder = AlertDialog.Builder(requireActivity())
        val view = layoutInflater.inflate(R.layout.dialogue_buscador,null)
        var sv_buscador = view.findViewById<SearchView>(R.id.sv_buscador)


        //Pasando la vista al builder
        builder.setView(view)

        //Creando dialog
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.TOP)
        dialog.show()

        //iniciar datos
        val rv_platoBuscar = view?.findViewById<RecyclerView>(R.id.rv_platoBuscar)
        rv_platoBuscar?.layoutManager = LinearLayoutManager(activity,RecyclerView.VERTICAL,false)
        adapterPlatoFiltrado = AdapterPlatoFiltrado(listaPlatoBuscado) { dataClassPlatoBuscado -> onItemDatosPlatoBuscado(dataClassPlatoBuscado) }
        rv_platoBuscar?.adapter = adapterPlatoFiltrado

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getPlatoBuscado()
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
            if(listaPedido[i].namePlato==datos.namePlato){
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
            listaPedido.add(DataClassPedido(1,dataClassPlatoBuscado.namePlato,dataClassPlatoBuscado.IdCategoria,dataClassPlatoBuscado.PrecioVenta.toBigDecimal(),dataClassPlatoBuscado.PrecioVenta.toBigDecimal(),""))
            rv_pedido?.adapter?.notifyDataSetChanged()
            rv_pedido?.scrollToPosition(listaPedido.size-1)
        }else{
            val lt = listaPedido.get(pos)
            var cantidad = lt.cantidad+1
            var precioTotal = dataClassPlatoBuscado.PrecioVenta*cantidad
            println("El precio es: $precioTotal")
            listaPedido.set(pos, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal.toBigDecimal(),lt.observacion))
            rv_pedido?.adapter?.notifyDataSetChanged()
        }
        //-------------------------------------------------------------------

        actualizarPrecioTotal()
    }
    fun buscarPlatosTotal() {

    }

    private fun regregarZonaPiso() {
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito

        val reenviar = Bundle()
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)

        val fragment = FrgZonaPiso()
        fragment.arguments = reenviar

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frm_panel,fragment)?.commit()
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
    fun enviarComanda(){

        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito

        val reenviar = Bundle()
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)

        val fragment = FrgZonaPiso()
        fragment.arguments = reenviar

        //****************************************************************

        try {
            getDataOrdenPedido()
        }catch (e:Exception){
            println("Error : $e")
        }finally {
            println("Fin")
        }

        //****************************************************************
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.frm_panel,fragment)?.commit()
        //.addToBackStack(null)
        //transaction?.replace(R.id.frm_panel,fragment)?.show(fragment)?.hide(FrgCatPlat())?.commit()
    }
    //ENVIAR ORDEN PEDIDO API
    fun getDataOrdenPedido() {

        //VOLVER A ENVIARLO LOS DATOS
        //RECIVIR DATOS Y USARLO
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito


        for (i in listaPedido.indices){
            listaDetalleOrdenPedido.add(
                Detalle(
                "18",
                0,
                0,
                0,
                0,
                "",
                "",
                "",
                "",
                0,
                0,
                0, //---- Conflicto
                0,
                0,
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                0,
                0,
                0,
                0,
                listaPedido[i].precioTotal.toInt(),     //// ACLARAR TEMA
                0,
                DatosUsuario.nameMozo,
                "",
                "",
                listaPedido[i].namePlato,
                listaPedido[i].observacion,
                0,
                0,
                0,
                0,
                listaPedido[i].precio.toInt(),         //// ACLARAR TEMA
                0,
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
            )
            )
        }

        val call: Call<DCOrdenPedido> = apiInterface!!.postOrdenPedido(
            DCOrdenPedido(
                "",
                "0001",
                DatosUsuario.codigO_EMPRESA,
                DatosUsuario.cdgmoneda,
                "0001",
                "",
                "",
                0,
                DatosUsuario.cdgpago,
                "",
                "",
                Detalle(
                    "18",
                    0,
                    0,
                    0,
                    0,
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    0,
                    0,
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,     //// ACLARAR TEMA
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    0,
                    0,
                    0,         //// ACLARAR TEMA
                    0,
                    0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                ),
                "",
                "",
                DatosUsuario.facturA_ADELANTADA,
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                DatosUsuario.iD_COTIZACION.toInt(),
                0,
                0,
                0,
                0,
                0,
                0,
                "",
                "", //------ problema
                "",
                "",
                "",
                "",
                "",
                0,
                DatosUsuario.poR_IGV.toInt(),
                DatosUsuario.puntO_VENTA,
                DatosUsuario.redondeo,
                "",
                "",
                "",
                DatosUsuario.sucursal,
                0,
                "",
                "",
                DatosUsuario.validez,
                0,
            )
        )
        call.enqueue(object  : Callback<DCOrdenPedido>{
            override fun onResponse(call: Call<DCOrdenPedido>, response: Response<DCOrdenPedido>) {
                println("Exito")
            }
            override fun onFailure(call: Call<DCOrdenPedido>, t: Throwable) {
                println("Error")
            }
        })


    }
    //LISTAR PRECUENTA
    fun consultaPrecuenta(){
        //RECIBE DATOS Y USA DATOS
        val datosRecuperados = arguments
        val datoZona = datosRecuperados?.getString("IDZONA")
        val datoMesa = datosRecuperados?.getString("IDMESA")

        fun getDataPreCuenta(idPedido: String) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiInterface!!.getPrecuenta("$idPedido")
                activity?.runOnUiThread {
                    if(response.isSuccessful){
                        var data = response.body()
                        for (i in data?.detalle!!.indices)
                        listaPedido.add(DataClassPedido(data?.detalle?.get(i).cantidad,data?.detalle?.get(i).nombre, "",data?.detalle?.get(i).precio.toBigDecimal(),data?.detalle?.get(i).importe.toBigDecimal(),""))
                        adapterPedido.notifyDataSetChanged()
                        actualizarPrecioTotal()
                    }else{
                        println("Fallo2")
                    }
                }
            }
        }

        //Informacion de la Mesa
        fun getInfoMesa(mesa:String,piso:String){
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiInterface!!.getPedidoZonaMesa("mesa eq '$mesa' and piso eq '$piso'" )
                activity?.runOnUiThread {
                    if(response.isSuccessful){
                        println(response.body())
                        if (!response.body()!!.isEmpty()){
                            getDataPreCuenta(response.body()?.get(0)?.idPedido.toString())
                        }
                    }else{
                        println("Fracaso")
                    }
                }
            }
        }

        println("*********  ANALISIS DE CARGAR PEDIDO ***********")
        println("Zona: ${datoZona} ${datoZona?.javaClass}")
        println("Mesa: ${datoMesa} ${datoMesa?.javaClass}")
        println("Segunda evaluacion: " + getInfoMesa(datoMesa!!,datoZona!!))
        println("*********  ************************* ***********")
    }


    //********************       CATEGORIA        ********************//
    fun initCategoria(){
        val rv_categoria = view?.findViewById<RecyclerView>(R.id.rv_categoria)
        rv_categoria?.layoutManager = GridLayoutManager(activity,2, RecyclerView.HORIZONTAL,false)
        adapterCategoria = AdapterCategoria(listaCategoria) { dataclassCategoria -> onItemDatosCategoria(dataclassCategoria) }
        rv_categoria?.adapter = adapterCategoria
    }
    fun onItemDatosCategoria(dataclassCategoria: DCCategoriaItem) {
        val idCategoria = dataclassCategoria.idCategoria
        getDataPlato(idCategoria)
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
    fun getDataPlato(idCat:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getPlato("idcategoria eq '$idCat'")
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
            if(listaPedido[i].namePlato==datos.namePlato){
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
            listaPedido.add(DataClassPedido(1,dataClassPlato.namePlato,dataClassPlato.IdCategoria,dataClassPlato.PrecioVenta.toBigDecimal(),dataClassPlato.PrecioVenta.toBigDecimal(),""))
            rv_pedido?.adapter?.notifyDataSetChanged()
            rv_pedido?.scrollToPosition(listaPedido.size-1)
        }else{
            val lt = listaPedido.get(pos)
            var cantidad = lt.cantidad+1
            var precioTotal = dataClassPlato.PrecioVenta*cantidad
            println("El precio es: $precioTotal")
            listaPedido.set(pos, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal.toBigDecimal(),lt.observacion))
            rv_pedido?.adapter?.notifyDataSetChanged()
        }
        //-------------------------------------------------------------------

        //----------------  CAMBIAR ESTADO  -------------------
        val datosRecuperados = arguments
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()

        if (action.equals(0)){
            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"L")
        }else{
            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"O")
        }


        //-----------------------------------------------------

        actualizarPrecioTotal()
    }

    private fun cambiarEstadoMesa(idZona:String,idMesa:Int,estadoMesa:String) {
        println("idZona: $idZona // idMesa: $idMesa  // estadoMesa: $estadoMesa")
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.putCambiarEstadoMesa(idZona,idMesa,estadoMesa)
            activity?.runOnUiThread {
                if(response.isSuccessful){
                    Toast.makeText(activity, "Se cambio estado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(activity, "Error en cambio de estado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //*********************   INICIAR PEDIDO  ***********************
    fun initPedido(){
        val rv_pedido = view?.findViewById<RecyclerView>(R.id.rv_pedido)
        rv_pedido?.layoutManager = LinearLayoutManager(activity)
        adapterPedido = AdapterPedido(listaPedido) {dataclassPedido -> onIntemDatosPedido(dataclassPedido)}
        rv_pedido?.adapter = adapterPedido

        /////*/////////////////////////////////////////////////////////
        val itemswipe = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean { return false }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listaPedido.removeAt(viewHolder.bindingAdapterPosition)
                actualizarPrecioTotal()
                rv_pedido?.adapter?.notifyDataSetChanged()
                if (listaPedido.indices.contains(viewHolder.bindingAdapterPosition)){
                    println("Paso aqui?")
                }
            }
        }
        val swap =  ItemTouchHelper(itemswipe)
        swap.attachToRecyclerView(rv_pedido)
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
            if (listaPedido[i].namePlato == datos.namePlato){
                println("Bucle: ${listaPedido[i].namePlato} == ${datos.namePlato}")
                index = i
                break
            }
        }
        //----------------------------------------------------------

        //-----------BOTON ELIMINAR----------------
        bt_eliminar?.setOnClickListener {

            if (datos.namePlato==listaPedido.get(index).namePlato && listaPedido.get(index).cantidad>1){
                val lt = listaPedido.get(index)
                var cantidad = lt.cantidad-1
                var precioTotal = lt.precio*cantidad.toBigDecimal()
                listaPedido.set(index, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion))
                rv_pedido?.adapter?.notifyDataSetChanged()
                println("Ya no cumple")
            }else if(datos.namePlato==listaPedido.get(index).namePlato && listaPedido.get(index).cantidad==1){

            }

            actualizarPrecioTotal()

        }

        //-----------BOTON AUMENTAR---------------
        bt_aumentar?.setOnClickListener {
            val lt = listaPedido[index]
            println("Boton aumentar $index")
            var cantidad = lt.cantidad+1
            var precioTotal = lt.precio*cantidad.toBigDecimal()
            listaPedido.set(index, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion))
            rv_pedido?.adapter?.notifyDataSetChanged()


            actualizarPrecioTotal()

        }

        //-----------BOTON DETALLE-----------------
        bt_detalle?.setOnClickListener {
            //***********  Alerta de Dialogo  ***********
            val builder = activity?.let { AlertDialog.Builder(it) }
            val vista = layoutInflater.inflate(R.layout.dialogue_detalle,null)
            val lt = listaPedido.get(index)

            builder?.setView(vista)

            val dialog = builder?.create()
            dialog?.show()

            //***********Declara elementos *****************
            var et_detalle = vista.findViewById<EditText>(R.id.et_detalle)
            val bt_guardarDetalle = vista.findViewById<Button>(R.id.bt_guardarDetalle)
            //val tv_observacion = findViewById<TextView>(R.id.tv_observacion)
            println("El index es: $index   ------------------------")


            //*********** BOTON GUARDAR DEL DIALOGO ********
            bt_guardarDetalle.setOnClickListener {

                var detalle:String = et_detalle.text.toString()

                println("Al comienzo: ${listaPedido[index].observacion}")
                listaPedido[index] = DataClassPedido(lt.cantidad,lt.namePlato,lt.categoria,lt.precio,lt.precioTotal,detalle)
                println("Al final: ${listaPedido[index].observacion}")

                dialog?.hide()
            }


        }


    }

    //*********************   OTRAS FUNCIONES  **************************
    // BUSQUEDA DE PEDIDO
    /*fun buscarPlato(query: String?){
        val filterdNamePlato: ArrayList<DCPlatoItem> = ArrayList()

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getPlatoNombre("filter=nombe eq '$query'")
            activity?.runOnUiThread {
                if(response.isSuccessful){
                    if (response.body() != null) {
                        for (i in response.body()!!.indices) {
                            if (query != null) {
                                if (listaPlato[i].namePlato.lowercase().contains(query.lowercase())) {
                                    filterdNamePlato.clear()
                                    filterdNamePlato.add(listaPlato[i])
                                }
                            }
                        }
                        adapterPlato.filterList(filterdNamePlato)
                    }
                }
            }
        }
    }*/

    private fun filter(text: String) {
        val filterdNamePlato: ArrayList<DCPlatoItem> = ArrayList()
        for (i in listaPlatoBuscado.indices) {
            if (listaPlatoBuscado[i].namePlato.lowercase().contains(text.lowercase())) {
                filterdNamePlato.add(listaPlatoBuscado[i])
            }
        }
        adapterPlatoFiltrado.filterList(filterdNamePlato)
    }




    // ACTUALIZA EL PRECIO TOTAL A PAGAR
    fun actualizarPrecioTotal(){
        //------------------  SUMA DE PRECIO DE LA LISTA---------------
        var cantidadLista:Float = 0f
        for (i in listaPedido.indices){
            cantidadLista += listaPedido[i].cantidad.toFloat()
        }

        val tv_PTotal = view?.findViewById<TextView>(R.id.tv_PTotal)
        val formato= DecimalFormat()
        formato.maximumFractionDigits = 2 //Numero maximo de decimales a mostrar
        tv_PTotal?.text = "S/. ${formato.format(cantidadLista)}"
        //-------------------------------------------------------------
    }
}

