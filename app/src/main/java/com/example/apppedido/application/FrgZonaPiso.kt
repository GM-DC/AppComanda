package com.example.apppedido.application.View

import com.example.apppedido.domain.Model.DCLoginDatosExito
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apppedido.*
import com.example.apppedido.domain.Model.DCMesaItem
import com.example.apppedido.domain.Model.DCZonaItem
import com.example.apppedido.infraestruture.network.APIService
import com.example.apppedido.infraestruture.adapters.AdapterMesa
import com.example.apppedido.infraestruture.adapters.AdapterZona
import com.example.apppedido.infraestruture.network.RetrofitCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FrgZonaPiso : Fragment() {

    //*************** INICIO ATRIBUTOS ********************
    private lateinit var adapterZona: AdapterZona
    private lateinit var adapterMesa: AdapterMesa
    private val listaZona = ArrayList<DCZonaItem>()
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
        initMesa()
        getDataZona()
        //INICIAR DATOS
        getDataMesa("0001")

        //DESAPARECER BARRA DE NAVEGACION
        desaparecerBarraNavegacion()
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
    }
    // Obtiene la informacion del API Zona
    private fun getDataZona(){
        CoroutineScope(Dispatchers.IO).launch {
            val responseZona = apiInterface!!.getZonas()
                activity?.runOnUiThread {
                if(responseZona.isSuccessful){
                    listaZona.clear()
                    listaZona.addAll(responseZona.body()!!)
                    adapterZona.notifyDataSetChanged()
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

        val fragment = FrgCatPlat()
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()
        fragment.arguments = enviarDatos

        //CAMBIAR FRAMENT
        transaction!!.add(R.id.frm_panel, fragment).addToBackStack(null).commit()

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

































    // RETROFIT
    fun getRetrofit(): APIService {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://heyeldevs-001-site1.gtempurl.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return  retrofit.create(APIService::class.java)
    }

}