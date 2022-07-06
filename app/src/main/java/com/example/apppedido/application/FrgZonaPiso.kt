package com.example.apppedido.application.View

import com.example.apppedido.domain.Model.DCLoginDatosExito
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.example.apppedido.domain.Model.DCMesaItem
import com.example.apppedido.domain.Model.DCZonaItem
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.infraestruture.adapters.AdapterMesa
import com.example.apppedido.infraestruture.adapters.AdapterZona
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FrgZonaPiso : Fragment() {

    //*************** INICIO ATRIBUTOS ********************
    private lateinit var adapterZona: AdapterZona
    private lateinit var adapterMesa: AdapterMesa
    private val listaZona = ArrayList<DCZonaItem>()
    private val listaInjeccion = ArrayList<DCZonaItem>()
    private val listaMesa = ArrayList<DCMesaItem>()
    var apiInterface: APIService? = null

    //*************** FIN ATRIBUTOS ********************
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frg_zona_piso, container, false)
        return view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //*************** INICIO ATRIBUTOS ********************
        apiInterface = RetrofitCall.client?.create(APIService::class.java) as APIService

        //INICIAR ZONAS
        initZona()
        iniciarZonas()
        initMesa()
        refreshMesa()

        //INICIAR DATOS

        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()

    }

    private fun refreshMesa() {

    }

    private fun iniciarZonas() {
        val datosRecuperados = arguments
        if (datosRecuperados?.getSerializable("ListaZona")==null){

            getDataZona()

        }else{
            val lista2: ArrayList<DCZonaItem> = datosRecuperados?.getSerializable("ListaZona") as ArrayList<DCZonaItem>
            val sr_mesa = view?.findViewById<SwipeRefreshLayout>(R.id.sr_mesa)
            listaZona.addAll(lista2)

            getDataMesa(listaZona[0].idZona)

            sr_mesa?.setOnRefreshListener{
                getDataMesa(listaZona[0].idZona)
                sr_mesa.isRefreshing = false
            }

            adapterZona.notifyDataSetChanged()
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
    fun initZona(){
        val rv_zona = view?.findViewById<RecyclerView>(R.id.rv_zona2)
        rv_zona?.layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)
        adapterZona = AdapterZona(listaZona) { dataclassZonas -> onItemDatosZonas(dataclassZonas) }
        rv_zona?.adapter = adapterZona

    }
    //SELECCIONAR ZONA Y SE LISTA LAS MESAS

    private fun onItemDatosZonas(dataclassZonas: DCZonaItem) {

        val idZona = dataclassZonas.idZona
        getDataMesa(idZona)

        val sr_mesa = view?.findViewById<SwipeRefreshLayout>(R.id.sr_mesa)
        sr_mesa?.setOnRefreshListener{
            getDataMesa(idZona)
            sr_mesa.isRefreshing = false
        }

    }

    // Obtiene la informacion del API Zona
    private fun getDataZona(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getZonas()
            activity?.runOnUiThread {
                if(response.isSuccessful){
                    val datoss = response.body()
                    for (i in datoss?.indices!!){
                        listaInjeccion.add(DCZonaItem(datoss[i].nombreZonas, datoss[i].idZona))
                    }
                    inyectarDatosZonas(listaInjeccion)
                }
            }
        }
    }

    fun inyectarDatosZonas(lista: ArrayList<DCZonaItem>){
        ///*************   ROOM FUNCIONAL ******************************************************
        GlobalScope.launch(Dispatchers.Default) {
            database.daoZona().deleteTable()
            database.daoZona().clearPrimaryKey()
            lista.forEach {database.daoZona().insertZona(EntityZona(0,it.idZona,it.nombreZonas))}

            for (i in 1..lista.size){
                val zonasId = database.daoZona().getZonaId(i)
                val zonasNombre = database.daoZona().getZonaNombre(i)

                withContext(Dispatchers.Main) {
                    listaZona.add(DCZonaItem(zonasNombre,zonasId))
                    if (i == lista.size){
                        adapterZona.notifyDataSetChanged()
                    }

                    val sr_mesa = view?.findViewById<SwipeRefreshLayout>(R.id.sr_mesa)
                    sr_mesa?.setOnRefreshListener{
                        getDataMesa(listaZona[0].idZona)
                        sr_mesa.isRefreshing = false
                        adapterZona.notifyDataSetChanged()
                    }
                    getDataMesa(listaZona[0].idZona)
                }
            }
        }
    }

    //********************         MESA        ********************//
    //INICIAR MESA
    fun initMesa(){
        val rv_mesa = view?.findViewById<RecyclerView>(R.id.rv_mesa2)
        rv_mesa?.layoutManager = GridLayoutManager(activity,4, RecyclerView.HORIZONTAL,false)
        adapterMesa = AdapterMesa(listaMesa) { dataclassMesa -> onItemDatosMesa(dataclassMesa) }
        rv_mesa?.adapter = adapterMesa
    }

    //SELECCIONAR MESA
    private fun onItemDatosMesa(dataclassMesa: DCMesaItem) {
        //ENVIAR DATOS DE MESA
        val datosRecuperados = arguments
        val recibeDatos: DCLoginDatosExito = datosRecuperados?.getSerializable("DATOUSUARIO") as DCLoginDatosExito
        val recibeDatosBorrador = datosRecuperados.getSerializable("BORRADOR")
        val recibeDatosListaCategoria = datosRecuperados.getSerializable("ListaCategoria")

        println("***** lista recibira de categoria ***********")
        println("$recibeDatosListaCategoria")
        println("***** *************************** ***********")


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
        enviarDatos.putString("IDMESA",idMesa.toString())
        enviarDatos.putString("IDZONA",idZona)
        enviarDatos.putSerializable("DatosUsuario",recibeDatos)
        enviarDatos.putSerializable("ListaZona",listaZona)
        enviarDatos.putSerializable("ListaCategoria",recibeDatosListaCategoria)


        println("DATOS RECIBIDO Y ENVIADO: $recibeDatosBorrador")

        enviarDatos.putSerializable("BORRADOR",recibeDatosBorrador)

        val fragment = FrgCatPlat()
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()
        fragment.arguments = enviarDatos

        //CAMBIAR FRAMENT
        transaction!!.replace(R.id.frm_panel, fragment).commit()
    }

    // Obtiene la informacion del API Mesa
    private fun getDataMesa(idZona:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiInterface!!.getMesa("piso eq '$idZona'" )
            activity?.runOnUiThread{
                if(response.isSuccessful){
                    listaMesa.clear()
                    listaMesa.addAll(response.body()!!)
                    adapterMesa.notifyDataSetChanged()
                }else{
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}