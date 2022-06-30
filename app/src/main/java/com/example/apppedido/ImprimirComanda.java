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
                        builder.append("[L] <b>"+val.getCantidad()+" "+val.getProducto()+"[L]\n");
                        if(!val.getObservacion().isEmpty()){
                        builder.append("[L] Obs: "+val.getObservacion()+"[L]\n");
                        builder.append("[L] _______________\n");
                        }
                    });

                    // imprimes
                    printer.printFormattedTextAndCut(
                            "[C]<u><font size='big'>ORDER N°"+item.getNumerO_PEDIDO()+"</font></u>\n[L]\n" +
                                    "[C]<u><font size='tall'>"+item.getZona()+" MESA: "+item.getMesa()+"</font></u>\n[L]\n" +
                                    "[L] _______________\n" +
                                    "[L] FECHAR Y HORA: "+item.getFechayhora()+"[L]\n"+
                                    "[L] DESTINO: "+item.getDestino()+"[L]\n"+
                                    "[L] PRODUCTOS [L]\n" +
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
