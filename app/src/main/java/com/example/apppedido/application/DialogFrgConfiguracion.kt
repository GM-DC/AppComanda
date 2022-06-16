package com.example.apppedido.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.apppedido.R

class DialogFrgConfiguracion : DialogFragment() {

    //******************    INICIAR COMPONENTES      **************
    val bt_guardarConfiguracion = view?.findViewById<Button>(R.id.bt_guardarConfiguracion)
    val et_ip = view?.findViewById<Button>(R.id.et_ip)
    val et_port = view?.findViewById<Button>(R.id.et_port)


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialogue_configuracion, container, false)
        return view

        //*****************       GUARDAR DATOS          ************
        guardarDatos()
    }

    private fun guardarDatos() {
        bt_guardarConfiguracion?.setOnClickListener {
            var IP = et_ip?.text.toString()
            var PORT = et_ip?.text.toString()
            
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /*
        val bt_guardarConfiguracion = view?.findViewById<Button>(R.id.bt_guardarConfiguracion)
        val et_ip = view?.findViewById<EditText>(R.id.et_ip)
        val et_port = view?.findViewById<EditText>(R.id.et_port)

        bt_guardarConfiguracion?.setOnClickListener {
            if (et_ip?.text.isNullOrEmpty() || et_port?.text.isNullOrEmpty()){
                Toast.makeText(activity, "Debe configurar la aplicación", Toast.LENGTH_SHORT).show()
            }else{
                prefs.saveDominio(et_ip?.text.toString())
                prefs.savePuerto(et_port?.text.toString())
                startActivity(Intent(activity,ActyLogin::class.java))
            }
        }
*/
    }



}