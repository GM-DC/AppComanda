package com.example.apppedido

import android.content.ClipData
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat


class FrgCatPlat() : Fragment() {

    private lateinit var adapterCategoria: AdapterCategoria
    private lateinit var adapterPlato: AdapterPlato
    private lateinit var adapterPedido: AdapterPedido


    private val listaCategoria = ArrayList<DCCategoriaItem>()
    private val listaPlato = ArrayList<DCPlatoItem>()
    private val listaPedido = ArrayList<DataClassPedido>()


    val rv_pedido = view?.findViewById<RecyclerView>(R.id.rv_pedido)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frg_cat_plat, container, false)
        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //INICIAR CATEGORIA
        initCategoria()
        getDataCategoria()

        //INICIAR CATEGORIA
        initPlato()

        //INICIAR PEDIDO
        initPedido()

        //Iniciar Datos
        getDataPlato("0001")




    }


    //********************         CATEGORIA        ********************//
    fun initCategoria(){
        val rv_categoria = view?.findViewById<RecyclerView>(R.id.rv_categoria)
        rv_categoria?.layoutManager = GridLayoutManager(activity,2, RecyclerView.HORIZONTAL,false)
        adapterCategoria = AdapterCategoria(listaCategoria) { dataclassCategoria -> onItemDatosCategoria(dataclassCategoria) }
        rv_categoria?.adapter = adapterCategoria
    }

    private fun onItemDatosCategoria(dataclassCategoria: DCCategoriaItem) {
        val idCategoria = dataclassCategoria.idCategoria
        getDataPlato(idCategoria)
    }


    // Obtiene la informacion del API Mesa
    private fun getDataCategoria() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = getRetrofit().getCategoria()
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



    //********************         PLATOS        ********************//

    fun initPlato(){
        val rv_plato = view?.findViewById<RecyclerView>(R.id.rv_platillo)
        rv_plato?.layoutManager = GridLayoutManager(activity,3, RecyclerView.VERTICAL,false)
        adapterPlato = AdapterPlato(listaPlato) { dataClassPlato -> onItemDatosPlato(dataClassPlato) }
        rv_plato?.adapter = adapterPlato
    }


    // Obtiene la informacion del API Mesa
    private fun getDataPlato(idCat:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = getRetrofit().getPlato("idcategoria eq '$idCat'")
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

        Toast.makeText(activity, "${dataClassPlato.namePlato}", Toast.LENGTH_SHORT).show()


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


    }

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
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listaPedido.removeAt(viewHolder.bindingAdapterPosition)
                rv_pedido?.adapter?.notifyDataSetChanged()
            }
        }
        val swap =  ItemTouchHelper(itemswipe)
        swap.attachToRecyclerView(rv_pedido)







    }

    fun onIntemDatosPedido(dataclassPedido: DataClassPedido) {

    }
















    // Devuelve un Retrofit
    fun getRetrofit(): APIService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://heyeldevs-001-site1.gtempurl.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return  retrofit.create(APIService::class.java)
    }
}