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
        val bt_cancelar = view?.findViewById<Button>(R.id.bt_cancelar)
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
        bt_cancelar?.setOnClickListener { regregarZonaPisoCancelado() }
        bt_precuenta?.setOnClickListener { imprimirPrecuenta() }
        bt_borrador?.setOnClickListener { guardarCambio() }
        bt_atras?.setOnClickListener{ regregarZonaPisoCancelado() }

        //BUSCAR PLATOS
        consultaPedidosPendiente()

        //Recibir datos de mesa y Zona
        iniZonaMesa()

        //listaPedido.add(0,DataClassPedido(1,"SALCHILOCURA","pollo",15.2,15.2,"","ATENDIDO"))


        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()

        borrador()



    }

    private fun borrador() {



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

    private fun guardarCambio() {

        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val reenviar = Bundle()
        reenviar.putSerializable("DATOUSUARIO",DatosUsuario)
        val fragment = FrgZonaPiso()
        fragment.arguments = reenviar

    }

    private fun imprimirPrecuenta() {
        //OBTENER DATOS DE ZONA Y MESA
        val datosRecuperados = arguments
        val DatosUsuario: DCLoginDatosExito = datosRecuperados?.getSerializable("DatosUsuario") as DCLoginDatosExito
        val NAMEZONA = datosRecuperados.getString("NAMEZONA")
        val IDMESA = datosRecuperados.getString("IDMESA")?.toInt()

        //SUMAR CANTIDAD DE PEDIDOS
        var cantidadLista = 0f
        for (i in listaPedido.indices){
            cantidadLista += listaPedido[i].precioTotal.toFloat()
        }

        val formato= DecimalFormat()
        formato.maximumFractionDigits = 2 //Numero maximo de decimales a mostrar

        var Total = cantidadLista
        var igv = formato.format((cantidadLista*18)/118).toFloat()
        var sub = formato.format(Total-igv).toFloat()

        //TERNER LA LISTA PARA LA PRECUENTA
        val listadetalleprecuenta = ArrayList<ListDetalle>()
        for (i in listaPedido.indices){
            listadetalleprecuenta.add(i, ListDetalle(listaPedido[i].cantidad,listaPedido[i].namePlato,listaPedido[i].precio,listaPedido[i].precioTotal))
        }

        //BOLETA DE PRECUENTA PREPARADA

        if(listaPedido.size>0){
            val boletaPreCuenta = DCPrecuenta(idpedido,DatosUsuario.nameMozo,NAMEZONA!!,IDMESA.toString()!!,"","","S/. $sub","S/. $igv","S/. $Total",listadetalleprecuenta.toList())
            val imprimir = Imprimir()
            imprimir.printTcp("192.168.1.114",9100, boletaPreCuenta)
        }else{
            Toast.makeText(activity, "No hay cuenta", Toast.LENGTH_SHORT).show()
        }

    }


    fun iconbuscarPlato(nameCategoria:String) {
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
        val IDZONA = datosRecuperados?.getString("IDZONA")
        val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()

        //************** EVALUA SI HAY PEDIDOS ************
        println("----------------------------${listaPedido.size}")

        if (listaPedido.size > 0){
            listaPedido.clear()
            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"L")
        }else{
            cambiarEstadoMesa(IDZONA!!,IDMESA!!,"L")
        }

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


        //***********Declara elementos *****************
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
        }

        bt_cancelarcomanda.setOnClickListener {
            dialog?.hide()
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

        for (i in listaPedido.indices){
            listaDetalleOrdenPedido.add(
                Detalle(
                    0,
                    listaPedido[i].idProducto,
                    listaPedido[i].cantidad,
                    listaPedido[i].namePlato,
                    listaPedido[i].precio,
                    0,
                    0,
                    0,
                    0,
                    0,
                    listaPedido[i].observacion,
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
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0,
                    0,
                    0,
                    "",
                    "2022-06-23T22:05:14.449Z",
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
                    "2022-06-23T22:05:14.449Z",
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
                "",
                "",
                "${LocalDateTime.now()}",
                "",
                0,
                0,
                0,
                0,
                0,
                0,
                "",
                "",
                "0001",
                0,
                0,
                "",
                "",
                "2022-06-18T21:31:14.558Z",
                "2022-06-18T21:31:14.558Z",
                "",
                "",
                0,
                0,
                "",
                "2022-06-18T21:31:14.558Z",
                "",
                "",
                "",
                "",
                0,
                0,
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                "",
                "$IDMESA",
                "$IDZONA",
                listaDetalleOrdenPedido.toList()
            )
        )
        call.enqueue(object  : Callback<DCOrdenPedido>{
            override fun onResponse(call: Call<DCOrdenPedido>, response: Response<DCOrdenPedido>) {
                println("*****************************************************")
                println("****************      Exito          ****************")
                println("*****************************************************")

                println("*****************************************************")
                Toast.makeText(activity, "IDPEDIDO: ${idpedido}", Toast.LENGTH_SHORT).show()
                println("********** Numero pedido:   ${response.body()?.iD_PEDIDO}    *************")
                println("*****************************************************")


                val IDZONA = datosRecuperados?.getString("IDZONA")
                val IDMESA = datosRecuperados?.getString("IDMESA")?.toInt()
                if (listaPedido.size > 0){
                    println()
                    println("Se cambio a O")
                    cambiarEstadoMesa(IDZONA!!,IDMESA!!,"O")
                }else{
                    println("Se cambio a L")
                    cambiarEstadoMesa(IDZONA!!,IDMESA!!,"L")
                }

                val transaction = fragmentManager?.beginTransaction()
                transaction?.replace(R.id.frm_panel,fragment)?.commit()

                println(response.body()?.iD_PEDIDO)

            }
            override fun onFailure(call: Call<DCOrdenPedido>, t: Throwable) {
                println("*****************************************************")
                println("****************      ERROR          ****************")
                println("*****************************************************")
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

                        //PREGUNTAR
                        /*
                        for(i in data!!.indices){
                            listaPedido.add(DataClassPedido(data[i].cantidad,data[i].nombre,"",data[i].precio.toDouble(),data[i].importe.toDouble(),"","ATENDIDO"))
                        }*/

                        adapterPedido.notifyDataSetChanged()
                        actualizarPrecioTotal()

                    }else{

                        println("*****************************************")
                        println("FALLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
                        println("*****************************************")
                    }
                }
            }
        }


        //Informacion de la Mesa
        fun getInfoMesa(mesa:String,piso:String){
            CoroutineScope(Dispatchers.IO).launch {
                val response = apiInterface!!.getPedidoZonaMesa(" mesa eq '$mesa' and piso eq '$piso' and estado eq '0002' " )
                activity?.runOnUiThread {

                    if(response.isSuccessful){
                        println(response.body())
                        if (!response.body()!!.isEmpty()){

                            idpedido = response.body()?.get(0)?.idPedido.toString()

                            getDataPreCuenta(idpedido)

                            println("*****************************************")
                            println("*****************************************")
                            println("IDPEDIDO: ${idpedido}")
                            println("*****************************************")
                            println("*****************************************")


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

        println(" ${nombreCat}   0001   ************************************************************************")
        println(" ${nombreCat}   0001   ************************************************************************")

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
            listaPedido.add(DataClassPedido(1,dataClassPlato.nombre,dataClassPlato.codigo,dataClassPlato.preciO_VENTA,dataClassPlato.preciO_VENTA,"","PENDIENTE",dataClassPlato.iD_PRODUCTO))
            rv_pedido?.adapter?.notifyDataSetChanged()
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
                    rv_pedido?.adapter?.notifyDataSetChanged()
                }else{
                    rv_pedido?.adapter?.notifyDataSetChanged()
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


            for (i in listaPedido.indices){
                if (datos.estadoPedido == "PENDIENTE"){
                    if (listaPedido[i].namePlato == datos.namePlato){
                        if (datos.namePlato== listaPedido[index].namePlato && listaPedido[index].cantidad>1){
                            val lt = listaPedido.get(index)
                            var cantidad = lt.cantidad-1
                            var precioTotal = lt.precio*cantidad
                            listaPedido.set(index, DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto))

                            rv_pedido?.adapter?.notifyDataSetChanged()
                            println("Ya no cumple")
                        }
                    }
                }else{
                    Toast.makeText(activity, "Pedido no modificable", Toast.LENGTH_SHORT).show()
                }
            }
            actualizarPrecioTotal()

        }

        //-----------BOTON AUMENTAR---------------
        bt_aumentar?.setOnClickListener {

            for (i in listaPedido.indices){

                if (datos.estadoPedido == "PENDIENTE"){

                    if (listaPedido[i].namePlato == datos.namePlato) {
                        val lt = listaPedido[index]
                        var cantidad = lt.cantidad + 1
                        var precioTotal = lt.precio * cantidad
                        listaPedido.set(
                            index,
                            DataClassPedido(cantidad,lt.namePlato,lt.categoria,lt.precio,precioTotal,lt.observacion,"PENDIENTE",lt.idProducto)
                        )
                    }
                    rv_pedido?.adapter?.notifyDataSetChanged()

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
                        desaparecerBarraNavegacion()
                    }
                }else{
                    Toast.makeText(activity, "Pedido no modificable", Toast.LENGTH_SHORT).show()
                }
            }
        }

        desaparecerBarraNavegacion()

        println(datos)

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
                    Toast.makeText(activity, "Se cambio estado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(activity, "Error en cambio de estado", Toast.LENGTH_SHORT).show()
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

