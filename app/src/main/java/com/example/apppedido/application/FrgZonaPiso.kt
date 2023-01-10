package com.example.apppedido.application.View

import com.example.apppedido.domain.Model.DCLoginDatosExito
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apppedido.*
import com.example.apppedido.DataBase.ComandaDB
import com.example.apppedido.DataBase.EntityZona
import com.example.apppedido.ValidarConfiguracion.Companion.database
import com.example.apppedido.domain.DataClassPedidoBorrador
import com.example.apppedido.domain.Model.DCMesaItem
import com.example.apppedido.domain.Model.DCZonaItem
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.infraestruture.adapters.AdapterMesa
import com.example.apppedido.infraestruture.adapters.AdapterZona
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.*


class FrgZonaPiso : Fragment() {
    private val _listTable: MutableLiveData<MutableList<DCMesaItem>> = MutableLiveData()
    var listTable: LiveData<MutableList<DCMesaItem>> = _listTable
    private val listatable = mutableListOf<DCMesaItem>()
    //*************** INICIO ATRIBUTOS ********************
    private lateinit var adapterZona: AdapterZona
    private lateinit var adapterMesa: AdapterMesa
    private val listaZona = ArrayList<DCZonaItem>()
    private val listaInjeccion = ArrayList<DCZonaItem>()
    private val listaMesa = mutableListOf<DCMesaItem>()
    var apiInterface: APIService? = null

    lateinit var job: Job

    //*************** FIN ATRIBUTOS ********************
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frg_zona_piso, container, false)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //*************** INICIO ATRIBUTOS ********************
        apiInterface = RetrofitCall.client?.create(APIService::class.java) as APIService

        //INICIAR COMPONENTES
        initComponetZona()
        initComponentMesa()

        iniciarZonas()
        getListTable()

        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()
    }

    val init = 0
    private fun iniciarZonas() {
        init + 1
        println("REPETIR  EL MISMO"+init)
        CoroutineScope(Dispatchers.IO).launch {
            println("*********  Tabla zona exite  ************")
            println(database.daoZona().isExistsZonas())
            println("Exite")
            if (database.daoZona().isExistsZonas()){
                listaZona.clear()
                database.daoZona().getAllZonas().forEach {
                    listaZona.add(DCZonaItem(
                        nombreZonas = it.nombreZonas,
                        idZona = it.idZona)
                    )
                }
                activity?.runOnUiThread {
                    getDataMesa(listaZona[0].idZona)
                    adapterZona.notifyDataSetChanged()
                }
            }else{
                println("No Exite")
                activity?.runOnUiThread {
                    getDataZona()
                }
            }
        }
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
    //********************         ZONA        ********************//
    //INICIAR MESA
    fun initComponetZona(){
        val rv_zona = view?.findViewById<RecyclerView>(R.id.rv_zona2)
        rv_zona?.layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)
        adapterZona = AdapterZona(listaZona) { dataclassZonas -> onItemDatosZonas(dataclassZonas) }
        rv_zona?.adapter = adapterZona
    }
    //SELECCIONAR ZONA Y SE LISTA LAS MESAS
    private fun onItemDatosZonas(dataclassZonas: DCZonaItem) {
        val idZona = dataclassZonas.idZona
        cancelarCorrutine()
        getDataMesa(idZona)
    }
    // Obtiene la informacion del API Zona
    private fun getDataZona(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getZonas()
            if(response.code() == 200){
                val dataResponse = response.body()!!
                listaZona.clear()
                database.daoZona().deleteTable()
                database.daoZona().clearPrimaryKey()
                dataResponse.forEach {
                    database.daoZona().insertZona(EntityZona(
                        id=0,
                        idZona = it.idZona,
                        nombreZonas = it.nombreZonas)
                    )
                }
                println("Recoleccion de datos")
                println(database.daoZona().getAllZonas())

                activity?.runOnUiThread {
                    dataResponse.forEach {
                        listaZona.add(
                            DCZonaItem(
                            it.nombreZonas,
                            it.idZona)
                        )
                    }
                    getDataMesa(listaZona[0].idZona)
                    adapterZona.notifyDataSetChanged()
                }
            }else{
                Toast.makeText(activity, "Error: ${response.code()} // getDataZona ", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //********************         MESA        ********************//
    //INICIAR MESA
    fun initComponentMesa(){
        val rv_mesa = view?.findViewById<RecyclerView>(R.id.rv_mesa2)
        rv_mesa?.layoutManager = GridLayoutManager(activity,4, RecyclerView.HORIZONTAL,false)
        adapterMesa = AdapterMesa(listatable) { dataclassMesa -> onItemDatosMesa(dataclassMesa) }
        rv_mesa?.adapter = adapterMesa
    }
    //SELECCIONAR MESA
    private fun onItemDatosMesa(dataclassMesa: DCMesaItem) {
        //ENVIAR DATOS DE MESA
        val datosRecuperados = arguments
        var recibeDatos: DCLoginDatosExito? = datosRecuperados!!.getSerializable("DatosUsuario") as DCLoginDatosExito
        var recibeDatosBorrador = datosRecuperados.getSerializable("BORRADOR")
        //var recibeDatosListaCategoria = datosRecuperados!!.getSerializable("ListaCategoria")


        println("***** lista Borrador ****************")
        println("$recibeDatosBorrador")
        println("***** *******************************")

        val nameMozo = dataclassMesa.NombreMozo
        val idZona = dataclassMesa.idZona
        var nameZona = ""

        //ENCONTRAR LA ZONA
        for (i in listaZona.indices){
            if(listaZona[i].idZona==idZona){
                nameZona = listaZona[i].nombreZonas
                break
            }
        }

        val idMesa = dataclassMesa.idMesa

        //ENVIAR DATOS
        val enviarDatos = Bundle()
        enviarDatos.putString("NAMEZONA",nameZona)
        enviarDatos.putString("NAMEMOZO",nameMozo)
        enviarDatos.putString("IDMESA",idMesa.toString())
        enviarDatos.putString("IDZONA",idZona)
        enviarDatos.putString("IDZONA",idZona)
        enviarDatos.putSerializable("DatosUsuario",recibeDatos)
        enviarDatos.putSerializable("ListaZona",listaZona)
        //enviarDatos.putSerializable("ListaCategoria",recibeDatosListaCategoria)


        println("DATOS USUARIO EXIOSO: $recibeDatos")


        println("DATOS RECIBIDO Y ENVIADO: $recibeDatosBorrador")

        enviarDatos.putSerializable("BORRADOR",recibeDatosBorrador)

        val fragment = FrgCatPlat()
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()
        fragment.arguments = enviarDatos

        //CAMBIAR FRAMENT
        for (i in 1..2){
            cancelarCorrutine()
        }
        transaction!!.replace(R.id.frm_panel, fragment).commit()
    }
    // Obtiene la informacion del API Mesa
    private fun getDataMesa(idZona:String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (true){
                val response = apiInterface!!.getMesa("piso eq '$idZona' and tipo eq 'A'" )
                activity?.runOnUiThread{
                    if(response.code() == 200){
                        _listTable.value = response.body()!!.toMutableList()
                    }else{
                        Toast.makeText(activity, "Error: ${response.code()} // getDataMesa // piso eq '$idZona' and tipo eq 'A'", Toast.LENGTH_SHORT).show()
                    }
                }
                delay(1500)
            }
        }
    }
    private fun getListTable() {
        listTable.observe(viewLifecycleOwner){ it ->
            adapterMesa.setItems(it)
        }
    }
    fun cancelarCorrutine(){
        job.cancel()
    }
}