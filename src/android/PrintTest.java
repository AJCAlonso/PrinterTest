package cordova.plugin.printtest;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;

/**
 * This class echoes a string called from JavaScript.
 */
public class PrintTest extends CordovaPlugin {

    private GertecPrinter gertecPrinter;
    
    private IGEDI iGedi = null;
    private IPRNTR iPrint = null;
    private GEDI_PRNTR_e_Status status;

    private ConfigPrint configPrint = new ConfigPrint();
    
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        gertecPrinter = new GertecPrinter(cordova.getActivity(), webView.getContext());
        gertecPrinter.setConfigImpressao(configPrint);
    }
    

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    //public boolean execute(String action, String args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("add")) {
            this.add(args, callbackContext);
            return true;
        } else if (action.equals("Print_QRCode")) {
            this.Print_QRCode(args, callbackContext);
            return true;
        } else if (action.equals("Print_Ticket")) {
            this.Print_Ticket(args, callbackContext);
            return true;
        } else if (action.equals("Print_Ticket_Pagamento")) {
            this.Print_Ticket_Pagamento(args, callbackContext);
            return true;
        } else if (action.equals("nativeToast")) {
            //Context context = this.cordova.getActivity().getApplicationContext();
            //gertecPrinter = new GertecPrinter(this.cordova.getActivity(), context);
            //gertecPrinter.setConfigImpressao(configPrint);
            try {
                String message = gertecPrinter.getStatusImpressora();
                nativeToast(message);
                //gertecPrinter.imprimeTexto(message.toString());
                gertecPrinter.imprimeBarCode("Teste Ionic", 200, 200, "QR_CODE");
                gertecPrinter.avancaLinha(10);
                gertecPrinter.ImpressoraOutput();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    gertecPrinter.ImpressoraOutput();
                } catch (GediException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    private void add(JSONArray args, CallbackContext callback) {
        //int species = args.getJSONObject(0).getInt("param1");
        //String sp2 = args.getJSONObject(0).getString("param2");
        //Toast.makeText(webView.getContext(), args, Toast.LENGTH_SHORT).show();
        if (args != null) {
            try {
                //Toast.makeText(webView.getContext(), "params :"+args.getJSONObject(0).getString("param1")+" e "+args.getJSONObject(0).getString("param2"), Toast.LENGTH_SHORT).show();
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));
                callback.success(""+(p1+p2));
                //callback.success("3");
            } catch (Exception e) {
                callback.error("Something went wrong :" + e);
            }
        } else {
            callback.error("Expected one non-empty JSON argument.");

        }
    }
    public void nativeToast(String sMessage){
        Toast.makeText(webView.getContext(), sMessage, Toast.LENGTH_SHORT).show();
    }

    private void Print_QRCode(JSONArray args, CallbackContext callback) {
        try {
            if (args != null) {
                String sStatus = gertecPrinter.getStatusImpressora();
                if(gertecPrinter.isImpressoraOK()) {
                    configPrint = new ConfigPrint();
                    gertecPrinter.setConfigImpressao(configPrint);
                    gertecPrinter.imprimeBarCode(args.getJSONObject(0).getString("Message"), 200, 200, "QR_CODE");
                    gertecPrinter.ImpressoraOutput();
                }else{
                    nativeToast(sStatus);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                gertecPrinter.ImpressoraOutput();
            } catch (GediException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void Print_Ticket(JSONArray args, CallbackContext callback) {
        try {
            if (args != null) {
                configPrint = new ConfigPrint();
                configPrint.setItalico(false);
                configPrint.setNegrito(true);
                configPrint.setTamanho(20);
                configPrint.setFonte("MONOSPACE");
                configPrint.setAlinhamento("CENTER");
                gertecPrinter.setConfigImpressao(configPrint);
                String sStatus = gertecPrinter.getStatusImpressora();
                if(gertecPrinter.isImpressoraOK()) {
                    gertecPrinter.imprimeTexto(args.getJSONObject(0).getString("estac"));
                    configPrint.setNegrito(false);
                    gertecPrinter.setConfigImpressao(configPrint);
                    gertecPrinter.imprimeTexto("CNPJ: " + args.getJSONObject(0).getString("cnpj"));
                    gertecPrinter.imprimeTexto("-------------------------------");
                    gertecPrinter.avancaLinha(2);

                    gertecPrinter.imprimeTexto("Número controle: ");
                    configPrint.setNegrito(true);
                    configPrint.setTamanho(40);
                    gertecPrinter.setConfigImpressao(configPrint);
                    gertecPrinter.imprimeTexto(args.getJSONObject(0).getString("controle"));
                    
                    configPrint.setNegrito(false);
                    configPrint.setAlinhamento("LEFT");
                    configPrint.setTamanho(20);
                    gertecPrinter.setConfigImpressao(configPrint);
                    gertecPrinter.imprimeTexto("Entrada: " + args.getJSONObject(0).getString("tkdata"));
                    gertecPrinter.imprimeTexto("Veiculo: " + args.getJSONObject(0).getString("veiculo"));
                    gertecPrinter.imprimeTexto("Placa  : " + args.getJSONObject(0).getString("placa"));
                    gertecPrinter.avancaLinha(2);
                    gertecPrinter.imprimeTexto("-------------------------------");

                    gertecPrinter.imprimeTexto(args.getJSONObject(0).getString("endereco")+ " - "+args.getJSONObject(0).getString("bairro")+"/"+args.getJSONObject(0).getString("estado"));
                    gertecPrinter.imprimeTexto("CEP: " + args.getJSONObject(0).getString("cep"));
                    gertecPrinter.avancaLinha(15);
                    
                    gertecPrinter.imprimeBarCode(args.getJSONObject(0).getString("controle"), 200, 200, "QR_CODE");
                    gertecPrinter.avancaLinha(20);
                    gertecPrinter.ImpressoraOutput();
                }else{
                    nativeToast(sStatus);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                gertecPrinter.ImpressoraOutput();
            } catch (GediException e) {
                e.printStackTrace();
            }
        }
    }
    private void Print_Ticket_Pagamento(JSONArray args, CallbackContext callback) {
        try {
            if (args != null) {
                configPrint = new ConfigPrint();
                configPrint.setItalico(false);
                configPrint.setNegrito(true);
                configPrint.setTamanho(20);
                configPrint.setFonte("MONOSPACE");
                configPrint.setAlinhamento("CENTER");
                gertecPrinter.setConfigImpressao(configPrint);
                String sStatus = gertecPrinter.getStatusImpressora();
                if(gertecPrinter.isImpressoraOK()) {
                    gertecPrinter.imprimeTexto(args.getJSONObject(0).getString("estac"));
                    configPrint.setNegrito(false);
                    configPrint.setAlinhamento("LEFT");
                    gertecPrinter.setConfigImpressao(configPrint);
                    gertecPrinter.imprimeTexto("CNPJ: " + args.getJSONObject(0).getString("cnpj"));
                    gertecPrinter.imprimeTexto("-------------------------------");
                    gertecPrinter.avancaLinha(2);

                    gertecPrinter.imprimeTexto("Cliente : " + args.getJSONObject(0).getString("cliente"));
                    gertecPrinter.imprimeTexto("CPF     : " + args.getJSONObject(0).getString("cpf"));
                    if (args.getJSONObject(0).getString("cpf") != "   .   .   -  ") {
                        gertecPrinter.imprimeTexto("CPF     : " + args.getJSONObject(0).getString("cpf"));
                    }
                    gertecPrinter.avancaLinha(15);

                    gertecPrinter.imprimeTexto("Controle: "+args.getJSONObject(0).getString("controle"));
                    gertecPrinter.imprimeTexto("Veículo : "+args.getJSONObject(0).getString("veiculo"));
                    gertecPrinter.imprimeTexto("Placa   : "+args.getJSONObject(0).getString("placa"));
                    gertecPrinter.avancaLinha(15);

                    
                    gertecPrinter.imprimeTexto("Serviço  : " + args.getJSONObject(0).getString("servico"));
                    gertecPrinter.imprimeTexto("Convênio : " + args.getJSONObject(0).getString("convenio"));
                    gertecPrinter.avancaLinha(15);

                    gertecPrinter.imprimeTexto("Entrada: " + args.getJSONObject(0).getString("tkdata"));
                    gertecPrinter.imprimeTexto("Saída  : " + args.getJSONObject(0).getString("tkdatasaida"));
                    gertecPrinter.imprimeTexto("Permanência: " + args.getJSONObject(0).getString("permanencia"));
                    gertecPrinter.avancaLinha(15);

                    gertecPrinter.imprimeTexto("Meio Pgto: " + args.getJSONObject(0).getString("meiopgto"));
                    gertecPrinter.imprimeTexto("Valor Pago: R$ " + args.getJSONObject(0).getString("valor"));
                    gertecPrinter.avancaLinha(4);
                    gertecPrinter.imprimeTexto("-------------------------------");

                    gertecPrinter.imprimeTexto(args.getJSONObject(0).getString("endereco")+ " - "+args.getJSONObject(0).getString("bairro")+"/"+args.getJSONObject(0).getString("estado"));
                    gertecPrinter.imprimeTexto("CEP: " + args.getJSONObject(0).getString("cep"));
                    gertecPrinter.avancaLinha(92);
                    
                    gertecPrinter.ImpressoraOutput();
                }else{
                    nativeToast(sStatus);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                gertecPrinter.ImpressoraOutput();
            } catch (GediException e) {
                e.printStackTrace();
            }
        }
    }
}
