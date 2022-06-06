package com.example.apppedido

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
        texto()
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

    fun texto(){
        val tx_texto = view?.findViewById<TextView>(R.id.tx_textoas)
        tx_texto?.text = "como estas"
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
        rv_mesa?.layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)
        adapterMesa = AdapterMesa(listaMesa)
        rv_mesa?.adapter = adapterMesa
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