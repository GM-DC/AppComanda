package com.example.apppedido;

import android.app.AlertDialog;
import android.util.DisplayMetrics;

import com.emh.thermalprinter.EscPosPrinter;
import com.emh.thermalprinter.connection.tcp.TcpConnection;
import com.emh.thermalprinter.exceptions.EscPosBarcodeException;
import com.emh.thermalprinter.exceptions.EscPosConnectionException;
import com.emh.thermalprinter.exceptions.EscPosEncodingException;
import com.emh.thermalprinter.exceptions.EscPosParserException;
import com.emh.thermalprinter.textparser.PrinterTextParserImg;

public class Imprimir {
/*
    public void printTcp(String ip, int port) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    EscPosPrinter printer = new EscPosPrinter(new TcpConnection(ip, port), 203, 65f, 42);


                    //Obtener Objeto Precuenta

                    Precuenta object;


                    // imprimes
                    printer.printFormattedTextAndCut(
                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, getApplicationContext().getResources().getDrawableForDensity(R.drawable.testp, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                                    "[L]\n" +
                                    "[C]<u><font size='big'>ORDER N°"object.nropedido"+</font></u>\n[L]\n" +
                                    "[L] _______________\n" +
                                    "[L] Descripción [R]Amount\n[L]\n" +


                                    "[L] <b>Beef Burger [R]10.00\n" +
                                    "[L] Sprite-200ml [R]3.00\n" +


                                    "[L] _______________\n" +
                                    "[L] TOTAL [R]13.00 BD\n" +
                                    "[L] Total Vat Collected [R]1.00 BD\n" +
                                    "[L]\n" +
                                    "[L] _______________\n" +
                                    "[L]\n" +
                                    "[C]<font size='tall'>Customer Info</font>\n" +
                                    "[L] EM Haseeb\n" +
                                    "[L] 14 Streets\n" +
                                    "[L] Cantt, LHR\n" +
                                    "[L] Tel : +923040017916\n" +
                                    "[L]\n" +
                            //  "[L] <barcode type='ean13' height='10'>831254784551</barcode>\n[L]\n" +
                            //    "[L] <qrcode>http://github.com/EmHaseeb/</qrcode>\n[L]\n[L]\n[L]\n"
                    );
                    printer.disconnectPrinter();
                } catch (EscPosConnectionException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity)
                            .setTitle("Broken connection")
                            .setMessage(e.getMessage())
                            .show();
                } catch (EscPosParserException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity)
                            .setTitle("Invalid formatted text")
                            .setMessage(e.getMessage())
                            .show();
                } catch (EscPosEncodingException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity)
                            .setTitle("Bad selected encoding")
                            .setMessage(e.getMessage())
                            .show();
                } catch (EscPosBarcodeException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(activity)
                            .setTitle("Invalid barcode")
                            .setMessage(e.getMessage())
                            .show();
                }
            }
        }).start();

    }


   */
}
