package com.example.apppedido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FrgZonaPiso : Fragment() {

    private lateinit var adapterZona: AdapterZona
    private lateinit var adapterMesa: AdapterMesa

    private val listaZona = ArrayList<DCZonaItem>()
    private val listaMesa = ArrayList<DCMesaItem>()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frg_zona_piso, container, false)



        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //INICIAR ZONAS
        initZona()
        initMesa()
        getDataZona()

        //Iniciar Datos
        getDataMesa("0001")




    }


    fun initZona(){
        val rv_zona = view?.findViewById<RecyclerView>(R.id.rv_zona2)
        rv_zona?.layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)
        adapterZona = AdapterZona(listaZona) { dataclassZonas -> onItemDatosZonas(dataclassZonas) }
        rv_zona?.adapter = adapterZona
    }

    private fun onItemDatosZonas(dataclassZonas: DCZonaItem) {
        val idZona = dataclassZonas.idZona
        getDataMesa(idZona)
    }


    // Obtiene la informacion del API Zona
    private fun getDataZona(){
        CoroutineScope(Dispatchers.IO).launch {
            val responseZona = getRetrofit().getZonas()
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

    fun initMesa(){
        val rv_mesa = view?.findViewById<RecyclerView>(R.id.rv_mesa2)
        rv_mesa?.layoutManager = GridLayoutManager(activity,4, RecyclerView.HORIZONTAL,false)
        adapterMesa = AdapterMesa(listaMesa) { dataclassMesa -> onItemDatosMesa(dataclassMesa) }
        rv_mesa?.adapter = adapterMesa
    }

    private fun onItemDatosMesa(dataclassMesa: DCMesaItem) {
        val idZona = dataclassMesa.idZona
        var nameZona:String = ""

        for (i in listaZona.indices){
            if(listaZona[i].idZona==idZona){
                nameZona = listaZona[i].nombreZonas
                break
            }
        }

        val idMesa = dataclassMesa.idMesa

        val enviarDatos = Bundle()
        enviarDatos.putString("ZONA",nameZona)
        enviarDatos.putString("MESA",idMesa.toString())


        val fragment = FrgCatPlat()
        val fragmentManager = activity?.supportFragmentManager
        val transaction = fragmentManager?.beginTransaction()
        fragment.arguments = enviarDatos
        transaction!!.replace(R.id.frm_panel, fragment).addToBackStack(null).commit()

    }


    // Obtiene la informacion del API Mesa
    private fun getDataMesa(idZona:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = getRetrofit().getMesa("piso eq '$idZona' and estadoTrans eq 'L'" )
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