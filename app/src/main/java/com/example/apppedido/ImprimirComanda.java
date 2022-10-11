package com.example.apppedido;

import android.annotation.SuppressLint;

import com.emh.thermalprinter.EscPosPrinter;
import com.emh.thermalprinter.connection.tcp.TcpConnection;
import com.emh.thermalprinter.exceptions.EscPosBarcodeException;
import com.emh.thermalprinter.exceptions.EscPosConnectionException;
import com.emh.thermalprinter.exceptions.EscPosEncodingException;
import com.emh.thermalprinter.exceptions.EscPosParserException;
import com.example.apppedido.domain.DCComanda;
import com.example.apppedido.domain.DCComandaItem;
import com.example.apppedido.domain.Model.DCPrecuenta;

import java.util.ArrayList;

public class ImprimirComanda {

    public void printTcp(String ip, int port, DCComandaItem item ) {

        new Thread(new Runnable() {
            @SuppressLint("NewApi")
            public void run() {
                try {
                    EscPosPrinter printer = new EscPosPrinter(new TcpConnection(ip, port), 203, 65f, 42);

                    final StringBuilder builder = new StringBuilder();
                    item.getDetalle().forEach((val)->{
                        builder.append("[L] <font size='tall'>"+val.getCantidad()+" "+val.getProducto()+"</font>[L]\n");
                        if(!val.getObservacion().isEmpty()){
                        builder.append("[L] <font size='tall'> Obs: "+val.getObservacion()+"</font>[L]\n");
                        builder.append("[L] _______________\n");
                        }
                    });

                    // imprimes
                    printer.printFormattedTextAndCut(
                                    "[C]<b><u><font size='big'>"+item.getDestino()+"</font></u></b>\n[L]\n" +
                                    "[L]<b><font size='tall'> ZONA: "+item.getZona()+"</font></b>[L]\n"+
                                    "[L]<b><font size='tall'> MESA: "+item.getMesa()+"</font></b>[L]\n"+
                                    "[L]<b><font size='tall'> MOZO: "+item.getMesero()+"</font></b>[L]\n"+
                                    "[L]<font size='tall'> PEDIDO: "+item.getNumerO_PEDIDO()+"</font>[L]\n"+
                                    "[L]<font size='tall'> FECHAR Y HORA:"+item.getFechayhora()+"</font>[L]\n"+
                                    "[L] \n" +
                                    "[L]<font size='tall'> PRODUCTOS [L]</font>\n" +
                                    builder+
                                    "[L] _______________\n"+
                                    "[L]\n"
                    );
                    printer.disconnectPrinter();
                } catch (EscPosConnectionException | EscPosParserException | EscPosEncodingException | EscPosBarcodeException e) {

                }
            }
        }).start();

    }

    String imp(String texto,int index)//Función sin parámetros
    {
        texto = "[L] <b>Beef Burger [R]10.00\n";
        return texto;
    }

}
